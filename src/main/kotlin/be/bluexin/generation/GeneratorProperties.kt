package be.bluexin.generation


/**
 * Part of dungeon-generator by bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
interface GeneratorProperties {
    val width: Int
    val height: Int
}

data class BaseGeneratorProperties(
        override val width: Int = 100,
        override val height: Int = width
) : GeneratorProperties
