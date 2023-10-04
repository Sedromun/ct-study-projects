#include "big_integer.h"

#include <algorithm>
#include <cmath>
#include <cstddef>
#include <iostream>
#include <ostream>
#include <stdexcept>
#include <vector>

big_integer::big_integer() : _number(0), _sign(true) {}

big_integer::big_integer(const big_integer& other) = default;

void big_integer::swap(big_integer& other) {
  std::swap(_number, other._number);
  std::swap(_sign, other._sign);
}

template <class T>
void big_integer::constructor(T a) {
  if (a == 0) {
    return;
  }
  std::uint64_t b = a;
  if (a == (1ll << 63)) {
    b = (1ull << 63);
  }
  _number.push_back(b % BASE);
  if (b / BASE != neutral()) {
    _number.push_back(b / BASE);
  }
}

big_integer::big_integer(long long a) : _sign(a >= 0) {
  constructor(a);
}

big_integer::big_integer(unsigned long long a) : _sign(true) {
  constructor(a);
}

big_integer::big_integer(long a) : _sign(a >= 0) {
  constructor(a);
}

big_integer::big_integer(unsigned long a) : _sign(true) {
  constructor(a);
}

big_integer::big_integer(int a) : _sign(a >= 0) {
  constructor(a);
}

big_integer::big_integer(unsigned int a) : _sign(true) {
  constructor(a);
}

big_integer::~big_integer() = default;

big_integer& big_integer::operator=(const big_integer& other) {
  if (&other != this) {
    big_integer(other).swap(*this);
  }
  return *this;
}

big_integer::big_integer(const std::string& str) : _number(1, 0), _sign(true) {
  bool add = false;
  bool sign = true;
  if (str[0] == '-') {
    sign = false;
    add = true;
  } else if (str[0] == '+') {
    add = true;
  }
  if (add >= str.size()) {
    throw std::invalid_argument("must be number");
  }
  for (size_t i = add; i < str.size(); ++i) {
    if (str[i] > '9' || str[i] < '0') {
      throw std::invalid_argument("must be number");
    }
  }
  size_t DIGIT_LEN = 9;
  for (std::size_t i = add; i < str.size();) {
    std::uint32_t a = std::stoi(str.substr(i, DIGIT_LEN));
    mult_short(static_cast<int>(pow(10, std::min(DIGIT_LEN, str.size() - i))));
    add_short(a);
    i += 9;
  }
  if (!sign) {
    negate();
  }
}

void big_integer::delete_zeros() {
  while (((_sign && !is_zero()) || (!_sign && len() > 1)) && _number.back() == neutral()) {
    _number.pop_back();
  }
}

bool big_integer::is_zero() const {
  return _number.empty();
}

void big_integer::add_short(int rhs) {
  std::int32_t carry = 0;
  std::size_t i = 0;
  _number.resize(len() + 2, neutral());
  while (i < len()) {
    std::uint64_t tmp = carry * 1ull + (i == 0 ? std::uint32_t(rhs) : (rhs >= 0 ? 0 : BASE - 1));
    carry = (tmp + _number[i] >= BASE);
    _number[i] += tmp;
    ++i;
  }
  _sign = _number.back() != BASE - 1;
  _number.pop_back();
  delete_zeros();
}

// sub - false, add - true
big_integer& big_integer::sub_add(bool sub_add, const big_integer& rhs) {
  std::int32_t carry = 0;
  std::size_t i = 0;
  _number.resize(std::max(len(), rhs.len()) + 2, neutral());
  while (i < len()) {
    std::uint64_t tmp = carry * 1ull + rhs.value(i);
    if (sub_add) {
      carry = (tmp + _number[i] >= BASE);
      _number[i] += tmp;
    } else {
      carry = (_number[i] < tmp);
      _number[i] -= tmp;
    }
    ++i;
  }
  _sign = _number.back() != BASE - 1;
  _number.pop_back();
  delete_zeros();
  return *this;
}

big_integer& big_integer::operator+=(const big_integer& rhs) {
  return sub_add(true, rhs);
}

big_integer& big_integer::operator-=(const big_integer& rhs) {
  return sub_add(false, rhs);
}

void big_integer::mult_short(int rhs) {
  uint32_t carry = 0;
  bool sign = (_sign == (rhs >= 0));
  abs();
  rhs = std::abs(rhs);
  _number.resize(len() + 1, neutral());
  for (size_t i = 0; i < len() || carry; ++i) {
    uint64_t cur = carry + _number[i] * 1ull * rhs;
    _number[i] = cur % BASE;
    carry = cur / BASE;
  }
  delete_zeros();
  if (_sign != sign) {
    negate();
  }
}

