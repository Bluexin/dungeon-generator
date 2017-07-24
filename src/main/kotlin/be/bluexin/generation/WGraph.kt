package be.bluexin.generation

import java.util.*
import kotlin.collections.HashMap

/**
 * Part of tests by bluexin, released under GNU GPLv3.
 * Weighted version of [Graph].
 *
 * @author Bluexin
 */
class WGraph(val grid: Grid) {

    private val q = PriorityQueue<WNode>()
    private val from = HashMap<Tile, Tile>()
    private val costs = HashMap<Tile, Pair<Float, Boolean>>()

    var breakCost = 1F
    var passTroughCost = 0.1F

    init {
        reset()
    }

    fun reset(): WGraph {
        from.clear()

        costs.clear() // Keeping the already 0'd costs isn't worth. -> except when you gotta do multiple passes
        /*val s = costs.size
        val iter = costs.iterator()
        while (iter.hasNext()) {
            val n = iter.next()
            if (!n.value.second) {
                iter.remove()
            } else {
                val t = n.key costs 0F
                q.add(t)
                costs[t.tile] = 0F to true
            }
        }
//        costs.entries.removeIf { !it.value.second }
        println("Costs decreased from $s to ${costs.size}.")

//        costs.clear()*/

        if (q.isEmpty()) {
            val t = grid.firstRoom costs 0F
            q.add(t)
            costs[t.tile] = 0F to true
        }

        return this
    }

    fun explore(): WGraph {
        while (q.isNotEmpty()) {
            val current = q.poll()
            current.neighbours().forEach {
                if (it.tile !in costs || costs[it.tile]!!.first > it.cost) {
                    costs[it.tile] = it.cost to it.connected
                    q.add(it)
                    from[it.tile] = current.tile
                }
            }
        }

        return this
    }

    fun makePaths(): WGraph {
        var c = 0
        while (grid.rooms.any { !(costs[grid[it]]?.second ?: true) }) {
            grid.rooms.forEach {
                var cpos = it
                var t = grid[cpos]
                while (!(costs[t]?.second ?: true)) {
                    val dir = cpos dir from[t]!!.pos
                    t.connections.add(dir)
                    cpos = cpos(dir)
                    t = grid[cpos]
                    t.connections.add(!dir)
                }
            }

            this.reset()
            this.explore()
            ++c
        }

        if (c > 1) println("Made paths using $c passes.")

        return this
    }

    fun cleanGrid(): WGraph {
        costs.forEach { tile, (_, connected) ->
            if (!connected) tile.connections.clear()
        }
        grid.clean()

        return this
    }

    infix fun Tile.costs(that: Float) = WNode(this, that, this@WGraph)

    override fun toString(): String {
        val sb = StringBuilder("Printing graph data :\n")

        costs.forEach { tile, cost ->
            if (tile is Room) sb.append("$tile -> $cost\n")
        }

        val a = Array(grid.height, { Array(grid.width, { "?" }) })
        costs.forEach { tile, (cost, connected) ->
            a[tile.pos.y][tile.pos.x] = "${ColorHelper.getColor(tile, connected)}${if (cost > 9) 'x' else cost.toString().first()}${ColorHelper.RESET}"
        }
        a.forEach {
            it.forEach { sb.append(it) }
            sb.append('\n')
        }

        return sb.toString()
    }
}

data class WNode(val tile: Tile, val cost: Float, private val graph: WGraph, val connected: Boolean = true) : Comparable<WNode> {
    override fun compareTo(other: WNode) = cost.compareTo(other.cost)

    fun neighbours(): List<WNode> {
        val l = mutableListOf<WNode>()
        Orientation.values().forEach {
            val p = this.tile.pos(it)
            val (costTo, connectedTo) = costTo(it)
            if (p in this.graph.grid) l.add(WNode(this.graph.grid[p], this.cost + costTo, this.graph, this.connected && connectedTo))
        }
        return l
    }

    private fun costTo(orientation: Orientation): Pair<Float, Boolean> {
        val to = orientation in tile.connections
        val from = !orientation in graph.grid[tile.pos(orientation)].connections
        return ((if (to) graph.passTroughCost else graph.breakCost) + (if (from) graph.passTroughCost else graph.breakCost)) to (to && from)
    }

    override fun equals(other: Any?) = tile == (other as? WNode)?.tile
    override fun hashCode() = tile.hashCode()
    override fun toString() = "$tile, cost=$cost"


}
