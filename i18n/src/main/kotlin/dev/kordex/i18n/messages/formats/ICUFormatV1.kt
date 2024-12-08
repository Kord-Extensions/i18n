/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.messages.formats

import dev.kordex.i18n.messages.MessageFormat
import java.util.Locale
import com.ibm.icu.text.MessageFormat as V1MessageFormat

public object ICUFormatV1 : MessageFormat {
	override val identifier: String = "icu-v1"

	override fun formatNamed(
		string: String,
		locale: Locale,
		placeholders: Map<String, Any?>
	): String =
		V1MessageFormat(string, locale)
			.format(placeholders)

	override fun formatOrdinal(
		string: String,
		locale: Locale,
		placeholders: Array<Any?>
	): String =
		V1MessageFormat(string, locale)
			.format(placeholders)
}
