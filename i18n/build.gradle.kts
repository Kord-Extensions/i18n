plugins {
    general
    published

	kotlin("plugin.serialization")
}

val projectVersion: String by project

group = "dev.kordex.i18n"
version = projectVersion

metadata {
	name = "KordEx: i18n Framework"
	description = "The modular internationalisation framework"
}

repositories {
    mavenCentral()
}

dependencies {
	implementation(libs.bundles.logging)

	api(libs.bundles.resources)
	api(libs.icu4j)
	api(libs.kx.ser)

	testImplementation(libs.bundles.testing)
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}
