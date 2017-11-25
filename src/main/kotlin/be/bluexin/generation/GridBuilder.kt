package be.bluexin.generation

/**
 * Part of dungeon-generator by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */

/**
 * Grid-related methods
 */
inline fun grid(width: Int = 30, height: Int = 100, rng: XoRoRNG = XoRoRNG(), init: Grid.() -> Unit) = Grid(width, height, rng).apply(init)

/**
 * Tile-related methods
 */
inline fun room(position: Position, init: Room.() -> Unit) = Room(pos = position).apply(init)

inline fun corridor(position: Position, init: Corridor.() -> Unit) = Corridor(pos = position).apply(init)

fun main(args: Array<String>) {
    test()
}

private fun test() {
    val grid = grid(height = 10, width = 10) {
        +room(9 to 9) { +Stairs.DOWN }
        +room(2 to 2) { +Chests.TRAPPED }
        +corridor(54 to 62) { +Chests.REGULAR }
    }

    println(grid)
}
