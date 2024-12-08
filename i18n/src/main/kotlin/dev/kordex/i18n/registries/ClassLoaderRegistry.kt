/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.registries

import dev.kordex.i18n.Bundle
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

public object ClassLoaderRegistry {
	private val logger: KLogger = KotlinLogging.logger { }
	private val bundleCache = mutableMapOf<String, ClassLoader>()

	public fun getForBundle(bundle: String): ClassLoader =
		bundleCache[bundle] ?: ClassLoader.getSystemClassLoader()

	public fun getForBundle(bundle: Bundle): ClassLoader =
		getForBundle(bundle.name)

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
}
