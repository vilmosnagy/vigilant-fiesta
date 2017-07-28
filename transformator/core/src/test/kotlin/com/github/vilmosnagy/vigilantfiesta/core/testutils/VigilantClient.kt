package com.github.vilmosnagy.vigilantfiesta.core.testutils

object VigilantClient {

    var methodName: String? = null
        private set
    var arguments: List<Any?>? = null
        private set

    @JvmStatic
    fun methodCallStarted(methodName: String, arguments: List<Any?>) {
        this.methodName = methodName
        this.arguments = arguments
    }

}