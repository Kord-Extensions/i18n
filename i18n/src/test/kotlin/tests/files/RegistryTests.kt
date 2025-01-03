package tests.files

import dev.akkinoc.util.YamlResourceBundle
import dev.kordex.i18n.files.PropertiesControl
import dev.kordex.i18n.registries.FileFormatRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.mpp.log
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

class RegistryTests : FunSpec({
	beforeTest {
		if (it.name.originalName == "functional registration") {
			log { "Registering file format with identifier 'test'" }

			FileFormatRegistry.register("test", PropertiesControl)
		}
	}

	afterTest {
		if (it.a.name.originalName == "functional registration") {
			log { "Unregistering file format with identifier 'test'" }

			FileFormatRegistry.unregister("test")
		}
	}

	test("returns registered controls") {
		val propertiesControl = FileFormatRegistry.get("properties")
		val yamlControlShort = FileFormatRegistry.get("yml")
		val yamlControlLong = FileFormatRegistry.get("yaml")

		assert(propertiesControl == PropertiesControl) {
			"Incorrect control returned - expected `PropertiesControl`, got $propertiesControl"
		}

		assert(yamlControlShort == YamlResourceBundle.Control) {
			"Incorrect control returned - expected `YamlResourceBundle.Control`, got $propertiesControl"
		}

		assert(yamlControlLong == YamlResourceBundle.Control) {
			"Incorrect control returned - expected `YamlResourceBundle.Control`, got $propertiesControl"
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

		assert(propertiesControl == PropertiesControl) {
			"Incorrect control returned - expected `PropertiesControl`, got $propertiesControl"
		}
	}
})
