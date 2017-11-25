package be.bluexin.generation

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest

/**
 * Part of dungeon-generator by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
internal class XoRoRNGTest {

    @RepeatedTest(20)
    @DisplayName("Test XoRoRNG Seeding Long")
    internal fun testSeed() {
        val rng1 = XoRoRNG(40931)
        val rng2 = XoRoRNG(40931)
        val arr1 = LongArray(100) { rng1.nextLong() }
        val arr2 = LongArray(100) { rng2.nextLong() }
        assertArrayEquals(arr1, arr2)
    }

    @RepeatedTest(20)
    @DisplayName("Test XoRoRNG Seeding Int")
    internal fun testSeed1() {
        val rng1 = XoRoRNG(40931)
        val rng2 = XoRoRNG(40931)
        val arr1 = IntArray(100) { rng1.nextInt() }
        val arr2 = IntArray(100) { rng2.nextInt() }
        assertArrayEquals(arr1, arr2)
    }
}
