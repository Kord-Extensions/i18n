plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
	}
}

rootProject.name = "i18n"

include("i18n")
include("i18n-generator")
include("i18n-gradle")
