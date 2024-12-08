/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n

public fun Key.capitalizeWords(): Key = withPostProcessor {
	it.capitalizeWords(locale)
}

public fun Key.capitalize(): Key = withPostProcessor { string ->
	string.replaceFirstChar {
		if (it.isLowerCase()) {
			it.titlecase(locale ?: I18n.defaultLocale)
		} else {
			it.toString()
		}
	}
}

public fun Key.lowercase(): Key = withPostProcessor {
	it.lowercase(locale ?: I18n.defaultLocale)
}

public fun Key.uppercase(): Key = withPostProcessor {
	it.uppercase(locale ?: I18n.defaultLocale)
}
