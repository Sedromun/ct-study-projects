#pragma once

#include <algorithm>
#include <cassert>
#include <cstdlib>
#include <iterator>
#include <memory>
#include <utility>

template <typename T>
class circular_buffer {
private:
  template <class U>
  struct basic_buffer_iterator {
    using value_type = T;
    using reference = U&;
    using pointer = U*;
    using difference_type = std::ptrdiff_t;
    using iterator_category = std::random_access_iterator_tag;

    basic_buffer_iterator() = default;

    basic_buffer_iterator(const circular_buffer* buffer, size_t index) : _buffer(buffer), _index(index) {}

    template <class Y>
    basic_buffer_iterator(basic_buffer_iterator<Y> iterator) : _buffer(iterator.buffer()),
                                                               _index(iterator.index()) {}

    ~basic_buffer_iterator() = default;

    basic_buffer_iterator& operator++() noexcept {
      ++_index;
      return *this;
    }

    basic_buffer_iterator operator++(int) noexcept {
      basic_buffer_iterator tmp = *this;
      ++*this;
      return tmp;
    }

    basic_buffer_iterator& operator--() noexcept {
      --_index;
      return *this;
    }

    basic_buffer_iterator operator--(int) noexcept {
      basic_buffer_iterator tmp = *this;
      --*this;
      return tmp;
    }

    basic_buffer_iterator& operator+=(ptrdiff_t val) noexcept {
      _index += val;
      return *this;
    }

    basic_buffer_iterator& operator-=(ptrdiff_t val) noexcept {
      _index -= val;
      return *this;
    }

    basic_buffer_iterator operator+(ptrdiff_t val) const noexcept {
      return {buffer(), index() + val};
    }

    friend basic_buffer_iterator operator+(ptrdiff_t val, const basic_buffer_iterator& right) noexcept {
      return {right.buffer(), right.index() + val};
    }

    basic_buffer_iterator operator-(ptrdiff_t val) const noexcept {
      return {buffer(), index() - val};
    }

    reference operator*() const noexcept {
      return data()[position(index())];
    }

    pointer operator->() const noexcept {
      return data() + position(index());
    }

    reference operator[](size_t shift) const noexcept {
      return data()[position(index() + shift)];
    }

    template <class Y>
    bool operator==(const Y& iter) const noexcept {
      return index() == iter.index();
    }

    template <class Y>
    bool operator!=(const Y& iter) const noexcept {
      return !(*this == iter);
    }

    bool operator<=(const basic_buffer_iterator& other) const noexcept {
      return index() <= other.index();
    }

    bool operator<(const basic_buffer_iterator& other) const noexcept {
      return index() < other.index();
    }

    bool operator>=(const basic_buffer_iterator& other) const noexcept {
      return !(*this < other);
    }

    bool operator>(const basic_buffer_iterator& other) const noexcept {
      return !(*this <= other);
    }

    ptrdiff_t operator-(const basic_buffer_iterator& other) const noexcept {
      return index() - other.index();
    }

    pointer data() const noexcept {
      return buffer()->data_;
    }

    size_t head() const noexcept {
      return buffer()->head_;
    }

    size_t size() const noexcept {
      return buffer()->size_;
    }

    size_t capacity() const noexcept {
      return buffer()->capacity_;
    }

    size_t index() const noexcept {
      return _index;
    }

    size_t position(size_t index) const noexcept {
      return (head() + index) % capacity();
    }

    const circular_buffer* buffer() const noexcept {
      return _buffer;
    }

  private:
    const circular_buffer* _buffer;
    size_t _index;
  };

public:
  using value_type = T;

  using reference = T&;
  using const_reference = const T&;

  using pointer = T*;
  using const_pointer = const T*;

  using iterator = basic_buffer_iterator<T>;
  using const_iterator = basic_buffer_iterator<const T>;

  using reverse_iterator = std::reverse_iterator<iterator>;
  using const_reverse_iterator = std::reverse_iterator<const_iterator>;

private:
  pointer data_;
  size_t head_;
  size_t size_;
  size_t capacity_;

  size_t shift(size_t index, int64_t shift) const {
    if (capacity_ == 0) {
      return 0;
    }
    return (capacity_ + index + shift) % capacity_;
  }

  circular_buffer(const circular_buffer& other, std::size_t new_capacity)
      : data_(new_capacity == 0 ? nullptr : static_cast<T*>(operator new(sizeof(T) * new_capacity))),
        head_(0),
        size_(other.size()),
        capacity_(new_capacity) {
    try {
      std::uninitialized_copy(other.begin(), other.end(), data_);
    } catch (...) {
      operator delete(data_);
      throw;
    }
  }

  size_t last() const {
    return shift(head_, size() - 1);
  }

public:
  // O(1), nothrow
  circular_buffer() noexcept : data_(nullptr), head_(0), size_(0), capacity_(0) {}

  // O(n), strong
  circular_buffer(const circular_buffer& other) : circular_buffer(other, other.size()) {}

