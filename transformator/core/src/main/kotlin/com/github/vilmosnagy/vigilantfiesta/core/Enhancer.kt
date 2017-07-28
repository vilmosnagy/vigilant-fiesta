package com.github.vilmosnagy.vigilantfiesta.core

import javax.inject.Inject

class Enhancer
@Inject constructor() {

    fun enhanceClass(classCode: ByteArray): ByteArray {
        return classCode
    }
}