package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.defaults.DefaultPermissionRequestHandler
import com.likethesalad.android.aaper.api.errors.HandlerNameAlreadyExists

/**
 * Created by César Muñoz on 29/07/20.
 */
class RequestHandlersProvider {

    private val handlers = mutableMapOf<String, PermissionRequestHandler>()
    private var defaultHandlerName = DefaultPermissionRequestHandler.NAME

    init {
        register(DefaultPermissionRequestHandler())
    }

    fun register(vararg handlers: PermissionRequestHandler) {
        handlers.forEach {
            addHandler(it)
        }
    }

    fun setDefaultHandlerName(name: String) {
        defaultHandlerName = name
    }

    internal fun getHandlerByName(name: String): PermissionRequestHandler {
        val handlerName = if (name.isEmpty()) {
            defaultHandlerName
        } else {
            name
        }

        return handlers.getValue(handlerName)
    }

    private fun addHandler(handler: PermissionRequestHandler) {
        val name = handler.getName()
        if (handlers.containsKey(name)) {
            throw HandlerNameAlreadyExists(name)
        }

        handlers[name] = handler
    }
}