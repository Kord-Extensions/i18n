/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.generator

import java.util.Properties

/** Convenient access to the kordex-build.properties file. **/
public val kordexProps: Properties by lazy {
	val props = Properties()

	props.load(
		TranslationsClass::class.java.getResourceAsStream(
			"/kordex-build.properties"
		)
	)

	props
}

/** Current Kord Extensions version. **/
public val VERSION: String? by lazy {
	kordexProps["versions"] as String?
}
