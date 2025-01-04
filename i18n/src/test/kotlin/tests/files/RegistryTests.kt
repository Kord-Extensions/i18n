/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package tests.files

import dev.kordex.i18n.files.FileFormat
import dev.kordex.i18n.files.PropertiesFormat
import dev.kordex.i18n.files.YamlFormat
import dev.kordex.i18n.registries.FileFormatRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.mpp.log
import org.junit.jupiter.api.assertThrows
import java.util.*

class RegistryTests : FunSpec({
	val customFormat = object : FileFormat {
		override val identifiers: Set<String> = setOf("test")
		override val control: ResourceBundle.Control = PropertiesFormat.control
	}

	beforeTest {
		if (it.name.originalName == "functional registration") {
			log { "Registering file format with identifiers: ${customFormat.identifiers.joinToString()}" }

			FileFormatRegistry.register(customFormat)
		}
	}

	afterTest {
		if (it.a.name.originalName == "functional registration") {
			log { "Unregistering file format with identifiers: ${customFormat.identifiers.joinToString()}" }

			FileFormatRegistry.unregister(customFormat)
		}
	}

	test("returns registered controls") {
		val propertiesControl = FileFormatRegistry.get("properties")
		val yamlControlShort = FileFormatRegistry.get("yml")
		val yamlControlLong = FileFormatRegistry.get("yaml")

		assert(propertiesControl == PropertiesFormat) {
			"Incorrect control returned - expected `PropertiesFormat`, got $propertiesControl"
		}

		assert(yamlControlShort == YamlFormat) {
			"Incorrect control returned - expected `YamlFormat`, got $propertiesControl"
		}

		assert(yamlControlLong == YamlFormat) {
			"Incorrect control returned - expected `YamlFormat`, got $propertiesControl"
		}
	}

	test("returns null on unregistered control by default") {
		val badControl = FileFormatRegistry.get("doesNotExist")

		assert(badControl == null) {
			"Incorrect control returned - expected `null`, got $badControl"
		}
	}

	test("throws on unregistered control when requested to") {
		assertThrows<IllegalStateException>("Expected a thrown IllegalStateException") {
			FileFormatRegistry.getOrError("doesNotExist")
		}
	}

	test("functional registration") {
		val propertiesControl = FileFormatRegistry.get("test")

		assert(propertiesControl == customFormat) {
			"Incorrect control returned - expected `customFormat`, got $propertiesControl"
		}
	}
})
