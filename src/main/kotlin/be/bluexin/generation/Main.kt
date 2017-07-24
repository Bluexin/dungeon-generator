package be.bluexin.generation

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlin.system.measureTimeMillis

/**
 * Part of tests by bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNotEmpty()) when (args[0]) {
            "many" -> {
                manyTimed(200)
                manyTimed(200)
                manyTimed(200)
                manyTimed(200)
            }
            "timed" -> timed()
            "help" -> println("Available options are: many, timed, demo. Defaults to demo when nothing is specified.")
            "demo" -> demo()
            else -> println("Invalid option ${args[0]}.\nAvailable options are: many, timed, demo. Defaults to demo when nothing is specified.")
        } else demo()

        Thread.sleep(10) // To make sure the process finishes printing before getting disconnected
    }

    private fun makeGraph(grid: Grid) = WGraph(grid)

    private fun demo() {
        val g = Grid(30, 100)
        g.generate()

        println("Printing original grid...\n$g")

        val gr = makeGraph(g)
        gr.explore()
        println("Printing first pass graph...\n$gr")
        gr.makePaths()

        println("Printing grid after paths resolution...\n$g")
        println("Printing second pass graph...\n$gr")
        gr.cleanGrid()

        println("Printing grid after cleanup...\n$g")

        gr.reset()
        gr.explore()
        println("Printing 3rd pass graph...\n$gr")
    }

    private fun timed() {
        var g: Grid? = null
        val time = measureTimeMillis {
            g = Grid(100, 100).generate()
            makeGraph(g!!).explore().makePaths().reset().explore().cleanGrid()
        }
        println("$g\n\nGenerated in $time millis.")
    }

    private fun manyTimed(amount: Int = 100) {
        val times = LongArray(amount)
        val total = measureTimeMillis {
            runBlocking {
                val l = List(amount) {
                    if (!isActive) return@runBlocking
                    launch(CommonPool) {
                        times[it] = measureTimeMillis {
                            makeGraph(Grid(100, 100).generate()).explore().makePaths().reset().explore().cleanGrid()
                        }
                    }
                }
                l.forEach { it.join() }
            }
        }
        println("Generated $amount dungeons of size 100*100 in ${times.sum()} ms, for an average of ${times.average()} ms each.\n" +
                "Longest took ${times.max()} ms and fastest took ${times.min()} ms.\nTook $total ms really (using coroutines).")
    }
}
