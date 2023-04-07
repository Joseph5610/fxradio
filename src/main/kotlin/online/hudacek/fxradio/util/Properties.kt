/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.util

import mu.KotlinLogging
import tornadofx.App
import tornadofx.Component
import tornadofx.ConfigProperties

private val logger = KotlinLogging.logger {}

/**
 * Keys for values stored in app.properties
 */
enum class Properties(val key: String) {
    UsePlatformMenus("menu.native"), // Not configurable in UI
    Volume("player.volume"),
    Player("player.type"),
    PlayerAnimated("player.animate"),
    ApiServer("app.server"),
    SearchQuery("search.query"),
    WindowDivider("windowDivider"),
    ShowLibrary("window.showLibrary"),
    ShowPinnedCountries("window.showPinned"),
    WindowWidth("window.width"),
    WindowHeight("window.height"),
    WindowX("window.x"),
    WindowY("window.y"),
    AccentColor("app.accentColor"),
    LogLevel("log.level"),
    DarkMode("app.darkmode"),
    UseTrayIcon("app.trayicon"),
    EnableDebugView("app.debug"), // Not configurable in UI
}

/**
 * Creates app configuration for property key [Properties]
 */
class Property(property: Properties, appConfig: ConfigProperties? = null) : Component() {

    val actualConfig = appConfig ?: app.config

    // Extracts value
    val key by lazy { property.key }

    val isPresent: Boolean
        // runCatching handles situations where config or key fields are throwing NPE
        get() = runCatching { actualConfig.keys.any { it == key } }.getOrDefault(false)

    inline fun <reified T> get(): T {
        return when (T::class) {
            Boolean::class -> actualConfig.boolean(key) as T
            Double::class -> actualConfig.double(key) as T
            Int::class -> actualConfig.int(key) as T
            String::class -> actualConfig.string(key) as T
            else -> {
                throw IllegalArgumentException("${T::class} is not supported argument!")
            }
        }
    }

    inline fun <reified T> getOrNull(): T? = runCatching { get<T>() }.getOrNull()

    /**
     * Return the value from the config based on key
     * or default value if the value does not exist there
     */
    inline fun <reified T> get(defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> (actualConfig.boolean(key, defaultValue as Boolean)) as T
            Double::class -> actualConfig.double(key, defaultValue as Double) as T
            Int::class -> actualConfig.int(key, defaultValue as Int) as T
            String::class -> actualConfig.string(key, defaultValue as String) as T
            else -> {
                defaultValue
            }
        }
    }

    fun <T> save(newValue: T) {
        logger.info { "Saving $key: $newValue " }
        with(actualConfig) {
            set(key to newValue)
            save()
        }
    }

    fun remove() {
        logger.info { "Remove value for key: $key " }
        with(actualConfig) {
            remove(key)
            save()
        }
    }
}

fun App.saveProperties(keyValueMap: Map<Properties, Any>) {
    logger.info { "Saving ${keyValueMap.keys}, ${keyValueMap.values} " }
    with(config) {
        keyValueMap.forEach {
            set(it.key.key to it.value)
        }
        save()
    }
}

/**
 * Helper method. Get value of property. If the value is not stored, returns [defaultValue]
 */
inline fun <reified T> Properties.value(defaultValue: T) = Property(this).get(defaultValue)

/**
 * Save new value for given property key
 */
fun <T> Properties.save(newValue: T) = Property(this).save(newValue)
