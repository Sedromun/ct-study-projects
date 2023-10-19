#pragma once

#include <algorithm>
#include <array>
#include <cassert>
#include <cstddef>
#include <iterator>
#include <memory>
#include <new>
#include <utility>

template <typename T, size_t SMALL_SIZE>
class socow_vector {
public:
  using value_type = T;

  using reference = T&;
  using const_reference = const T&;

  using pointer = T*;
  using const_pointer = const T*;

  using iterator = pointer;
  using const_iterator = const_pointer;

private:
  struct buffer {
    size_t capacity;
    size_t ref_count;
    T data[0];

    buffer(size_t capacity) : capacity(capacity), ref_count(1) {}

    void delete_buffer(size_t len) {
      std::destroy_n(data, len);
    }
  };

  size_t size_;
  bool is_data_static; // static - 1, dynamic - 0

  union {
    buffer* dynamic_data_;
    T static_data_[SMALL_SIZE];
  };

  buffer* new_empty_dynamic_buffer(size_t capacity) {
    return new (static_cast<buffer*>(operator new(sizeof(buffer) + sizeof(T) * capacity))) buffer(capacity);
  }

  void clear_static_data() {
    std::destroy_n(static_data_, size());
  }

  void clear_data() {
    if (!is_data_static) {
      release_ref(dynamic_data_);
    } else {
      clear_static_data();
      is_data_static = false;
    }
  }

  buffer* create_buffer_from_data_no_free_memory(size_t capacity, size_t size, const T* input_data) {
    buffer* new_dynamic_data = new_empty_dynamic_buffer(capacity);

    try {
      std::uninitialized_copy_n(input_data, size, new_dynamic_data->data);
    } catch (...) {
      operator delete(new_dynamic_data);
      throw;
    }

    return new_dynamic_data;
  }

  buffer* create_buffer_from_data(size_t capacity, size_t size, const T* input_data) {
    buffer* new_dynamic_data = create_buffer_from_data_no_free_memory(capacity, size, input_data);

    clear_data();

    return new_dynamic_data;
  }

  void add_ref(buffer* buf) {
    assert(!is_data_static);
    if (!buf) {
      return;
    }
    ++buf->ref_count;
  }

  void release_ref(buffer* buf) {
    assert(!is_data_static);
    if (!buf) {
      return;
    }

    if (--buf->ref_count == 0) {
      buf->delete_buffer(size());
      operator delete(buf);
    }
  }

  void unshare(size_t capacity, size_t len) {
    assert(dynamic_data_);
    assert(dynamic_data_->ref_count);

    if (is_data_static || dynamic_data_->ref_count == 1) {
      return;
    }

    dynamic_data_ = create_buffer_from_data(capacity, len, dynamic_data_->data);
  }

  void create_static_data_from_buffer() {
    assert(size() <= SMALL_SIZE);
    buffer* tmp = dynamic_data_;

    try {
      std::uninitialized_copy_n(tmp->data, size(), static_data_);
    } catch (...) {
      dynamic_data_ = tmp;
      throw;
    }
    release_ref(tmp);
    is_data_static = true;
  }

  void set_capacity(size_t new_capacity) {
    if (new_capacity <= SMALL_SIZE && !is_data_static) {
      create_static_data_from_buffer();
    } else if (new_capacity > SMALL_SIZE && is_data_static) {
      dynamic_data_ = create_buffer_from_data(new_capacity, size(), static_data_);
      is_data_static = false;
    } else if (new_capacity >= size() && !is_data_static) {
      if (dynamic_data_->ref_count > 1) {
        unshare(new_capacity, size());
      } else {
        dynamic_data_ = create_buffer_from_data(new_capacity, size(), dynamic_data_->data);
      }
    }
  }

  void delete_last_element(const T* data) {
    data[--size_].~T();
  }

