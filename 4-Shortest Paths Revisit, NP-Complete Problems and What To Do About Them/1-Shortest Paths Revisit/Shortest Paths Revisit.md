### Shortest Paths Revisit

Drawbacks of Dijkstra's Shortest-Path Algorithm:

1. Not always correct with negative edge lengths

   e.g., if edges <—> financial transactions

2. Every vertex needs to know the entire graph.

   => Thus non-feasible for massive graphs.

   e.g., if the problem involves Internet routing

<br>

From drawback #2, we yield another problem:

### "Distributed" Shortest Paths Problem

Each vertex uses only **local** computation, i.e., communicates with only the **vertices it is connected to**.

#### Bellman-Ford Shortest-Path Algorithm: (Dynamic programming)

*Though we may assume that the input graph doesn't have negative directed cycles (directed cycles with negative overall length), the Bellman-Ford Shortest-Path Algorithm is still able to discover this anyway, i.e., finds an excuse that why the shorest paths cannot be computed.*

=>

GIven a directed graph $G=(V,E)$ with each edge lengths $c_e$ could possibly be negative, and a source vertex $s$, the Bellman-Ford Shortest-Path Algorithm will either computes the shortest paths from $s$ to any other vertices, or discovers negative cycles.

<br>

*Fix a destination $v$, we introduce an extra parameter $i$ to describe the maximum allowed number of edges from $s$ to $v$ (like a "budget"), and this "budget" will be used to represent the subproblem size.*

**Optimal substructure lemma:**

Let $P(s, v, i)$ be the optimal solution (shortest path with minimum total length) from $s$ to some destination $v$, using at most $i$ edges

*注意: 由于有了budget $i$的限制, 我们可以允许$P(s, v, i)$中有negative cycle, 而不用担心使用无限次该negative cycle导致产生负无穷的shortest path*

* Case 1: If $P(s, v, i)$ has $\le (i-1)$ edges, (i.e., $P$ doesn't use up its budget $i$.)

  => $P(s, v, i) \ = \ P(s, v, i-1)$

* Case 2: If $P(s, v, i)$ has exactly $i$ edges, (i.e., $P$ uses up all of its budget $i$.), with final edge ($w$, $v$)

  => By plucking off the final edge ($w$, $v$) from $P$, we form $P'(s, w, i-1)$.

  => $P(s, v, i) \ = \ min_{(w, v)} {P'(s, w, i-1)}$ + $c_{(w, v)}$   (in-degree($v$) candidates)

=> $P(s, v, i)$ is the minimum among the above (1 + in-degree(v)) candidates.

*(Assume the input graph doesn't have negative cycles, then for each path, removing cycles only makes the total length go down, so each shortest path must not contain cycles; therefore, each shortest path has at most ($n$ - 1) edges.)*

*=> $i$ can be restricted to be $\le (n-1)$, i.e., $i$ = {1, 2, …, $n$ - 1}.*

**Pseudo code: (Bottom-up calculation)**

* Let $L[v, i]$ = 2D array indexed by $v$ and $i$
* Initialization:
* For $i$ = 1, 2, …, $n$ - 1
  * For $v \in V$:
    * $L[v, i] \ = \ min\{L[v, i-1], min_{(w, v)}L(w, i-1)\}$
* The final solution lies in $L[v, n-1]$ for $v \in V$.

**Optimization 1: Early stopping**

The algorithm may stop early when in the current iteration, no update is made for any vertex.

**Optimization 2: Space optimization**
