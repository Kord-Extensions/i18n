/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n

import java.util.Locale

/**
 * Capitalise words in this string according to the given locale. Defaults to the configured default i18n locale.
 */
public fun String.capitalizeWords(locale: Locale? = null): String {
	return split(" ").joinToString(" ") { word ->
		word.replaceFirstChar {
			if (it.isLowerCase()) {
				it.titlecase(locale ?: I18n.defaultLocale)
			} else {
				it.toString()
			}
		}
	}
}

/**
 * Split a string into a list of substrings using the given [separator], filtering out empty values based on
 * [EMPTY_VALUE_STRING].
 */
public fun String.iToList(separator: String = ","): List<String> =
	split(separator)
		.map { it.trim() }
		.filter { it != EMPTY_VALUE_STRING }

/**
 * Returns `true` when the string matches [EMPTY_VALUE_STRING] or [isEmpty] returns `true`, `false` otherwise.
 */
public fun String.iIsEmpty(): Boolean =
	this == EMPTY_VALUE_STRING || this.isEmpty()