big_integer& big_integer::operator*=(const big_integer& rhs) {
  bool sign = (_sign == rhs._sign);
  big_integer r = rhs;
  abs();
  big_integer l = *this;
  r.abs();
  _number.clear();
  _number.resize(l.len() * rhs.len() + 1);
  for (std::size_t i = 0; i < l.len(); ++i) {
    std::uint32_t carry = 0;
    for (std::size_t j = 0; j < r.len() || carry; ++j) {
      std::uint64_t cur = _number[i + j] + l._number[i] * 1ull * r.value(j) + carry;
      _number[i + j] = std::uint32_t(cur % BASE);
      carry = std::uint32_t(cur / BASE);
    }
  }
  delete_zeros();
  if (_sign != sign) {
    negate();
  }
  return *this;
}

std::uint64_t big_integer::division_long_short(std::uint64_t k) {
  std::uint64_t carry = 0;
  for (std::size_t i = _number.size(); i > 0; --i) {
    std::uint64_t tmp = BASE * carry + _number[i - 1];
    _number[i - 1] = (tmp / k);
    carry = tmp % k;
  }
  delete_zeros();
  return carry;
}

std::uint32_t big_integer::value(std::size_t pos) const {
  return pos < len() ? _number[pos] : neutral();
}

std::uint32_t big_integer::trial(const big_integer& r, const big_integer& d, std::size_t k, std::size_t m) {
  std::size_t km = k + m;
  std::uint64_t res = (r.value(km) * BASE + r.value(km - 1)) / d.value(m - 1);

  if (res >= BASE) {
    return std::uint32_t(BASE - 1);
  } else {
    return std::uint32_t(res);
  }
}

bool big_integer::smaller(const big_integer& r, const big_integer& dq, std::size_t k, std::size_t m) {
  std::size_t i = m;
  while (i != 0 && r.value(i + k) == dq.value(i)) {
    --i;
  }
  return r.value(i + k) < dq.value(i);
}

void big_integer::difference(const big_integer& dq, std::size_t k, std::size_t m) {
  std::uint32_t borrow = 0;
  std::uint64_t diff;
  for (std::size_t i = 0; i <= m; ++i) {
    diff = value(i + k) * 1ll - dq.value(i) - borrow + BASE;
    if (i + k < len()) {
      _number[i + k] = std::uint32_t(diff % BASE);
    }
    borrow = 1 - diff / BASE;
  }
}

std::pair<big_integer, big_integer> big_integer::division_long_long(const big_integer& y) {
  std::size_t m = y.len();
  std::size_t n = len();
  if (m == 0) {
    throw std::invalid_argument("division by 0");
  } else if (m == 1) {
    std::uint32_t y1 = y._number[0];
    big_integer r = division_long_short(y1);
    return {*this, r};
  } else {
    if (*this < y) {
      return {big_integer(), *this};
    } else {
      std::uint32_t f = BASE / (y._number[m - 1] + 1);
      big_integer r = *this * f;
      big_integer d = y * f;
      big_integer q;
      for (std::size_t k = n - m + 1; k > 0; --k) {
        std::uint32_t qt = trial(r, d, k - 1, m);
        big_integer dq = d * qt;
        while (smaller(r, dq, k - 1, m)) {
          qt--;
          dq = d * qt;
        }
        q._number.push_back(qt);
        if (qt != 0) {
          r.difference(dq, k - 1, m);
        }
      }
      r.division_long_short(f);
      std::reverse(q._number.begin(), q._number.end());
      q.delete_zeros();
      return {q, r};
    }
  }
}

big_integer& big_integer::operator/=(const big_integer& rhs) {
  bool sign = (_sign == rhs._sign);
  big_integer r = rhs;
  r.abs();
  abs();
  *this = division_long_long(r).first;
  if (_sign != sign) {
    negate();
  }
  return *this;
}

big_integer& big_integer::operator%=(const big_integer& rhs) {
  bool sign = _sign;
  big_integer r = rhs;
  r.abs();
  abs();
  *this = division_long_long(r).second;
  if (_sign != sign) {
    negate();
  }
  return *this;
}

template <class Operation>
void big_integer::bitwise_operation(const big_integer& rhs, Operation bitwise) {
  _number.resize(std::max(len(), rhs.len()), neutral());
  for (std::size_t i = 0; i < len(); ++i) {
    std::uint32_t b = rhs.value(i);
    _number[i] = bitwise(_number[i], b);
  }
  delete_zeros();
}

big_integer& big_integer::operator&=(const big_integer& rhs) {
  bitwise_operation(rhs, std::bit_and<std::uint32_t>{});
  _sign = _sign || rhs._sign;
  return *this;
}

big_integer& big_integer::operator|=(const big_integer& rhs) {
  bitwise_operation(rhs, std::bit_or<std::uint32_t>{});
  if (_sign && !rhs._sign) {
    _sign = false;
  }
  return *this;
}

big_integer& big_integer::operator^=(const big_integer& rhs) {
  bitwise_operation(rhs, std::bit_xor<std::uint32_t>{});
  _sign = (!_sign || rhs._sign) && (_sign || !rhs._sign);
  return *this;
}

