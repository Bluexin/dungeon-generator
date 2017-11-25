package be.bluexin.generation

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * Part of dungeon-generator by Bluexin, released under GNU GPLv3.
 * Floors go from 0 (inclusive) to [floorCount] (exclusive).
 * Floor 0 is supposed to be the highest one, and floor [floorCount - 1] is the lowest one.
 *
 * @author Bluexin
 */
class DungeonGenerator(val floorCount: Int = 10, val width: Int = 100, val height: Int = 100, private val rng: XoRoRNG = XoRoRNG()) {

    val floors = sortedMapOf<Int, Grid>()

    fun generate(): DungeonGenerator {
        runBlocking {
            val rns = LongArray(floorCount) { rng.nextLong() }
            val roomsDown = provideRooms()
            val l = List(floorCount) {
                if (!isActive) return@runBlocking
                launch(CommonPool) {
                    floors[it] = WGraph(grid(width, height, XoRoRNG(rns[it])) {
                        if (it < floorCount - 1) +room(roomsDown[it]) { +Stairs.DOWN }
                        if (it > 0) +room(roomsDown[it - 1]) { +Stairs.UP }
                    }.generate()).explore().makePaths().reset().explore().cleanGrid().grid
                }
            }
            l.forEach { it.join() }
        }

        return this
    }

    suspend fun CoroutineScope.generateSuspend(): DungeonGenerator {
        val rns = LongArray(floorCount) { rng.nextLong() }
        val roomsDown = provideRooms()
        val l = List(floorCount) {
            if (!isActive) return this@DungeonGenerator
            launch(CommonPool) {
                floors[it] = WGraph(grid(width, height, XoRoRNG(rns[it])) {
                    if (it < floorCount - 1) +room(roomsDown[it]) { +Stairs.DOWN }
                    if (it > 0) +room(roomsDown[it - 1]) { +Stairs.UP }
                }.generate()).explore().makePaths().reset().explore().cleanGrid().grid
            }
        }
        l.forEach { it.join() }

        return this@DungeonGenerator
    }

    private fun provideRooms(): Array<Position> {
        val rooms = mutableListOf<Position>()

        rooms += rng.nextInt(width) to rng.nextInt(height)

        repeat(floorCount - 1) {
            var pos = rng.nextInt(width) to rng.nextInt(height)
            while (pos.x in (rooms[it].x - 2)..(rooms[it].x + 1) || pos.y in (rooms[it].y - 2)..(rooms[it].y + 1)) pos = rng.nextInt(width) to rng.nextInt(height)
            rooms += pos;
        }

        return rooms.toTypedArray()
    }
}
