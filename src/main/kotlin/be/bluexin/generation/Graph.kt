package be.bluexin.generation

import java.util.*
import kotlin.collections.HashMap


/**
 * Part of tests by bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class Graph(val grid: Grid) {

    private val q = PriorityQueue<Node>()
    private val from = HashMap<Tile, Tile>(grid.width * grid.height)
    private val costs = HashMap<Tile, Int>(grid.width * grid.height)

    init {
        reset()
    }

    fun reset(): Graph {
        from.clear()
        costs.clear()
        val t = grid.firstRoom to 0
        q.add(t)
        costs[t.tile] = 0

        return this
    }

    fun explore(): Graph {
        while (q.isNotEmpty()) {
            val current = q.poll()
            val l = current.neighbours()
            l.forEach {
                if (it.tile !in costs || costs[it.tile]!! > it.cost) {
                    costs[it.tile] = it.cost
                    q.add(it)
                    from[it.tile] = current.tile
                }
            }
        }

        return this
    }

    fun makePaths(): Graph {
        grid.rooms.forEach {
            var cpos = it
            var t = grid[cpos]
            while (costs[t]!! > 0) {
                val dir = cpos dir from[t]!!.pos
                t.connections.add(dir)
                cpos = cpos(dir)
                t = grid[cpos]
                t.connections.add(!dir)
            }
        }

        return this
    }

    fun cleanGrid(): Graph {
        costs.forEach { tile, cost ->
            if (cost > 0) tile.connections.clear()
        }
        grid.clean()

        return this
    }

    infix fun Tile.to(that: Int) = Node(this, that, this@Graph)

    override fun toString(): String {
        val sb = StringBuilder("Printing graph data :\n")

        costs.forEach { tile, cost ->
            if (tile is Room) sb.append("$tile -> $cost\n")
        }

        val a = Array(grid.height, { CharArray(grid.height, { '?' }) })
        costs.forEach { tile, cost ->
            a[tile.pos.y][tile.pos.x] = if (cost > 9) 'x' else cost.toString().first()
        }
        a.forEach {
            it.forEach { sb.append(it) }
            sb.append('\n')
        }

        return sb.toString()
    }
}


data class Node(val tile: Tile, val cost: Int, private val graph: Graph) : Comparable<Node> {
    override fun compareTo(other: Node) = cost.compareTo(other.cost)

    fun neighbours(): List<Node> {
        val l = mutableListOf<Node>()
        Orientation.values().forEach {
            val p = this.tile.pos(it)
            if (p in this.graph.grid) l.add(Node(this.graph.grid[p], this.cost + costTo(it), this.graph))
        }
        return l
    }

    private fun costTo(orientation: Orientation) = (if (orientation in tile.connections) 0 else 1) + (if (!orientation in graph.grid[tile.pos(orientation)].connections) 0 else 1)

    override fun equals(other: Any?) = tile == (other as? Node)?.tile
    override fun hashCode() = tile.hashCode()
    override fun toString() = "$tile, cost=$cost"


}
