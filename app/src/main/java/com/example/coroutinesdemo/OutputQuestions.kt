package com.example.coroutinesdemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() {
    cancelNonSuspendingTask()
}

fun defaultSequentialCall() {
    runBlocking {
        doLongRunningTaskOne()
        doLongRunningTaskTwo()
        println("Fired both tasks")
    }
    println("Completed my execution")
}

fun concurrentCallWithLaunch() {
    runBlocking {
        launch { doLongRunningTaskOne() }
        launch { doLongRunningTaskTwo() }
        println("I've launched both coroutines")
    }
    println("Completed my execution")
}

fun concurrentCallWithCoroutineScopeInsideRunBlocking() {
    runBlocking {
        coroutineScope {
            launch { doLongRunningTaskOne() }
            launch { doLongRunningTaskTwo() }
            println("I've launched both coroutines")
        }
        println("I'm outside coroutineScope")
    }
    println("Completed my execution")
}

fun concurrentCallWithCustomCoroutineScopeInsideRunBlocking() {
    runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            launch { doLongRunningTaskOne() }
            launch { doLongRunningTaskTwo() }
            println("I've launched both coroutines")
        }
        println("I'm outside coroutineScope")
    }
    println("Completed my execution")
}

fun concurrentCallWithCustomCoroutineScopeInsideRunBlockingWithJoin() {
    runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            launch { doLongRunningTaskOne() }
            launch { doLongRunningTaskTwo() }
            println("I've launched both coroutines")
        }.join()
        println("I'm outside coroutineScope")
    }
    println("Completed my execution")
}

fun concurrentCallWithMultipleCustomCoroutineScopeInsideRunBlocking() {
    runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            launch { doLongRunningTaskOne() }
            launch { doLongRunningTaskTwo() }
            println("I've launched both coroutines of scope 1")

            CoroutineScope(Dispatchers.IO).launch {
                launch { doLongRunningTaskOne() }
                launch { doLongRunningTaskTwo() }
                println("I've launched both coroutines of scope 2")
            }
        }.join()
        println("I'm outside coroutineScope")
    }
    println("Completed my execution")
}

fun concurrentCallUsingAsync() {
    runBlocking {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("Final answer is ${one.await() + two.await()}")
    }
    println("Completed my execution")
}

fun sequentialCallUsingAsync() {
    runBlocking {
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        println("Final answer is ${one.await() + two.await()}")
    }
    println("Completed my execution")
}

fun lazyStartWithAsync() {
    runBlocking {
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        one.start()
        two.start()
        println("Final answer is ${one.await() + two.await()}")
    }
    println("Completed my execution")
}

fun printWithCoroutines(){
    runBlocking {
        launch {
            repeat(5){
                println("Iteration $it in Coroutine 1")
            }
        }

        launch {
            repeat(5){
                println("Iteration $it in Coroutine 2")
            }
        }
    }
}

fun printWithCoroutinesUsingYieldInOne(){
    runBlocking {
        launch {
            repeat(5){
                println("Iteration $it in Coroutine 1")
                yield()
            }
        }

        launch {
            repeat(5){
                println("Iteration $it in Coroutine 2")
            }
        }
    }
}

fun printWithCoroutinesUsingYieldInBoth(){
    runBlocking {
        launch {
            repeat(5){
                println("Iteration $it in Coroutine 1")
                yield()
            }
        }

        launch {
            repeat(5){
                println("Iteration $it in Coroutine 2")
                yield()
            }
        }
    }
}

fun printWithSingleCoroutinesUsingYield(){
    runBlocking {
        launch {
            repeat(5){
                println("Iteration $it in Coroutine 1")
                yield()
            }
        }
    }
}

fun cancelLongRunningTask() {
    runBlocking {
        val job = launch {
            repeat(1000) {
                doLongRunningTaskTwo()
            }
        }
        delay(4000L)
        println("I'm tired of waiting!")
        job.cancel()
        job.join()
        println("Now I can quit.")
    }
}

fun cancelNonSuspendingTask() {
    runBlocking {
        var sum = 0
        val job = launch(Dispatchers.Default) {
            for (i in 1..1000) {
                sum += i
                println("Partial sum after $i iterations: $sum")
            }
        }
//        delay(5)
        println("I'm tired of waiting!")
        job.cancelAndJoin()
        println("Now I can quit.")
    }
}

suspend fun doLongRunningTaskOne(): Int {
    delay(2400)
    println("Task one is getting executed")
    return 33
}

suspend fun doLongRunningTaskTwo(): Int {
    delay(1500)
    println("Task two is getting executed")
    return 6
}

suspend fun doLongRunningTaskThree(): Int {
    println("Task one is getting executed")
    return 15
}