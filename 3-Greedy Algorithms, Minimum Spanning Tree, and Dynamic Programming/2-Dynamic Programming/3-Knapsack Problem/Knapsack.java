/**
 * Given n items, each item with a non-negative value v and a non-negative integral size (weight) w, and a non-negative
 * integral capacity W, select a subset of the items, that maximizes sum(v), subject to sum(w) <= W.
 *
 * Algorithm: (Dynamic programming) Denote S to be the optimal solution and item-n to be the last item. Consider whether
 * item-n is in S: 1. item-n is NOT in S: => S must be optimal among only the first (n - 1) items and capacity W. => S =
 * the optimal solution among the first (n - 1) items and capacity W 2. item-n is in S: => {S - item-n} must be optimal
 * among only the first (n - 1) items and the residual capacity (W - w_n) (i.e., the space is "reserved" for item-n). =>
 * S = the optimal solution among the first (n - 1) items and the residual capacity (W - w_n) + item-n
 *
 * i.e., Let S(i, x) be the optimal solution for the subproblem among the first i items and capacity x, then S(i, x) =
 * max{S(i - 1, x), S(i - 1, x - w_i) + v_i}
 */

import java.util.HashSet;
import java.util.Set;

public class Knapsack {

    /**
     * Subproblem solutions.
     * Since there are only O(nW) distinct subproblems, the first time we solve
     * a subproblem, we can cache its solution in a global take for O(1) lookup
     * time later on.
     */
    private double[][] subproblems;

    /**
     * Solves the knapsack problem of the items with the given values and
     * weights, and the given capacity, in an improved bottom-up way.
     * @param vals values of the items
     * @param weights weights of the items
     * @param cap capacity of the knapsack
     * @return included items
     */
    public Set<Integer> knapsack(double[] vals, int[] weights, int cap) {
        // Check whether the input arrays are null or empty
        if ((vals == null) || (vals.length == 0)) {
            return new HashSet<>();
        }
        // Check whether the input capacity is non-negative
        if (cap < 0) {
            return new HashSet<>();
        }

        int n = vals.length;
        // Initialization
        subproblems = new double[n][cap + 1];
        for (int x = 0; x <= cap; ++x) {
            if (weights[0] <= x) {
                subproblems[0][x] = vals[0];
            }
        }
        // Bottom-up calculation
        for (int item = 1; item < n; ++item) {
            for (int x = 0; x <= cap; ++x) {
                if (weights[item] > x) {
                    subproblems[item][x] = subproblems[item - 1][x];
                } else {
                    subproblems[item][x] = Math.max(subproblems[item - 1][x],
                            subproblems[item - 1][x - weights[item]] + vals[item]);
                }
            }
        }
        return reconstruct(vals, weights, cap);
        // Overall running time complexity: O(nW), where W is the knapsack capacity
    }

    /**
     * Private helper method to reconstruct the included items according to the
     * optimal solution using backtracking.
     * @param vals values of the items
     * @param weights weights of the items
     * @param cap capacity of the knapsack
     * @return included items
     */
    private Set<Integer> reconstruct(double[] vals, int[] weights, int cap) {
        Set<Integer> included = new HashSet<>();
        int item = vals.length - 1, currCap = cap;
        while (item >= 1) {
            if ((weights[item] <= currCap) &&
                    (subproblems[item - 1][currCap] < (subproblems[item - 1][currCap - weights[item]] + vals[item]))) {
                // Case 2: The current item is included.
                included.add(item);
                currCap -= weights[item];
            }
            --item;
        }
        if (weights[0] <= currCap) {
            included.add(0);
        }
        return included;
        // Running time complexity: O(n)
    }

}
