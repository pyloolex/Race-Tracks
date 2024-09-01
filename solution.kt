import kotlin.collections.ArrayDeque


const val N = 31


// (x, y) - position. (vx, vy) - velocity/speed.
data class State(
    val x: Int,
    val y: Int,
    val vx: Int,
    val vy: Int,
)


fun solve(X: Int, Y: Int, startX: Int, startY: Int, endX: Int, endY: Int,
          obstacles: Array<IntArray>): String {
    // Create a field of free and occupied cells.
    // true - free
    // false - blocked by the obstacle
    val grid = Array< Array<Boolean> >(N) {
        Array<Boolean>(N) {true}
    }

    // Create an array of distances from the starting position.
    // dp[i][j][k][m] stores the number of how many hops it takes
    // to reach a position (i, j) on the field with the velocity (k, m).
    val dist = Array< Array< Array< Array<Int> > > >(N) {
        Array< Array< Array<Int> > >(N) {
            Array< Array<Int> >(N) {
                Array<Int>(N) {-1}
            }
        }
    }

    // Place all the obstacle on the grid.
    for (obstacle in obstacles) {
        for (i in obstacle[0] until obstacle[1] + 1) {
            for (j in obstacle[2] until obstacle[3] + 1) {
                grid[i][j] = false
            }
        }
    }

    // Speed could be a negative number. We will store it in the
    // array with the artificial shift N/2.
    //
    // Thus,
    // real 0 speed is stored in N/2 index,
    // real -1 speed is stored in N/2 - 1 index,
    // real -2 speed is stored in N/2 - 2 index,
    // real 1 speed is stored in N/2 + 1 index,
    // real 2 speed is stored in N/2 + 2 index,
    // etc.
    val line = ArrayDeque<State>()
    line.add(State(startX, startY, N / 2, N / 2))
    dist[startX][startY][N / 2][N / 2] = 0

    // Use BFS algorithm to find the shortest path.
    while (!line.isEmpty()) {
        val top = line.removeFirst()
        if (top.x == endX && top.y == endY) {
            val answer = dist[top.x][top.y][top.vx][top.vy]
            return "Optimal solution takes $answer hops."
        }

        for (dx in -1..1) {
            for (dy in -1..1) {
                // Iterate over all the possible speed changes
                // and check whether the next jump to that direction
                // is possible.
                val newVx = top.vx + dx
                val newVy = top.vy + dy
                val newX = top.x + newVx - N / 2
                val newY = top.y + newVy - N / 2
                if (newX >= 0 && newX < X &&
                        newY >= 0 && newY < Y &&
                        grid[newX][newY] &&
                        dist[newX][newY][newVx][newVy] == -1) {
                    dist[newX][newY][newVx][newVy] = (
                        dist[top.x][top.y][top.vx][top.vy] + 1)
                    line.add(State(newX, newY, newVx, newVy))
                }
            }
        }
    }

    return "No solution."
}


