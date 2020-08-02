package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.defaults.DefaultRequestStrategy
import com.likethesalad.android.aaper.api.errors.HandlerNameAlreadyExistsException

/**
 * Created by César Muñoz on 29/07/20.
 */
class RequestHandlersProvider {

    private val handlers = mutableMapOf<String, RequestStrategy>()
    private var defaultHandlerName = DefaultRequestStrategy.NAME

    init {
        register(DefaultRequestStrategy())
    }

    fun register(vararg strategies: RequestStrategy) {
        strategies.forEach {
            addHandler(it)
        }
    }

    fun setDefaultHandlerName(name: String) {
        defaultHandlerName = name
    }

    internal fun getHandlerByName(name: String): RequestStrategy {
        val handlerName = if (name.isEmpty()) {
            defaultHandlerName
        } else {
            name
        }

        return handlers.getValue(handlerName)
    }

    private fun addHandler(strategy: RequestStrategy) {
        val name = strategy.getName()
        if (handlers.containsKey(name)) {
            throw HandlerNameAlreadyExistsException(name)
        }

        handlers[name] = strategy
    }
}