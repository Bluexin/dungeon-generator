package be.bluexin.generation

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Part of dungeon-generator by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
internal class GeneratorTests {

    @Test
    @DisplayName("Test Grid Generation equality")
    internal fun test1() {
        assertEquals(Grid(rng = XoRoRNG(40931)).generate(), Grid(rng = XoRoRNG(40931)).generate())
    }

    @Test
    @DisplayName("Test Dungeon Generation equality")
    internal fun test2() {
        assertArrayEquals(DungeonGenerator(rng = XoRoRNG(40931)).generate().floors.values.toTypedArray(), DungeonGenerator(rng = XoRoRNG(40931)).generate().floors.values.toTypedArray())
    }
}
