package be.bluexin.generation

import sun.management.ManagementFactoryHelper

/**
 * Part of dungeon-generator by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
object ColorHelper {
    val SUPPORTS_COLOR = "win" !in System.getProperty("os.name").toLowerCase() ||
            ManagementFactoryHelper.getRuntimeMXBean().inputArguments.any { "idea" in it.toLowerCase() }

    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"

    fun getColor(tile: Tile, connected: Boolean) = if (SUPPORTS_COLOR) when (tile) {
        is Room -> if (connected) GREEN else RED
        is Corridor -> if (connected) YELLOW else BLUE
        else -> RESET
    } else ""

}
