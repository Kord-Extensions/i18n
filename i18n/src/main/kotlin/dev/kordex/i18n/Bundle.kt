/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n

import dev.kordex.i18n.messages.MessageFormat
import dev.kordex.i18n.messages.formats.ICUFormatV1
import dev.kordex.i18n.registries.ClassLoaderRegistry
import dev.kordex.i18n.registries.FileFormatRegistry
import dev.kordex.i18n.registries.MessageFormatRegistry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.ResourceBundle

@Serializable
public data class Bundle(
	val name: String,

	val fileFormat: String = "properties",
	val messageFormat: String = ICUFormatV1.identifier,

	@Transient
	val classLoader: ClassLoader = ClassLoaderRegistry.getForBundle(name),
) {
	init {
		ClassLoaderRegistry.register(this)
	}

	public fun getResourceBundleControl(): ResourceBundle.Control =
		FileFormatRegistry.getOrError(fileFormat)

	public fun getMessageFormatter(): MessageFormat =
		MessageFormatRegistry.getOrError(messageFormat)

	override fun toString(): String =
		"Bundle $name/$fileFormat/$messageFormat"
}
