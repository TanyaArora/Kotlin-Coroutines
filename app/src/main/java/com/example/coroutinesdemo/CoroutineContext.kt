package com.example.coroutinesdemo

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

fun main() {
    contextSwitching()
}

fun contextSwitching() = runBlocking {
    println("Main program starts on thread: ${Thread.currentThread().name}")

    // Launch a coroutine on the default dispatcher
    launch(Dispatchers.Default) {
        println("Started default dispatcher on thread: ${Thread.currentThread().name}")

        // Perform a network request on the IO dispatcher
        val result = withContext(Dispatchers.IO) {
            println("Performing IO operation on thread: ${Thread.currentThread().name}")
            // Simulate network request
            delay(1000)
            "Network Result"
        }

        // Update the UI (simulate) on the main dispatcher
//        withContext(Dispatchers.Main) {
            println("Updating UI on thread: ${Thread.currentThread().name}")
            println("Result: $result")
//        }
    }.join()

    println("Main program ends on thread: ${Thread.currentThread().name}")
}

fun customCoroutineName() = runBlocking {
    launch(Dispatchers.IO + CoroutineName("MyCustomCoroutineName")) {
        println("I'm working in thread ${Thread.currentThread().name}")
    }
}

fun unconfinedWithContextSwitching() = runBlocking {
    println("Main program starts on thread: ${Thread.currentThread().name}")

    val job = launch(Dispatchers.Unconfined + CoroutineName("Tanya")) {
        println("Coroutine starts on thread: ${Thread.currentThread().name}")
        delay(500)
        println("Coroutine resumes after delay on thread: ${Thread.currentThread().name}")
        withContext(Dispatchers.Default) {
            println("Inside withContext block before delay on thread: ${Thread.currentThread().name}")
            delay(500)
            println("Inside withContext block after delay on thread: ${Thread.currentThread().name}")
        }
        println("Coroutine resumes after withContext on thread: ${Thread.currentThread().name}")
    }

    job.join()
    println("Main program ends on thread: ${Thread.currentThread().name}")
}

fun unconfinedDispatcherWithMultipleSuspendFuns() = runBlocking {
    launch(Dispatchers.Unconfined) {
        println("Coroutine starts on thread: ${Thread.currentThread().name}")
        delay(1000)
        println("Coroutine resumes after first delay on thread: ${Thread.currentThread().name}")
        doSomethingUsefulOne()
        println("Coroutine resumes after second delay on thread: ${Thread.currentThread().name}")
    }

    println("Main program ends on thread: ${Thread.currentThread().name}")
}

suspend fun doSomethingUseful() {
    delay(2400)
    println("Task one is getting executed")
}

fun nestedUnconfinedDispatcher() = runBlocking {
    launch(Dispatchers.Unconfined) {
        println("I'm in first launch before delay, working in ${Thread.currentThread().name}")
        delay(1000)
        println("I'm in first launch after delay, working in ${Thread.currentThread().name}")
        launch(Dispatchers.Unconfined) {
            println("I'm in second launch before delay, working in ${Thread.currentThread().name}")
            delay(1000)
            println("I'm in second launch after delay, working in ${Thread.currentThread().name}")
        }
    }
    println("I'm done, working in ${Thread.currentThread().name}")
}


fun confinedAndUnconfinedDispatcher() = runBlocking {
    launch(Dispatchers.IO) {
        println("IO dispatcher, working in ${Thread.currentThread().name}")
        delay(1000)
        println("IO dispatcher, after delay I'm, working in ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Unconfined) {
        println("Unconfined dispatcher, working in ${Thread.currentThread().name}")
        delay(1000)
        println("Unconfined dispatcher, after delay I'm, working in ${Thread.currentThread().name}")
    }
}


fun unconfinedDispatcher() = runBlocking {
    launch(Dispatchers.Unconfined) {
        println("I'm Job with Unconfined dispatcher, working in ${Thread.currentThread().name}")
        doSomethingUsefulOne() // this is a suspend function
        println("I'm Job with Unconfined dispatcher, after delay I'm, working in ${Thread.currentThread().name}")
    }
}


fun defaultDispatcher(): Int {
    var maxThreadNo = 0

    runBlocking {
        for (i in 1..10) {
            launch(Dispatchers.Default) {
                println("I'm Dispatcher $i, working in ${Thread.currentThread().name}")
                delay(50)

                val threadName = Thread.currentThread().name
                val threadNumber = threadName.filter { it.isDigit() }.toInt()
                maxThreadNo = if (maxThreadNo < threadNumber) threadNumber else maxThreadNo
                println("I'm Dispatcher $i, working in $threadName")
            }
        }
    }
    return maxThreadNo
}


fun jobExample() = runBlocking {
    launch() {

    }
    coroutineScope() {

    }
    suspendCoroutine<String>() {

    }
    CoroutineScope(Job() + Dispatchers.IO).launch {

    }
    coroutineContext
}