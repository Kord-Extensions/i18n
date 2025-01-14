pluginManagement {
	val pluginVersion: String by settings

	plugins {
		kotlin("jvm") version "2.0.21"

		id("dev.kordex.gradle.i18n") version pluginVersion
	}

    repositories {
        google()
        gradlePluginPortal()

	    maven("https://releases-repo.kordex.dev")
	    maven("https://snapshots-repo.kordex.dev")

	    mavenCentral()
        mavenLocal()
    }
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        mavenLocal()
    }
}

rootProject.name = "testModule"
