/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.registries

import dev.akkinoc.util.YamlResourceBundle
import dev.kordex.i18n.files.PropertiesControl
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.ResourceBundle

public object FileFormatRegistry {
	private val logger: KLogger = KotlinLogging.logger { }
	private val formats: MutableMap<String, ResourceBundle.Control> = mutableMapOf()

	init {
		register("properties", PropertiesControl)
		register("yaml", YamlResourceBundle.Control)
	}

	public fun get(identifier: String): ResourceBundle.Control? =
		formats[identifier]

	public fun getOrError(identifier: String): ResourceBundle.Control =
		formats[identifier]
			?: error("Unknown file format: $identifier")

	public fun register(identifier: String, control: ResourceBundle.Control) {
		formats[identifier] = control

		logger.trace { "Registered file format \"$identifier\" to control $control" }
	}
}
