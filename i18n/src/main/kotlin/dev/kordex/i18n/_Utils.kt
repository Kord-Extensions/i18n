/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n

import java.util.Locale

private val translationKeyMap: MutableMap<String, Key> = mutableMapOf()

public fun String.toKey(
	bundle: Bundle? = null,
	locale: Locale? = null,
	presetPlaceholderPosition: PlaceholderPosition? = null,
	translateNestedKeys: Boolean? = null,
): Key {
	var key = translationKeyMap.getOrPut(this) { Key(this) }

	if (bundle != null || locale != null) {
		key = key.withBoth(bundle, locale)
	}

	if (presetPlaceholderPosition != null) {
		key = key.withPresetPlaceholderPosition(presetPlaceholderPosition)
	}

	if (translateNestedKeys != null) {
		key = key.withTranslateNestedKeys(translateNestedKeys)
	}

	return key
}

public fun String.toKey(bundle: String, locale: Locale? = null): Key =
	toKey(Bundle(bundle), locale)

public fun String.toKey(locale: Locale): Key =
	translationKeyMap.getOrPut(this) { Key(this) }.withLocale(locale)

public fun String.toKey(bundle: Bundle): Key =
	translationKeyMap.getOrPut(this) { Key(this) }.withBundle(bundle)

public fun String.toKey(bundle: String): Key =
	toKey(Bundle(bundle))
