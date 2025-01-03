/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package tests.files

import dev.kordex.i18n.files.PropertiesControl
import fixtures.TestConstants
import io.kotest.core.spec.style.FunSpec
import java.util.Locale
import java.util.ResourceBundle
import java.util.ResourceBundle.Control.FORMAT_PROPERTIES

class PropertiesTests : FunSpec({
	test("correct formats returned") {
		val formats = PropertiesControl.getFormats(TestConstants.prefixedBaseName)

		assert(formats == FORMAT_PROPERTIES) {
			"Incorrect formats returned - expected [${FORMAT_PROPERTIES.joinToString()}], " +
				"got [${formats.joinToString()}]"
		}
	}

	test("loads base file") {
		val englishResourceBundle = ResourceBundle.getBundle(
			TestConstants.prefixedBundle,
			Locale.of("en"),
			PropertiesControl
		)

		val germanResourceBundle = ResourceBundle.getBundle(
			TestConstants.prefixedBundle,
			Locale.of("de"),
			PropertiesControl
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
