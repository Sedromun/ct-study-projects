#pragma once

#include <iosfwd>
#include <limits>
#include <string>
#include <vector>

struct big_integer {
private:
  std::vector<std::uint32_t> _number;
  static const std::uint64_t BASE = (1ull << 32);
  bool _sign;

  void swap(big_integer& other);

  template <class T>
  void constructor(T a);

  big_integer& sub_add(bool sub_add, const big_integer& rhs);

  static bool smaller(const big_integer& r, const big_integer& dq, std::size_t k, std::size_t m);
  static std::uint32_t trial(const big_integer& r, const big_integer& d, std::size_t k, std::size_t m);
  void difference(const big_integer& dq, std::size_t k, std::size_t m);
  std::uint32_t value(std::size_t pos) const;
  std::uint64_t division_long_short(std::uint64_t k);
  std::pair<big_integer, big_integer> division_long_long(const big_integer& y);

  void add_short(int rhs);
  void mult_short(int rhs);

  template <class Operation>
  void bitwise_operation(const big_integer& rhs, Operation bitwise);

  void abs();
  void negate();
  std::size_t len() const;
  std::uint32_t neutral() const;
  void delete_zeros();
  bool is_zero() const;

public:
  big_integer();
  big_integer(const big_integer& other);

  big_integer(int a);
  big_integer(unsigned int a);
  big_integer(long a);
  big_integer(unsigned long a);
  big_integer(long long a);
  big_integer(unsigned long long a);

  explicit big_integer(const std::string& str);
  ~big_integer();

  big_integer& operator=(const big_integer& other);

  big_integer& operator+=(const big_integer& rhs);
  big_integer& operator-=(const big_integer& rhs);
  big_integer& operator*=(const big_integer& rhs);
  big_integer& operator/=(const big_integer& rhs);
  big_integer& operator%=(const big_integer& rhs);

  big_integer& operator&=(const big_integer& rhs);
  big_integer& operator|=(const big_integer& rhs);
  big_integer& operator^=(const big_integer& rhs);

  big_integer& operator<<=(int rhs);
  big_integer& operator>>=(int rhs);

  big_integer operator+() const;
  big_integer operator-() const;
  big_integer operator~() const;

  big_integer& operator++();
  big_integer operator++(int);

  big_integer& operator--();
  big_integer operator--(int);

  friend bool operator==(const big_integer& a, const big_integer& b);
  friend bool operator!=(const big_integer& a, const big_integer& b);
  friend bool operator<(const big_integer& a, const big_integer& b);
  friend bool operator>(const big_integer& a, const big_integer& b);
  friend bool operator<=(const big_integer& a, const big_integer& b);
  friend bool operator>=(const big_integer& a, const big_integer& b);

  friend std::string to_string(const big_integer& a);
};

big_integer operator+(const big_integer& a, const big_integer& b);
big_integer operator-(const big_integer& a, const big_integer& b);
big_integer operator*(const big_integer& a, const big_integer& b);
big_integer operator/(const big_integer& a, const big_integer& b);
big_integer operator%(const big_integer& a, const big_integer& b);

big_integer operator&(const big_integer& a, const big_integer& b);
big_integer operator|(const big_integer& a, const big_integer& b);
big_integer operator^(const big_integer& a, const big_integer& b);

big_integer operator<<(const big_integer& a, int b);
big_integer operator>>(const big_integer& a, int b);

bool operator==(const big_integer& a, const big_integer& b);
bool operator!=(const big_integer& a, const big_integer& b);
bool operator<(const big_integer& a, const big_integer& b);
bool operator>(const big_integer& a, const big_integer& b);
bool operator<=(const big_integer& a, const big_integer& b);
bool operator>=(const big_integer& a, const big_integer& b);

std::string to_string(const big_integer& a);
std::ostream& operator<<(std::ostream& out, const big_integer& a);
