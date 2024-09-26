package com.example.coroutinesdemo

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.FileReader

var acquired = 0

class Resource {
    init {
        acquired++
    } // Acquire the resource

    fun close() {
        acquired--
    } // Release the resource
}

//fun main() {
//    runBlocking {
//        repeat(10_000) { // Launch 10K coroutines
//            launch {
//                val resource = withTimeout(51) { // Timeout of 60 ms
//                    delay(50) // Delay for 50 ms
//                    println("Resource allocated")
//                    Resource() // Acquire a resource and return it from withTimeout block
//                }
//                println("Resource $resource")
//            resource.close() // Release the resource
//        }
//    }
//}
//// Outside of runBlocking all coroutines have completed
//println(acquired) // Print the number of resources still acquired
//}

fun main() {
    cancelCoroutineWithIsActive()
}

fun returnNullAfterTimeout() = runBlocking {
    val job = withTimeoutOrNull(1500) {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(400L)
        }
    }
    println(job)
}

fun cancelAfterTimeout() = runBlocking {
    withTimeout(1500) {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(400L)
        }
    }
}

fun suspendFunInFinallyBlock() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            println("job: I'm running finally")
            delay(1000L)
            println("job: And I've just delayed for 1 sec because I'm non-cancellable")
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

fun runNonCancellableBlock() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            withContext(NonCancellable) {
                println("job: I'm running finally")
                delay(1000L)
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

fun closeResourcesUsingUse() = runBlocking {
    val job = launch {
        try {
            val lines = readFileAsync("example.txt")
            lines.forEach { println(it) }
        } catch (e: Exception) {
            println("Error reading file: $e")
        }
    }

    delay(1000)
    job.cancelAndJoin()
    println("Job cancelled and joined.")
}

suspend fun readFileAsync(filename: String): List<String> = coroutineScope {
    return@coroutineScope withContext(Dispatchers.Default) {
        withContext(Dispatchers.IO) {
            BufferedReader(FileReader(filename)).use { reader ->
                val lines = mutableListOf<String>()
                var line: String? = reader.readLine()
                while (line != null) {
                    lines.add(line)
                    line = reader.readLine()
                }
                lines
            }
        }
    }
}

fun closeResourcesInFinally() = runBlocking {
    var sum = 0
    val job = launch(Dispatchers.Default) {
        try {
            for (i in 1..1000) {
                if (isActive) {
                    sum += i
                    println("Partial sum after $i iterations: $sum")
                }
                delay(500)
            }
        } catch (e: CancellationException) {
            println("Coroutine canceled: $e")
        } catch (e: Exception) {
            println("Custom Exception: $e")
        } finally {
            println("Cancellable function: Finally block executed")
        }
    }

    delay(1000)
    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("Now I can quit.")
}

fun cancelCoroutineWithIsActive() = runBlocking {
    var sum = 0
    val job = launch(Dispatchers.Default) {
        for (i in 1..1000) {
            if (isActive) {
                sum += i
                println("Partial sum after $i iterations: $sum")
            }
        }
    }

    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("Now I can quit.")
}

fun cancelCoroutineWithYield() = runBlocking {
    var sum = 0
    val job = launch(Dispatchers.Default) {
        for (i in 1..1000) {
            yield()
            sum += i
            println("Partial sum after $i iterations: $sum")
        }
    }

    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("Now I can quit.")
}

fun catchExcep() = runBlocking {
    val result = runCatching {
        launch {
            delay(1000)
            throw CancellationException("Explicit cancellation")
        }
    }

    result.onFailure { exception ->
        println("Caught exception: $exception")
    }
}

fun catchExceptionInACoroutineWithSuspendFunction() = runBlocking {
    var sum = 0
    val job = launch(Dispatchers.Default) {
        for (i in 1..1000) {
            try {
                sum += i
                println("Partial sum after $i iterations: $sum")
                delay(100)
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("Now I can quit.")
}

fun cancellableCoroutineWithSuspendFunction() = runBlocking {
    var sum = 0
    val job = launch(Dispatchers.Default) {
        for (i in 1..1000) {
            sum += i
            println("Partial sum after $i iterations: $sum")
            delay(500)
        }
    }

    delay(5)
    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("Now I can quit.")
}

fun nonCancellableCoroutine() = runBlocking {
    var sum = 0
    val job = launch(Dispatchers.Default) {
        for (i in 1..100) {
            sum += i
            println("Partial sum after $i iterations: $sum")
        }
    }

    println("I'm tired of waiting!")
    job.cancelAndJoin()
    println("Now I can quit.")
}

fun cancelCoroutine() =
    runBlocking {
        val job = launch {
            repeat(1000) { i ->
                println("job: Working $i ...")
                delay(500L)
            }
        }
        delay(2100L) // delay a bit
        println("I'm tired of waiting!")
        job.cancel() // cancels the job
        job.join() // waits for job's completion
        println("Now I can quit.")
    }
