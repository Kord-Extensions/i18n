import org.gradle.kotlin.dsl.invoke

plugins {
    val env = System.getenv()

    general

    if (env.contains("GITHUB_ACTIONS") && !env.contains("NO_SIGNING")) {
        published
    }

	id("com.github.johnrengelman.shadow") version "8.1.1"
}

val projectVersion: String by project

group = "dev.kordex.i18n"
version = projectVersion

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
    api("com.squareup:kotlinpoet:1.18.1")
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