  void push_to_place_with_copy(size_t capacity, size_t pos, const T& value) {
    buffer* new_buffer = create_buffer_from_data_no_free_memory(capacity, pos, std::as_const(*this).data());
    socow_vector tmp;
    tmp.dynamic_data_ = new_buffer;
    tmp.is_data_static = false;
    tmp.size_ = pos;

    new (tmp.dynamic_data_->data + pos) T(value);
    tmp.size_++;
    std::uninitialized_copy_n(std::as_const(*this).data() + pos, size() - pos, tmp.dynamic_data_->data + pos + 1);
    tmp.size_ = size() + 1;

    *this = tmp;
  }

public:
  socow_vector() : size_(0), is_data_static(true) {}

  socow_vector(const socow_vector& other) : size_(other.size_), is_data_static(other.is_data_static) {
    if (other.is_data_static) {
      std::uninitialized_copy_n(other.static_data_, size(), static_data_);
    } else {
      dynamic_data_ = other.dynamic_data_;
      add_ref(dynamic_data_);
    }
  }

  socow_vector& operator=(const socow_vector& other) & {
    if (&other == this) {
      return *this;
    }
    if (other.is_data_static) {
      if (!is_data_static) {
        buffer* buf = dynamic_data_;

        try {
          std::uninitialized_copy_n(other.static_data_, other.size(), static_data_);
        } catch (...) {
          dynamic_data_ = buf;
          throw;
        }
        release_ref(buf);
      } else {
        size_t len = std::min(size(), other.size());
        socow_vector tmp;

        std::uninitialized_copy_n(other.static_data_, len, tmp.static_data_);
        std::destroy_n(static_data_ + len, size() - len);
        tmp.size_ = len;
        std::uninitialized_copy_n(other.static_data_ + len, other.size() - len, static_data_ + len);
        size_ = other.size();
        std::swap_ranges(tmp.static_data_, tmp.static_data_ + len, static_data_);
      }
      is_data_static = true;
    } else {
      if (!is_data_static) {
        release_ref(dynamic_data_);
      } else {
        clear_static_data();
      }
      dynamic_data_ = other.dynamic_data_;
      is_data_static = false;
      add_ref(dynamic_data_);
    }
    size_ = other.size();
    return *this;
  }

  ~socow_vector() {
    if (!is_data_static) {
      release_ref(dynamic_data_);
      size_ = 0;
    } else {
      clear_static_data();
    }
  }

  reference operator[](size_t index) {
    assert(index < size_);
    return data()[index];
  }

  const_reference operator[](size_t index) const {
    assert(index < size_);
    return data()[index];
  }

  pointer data() {
    if (is_data_static) {
      return static_data_;
    } else {
      assert(dynamic_data_);

      unshare(capacity(), size());
      return dynamic_data_->data;
    }
  }

  const_pointer data() const {
    if (is_data_static) {
      return static_data_;
    } else {
      assert(dynamic_data_);

      return dynamic_data_->data;
    }
  }

  size_t size() const {
    return size_;
  }

  reference front() {
    assert(size() > 0);
    return data()[0];
  }

  const_reference front() const {
    assert(size() > 0);
    return data()[0];
  }

  reference back() {
    assert(size() > 0);
    return data()[size() - 1];
  }

  const_reference back() const {
    assert(size_ > 0);
    return data()[size() - 1];
  }

  void push_back(const T& element) {
    if (size() == capacity() || (!is_data_static && dynamic_data_->ref_count > 1)) {
      push_to_place_with_copy(size() == capacity() ? 2 * size() : capacity(), size(), element);
    } else {
      new (data() + size()) T(element);
      size_++;
    }
  }

  void pop_back() {
    assert(!empty());
    if (is_data_static || dynamic_data_->ref_count == 1) {
      delete_last_element(data());
    } else {
      unshare(capacity(), size() - 1);
      --size_;
    }
  }

  bool empty() const {
    return (size_ == 0);
  }

  size_t capacity() const {
    return is_data_static ? SMALL_SIZE : dynamic_data_->capacity;
  }

  void reserve(size_t new_capacity) {
    if (!is_data_static && dynamic_data_->ref_count == 1 && new_capacity < capacity()) {
      return;
    }
    set_capacity(new_capacity);
  }

