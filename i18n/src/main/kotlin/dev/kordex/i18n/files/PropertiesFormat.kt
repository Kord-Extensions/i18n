/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.files

import dev.kordex.i18n.I18n
import java.util.*

public object PropertiesFormat : FileFormat {
	override val identifiers: Set<String> = setOf("properties")
	override val control: ResourceBundle.Control = Control

	override fun toString(): String = "PropertiesFormat(${identifiers.joinToString(", ")})"

	private object Control : ResourceBundle.Control() {
		override fun getFormats(baseName: String?): MutableList<String> {
			if (baseName == null) {
				throw NullPointerException()
			}

			return FORMAT_PROPERTIES
		}

		override fun getFallbackLocale(baseName: String?, locale: Locale?): Locale? {
			if (baseName == null) {
				throw NullPointerException()
			}

			return if (locale == I18n.defaultLocale) {
				null
			} else {
				I18n.defaultLocale
			}
		}
	}
}
