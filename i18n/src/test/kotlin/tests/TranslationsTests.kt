/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package tests

import dev.kordex.i18n.Bundle
import dev.kordex.i18n.I18n
import dev.kordex.i18n.Key
import fixtures.TestTranslations
import io.kotest.core.spec.style.FunSpec
import java.util.*

class TranslationsTests : FunSpec({
	beforeTest {
		I18n.defaultBundle = Bundle("kordex.strings")
	}

	test("full stack test with generated objects") {
		val english = TestTranslations.Command.banana
			.withLocale(Locale.of("en"))
			.translate()

		val german = TestTranslations.Command.banana
			.withLocale(Locale.of("de"))
			.translate()

		assert(english == "banana") { "Incorrect English translation returned - expected 'banana', got '$english'" }
		assert(german == "banane") { "Incorrect German translation returned - expected 'banane', got '$german'" }
	}

	test("unknown translation fallback with generated objects") {
		val correctEnglish = "Command check did not pass."

		val english = TestTranslations.Check.simple
			.withLocale(Locale.of("en"))
			.translate()

		val german = TestTranslations.Check.simple
			.withLocale(Locale.of("de"))
			.translate()

		assert(english == "Command check did not pass.") {
			"Incorrect English translation returned - expected '$correctEnglish', got '$english'"
		}

		assert(german == "Command check did not pass.") {
			"Incorrect German translation returned - expected '$correctEnglish', got '$german'"
		}
	}

	test("unknown translation returns key with generated objects") {
		val baseKey = Key("wrong.key", TestTranslations.bundle)

		val english = baseKey
			.withLocale(Locale.of("en"))
			.translate()

		assert(english == baseKey.key) {
			"Incorrect English translation returned - expected '${baseKey.key}', got '$english'"
		}
	}
})
