/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.registries

import dev.kordex.i18n.Bundle
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

public typealias ClassLoaderCallback = () -> Map<String, ClassLoader>

public object ClassLoaderRegistry {
	private val logger: KLogger = KotlinLogging.logger("dev.kordex.i18n.registries.ClassLoaderRegistry")

	private val bundleCache = mutableMapOf<String, ClassLoader>()
	private val callbacks: MutableSet<ClassLoaderCallback> = mutableSetOf()

	public fun getForBundle(bundle: String): ClassLoader =
		bundleCache[bundle] ?: ClassLoader.getSystemClassLoader()

	public fun getForBundle(bundle: Bundle): ClassLoader =
		getForBundle(bundle.name)

	@Suppress("TooGenericExceptionCaught")
	public fun getCallbackClassloaders(): Map<String, ClassLoader> =
		callbacks
			.map {
				try {
					it.invoke()
				} catch (e: Exception) {
					logger.warn(e) { "Exception thrown by classloader callback $it - skipping..." }

					mapOf()
				}
			}
			.flatMap { it.entries }
			.associate(Map.Entry<String, ClassLoader>::toPair)

	public fun register(bundle: String, classLoader: ClassLoader): Boolean {
		if (bundle !in bundleCache) {
			bundleCache[bundle] = classLoader

			logger.trace { "Registered classloader $classLoader for bundle $bundle" }

			return true
		}

		return false
	}

	public fun register(bundle: Bundle): Boolean =
		register(bundle.name, bundle.classLoader)

	public fun register(callback: ClassLoaderCallback): Boolean {
		val result = callbacks.add(callback)

		if (result) {
			logger.trace { "Registered class-loader callback: $callback" }
		} else {
			logger.trace { "Class-loader callback already registered: $callback" }
		}

		return result
	}
}
