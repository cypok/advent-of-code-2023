import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
@Suppress("unused")
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

// Inspired by Iterable.sumOf()
inline fun <T> Iterable<T>.productOf(selector: (T) -> Long): Long =
    fold(1) { acc, x -> acc * selector(x) }

fun <T> List<T>.split(separator: T): Sequence<Sequence<T>> = sequence {
    val remaining = this@split.iterator()
    while (remaining.hasNext()) {
        yield(sequence {
            while (remaining.hasNext()) {
                val elem = remaining.next()
                if (elem == separator) {
                    break
                }
                yield(elem)
            }
        })
    }
}

/**
 * The cleaner shorthand for printing output.
 */
@Deprecated("just don't use it")
fun Any?.println() = println(this)

private val PART_NUM_PATTERN = Regex(""".*(?:\b|_)part(\d)(?:\b|_).*""")

fun test(vararg parts: (List<String>) -> Any) {
    val className = Throwable().stackTrace
        .map { it.className }
        .dropWhile { !it.startsWith("Day") || !it.endsWith("Kt") }
        .first()
    val day = className.substringBefore("Kt")

    val inputFiles =
        File("src/").listFiles { _, name -> name.startsWith(day) && name.endsWith(".txt") } ?: arrayOf()

    for ((i, p) in parts.withIndex()) {
        for (f in inputFiles.sortedBy { it.length() }) {
            val partNum = i + 1
            val kind = f.nameWithoutExtension
                .substringAfter(day).substringAfter('_')
                .takeIf { it.isNotEmpty() } ?: "real"

            val partNumMatch = PART_NUM_PATTERN.matchEntire(kind)
            if (partNumMatch != null && partNumMatch.groupValues[1].toInt() != partNum) {
                // Skip inputs for other parts.
                continue
            }

            runCatching { f.toPath().readLines() }
                .onSuccess { input ->
                    print("part$partNum, $kind: ")
                    val (result, time) = measureTimedValue { runCatching { p(input) } }
                    print("${result.getOrElse { "ERROR" }} (took ${time.inWholeMilliseconds} ms)")
                    result
                        .onSuccess { println() }
                        .onFailure { it.printStackTrace(System.out) }
                }
        }
    }
}

class StringArray2D(private val strings: List<String>) {

    val height: Int get() = strings.size
    val width: Int get() = strings[0].length

    operator fun get(row: Int, col: Int): Char =
        strings[row][col]

    operator fun get(pos: Pair<Int, Int>): Char =
        get(pos.first, pos.second)

    fun getOrNull(row: Int, col: Int): Char? =
        strings.getOrNull(row)?.getOrNull(col)

    fun getOrNull(pos: Pair<Int, Int>): Char? =
        getOrNull(pos.first, pos.second)

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
}

operator fun <T> Array<Array<T>>.get(i: Int, j: Int): T = this[i][j]
operator fun <T> Array<Array<T>>.get(pos: Pair<Int, Int>): T = this[pos.first][pos.second]
operator fun <T> Array<Array<T>>.set(i: Int, j: Int, v: T) { this[i][j] = v }
operator fun <T> Array<Array<T>>.set(pos: Pair<Int, Int>, v: T) { this[pos.first][pos.second] = v }

/**
 * Transform
 *
 *     [[a,b], [c], [d,e]]
 *
 * to
 *
 *     [[a,c,d], [a,c,e], [b,c,d], [b,c,e]]
 */
fun <T> Collection<Iterable<T>>.cartesianProduct(): List<List<T>> {
    if (isEmpty()) return listOf(emptyList())
    val tails = drop(1).cartesianProduct()
    return first().flatMap { head -> tails.map { tail -> listOf(head) + tail } }
}

fun gcd(x: Long, y: Long): Long {
    var a = max(x, y)
    var b = min(x, y)
    while (b > 0L) {
        val rem = a % b
        a = b
        b = rem
    }
    return a
}

fun lcm(x: Long, y: Long) = x / gcd(x, y) * y

fun Long.toIntExact() = Math.toIntExact(this)

inline fun <T, R> Pair<T, T>.map(transform: (T) -> R): Pair<R, R> =
    Pair(transform(first), transform(second))
