package utils

import kotlin.math.max
import kotlin.math.min

data class Point(val row: Int, val col: Int) {
    val i get() = row
    val j get() = col

    override fun toString(): String = "${row}x${col}"

    fun moveInDir(dir: Dir, steps: Int = 1): Point =
        when (dir) {
            Dir.UP -> (row - steps) x col
            Dir.DOWN -> (row + steps) x col
            Dir.LEFT -> row x (col - steps)
            Dir.RIGHT -> row x (col + steps)
        }
}

infix fun Int.x(that: Int) = Point(this, that)

enum class Dir {
    UP, DOWN, LEFT, RIGHT;

    val opposite
        get() = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }

    val left
        get() = when (this) {
            UP -> LEFT
            DOWN -> RIGHT
            LEFT -> DOWN
            RIGHT -> UP
        }

    val right
        get() = left.opposite

    companion object {
        fun fromChar(ch: Char) = when (ch) {
            'U' -> UP
            'D' -> DOWN
            'L' -> LEFT
            'R' -> RIGHT
            else -> throw IllegalArgumentException(ch.toString())
        }
    }

}

class StringArray2D(private val strings: List<String>) {

    val height: Int get() = strings.size
    val width: Int get() = strings[0].length

    operator fun get(row: Int, col: Int): Char =
        strings[row][col]

    operator fun get(pos: Point): Char =
        get(pos.row, pos.col)

    fun getOrNull(row: Int, col: Int): Char? =
        strings.getOrNull(row)?.getOrNull(col)

    fun getOrNull(pos: Point): Char? =
        getOrNull(pos.row, pos.col)

    val rows: List<List<Char>> =
        object : AbstractList<List<Char>>() {
            override val size: Int get() = height
            override fun get(index: Int): List<Char> = row(index)
        }

    val cols: List<List<Char>> =
        object : AbstractList<List<Char>>() {
            override val size: Int get() = width
            override fun get(index: Int): List<Char> = col(index)
        }

    fun row(row: Int): List<Char> =
        object : AbstractList<Char>() {
            override val size: Int get() = width
            override fun get(index: Int): Char = get(row, index)
        }

    fun col(col: Int): List<Char> =
        object : AbstractList<Char>() {
            override val size: Int get() = height
            override fun get(index: Int): Char = get(index, col)
        }

    val diagonalsRight: List<List<Char>>
        get() = buildList {
            // It could be done easier by just checking out of bounds access,
            // but I wanted to practice some math.
            // And it also could be done without any array allocations like rows above,
            // but it requires even more math.
            for (j in -height+1 ..< width) {
                val iMin = max(0, -j)
                val iMax = min(height-1, -j+width-1)
                add((iMin..iMax).map { i -> get(i, j + i) })
            }
        }

    val diagonalsLeft: List<List<Char>>
        get() = buildList {
            for (j in 0 ..< width+height-1) {
                val iMin = max(0, j-width+1)
                val iMax = min(height-1, j)
                add((iMin..iMax).map { i -> get(i, j - i) })
            }
        }
}

operator fun <T> Array<Array<T>>.get(i: Int, j: Int): T = this[i][j]
operator fun <T> Array<Array<T>>.set(i: Int, j: Int, v: T) { this[i][j] = v }
operator fun <T> Array<Array<T>>.get(pos: Point): T = this[pos.i][pos.j]
operator fun <T> Array<Array<T>>.set(pos: Point, v: T) { this[pos.i][pos.j] = v }
fun <T> Array<Array<T>>.getOrNull(pos: Point): T? = this.getOrNull(pos.i)?.getOrNull(pos.j)

fun List<Char>.asString(): String =
    String(toCharArray())