import org.gradle.kotlin.dsl.invoke

plugins {
	application
    general
    published

	id("com.github.johnrengelman.shadow") version "8.1.1"
}

val projectVersion: String by project

group = "dev.kordex.i18n"
version = projectVersion

application {
	mainClass.set("dev.kordex.i18n.generator.MainKt")
}

tasks.jar {
	manifest {
		attributes(
			"Main-Class" to "dev.kordex.i18n.generator.MainKt"
		)
	}
}

metadata {
	name = "KordEx: i18n Class Generator"
	description = "API and CLI for generating translation classes from properties files"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.6")

    api("com.hanggrian:kotlinpoet-dsl:0.2")
    api("com.squareup:kotlinpoet:2.0.0")
}

val propsTask = tasks.register<WriteProperties>("kordExProps") {
	group = "generation"
	description = "Generate KordEx properties file"

	comment = "Generated during KordEx compilation"
	destinationFile = layout.buildDirectory.file("kordex-build.properties")
	encoding = "UTF-8"

	property("version", project.version)
}

tasks.processResources {
	from(propsTask) {
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
	}
}

tasks.build {
	dependsOn(tasks.shadowJar)
}
