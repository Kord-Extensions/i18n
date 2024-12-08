/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.messages.formats

import com.ibm.icu.message2.MessageFormatter
import dev.kordex.i18n.messages.MessageFormat
import java.util.Locale

public object ICUFormatV2 : MessageFormat {
	override val identifier: String = "icu-v2"

	@Suppress("DEPRECATION")
	override fun formatNamed(
		string: String,
		locale: Locale,
		placeholders: Map<String, Any?>
	): String =
		MessageFormatter.builder().apply {
			setLocale(locale)
			setPattern(string)
		}.build()
			.formatToString(placeholders)

	@Suppress("DEPRECATION")
	override fun formatOrdinal(
		string: String,
		locale: Locale,
		placeholders: Array<Any?>
	): String =
		MessageFormatter.builder().apply {
			setLocale(locale)
			setPattern(string)
		}.build()
			.formatToString(
				placeholders.mapIndexed { index, value ->
					index.toString() to value
				}.toMap()
			)
}
