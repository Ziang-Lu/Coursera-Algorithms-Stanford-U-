#!bin/usr/env python3
# -*- coding: utf-8 -*-

"""
Input:
1. Two strings X and Y of lengths m and n, respectively, over some alphabet,
   like {A, C, G, T} representing DNA portions
2. Experimentally determined penalties for gaps and various mismatches, i.e.,
   Penalty pen_gap >= 0 for each gap
   Penalty pen_{a,b} >= 0 for each pair of the characters (a, b) in the alphabet

Output:
The optimal alignment (by inserting gaps to both of the strings) of the given
two strings that minimizes the total penalty.

Algorithm: (Dynamic programming)
Denote Sx and Sy to be the optimal solution, which is derived by inserting gaps
towards X and Y, respectively.
Consider the final position in Sx and Sy:
1. x_m at Sx and y_n at Sy:
   Let X' = {X - x_m} and Y' = {Y - y_n}
   => {Sx - x_m} and {Sy - y_n} is the optimal solution with X' and Y'
   => S = the optimal solution with X' and Y' + pen_{x_m,y_n}
2. x_m at Sx and a gap at Sy:
   => {Sx - x_m} and Sy is the optimal solution with X' and Y
   => S = the optimal solution with X' and Y + pen_gap
3. A gap at Sx and y_n at Sy:
   => Sx and {Sy - y_n} is the optimal solution with X and Y'
   => S = the optimal solution with X and Y' + pen_gap

i.e.,
Let S(X_i, Y_j) be the optimal solution for the subproblem with the prefi of i
characters of X and j characters of Y, respectively, then
S(X_i, Y_j) = min{S(X_i - x_i, Y_j - y_j) + pen_{x_i,y_j},
                  S(X_i - x_i, Y_j) + pen_gap,
                  S(X_i, Y_j - y_j) + pen_gap}
"""

__author__ = 'Ziang Lu'

from typing import Dict, List


def sequence_alignment(x: str, y: str, gap_pen: int,
                       pen_map: Dict[str, Dict[str, int]]) -> List[str]:
    """
    Solves the sequence alignment problem of the given two strings with the
    given penalties in an improved bottom-up way.
    :param x: str
    :param y: str
    :param gap_pen: int
    :param pen_map: dict{str: dict{str: int}}
    :return: list[str]
    """
    # Check whether the input strings are None or empty
    if not x or not y:
        return []
    # Check whether the input gap penalty is non-negative
    if gap_pen < 0:
        return []
    # Check whether the input penalty map is None
    if not pen_map:
        return []

    m, n = len(x), len(y)
    # Initialization
    subproblems = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(m + 1):
        subproblems[i][0] = i * gap_pen
    for j in range(n + 1):
        subproblems[0][j] = j * gap_pen
    # Bottom-up calculation
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            x_curr, y_curr = x[i - 1], y[j - 1]
            result1 = subproblems[i - 1][j - 1] + pen_map[x_curr][y_curr]
            result2 = subproblems[i - 1][j] + gap_pen
            result3 = subproblems[i][j - 1] + gap_pen
            subproblems[i][j] = min(result1, result2, result3)
    return _reconstruct_optimal_alignment(x, y, gap_pen, pen_map, subproblems)
    # Overall running time complexity: O(mn)


def _reconstruct_optimal_alignment(
    x: str, y: str, gap_pen: int, pen_map: Dict[str, Dict[str, int]],
    dp: List[List[int]]
) -> List[str]:
    """
    Private helper function to reconstruct the optimal alignment according to
    the optimal solution using backtracking.
    :param x: str
    :param y: str
    :param gap_pen: int
    :param pen_map: dict{str: dict{str: int}}
    :param dp: list[list[int]]
    :return: list[str]
    """
    sx, sy = '', ''
    i, j = len(x), len(y)
    while i >= 1 and j >= 1:
        x_curr, y_curr = x[i - 1], y[j - 1]
        result1 = dp[i - 1][j - 1] + pen_map[x_curr][y_curr]
        result2 = dp[i - 1][j] + gap_pen
        result = dp[i][j]
        if result == result1:
            # Case 1: The final positions are x_i and y_j.
            sx = x_curr + sx
            sy = y_curr + sy
            i -= 1
            y -= 1
        elif result == result2:
            # Case 2: The final positions are x_i and a gap.
            sx = x_curr + sx
            sy = ' ' + sy
            i -= 1
        else:
            # Case 3: The final positions are a gap and y_j.
            sx = ' ' + sx
            sy = y_curr + sy
            j -= 1
    if i:
        sy = ' ' * i + sy
    elif j:
        sx = ' ' * j + sx
    return [sx, sy]
    # Running time complexity: O(m + n)