big_integer& big_integer::operator<<=(int rhs) {
  int shifts = rhs / 32;
  rhs %= 32;
  size_t lgt = len();
  _number.resize(lgt + shifts);
  if (shifts > 0) {
    for (std::size_t i = lgt; i > 0; --i) {
      _number[i + shifts - 1] = _number[i - 1];
      _number[i - 1] = 0;
    }
  }
  *this *= (1u << rhs);
  return *this;
}

big_integer& big_integer::operator>>=(int rhs) {
  bool sign = _sign;
  int shifts = rhs / 32;
  rhs %= 32;
  for (std::size_t i = 0; i < len() - shifts; i++) {
    _number[i] = _number[i + shifts];
  }
  _number.resize(len() - shifts);
  *this /= (1u << rhs);
  if (!sign && rhs != 0) {
    *this -= 1;
  }
  return *this;
}

big_integer big_integer::operator+() const {
  return *this;
}

void big_integer::negate() {
  if (is_zero()) {
    return;
  }
  for (std::size_t i = 0; i < len(); ++i) {
    _number[i] = ~_number[i];
  }
  _sign = !_sign;
  *this += 1;
}

big_integer big_integer::operator-() const {
  if (is_zero()) {
    return *this;
  }
  return ++(~*this);
}

big_integer big_integer::operator~() const {
  big_integer tmp(*this);
  for (std::size_t i = 0; i < len(); ++i) {
    tmp._number[i] = ~_number[i];
  }
  tmp._sign = !_sign;
  return tmp;
}

big_integer& big_integer::operator++() {
  add_short(1);
  return *this;
}

big_integer big_integer::operator++(int) {
  big_integer tmp(*this);
  ++(*this);
  return tmp;
}

big_integer& big_integer::operator--() {
  add_short(-1);
  return *this;
}

big_integer big_integer::operator--(int) {
  big_integer tmp(*this);
  --(*this);
  return tmp;
}

big_integer operator+(const big_integer& a, const big_integer& b) {
  return big_integer(a) += b;
}

big_integer operator-(const big_integer& a, const big_integer& b) {
  return big_integer(a) -= b;
}

big_integer operator*(const big_integer& a, const big_integer& b) {
  return big_integer(a) *= b;
}

big_integer operator/(const big_integer& a, const big_integer& b) {
  return big_integer(a) /= b;
}

big_integer operator%(const big_integer& a, const big_integer& b) {
  return big_integer(a) %= b;
}

big_integer operator&(const big_integer& a, const big_integer& b) {
  return big_integer(a) &= b;
}

big_integer operator|(const big_integer& a, const big_integer& b) {
  return big_integer(a) |= b;
}

big_integer operator^(const big_integer& a, const big_integer& b) {
  return big_integer(a) ^= b;
}

big_integer operator<<(const big_integer& a, int b) {
  return big_integer(a) <<= b;
}

big_integer operator>>(const big_integer& a, int b) {
  return big_integer(a) >>= b;
}

bool operator==(const big_integer& a, const big_integer& b) {
  return a._sign == b._sign && a._number == b._number;
}

bool operator!=(const big_integer& a, const big_integer& b) {
  return !(a == b);
}

bool operator<(const big_integer& a, const big_integer& b) {
  if (a.is_zero() && b._sign && !b.is_zero()) {
    return true;
  }
  if (a._sign && !b._sign) {
    return false;
  }
  if (!a._sign && b._sign && !a.is_zero()) {
    return true;
  }
  if (a.len() != b.len()) {
    return (b.len() > a.len()) ^ !a._sign;
  }
  for (std::size_t i = a.len(); i > 0; --i) {
    if (a._number[i - 1] != b._number[i - 1]) {
      return (b._number[i - 1] > a._number[i - 1]);
    }
  }
  return false;
}

bool operator>(const big_integer& a, const big_integer& b) {
  return (b < a);
}

bool operator<=(const big_integer& a, const big_integer& b) {
  return !(a > b);
}

bool operator>=(const big_integer& a, const big_integer& b) {
  return (b <= a);
}

std::string to_string(const big_integer& a) {
  if (a.is_zero()) {
    return "0";
  }
  std::string string;
  big_integer tmp(a);
  if (!a._sign) {
    tmp.negate();
  }
  while (true) {
    uint32_t qr = tmp.division_long_short(1000000000);
    size_t i = 0;
    while (qr > 0) {
      string.push_back(qr % 10 + '0');
      qr /= 10;
      ++i;
    }
    if (tmp.is_zero()) {
      break;
    }
    for (; i < 9; ++i) {
      string.push_back('0');
    }
  }
  std::reverse(string.begin(), string.end());
  return (a._sign ? "" : "-") + string;
}

void big_integer::abs() {
  if (!_sign) {
    negate();
  }
}

std::size_t big_integer::len() const {
  return _number.size();
}

std::uint32_t big_integer::neutral() const {
  return _sign ? 0 : BASE - 1;
}

std::ostream& operator<<(std::ostream& out, const big_integer& a) {
  return out << to_string(a);
}
