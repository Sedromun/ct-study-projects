#include <cerrno>
#include <cstdio>
#include <cstdlib>
#include <cstring>

static size_t prefix_f(const size_t* pi, int c, const char* str, size_t prev_value) {
  size_t j = prev_value;
  while (j > 0 && c != str[j]) {
    j = pi[j - 1];
  }
  return (c == str[j]) ? j + 1 : 0;
}

int main(int argc, char* argv[]) {
  if (argc != 3) {
    std::fprintf(stderr, "Incorrect number of argument: 2 - expected: solution <filepath> <string>, %d - got\n",
                 argc - 1);
    return EXIT_FAILURE;
  }

  std::FILE* input = std::fopen(argv[1], "rb");
  if (input == nullptr) {
    std::fprintf(stderr, "Can't open file \"%s\". Reason: %s\n", argv[1], std::strerror(errno));
    std::fclose(input);
    return EXIT_FAILURE;
  }
  char* substr = argv[2];
  size_t arg_len = std::strlen(substr);

  auto* pi = static_cast<size_t*>(std::malloc(arg_len * sizeof(size_t)));
  if (pi == nullptr) {
    std::fprintf(stderr, "Error during memory allocation\n");
    std::fclose(input);
    return EXIT_FAILURE;
  }
  pi[0] = 0;
  for (size_t i = 1; i < arg_len; ++i) {
    pi[i] = prefix_f(pi, substr[i], substr, pi[i - 1]);
  }

  int c;
  size_t cur_prefix_f_value = 0;
  while ((c = std::fgetc(input)) != EOF) {
    cur_prefix_f_value = prefix_f(pi, c, substr, cur_prefix_f_value);
    if (cur_prefix_f_value == arg_len) {
      std::free(pi);
      if (std::ferror(input)) {
        std::fprintf(stderr, "Error reading file\n");
      }
      std::puts("Yes");
      std::fclose(input);
      return EXIT_SUCCESS;
    }
  }
  std::free(pi);

  if (std::ferror(input)) {
    std::fprintf(stderr, "Error reading file\n");
    std::fclose(input);
    return EXIT_FAILURE;
  }

  std::fclose(input);
  std::puts("No");
  return EXIT_SUCCESS;
}
