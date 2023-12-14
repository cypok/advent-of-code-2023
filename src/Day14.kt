fun main() = test(
    ::solve1,
    ::solve2,
)

private fun solve1(input: List<String>): Long {
    return StringArray2D(input).cols.sumOf { col ->
        col.withIndex().fold(Pair(0, 0L)) { acc, (i, ch) ->
            if (ch != 'O') return@fold acc

            val (nextEmpty, weight) = acc

            val emptySpot = 1 + ((i downTo nextEmpty)
                .firstOrNull { col[it] == '#' }
                ?: (nextEmpty - 1))

            Pair(emptySpot + 1, weight + (col.size - emptySpot).toLong())
        }.second
    }
}

private fun solve2(input: List<String>): Pair<Int, Long> {
    val originalMap = StringArray2D(input)
    val height = originalMap.height
    val width = originalMap.width

    val map = Array(height) { i -> Array(width) { j -> originalMap[i, j] } }

    fun tilt(w: Int, h: Int, cellGet: (Int, Int) -> Char, cellSet: (Int, Int, Char) -> Unit) {
        for (j in 0 until w) {
            var nextPossible = 0
            for (i in 0 until h) {
                when (cellGet(i, j)) {
                    '.' -> {}
                    '#' -> nextPossible = i + 1
                    'O' -> {
                        for (reallyEmpty in nextPossible..<i) {
                            if (cellGet(reallyEmpty, j) == '.') {
                                cellSet(reallyEmpty, j, 'O')
                                cellSet(i, j, '.')
                                nextPossible = reallyEmpty + 1
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    fun tiltN() = tilt(width, height, { i, j -> map[i][j] }, { i, j, c -> map[i][j] = c } )
    fun tiltW() = tilt(height, width, { i, j -> map[j][i] }, { i, j, c -> map[j][i] = c } )
    fun tiltS() = tilt(width, height, { i, j -> map[height - i - 1][j] }, { i, j, c -> map[height - i - 1][j] = c } )
    fun tiltE() = tilt(height, width, { i, j -> map[j][width - i - 1] }, { i, j, c -> map[j][width - i - 1] = c } )

    fun tiltCycle() {
        tiltN()
        tiltW()
        tiltS()
        tiltE()
    }

    fun calcLoadN(): Long =
        (0 until height).sumOf { i ->
            (0 until width).sumOf { j ->
                (if (map[i][j] == 'O') height - i else 0).toLong()
            }
        }

    val pre = 1000
    val measure = 1000
    val rem = 1_000_000_000 - pre - measure

    repeat(pre) {
        tiltCycle()
    }
    val lastLoads = (0 until measure).map {
        tiltCycle()
        calcLoadN()
    }
    fun lastLoad(idxFromEnd: Int) =
        lastLoads[lastLoads.size - 1 - idxFromEnd]

    val checkCycleTimes = 5
    fun isCycle(len: Int): Boolean {
        for (i in 0 until len) {
            for (j in 1 until checkCycleTimes) {
                if (lastLoad(i) != lastLoad(i + len * j)) {
                    return false
                }
            }
        }
        return true
    }

    val cycle = (1 until measure/checkCycleTimes)
        .firstOrNull { isCycle(it) }
        ?: throw IllegalStateException("cycle not found")
    val realLastLoad = lastLoad(cycle - rem % cycle)

    return Pair(cycle, realLastLoad)
}