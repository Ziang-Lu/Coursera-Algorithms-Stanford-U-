/**
 * Input:
 * 1. Two strings X and Y of lengths m and n, respectively, over some alphabet,
 *    like {A, C, G, T} representing DNA portions
 * 2. Experimentally determined penalties for gaps and various types of
 *    mismatches, i.e.,
 *    Penalty pen_gap >= 0 for each gap
 *    Penalty pen_{a,b} >= 0 for each pair of the characters (a, b) in the
 *    alphabet
 *
 * Output:
 * The optimal alignment (by inserting gaps to both of the strings) of the given
 * two strings that minimizes the total penalty.
 *
 * Algorithm: (Dynamic programming)
 * Denote Sx and Sy to be the optimal solution, which is derived by inserting
 * gaps towards X and Y, respectively.
 * Consider the final position in Sx and Sy:
 * 1. x_m at Sx and y_n at Sy:
 *    Let X' = {X - x_m} and Y' = {Y - y_n}
 *    => {Sx - x_m} and {S_y - y_n} is the optimal solution with X' and Y'
 *    => S = the optimal solution with X' and Y' + pen_{x_m,y_n}
 * 2. x_m at Sx and a gap at Sy:
 *    => {Sx - x_m} and Sy is the optimal solution with X' and Y
 *    => S = the optimal solution with X' and Y + pen_gap
 * 3. A gap at Sx and y_n at Sy:
 *    => Sx and {Sy - y_n} is the optimal solution with X and Y'
 *    => S = the optimal solution with X and Y' + pen_gap
 *
 * i.e.,
 * Let S(X_i, Y_j) be the optimal solution for the subproblem with the prefix
 * of i characters of X and j characters of Y, respectively, then
 * S(X_i, Y_j) = min(S(X_i - x_i, Y_j - y_j) + pen_{x_i,y_j},
 *                   S(X_i - x_i, Y_j) + pen_gap,
 *                   S(X_i, Y_j - y_j) + pen_gap)
 */

import java.util.HashMap;

public class SequenceAlignment {

    /**
     * Default negative value for subproblem solutions.
     */
    private static final int DEFAULT_SUBPROBLEM_SOL = -1;

    /**
     * Subproblem solutions.
     * Since there are only O(mn) subproblems, the first time we solve a
     * subproblem, we can cache its solution in a global take for O(1) lookup
     * time later on.
     */
    private int[][] subproblemSols;

    /**
     * Solves the sequence alignment of the given two strings with the given
     * penalties in a straightforward way.
     * @param x first string
     * @param y second string
     * @param gapPen penalty for gap
     * @param penMap penalty between each character pair
     * @return optimal alignment
     */
    public String[] sequenceAlignmentStraightforward(String x, String y, int gapPen,
            HashMap<Character, HashMap<Character, Integer>> penMap) {
        // Check whether the input strings are null or empty
        if ((x == null) || (x.length() == 0) || (y == null) || (y.length() == 0)) {
            throw new IllegalArgumentException("The input sequences should not be null or empty.");
        }
        // Check whether the input gap penalty is non-negative
        if (gapPen < 0) {
            throw new IllegalArgumentException("The input gap penalty should be non-negative.");
        }
        // Check whether the input map is null
        if (penMap == null) {
            throw new NullPointerException("The input penalty map should not be null.");
        }

        initializeSubproblemSols(x.length(), y.length());
        sequenceAlignmentHelper(x, y, gapPen, penMap);
        return reconstructOptimalAlignment(x, y, gapPen, penMap);
        // With memoization, the overall running time complexity is O(mn).
    }

    /**
     * Private helper method to initialize the subproblem solutions.
     * @param m length of the first string
     * @param n length of the second string
     */
    private void initializeSubproblemSols(int m, int n) {
        subproblemSols = new int[m + 1][n + 1];
        for (int i = 0; i <= m; ++i) {
            for (int j = 0; j <= n; ++j) {
                subproblemSols[i][j] = DEFAULT_SUBPROBLEM_SOL;
            }
        }
        // Running time complexity: O(mn)
    }

