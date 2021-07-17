import fileinput
import sys
from collections import defaultdict


def parse(lines):
    cells = defaultdict(bool)
    w = len(lines[0])
    h = len(lines)
    for y in range(h):
        row = lines[y]
        for x in range(w):
            c = row[x]
            if c == '#':
                cells[(x, y)] = True
    return (w, h, cells)


def solve_1(w, h, cells):
    history = set()
    while True:
        add_to_history(history, cells)
        cells = step(w, h, cells)
        if is_in_history(history, cells):
            break
    return biodiversity_rating(w, h, cells)


def add_to_history(history, cells):
    cell_items = sorted_items(cells)
    history.add(cell_items)


def is_in_history(history, cells):
    cell_items = sorted_items(cells)
    return cell_items in history


def sorted_items(cells):
    return tuple(sorted(cells.items()))


def step(w, h, cells):
    result = defaultdict(bool)
    for y in range(h):
        for x in range(w):
            bug = cells[(x, y)]
            ns = neighbors(cells, (x, y))
            n = nb_alive(ns)
            if bug and n == 1:
                result[(x, y)] = True
            if (not bug) and (n == 1 or n == 2):
                result[(x, y)] = True
    return result


def neighbors(cells, cell):
    result = []
    (x, y) = cell
    for (dx, dy) in [(0, -1), (1, 0), (0, 1), (-1, 0)]:
        result.append(cells[(x+dx, y+dy)])
    return result


def nb_alive(ns):
    return len([x for x in ns if x])


def biodiversity_rating(w, h, cells):
    result = 0
    for y in range(h):
        for x in range(w):
            if cells[(x, y)]:
                result += pow(2, x + w*y)
    return result


if __name__ == '__main__' and not sys.flags.interactive:
    lines = [line.strip() for line in fileinput.input()]
    (w, h, cells) = parse(lines)
    print(solve_1(w, h, cells))
