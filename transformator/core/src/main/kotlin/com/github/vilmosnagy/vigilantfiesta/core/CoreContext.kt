package com.github.vilmosnagy.vigilantfiesta.core

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface CoreContext {

    companion object {
        internal var mockedPluginCtx: CoreContext? = null

        val instance: CoreContext
            get() = this.mockedPluginCtx ?: DaggerCoreContext.create()
    }

    val enhancer: Enhancer

}