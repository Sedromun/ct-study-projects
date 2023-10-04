#pragma once

#include <cstddef>
#include <new>
#include <utility>

template <typename T>
class vector {
public:
  using value_type = T;

  using reference = T&;
  using const_reference = const T&;

  using pointer = T*;
  using const_pointer = const T*;

  using iterator = pointer;
  using const_iterator = const_pointer;

private:
  pointer data_;
  size_t size_;
  size_t capacity_;

private:
  void free_data(size_t begin) {
    while (begin > 0) {
      data_[--begin].~T();
    }
    operator delete(data_);
  }

  vector(const vector& other, std::size_t new_capacity)
      : data_(new_capacity == 0 ? nullptr : static_cast<T*>(operator new(sizeof(T) * new_capacity))),
        size_(other.size_),
        capacity_(new_capacity) {
    for (std::size_t i = 0; i < other.size_; i++) {
      try {
        new (data_ + i) T(other.data_[i]);
      } catch (...) {
        free_data(i);
        throw;
      }
    }
  }

public:
  vector() noexcept : data_(nullptr), size_(0), capacity_(0) {}

  vector(const vector& other) : vector(other, other.size_) {}

  vector& operator=(const vector& other) & {
    if (&other != this) {
      vector(other).swap(*this);
    }
    return *this;
  }

  ~vector() noexcept {
    free_data(size_);
  }

  reference operator[](size_t index) {
    return data_[index];
  }

  const_reference operator[](size_t index) const {
    return data_[index];
  }

  pointer data() noexcept {
    return data_;
  }

  const_pointer data() const noexcept {
    return data_;
  }

  size_t size() const noexcept {
    return size_;
  }

  reference front() noexcept {
    return data_[0];
  }

  const_reference front() const noexcept {
    return data_[0];
  }

  reference back() noexcept {
    return data_[size_ - 1];
  }

  const_reference back() const noexcept {
    return data_[size_ - 1];
  }

  void push_back(const T& element) {
    if (size_ == capacity_) {
      vector tmp(*this, capacity_ * 2 + 1);
      tmp.push_back(element);
      swap(tmp);
    } else {
      new (data_ + size_) T(element);
      size_++;
    }
  }

  void pop_back() noexcept {
    data_[--size_].~T();
  }

  bool empty() const noexcept {
    return (size_ == 0);
  }

  size_t capacity() const noexcept {
    return capacity_;
  }

  void reserve(size_t new_capacity) {
    if (new_capacity > capacity_) {
      vector(*this, new_capacity).swap(*this);
    }
  }

  void shrink_to_fit() {
    if (size_ != capacity_) {
      vector(*this, size_).swap(*this);
    }
  }

  void clear() noexcept {
    while (!empty()) {
      pop_back();
    }
  }

  void swap(vector& other) noexcept {
    std::swap(capacity_, other.capacity_);
    std::swap(size_, other.size_);
    std::swap(data_, other.data_);
  }

  iterator begin() noexcept {
    return data_;
  }

  iterator end() noexcept {
    return data_ + size_;
  }

  const_iterator begin() const noexcept {
    return data_;
  }

  const_iterator end() const noexcept {
    return data_ + size_;
  }

  iterator insert(const_iterator pos, const T& value) {
    ptrdiff_t abs_pos = pos - begin();
    push_back(value);
    iterator iter = end() - 1;
    while (iter > begin() + abs_pos) {
      std::swap(*iter, *(iter - 1));
      --iter;
    }
    return iter;
  }

  iterator erase(const_iterator pos) noexcept {
    return erase(pos, pos + 1);
  }

  iterator erase(const_iterator first, const_iterator last) noexcept {
    ptrdiff_t len = last - first;
    iterator en = end();
    auto iter = const_cast<iterator>(last);
    for (; iter < end(); ++iter) {
      std::swap(*iter, *(iter - len));
    }
    for (ptrdiff_t i = 0; i < len; i++) {
      pop_back();
    }
    if (len <= 0 || last == en) {
      return end();
    } else {
      return const_cast<iterator>(first);
    }
  }
};
