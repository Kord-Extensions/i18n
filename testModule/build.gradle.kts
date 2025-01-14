plugins {
	kotlin("jvm")

	id("dev.kordex.gradle.i18n")
}

val pluginVersion: String by project
version = "1.0.0"

repositories {
	maven("https://releases-repo.kordex.dev")
	maven("https://snapshots-repo.kordex.dev")

	mavenCentral()
	mavenLocal()
}

i18n {
	classPackage = "template.i18n"
	translationBundle = "template.strings"

	publicVisibility = false
}

dependencies {
	implementation("dev.kordex.i18n:i18n:$pluginVersion")
}
