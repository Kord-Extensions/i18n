/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n

import java.util.Locale

public object I18n {
	public var defaultLocale: Locale = Locale.getDefault()
	public var defaultBundlePrefix: String = "translations"
	public var defaultBundleSuffix: String = "strings"

	public lateinit var defaultBundle: Bundle
}
