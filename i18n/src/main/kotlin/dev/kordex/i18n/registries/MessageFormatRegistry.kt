/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.registries

import dev.kordex.i18n.messages.MessageFormat
import dev.kordex.i18n.messages.formats.ICUFormatV1
import dev.kordex.i18n.messages.formats.ICUFormatV2
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

public object MessageFormatRegistry {
	private val logger: KLogger = KotlinLogging.logger { }
	private val formats: MutableMap<String, MessageFormat> = mutableMapOf()

	init {
		register(ICUFormatV1)
		register(ICUFormatV2)
	}

	public fun get(identifier: String): MessageFormat? =
		formats[identifier]

	public fun getOrError(identifier: String): MessageFormat =
		formats[identifier]
			?: error("Unknown message format: $identifier")

	public fun register(format: MessageFormat) {
		formats[format.identifier] = format

		logger.trace { "Registered message format \"${format.identifier}\" - $format" }
	}
}
