package be.bluexin.generation

import java.util.*


/**
 * Part of tests by bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class Grid(val width: Int = 30, val height: Int = 100) {

    private val grid = Array(width) { x -> Array<Tile>(height) { y -> Empty(x to y) } }
    val rooms = mutableSetOf<Position>()

    override fun toString(): String {
        val sb = StringBuilder("Rooms: $rooms\nGrid :\n")

        grid[0].indices.forEach { y ->
            val m = grid.map { it[y].text }
            (0 until 5).forEach { i ->
                m.forEach { sb.append(it[i]) }
                sb.append('\n')
            }
        }
        return sb.toString()
    }

    fun clean() = this.forEach {
        it.connections.removeIf { o -> (!o !in this[it.pos(o)].connections) }
        if (it.replaceable) this[it.pos] = Wall(it.pos)
    }

    private operator fun set(pos: Position, tile: Tile) {
        if (pos in this) grid[pos] = tile
    }

    operator fun get(pos: Position) = if (pos in this) grid[pos] else Wall(pos)

    operator fun contains(pos: Position) = pos.x in 0 until width && pos.y in 0 until height

    private inline fun forEachIndexed(body: (Position, Tile) -> Unit) {
        grid.forEachIndexed { x, it ->
            it.forEachIndexed { y, tile -> body(x to y, tile) }
        }
    }

    private inline fun forEach(body: (Tile) -> Unit) = grid.forEach { it.forEach(body) }

    operator fun plusAssign(room: Room) {
        repeat(4) { x ->
            repeat(4) { y ->
                if (this[room.pos + (x - 2 to y - 2)] is Room) return@plusAssign
            }
        }

        rooms.add(room.pos)
        this[room.pos] = room
    }

    operator fun plusAssign(tile: Tile) {
        if (this[tile.pos].replaceable) this[tile.pos] = tile
    }

    fun getSetConnectionsTo(pos: Position): Array<Orientation> {
        val l = mutableListOf<Orientation>()
        Orientation.values().forEach {
            val t = this[pos(it)]
            if (!t.replaceable && !it in t.connections) l.add(it)
        }
        return l.toTypedArray()
    }

    fun generate(): Grid {
        val rng = Random(40931)
        this.forEachIndexed { position, tile ->
            if (tile.replaceable) {
                if (rng.nextFloat() > 0.66f) this += Room(position)
                if (this[position].replaceable) {
                    if (rng.nextFloat() > 0.25f) this += Corridor(position, *Orientation.generate(rng, rng.nextInt(4) + 1, *this.getSetConnectionsTo(position)))
                    else this += Corridor(position)
                }
            }
        }

        return this
    }

    val firstRoom
        get() = this[rooms.first()]
}