  void shrink_to_fit() {
    if (size() == capacity()) {
      return;
    }
    set_capacity(size());
  }

  void clear() {
    if (!is_data_static) {
      if (dynamic_data_->ref_count != 1) {
        buffer* buf = new_empty_dynamic_buffer(capacity());
        release_ref(dynamic_data_);
        dynamic_data_ = buf;
      } else {
        std::destroy_n(dynamic_data_->data, size());
      }
      size_ = 0;
    } else {
      while (!empty()) {
        pop_back();
      }
    }
  }

  void swap_two_static(socow_vector& socow1, socow_vector& socow2) {
    std::uninitialized_copy_n(socow1.static_data_ + socow2.size(), socow1.size() - socow2.size(),
                              socow2.static_data_ + socow2.size());
    try {
      std::swap_ranges(socow2.static_data_, socow2.static_data_ + socow2.size(), socow1.static_data_);
    } catch (...) {
      std::destroy_n(socow2.static_data_ + socow2.size(), socow1.size() - socow2.size());
      throw;
    }
    std::destroy_n(socow1.static_data_ + socow2.size(), socow1.size() - socow2.size());
  }

  void swap_static_and_dynamic(socow_vector& dynamic_vector, socow_vector& static_vector) {
    socow_vector tmp(dynamic_vector);
    dynamic_vector = static_vector;
    static_vector = tmp;
  }

  void swap(socow_vector& other) {
    if (this == &other) {
      return;
    }
    if (is_data_static && other.is_data_static) {
      if (size() < other.size()) {
        swap_two_static(other, *this);
      } else {
        swap_two_static(*this, other);
      }
      std::swap(size_, other.size_);
    } else if (!is_data_static && !other.is_data_static) {
      std::swap(dynamic_data_, other.dynamic_data_);
      std::swap(size_, other.size_);
    } else if (!is_data_static && other.is_data_static) {
      swap_static_and_dynamic(*this, other);
    } else {
      swap_static_and_dynamic(other, *this);
    }
  }

  iterator begin() {
    return data();
  }

  iterator end() {
    return data() + size();
  }

  const_iterator begin() const {
    return data();
  }

  const_iterator end() const {
    return data() + size();
  }

  iterator insert(const_iterator pos, const T& value) {
    size_t abs_pos = pos - std::as_const(*this).begin();

    if (size() == capacity() || (!is_data_static && dynamic_data_->ref_count > 1)) {
      push_to_place_with_copy(size() == capacity() ? 2 * size() : capacity(), abs_pos, value);
      return begin() + abs_pos;
    }

    push_back(value);

    iterator iter = end() - 1;
    while (iter > begin() + abs_pos) {
      std::iter_swap(iter, iter - 1);
      --iter;
    }
    return iter;
  }

  iterator erase(const_iterator pos) {
    return erase(pos, pos + 1);
  }

  iterator erase(const_iterator first, const_iterator last) {
    size_t len = last - first;
    size_t first_offset = first - std::as_const(*this).begin();
    size_t last_offset = last - std::as_const(*this).begin();

    if (len == 0) {
      return begin() + first_offset;
    }

    if (!is_data_static && dynamic_data_->ref_count > 1) {
      buffer* new_buffer = create_buffer_from_data_no_free_memory(capacity(), first_offset, dynamic_data_->data);
      {
        socow_vector tmp;
        tmp.dynamic_data_ = new_buffer;
        tmp.size_ = first_offset;
        tmp.is_data_static = false;

        std::uninitialized_copy_n(dynamic_data_->data + last_offset, size() - last_offset,
                                  tmp.dynamic_data_->data + first_offset);
        tmp.size_ = size() - len;

        *this = tmp;
      }
      return begin() + first_offset;
    }

    auto iter = begin() + last_offset;
    for (; iter < end(); ++iter) {
      std::swap(*iter, *(iter - len));
    }
    for (ptrdiff_t i = 0; i < len; i++) {
      pop_back();
    }

    return begin() + first_offset;
  }
};
