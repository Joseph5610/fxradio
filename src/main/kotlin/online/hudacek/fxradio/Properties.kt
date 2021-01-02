/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio

import tornadofx.Component

/**
 * Keys for values stored in app.properties
 */
enum class Properties(val key: String) {
    PLATFORM_MENU_BAR("menu.native"),
    VOLUME("player.volume"),
    PLAYER("player.type"),
    PLAYER_ANIMATE("player.animate"),
    PLAYER_CUSTOM_REFRESH_META("player.refreshMeta"),
    API_SERVER("app.server"),
    SEARCH_QUERY("search.query"),
    NOTIFICATIONS("notifications"),
    WINDOW_DIVIDER("windowDivider"),
    WINDOW_SHOW_LIBRARY("window.showLibrary"),
    WINDOW_SHOW_COUNTRIES("window.showCountries"),
    LOG_LEVEL("log.level");
}

/**
 * Basic app configuration
 */
class Property(property: Properties) : Component() {

    //Extract value
    val key = property.key

    val isPresent: Boolean
        get() {
            return try {
                app.config.keys.any { it == key }
            } catch (e: Exception) {
                false
            }
        }

    inline fun <reified T> get(): T {
        return when (T::class) {
            Boolean::class -> app.config.boolean(key) as T
            Double::class -> app.config.double(key) as T
            Int::class -> app.config.int(key) as T
            String::class -> app.config.string(key) as T
            else -> {
                throw IllegalArgumentException("${T::class} is not supported argument")
            }
        }
    }

    /**
     * Return the value from the app.config based on key
     * or default value if the value does not exist there
     */
    inline fun <reified T> get(defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> app.config.boolean(key, defaultValue as Boolean) as T
            Double::class -> app.config.double(key, defaultValue as Double) as T
            Int::class -> app.config.int(key, defaultValue as Int) as T
            String::class -> app.config.string(key, defaultValue as String) as T
            else -> {
                defaultValue
            }
        }
    }

    fun <T> save(newValue: T) {
        with(app.config) {
            set(key to newValue)
            save()
        }
    }

    fun remove() {
        with(app.config) {
            remove(key)
            save()
        }
    }
}

fun Component.saveProperties(keyValueMap: Map<Properties, Any>) {
    with(app.config) {
        keyValueMap.forEach {
            set(it.key.key to it.value)
        }
        save()
    }
}

inline fun <reified T> property(key: Properties, defaultValue: T) = Property(key).get(defaultValue)