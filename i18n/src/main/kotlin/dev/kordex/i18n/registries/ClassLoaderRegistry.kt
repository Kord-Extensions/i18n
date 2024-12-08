/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.registries

import dev.kordex.i18n.Bundle

public object ClassLoaderRegistry {
	private val bundleCache = mutableMapOf<String, ClassLoader>()

	public fun getForBundle(bundle: String): ClassLoader =
		bundleCache.getOrPut(bundle) { ClassLoader.getSystemClassLoader() }

	public fun register(bundle: String, classLoader: ClassLoader) {
		bundleCache.put(bundle, classLoader)
	}

	public fun register(bundle: Bundle) {
		bundleCache.put(bundle.name, bundle.classLoader)
	}
}
