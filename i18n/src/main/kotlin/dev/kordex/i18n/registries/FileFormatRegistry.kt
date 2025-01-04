/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.registries

import dev.kordex.i18n.files.FileFormat
import dev.kordex.i18n.files.PropertiesFormat
import dev.kordex.i18n.files.YamlFormat
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

public object FileFormatRegistry {
	private val logger: KLogger = KotlinLogging.logger { }
	private val formats: MutableMap<String, FileFormat> = mutableMapOf()

	init {
		register(PropertiesFormat)
 		register(YamlFormat)
	}

	public fun get(identifier: String): FileFormat? =
		formats[identifier]

	public fun getOrError(identifier: String): FileFormat =
		formats[identifier]
			?: error("Unknown file format: $identifier")

	public fun register(format: FileFormat) {
		format.identifiers.forEach { identifier ->
			formats[identifier] = format
		}

		logger.trace { "Registered file format: $format (${format.identifiers.joinToString()})" }
	}

	public fun unregister(identifier: String): FileFormat? {
		val result = formats.remove(identifier)

		if (result != null) {
			logger.trace { "Unregistered file format for identifier \"$identifier\", was $result" }
		}

		return result
	}

	public fun unregister(format: FileFormat): Map<String, FileFormat?> {
		val result = format.identifiers.associateWith { identifier ->
			formats.remove(identifier)
		}.filterValues { it != null }

		if (result.isNotEmpty()) {
			logger.trace { "Couldn't remove non-registered file format: $format" }
		}

		return result
	}

	public fun getFormats(): MutableSet<String> =
		formats.keys
}
