import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        long[] in = new long[n], out = new long[n];   // in[v]: v tested, out[v]: v not tested (subtree of v only)
        int[] parent = new int[n];
        dfsCost(0, -1, nb, cost, in, out, parent);
        boolean[] tested = new boolean[n];
        // rebuild from the root: walk down with the same rule
        rebuild(0, -1, nb, in, out, in[0] <= out[0], tested);
        return tested;
    }

    private static void dfsCost(int v, int par, int[][] nb, int[] cost, long[] in, long[] out, int[] parent) {
        parent[v] = par;
        in[v] = cost[v];
        out[v] = 0;
        for (int c : nb[v]) {
            if (c == par) continue;
            dfsCost(c, v, nb, cost, in, out, parent);
            in[v] += Math.min(in[c], out[c]);   // v tested: each child is free to choose
            out[v] += in[c];                    // v not tested: every child must be tested
        }
    }

    private static void rebuild(int v, int par, int[][] nb, long[] in, long[] out, boolean isTested, boolean[] tested) {
        tested[v] = isTested;
        for (int c : nb[v]) {
            if (c == par) continue;
            boolean childTested = isTested ? in[c] <= out[c] : true;
            rebuild(c, v, nb, in, out, childTested, tested);
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        int[] best = new int[1];
        down(0, -1, nb, best);
        return best[0];
    }

    /** Returns the longest way down from v (in links); updates best with the longest chain through v. */
    private static int down(int v, int par, int[][] nb, int[] best) {
        int first = 0, second = 0;   // two longest ways down through different children
        for (int c : nb[v]) {
            if (c == par) continue;
            int len = down(c, v, nb, best) + 1;
            if (len > first) { second = first; first = len; }
            else if (len > second) second = len;
        }
        best[0] = Math.max(best[0], first + second);
        return first;
    }
}