  // O(n), strong
  circular_buffer& operator=(const circular_buffer& other) {
    if (&other != this) {
      circular_buffer tmp(other);
      swap(*this, tmp);
    }
    return *this;
  }

  // O(n), nothrow
  ~circular_buffer() {
    clear();
    operator delete(data_);
  }

  // O(1), nothrow
  size_t size() const noexcept {
    return size_;
  }

  // O(1), nothrow
  bool empty() const noexcept {
    return size() == 0;
  }

  // O(1), nothrow
  size_t capacity() const noexcept {
    return capacity_;
  }

  // O(1), nothrow
  iterator begin() noexcept {
    return {this, 0};
  }

  // O(1), nothrow
  const_iterator begin() const noexcept {
    return {this, 0};
  }

  // O(1), nothrow
  iterator end() noexcept {
    return {this, size()};
  }

  // O(1), nothrow
  const_iterator end() const noexcept {
    return {this, size()};
  }

  // O(1), nothrow
  reverse_iterator rbegin() noexcept {
    return static_cast<const_reverse_iterator>(end());
  }

  // O(1), nothrow
  const_reverse_iterator rbegin() const noexcept {
    return static_cast<const_reverse_iterator>(end());
  }

  // O(1), nothrow
  reverse_iterator rend() noexcept {
    return static_cast<const_reverse_iterator>(begin());
  }

  // O(1), nothrow
  const_reverse_iterator rend() const noexcept {
    return static_cast<const_reverse_iterator>(begin());
  }

  // O(1), nothrow
  T& operator[](size_t index) {
    return data_[shift(head_, index)];
  }

  // O(1), nothrow
  const T& operator[](size_t index) const {
    return data_[shift(head_, index)];
  }

  // O(1), nothrow
  T& back() {
    return data_[last()];
  }

  // O(1), nothrow
  const T& back() const {
    return data_[last()];
  }

  // O(1), nothrow
  T& front() {
    return data_[head_];
  }

  // O(1), nothrow
  const T& front() const {
    return data_[head_];
  }

  // O(1), strong
  void push_back(const T& val) {
    if (size() == capacity()) {
      circular_buffer tmp(*this, capacity_ * 2 + 1);
      tmp.push_back(val);
      swap(*this, tmp);
    } else {
      new (data_ + shift(head_, size())) T(val);
      ++size_;
    }
  }

  // O(1), strong
  void push_front(const T& val) {
    if (size() == capacity()) {
      circular_buffer tmp(*this, capacity_ * 2 + 1);
      tmp.push_front(val);
      swap(*this, tmp);
    } else {
      new (data_ + shift(head_, -1)) T(val);
      ++size_;
      head_ = shift(head_, -1);
    }
  }

  // O(1), nothrow
  void pop_back() {
    std::destroy_n(end() - 1, 1);
    --size_;
  }

  // O(1), nothrow
  void pop_front() {
    std::destroy_n(begin(), 1);
    head_ = shift(head_, 1);
    --size_;
  }

  // O(n), strong
  void reserve(size_t desired_capacity) {
    if (desired_capacity > capacity_) {
      circular_buffer tmp(*this, desired_capacity);
      swap(*this, tmp);
    }
  }

  // O(n), basic
  iterator insert(const_iterator pos, const T& val) {
    if (pos.index() >= size() / 2) {
      push_back(val);
      iterator iter = end() - 1;
      while (iter != pos) {
        std::iter_swap(iter, iter - 1);
        --iter;
      }
      return iter;
    } else {
      push_front(val);
      iterator iter = begin();
      while (iter != pos) {
        std::iter_swap(iter, iter + 1);
        ++iter;
      }
      return iter;
    }
  }

  // O(n), basic
  iterator erase(const_iterator pos) {
    return erase(pos, pos + 1);
  }

  // O(n), basic
  iterator erase(const_iterator first, const_iterator last) {
    if (last <= first) {
      return first;
    }
    ptrdiff_t figin = first - begin();
    if (first - begin() >= end() - last) { // move to end
      ptrdiff_t len = last - first;
      iterator iter = last;
      for (; iter < end(); ++iter) {
        std::swap(*iter, *(iter - len));
      }
      for (ptrdiff_t i = 0; i < len; i++) {
        pop_back();
      }
      return begin() + figin;
    } else { // move to front
      ptrdiff_t len = last - first;
      if (first != begin()) {
        iterator iter = first;
        while (iter != begin()) {
          --iter;
          std::swap(*iter, *(iter + len));
        }
      }
      for (ptrdiff_t i = 0; i < len; i++) {
        pop_front();
      }
      return begin() + figin;
    }
  }

  // O(n), nothrow
  void clear() noexcept {
    while (!empty()) {
      pop_back();
    }
  }

  // O(1), nothrow
  friend void swap(circular_buffer& left, circular_buffer& right) noexcept {
    std::swap(left.capacity_, right.capacity_);
    std::swap(left.size_, right.size_);
    std::swap(left.data_, right.data_);
    std::swap(left.head_, right.head_);
  }
};
