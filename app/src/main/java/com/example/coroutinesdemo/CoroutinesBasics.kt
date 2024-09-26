package com.example.coroutinesdemo

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    usingThreads()
    usingCoroutines()
}

// will consume less memory
private fun usingCoroutines() = runBlocking {

    repeat(50_000) { // launch a lot of coroutines
        launch {
            delay(5000L)
            print(".")
        }.join()
    }
}

// will consume more memory
private fun usingThreads() = repeat(50_000) {
    Thread { // launch a lot of threads
        Thread.sleep(5000L)
        print(".")
    }
}