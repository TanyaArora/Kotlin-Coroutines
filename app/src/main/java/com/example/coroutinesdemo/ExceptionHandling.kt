package com.example.coroutinesdemo

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import java.io.IOException
import kotlin.ArithmeticException

fun main() {
    cancellationExceptionInChildCoroutine()
}

fun cancellationExceptionInChildCoroutine() = runBlocking {
    CoroutineScope(Dispatchers.IO).launch {
        println("Start parent coroutine")
        val job1 = launch {
            println("Start child coroutine 1")
            delay(200)
            println("Child coroutine 1 cancelled")
        }

        launch {
            println("Start child coroutine 2")
            delay(300)
            println("In coroutine 2 after child coroutine 1 was cancelled")
        }
        delay(50)
        job1.cancelAndJoin()
        delay(500)
        println("End parent coroutine")
    }.join()
}

fun usingSupervisorScope() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Caught exception: $throwable")
    }
    supervisorScope {
        println("Start parent coroutine")
        launch(exceptionHandler) {
            println("Start child coroutine 1")
            delay(100)
            throw IOException()
        }

        launch {
            println("Start child coroutine 2")
            delay(300)
            println("Child coroutine 1 was cancelled but coroutine 2 is running")
        }

        delay(1000)
        println("End parent coroutine")
    }
}

fun supervisorJob() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Caught exception: $throwable")
    }
    with(CoroutineScope(SupervisorJob() + exceptionHandler)) {
        println("Start parent coroutine")
        launch {
            println("Start child coroutine 1")
            delay(100)
            throw IOException()
        }

        launch {
            println("Start child coroutine 2")
            delay(300)
            println("Child coroutine 1 was cancelled but coroutine 2 is running")
        }

        delay(1000)
        println("End parent coroutine")
    }
}

fun exceptionAggregation() = runBlocking {
    val handler = CoroutineExceptionHandler { _, throwable ->
        println("Exception caught: $throwable with suppressed ${throwable.suppressed.contentToString()}")
    }
    CoroutineScope(Job() + handler).launch {
        launch {
            println("Start child coroutine 1")
            delay(100)
            throw IOException()
        }

        launch {
            try {
                println("Start child coroutine 2")
                delay(300)
            } finally {
                throw ArithmeticException()
            }
        }
    }.join()
}

fun otherExceptionInChildCoroutine() = runBlocking {
    CoroutineScope(Dispatchers.IO).launch {
        println("Start parent coroutine")
        launch {
            println("Start child coroutine 1")
            delay(100)
            throw IOException()
        }

        launch {
            println("Start child coroutine 2")
            delay(300)
            println("Child coroutine 1 was cancelled")
        }
        delay(500)
        println("End parent coroutine")
    }.join()
}


@OptIn(DelicateCoroutinesApi::class)
fun catchExceptionUsingHandlerInAsync() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Caught exception: $throwable")
    }
    val deferred = GlobalScope.async(exceptionHandler) {
        println("Inside parent coroutine")
        async {
            println("Inside child coroutine")
            throw Exception("My exception")
        }
    }
    deferred.await()
    println("Job completed")
}

@OptIn(DelicateCoroutinesApi::class)
fun catchExceptionUsingHandlerInLaunch() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Caught exception: $throwable")
    }
    val job = GlobalScope.launch(exceptionHandler) {
        println("Inside parent coroutine")
        launch {
            println("Inside child coroutine")
            throw Exception("My exception")
        }
    }
    job.join()
    println("Job completed")
}

@OptIn(DelicateCoroutinesApi::class)
fun propagateExceptionInAsync() = runBlocking {
    GlobalScope.async {
        println("Inside parent coroutine")
        async(Dispatchers.IO) {
            println("Inside child coroutine")
            async {
                println("Inside sub-child coroutine")
                throw Exception("My Exception")
            }
        }
    }.await()
    println("Job completed")
}

@OptIn(DelicateCoroutinesApi::class)
fun propagateExceptionInLaunch() = runBlocking {
    GlobalScope.launch {
        println("Inside parent coroutine")
        launch(Dispatchers.IO) {
            println("Inside child coroutine")
            launch {
                println("Inside sub-child coroutine")
                throw Exception("My Exception")
            }
        }

        launch {
            println("Inside child coroutine 2 ")
        }
    }.join()
    println("Job completed")
}

@OptIn(DelicateCoroutinesApi::class)
fun uncaughtExceptionInAsync() = runBlocking {
    val deferred = GlobalScope.async {
        println("async: About to throw my custom exception")
        throw Exception("My exception")
    }
    deferred.await()
    println("Job complete")
}

@OptIn(DelicateCoroutinesApi::class)
fun uncaughtExceptionInLaunch() = runBlocking {
    val job = GlobalScope.launch {
        println("About to throw my custom exception")
        throw Exception("My exception")
    }
    job.join()
    println("Job complete")
}

fun basicExceptionHandling() = runBlocking {
    val job = launch {
        try {
            throw Exception("My exception")
        } catch (e: Exception) {
            println("Caught exception: $e")
        }
    }
    job.join()
}