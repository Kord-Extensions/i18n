/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("DEPRECATION")

package tests.files

import dev.kordex.i18n.files.PropertiesFormat
import fixtures.TestConstants
import io.kotest.core.spec.style.FunSpec
import java.util.Locale
import java.util.ResourceBundle

class PropertiesTests : FunSpec({
	test("loads base file") {
		val englishResourceBundle = ResourceBundle.getBundle(
			TestConstants.prefixedBundle,
			Locale("en"),
			PropertiesFormat.control
		)

		val germanResourceBundle = ResourceBundle.getBundle(
			TestConstants.prefixedBundle,
			Locale("de"),
			PropertiesFormat.control
		)

		var translation = englishResourceBundle.getString("command.banana")

		assert(translation == "banana") {
			"Incorrect English translation retrieved - expected 'banana', got '$translation'"
		}

		translation = germanResourceBundle.getString("command.banana")

		assert(translation == "banane") {
			"Incorrect German translation retrieved - expected 'banane', got '$translation'"
		}
	}
})