fun main() {
    // (1)
    // ....s
    // .....
    // .xxxx
    // .xxxx
    // ....e
    // Solution:
    // (3, 1)
    // (1, 1)
    // (0, 2)
    // (0, 3)
    // (1, 4)
    // (2, 4)
    // (4, 4)
    assert(
        "Optimal solution takes 7 hops." ==
        solve(
            5, 5, 4, 0, 4, 4,
            arrayOf(
                intArrayOf(1, 4, 2, 3),
            )
        )
    )

    // (2)
    // sx.
    // xxx
    // .xe
    assert(
        "No solution." ==
        solve(
            3, 3, 0, 0, 2, 2,
            arrayOf(
                intArrayOf(1, 1, 0, 2),
                intArrayOf(0, 2, 1, 1),
            )
        )
    )

    // (3)
    // .s.
    // .xx
    // ..e
    // Solution:
    // (0, 1)
    // (0, 2)
    // (1, 2)
    // (2, 2)
    assert(
        "Optimal solution takes 4 hops." ==
        solve(
            3, 3, 1, 0, 2, 2,
            arrayOf(
                intArrayOf(1, 2, 1, 1),
            )
        )
    )

    // (4)
    // .sx...
    // ..x..e
    // x.x...
    // Solution:
    // (0, 1)
    // (0, 1)
    // (1, 1)
    // (3, 1)
    // (5, 1)
    assert(
        "Optimal solution takes 5 hops." ==
        solve(
            6, 3, 1, 0, 5, 1,
            arrayOf(
                intArrayOf(2, 2, 0, 2),
                intArrayOf(0, 0, 2, 2),
            )
        )
    )

    // (5)
    // .s...
    // ....x
    // .....
    assert(
        "No solution." ==
        solve(
            5, 3, 1, 0, 4, 1,
            arrayOf(
                intArrayOf(4, 4, 1, 1),
            )
        )
    )

    // (6)
    // Min test.
    assert(
        "Optimal solution takes 0 hops." ==
        solve(
            1, 1, 0, 0, 0, 0,
            arrayOf(),
        )
    )

    // (7)
    // ....xxs.
    // ....xx..
    // .e..xx..
    // ....xx..
    // xxxxxx..
    // xxxxxx..
    // ........
    // ........
    // ........
    // ........
    // Solution:
    // (6, 1)
    // (6, 3)
    // (5, 6)
    // (3, 8)
    // (1, 9)
    // (0, 9)
    // (0, 8)
    // (0, 6)
    // (0, 3)
    // (0, 1)
    // (1, 0)
    // (2, 0)
    // (2, 1)
    // (1, 2)
    assert(
        "Optimal solution takes 14 hops." ==
        solve(
            8, 10, 6, 0, 1, 2,
            arrayOf(
                intArrayOf(0, 5, 4, 5),
                intArrayOf(4, 5, 0, 3),
            ),
        )
    )

    // (8)
    // ....xx.e
    // s...xx..
    assert(
        "No solution." ==
        solve(
            8, 2, 0, 1, 7, 0,
            arrayOf(
                intArrayOf(4, 5, 0, 1),
            ),
        )
    )

    // (9)
    // ..xx.xx............e
    // .xx..xx.......xxxxxx
    // .x...xx.......xxxxxx
    // .x...xx.......xxxxxx
    // .x...xx.............
    // .x...xx.............
    // .x..................
    // xxxxxxxxxxxxxxxxxxxx
    // xxxxxxxxxxxxxxxxxxxx
    // xxxxxxxxxxxxxxxxxxxx
    // .xxxxxxxxxxxxxxxxxxx
    // ....................
    // ....................
    // x......xx...........
    // x......xx...........
    // .......xx...........
    // .......xx...........
    // .......xx...........
    // .......xx...........
    // ....................
    // ..................s.
    // Solution:
    // (17, 19)
    // (15, 17)
    // (12, 16)
    // (9, 16)
    // (6, 16)
    // (3, 15)
    // (1, 13)
    // (0, 10)
    // (0, 6)
    // (0, 3)
    // (0, 1)
    // (0, 0)
    // (1, 0)
    // (3, 1)
    // (4, 3)
    // (4, 5)
    // (4, 6)
    // (5, 6)
    // (7, 5)
    // (9, 3)
    // (12, 1)
    // (16, 0)
    // (19, 0)
    assert(
        "Optimal solution takes 23 hops." ==
        solve(
            20, 21, 18, 20, 19, 0,
            arrayOf(
                intArrayOf(7, 8, 13, 18),
                intArrayOf(0, 0, 13, 14),
                intArrayOf(1, 19, 10, 10),
                intArrayOf(0, 19, 7, 9),
                intArrayOf(1, 1, 2, 6),
                intArrayOf(1, 2, 1, 1),
                intArrayOf(2, 3, 0, 0),
                intArrayOf(5, 6, 0, 5),
                intArrayOf(14, 19, 1, 3),
            )
        )
    )

    // (10)
    // Max test.
    // s.............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // ..............................
    // .............................e
    // Solution:
    // (1, 1)
    // (2, 2)
    // (4, 4)
    // (7, 7)
    // (11, 11)
    // (16, 16)
    // (22, 22)
    // (29, 29)
    assert(
        "Optimal solution takes 8 hops." ==
        solve(
            30, 30, 0, 0, 29, 29,
            arrayOf(),
        )
    )

    println("All tests have passed.")
}