    /**
     * Private helper method to solve the sequence alignment problem with the
     * given prefix of the original strings and the given penalties recursively.
     * @param xPrefix prefix of the first string
     * @param yPrefix prefix of the second string
     * @param gapPen penalty for gap
     * @param penMap penalty between each character pair
     */
    private void sequenceAlignmentHelper(String xPrefix, String yPrefix, int gapPen,
            HashMap<Character, HashMap<Character, Integer>> penMap) {
        int i = xPrefix.length(), j = yPrefix.length();
        if (subproblemSols[i][j] != DEFAULT_SUBPROBLEM_SOL) {
            return;
        }

        // Base case
        if ((i == 0) || (j == 0)) {
            subproblemSols[i][j] = Math.max(i, j) * gapPen;
            return;
        }
        // Recursive case
        sequenceAlignmentHelper(xPrefix.substring(0, i - 1), yPrefix.substring(0, j - 1), gapPen, penMap);
        char xFinal = xPrefix.charAt(i - 1), yFinal = yPrefix.charAt(j - 1);
        int result1 = subproblemSols[i - 1][j - 1] + penMap.get(xFinal).get(yFinal);
        sequenceAlignmentHelper(xPrefix.substring(0, i - 1), yPrefix, gapPen, penMap);
        int result2 = subproblemSols[i - 1][j] + gapPen;
        sequenceAlignmentHelper(xPrefix, yPrefix.substring(0, j - 1), gapPen, penMap);
        int result3 = subproblemSols[i][j - 1] + gapPen;
        subproblemSols[i][j] = Math.min(Math.min(result1, result2), result3);
    }

    /**
     * Private helper method to reconstruct the optimal alignment according to
     * the optimal solution using backtracking.
     * @param x first string
     * @param y second string
     * @param gapPen penalty for gap
     * @param penMap penalty between each character pair
     * @return optimal alignment
     */
    private String[] reconstructOptimalAlignment(String x, String y, int gapPen,
            HashMap<Character, HashMap<Character, Integer>> penMap) {
        StringBuilder sx = new StringBuilder(), sy = new StringBuilder();
        int i = x.length(), j = y.length();
        while ((i >= 1) && (j >= 1)) {
            char xFinal = x.charAt(i - 1), yFinal = y.charAt(j - 1);
            int result1 = subproblemSols[i - 1][j - 1] + penMap.get(xFinal).get(yFinal);
            int result2 = subproblemSols[i - 1][j] + gapPen;
            int result = subproblemSols[i][j];
            if (result == result1) {
                // Case 1: The final positions are x_i and y_j.
                sx.insert(0, xFinal);
                sy.insert(0, yFinal);
                --i;
                --j;
            } else if (result == result2) {
                // Case 2: The final positions are x_i and a gap.
                sx.insert(0, xFinal);
                sy.insert(0, ' ');
                --i;
            } else {
                // Case 3: The final positions are a gap and y_j.
                sx.insert(0, ' ');
                sy.insert(0, yFinal);
                --j;
            }
        }
        if (i != 0) {
            padSpaces(sy, i);
        } else if (j != 0) {
            padSpaces(sx, j);
        }
        return new String[]{sx.toString(), sy.toString()};
        // Running time complexity: O(m + n)
    }

    /**
     * Helper method to pad the given number of spaces to the given
     * StringBuilder.
     * @param s StringBuilder to pad
     * @param n number of spaces
     */
    private void padSpaces(StringBuilder s, int n) {
        for (int k = 0; k < n; ++k) {
            s.insert(0, ' ');
        }
    }

    /**
     * Solves the sequence alignment of the given two strings with the given
     * penalties in an improved bottom-up way.
     * @param x first string
     * @param y second string
     * @param gapPen penalty for gap
     * @param penMap penalty between each character pair
     * @return optimal alignment
     */
    public String[] sequenceAlignment(String x, String y, int gapPen,
            HashMap<Character, HashMap<Character, Integer>> penMap) {
        // Check whether the input strings are null or empty
        if ((x == null) || (x.length() == 0) || (y == null) || (y.length() == 0)) {
            throw new IllegalArgumentException("The input sequences should not be null or empty.");
        }
        // Check whether the input gap penalty is non-negative
        if (gapPen < 0) {
            throw new IllegalArgumentException("The input gap penalty should be non-negative.");
        }
        // Check whether the input map is null
        if (penMap == null) {
            throw new IllegalArgumentException("The input penalty map should not be null.");
        }

        int m = x.length(), n = y.length();
        // Initialization
        subproblemSols = new int[m + 1][n + 1];
        for (int i = 0; i <= m; ++i) {
            subproblemSols[i][0] = i * gapPen;
        }
        for (int j = 0; j <= n; ++j) {
            subproblemSols[0][j] = j * gapPen;
        }
        // Bottom-up calculation
        for (int i = 1; i <= m; ++i) {
            for (int j = 1; j <= n; ++j) {
                char xCurr = x.charAt(i - 1), yCurr = y.charAt(j - 1);
                int result1 = subproblemSols[i - 1][j - 1] + penMap.get(xCurr).get(yCurr);
                int result2 = subproblemSols[i - 1][j] + gapPen;
                int result3 = subproblemSols[i][j - 1] + gapPen;
                subproblemSols[i][j] = Math.min(Math.min(result1, result2), result3);
            }
        }
        return reconstructOptimalAlignment(x, y, gapPen, penMap);
        // Overall running time complexity: O(mn)
    }

}
