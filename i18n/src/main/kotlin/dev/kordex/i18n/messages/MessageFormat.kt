/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.messages

import java.util.Locale

public interface MessageFormat {
	public val identifier: String

	public fun formatNamed(string: String, locale: Locale, placeholders: Map<String, Any?>): String
	public fun formatOrdinal(string: String, locale: Locale, placeholders: Array<Any?>): String
}
