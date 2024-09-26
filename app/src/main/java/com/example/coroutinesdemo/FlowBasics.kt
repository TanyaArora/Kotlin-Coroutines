package com.example.coroutinesdemo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        getListUsingFlow().collect{
            println(it)
        }
    }
}

fun getListUsingFlow() =
    flow {
        for (i in 1..5) {
            delay(1000)
            emit(i)
        }
    }

suspend fun getListOfData(): List<Int> {
    val list = mutableListOf<Int>()
    for (i in 1..5) {
        delay(1000)
        list.add(i)
    }
    return list
}

