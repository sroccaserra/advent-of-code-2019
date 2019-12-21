# Advent of Code 2019

See:

- <https://adventofcode.com/2019>

## Running

Use this command, choosing the day number you want:

    $ lein run-day-01 < resources/day_01/input.txt

## Running tests

    $ lein test-refresh


## What I learned

- Using `stdin` instead of a file is handy: I can use `echo` to easily pass
  various made up input to the `main` function.

- [fn][fn] defines a recursion point, so I don't need to `loop` to `recur` a
  `fn` (also true for `defn`).

### References

- <https://github.com/tpope/vim-fireplace>
- <https://github.com/jakemcc/lein-test-refresh>

[fn]: https://clojure.org/reference/special_forms#fn
