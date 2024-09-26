package com.example.coroutinesdemo

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ArithmeticException
import kotlin.system.measureTimeMillis

fun main() {
    calculateTimeTakenInAsynchronousLazyCalls()
}

fun calculateTimeTakenInScopedCallWithError() = try {
    runBlocking {
        val time = measureTimeMillis {
            val one = async<Int> {
                try {
                    doSomethingUsefulOne()
                } catch (e: CancellationException) {
                    println("Task one is cancelled")
                    0
                } finally {
                    println("Task one finally block executed")
                }
            }
            val two = async<Int> {
                try {
                    doSomethingUsefulTwo()
                } catch (e: CancellationException) {
                    println("Task two is cancelled")
                    0
                } finally {
                    println("Task two finally block executed")
                }
            }
            delay(500)
            println("Final answer is ${one.await() + two.await()}")
        }

        println("Completed in $time ms")
    }
} catch (e: Exception) {
    println(e)
}

@OptIn(DelicateCoroutinesApi::class)
fun calculateTimeTakenInAsyncCallError() {
    try {
        val time = measureTimeMillis {
            val one = GlobalScope.async<Int> {
                try {
                    doSomethingUsefulOne()
                } catch (e: CancellationException) {
                    println("Task one is cancelled")
                    0
                } finally {
                    println("Task one finally block executed ")
                }
            }
            val two = GlobalScope.async<Int> {
                try {
                    doSomethingUsefulTwo()
                } catch (e: CancellationException) {
                    println("Task two is cancelled")
                    0
                } finally {
                    println("Task two finally block executed")
                }
            }
            runBlocking {
                delay(500)
                println("Final answer is ${one.await() + two.await()}")
            }
        }
        println("Completed in $time ms")
    } catch (e: Exception) {
        println(e)
    }
}

suspend fun doSomethingUsefulOne(): Int {
    delay(2400)
    println("Task one is getting executed")
    return 33
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1500)
    val suffix = 0
//         Math.floorDiv(5, 0) // this will cause an Arithmetic Exception as we are diving 5 by 0
    println("Task two is getting executed")
    return 6 + suffix
}

@OptIn(DelicateCoroutinesApi::class)
fun calculateTimeTakenInAsyncCall() {
    val time = measureTimeMillis {
        val one = GlobalScope.async { doSomethingUsefulOne() }
        val two = GlobalScope.async { doSomethingUsefulTwo() }
        runBlocking {
            println("Final answer is ${one.await() + two.await()}")
        }
    }
    println("Completed in $time ms")
}

//suspend fun doSomethingUsefulOne(): Int {
//    delay(2400)
//    return 33
//}
//
//suspend fun doSomethingUsefulTwo(): Int {
//    delay(1500)
//    return 6
//}

fun calculateTimeTakenInAsynchronousLazyCalls() = runBlocking {
    val time = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        one.start()
        two.start()
        println("Final answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

fun calculateTimeTakenInSynchronousLazyCalls() = runBlocking {
    val time = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        println("Final answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

fun calculateTimeTakenInConcurrentCall() = runBlocking {
    val time = measureTimeMillis {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("Final answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

suspend fun doSomethingUsefulTwo(resultOne: Int): Int {
    delay(1500)
    return resultOne - 6
}

fun calculateTimeTakenInSequentialCall() = runBlocking {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo(one)
        println("Final answer is $two")
    }
    println("Completed in $time ms")
}

