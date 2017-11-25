package be.bluexin.generation

/**
 * Part of dungeon-generator by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
interface TileDecoration {
    val appliesTo: Array<Class<out Tile>>

    val isPositive
        get() = true
}

enum class Stairs : TileDecoration {
    UP,
    DOWN;

    override val appliesTo: Array<Class<out Tile>> = arrayOf(Room::class.java)
}

enum class Chests(override val isPositive: Boolean = true) : TileDecoration {
    REGULAR,
    TRAPPED(false);

    override val appliesTo: Array<Class<out Tile>> = arrayOf(Room::class.java, Corridor::class.java)
}
