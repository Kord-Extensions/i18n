package fixtures

@Suppress("ConstPropertyName")
object TestConstants {
	const val bundle = "test.strings"
	const val prefixedBundle = "translations.$bundle"

	val baseName = bundle.replace(".", "/")
	val prefixedBaseName = prefixedBundle.replace(".", "/")
}
