package com.github.vilmosnagy.vigilantfiesta.core.testutils

class ClassLoaderFromMemory(
        val classesByName: Map<String, ByteArray>,
        parent: ClassLoader
) : ClassLoader(parent) {


    override fun findClass(name: String): Class<*> {
        val klass = classesByName[name]
        return defineClass(name, klass, 0, klass?.size ?: 0)
    }
}