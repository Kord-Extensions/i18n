import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.dokka.gradle.formats.DokkaJavadocPlugin

plugins {
	id("org.jetbrains.dokka") version "2.0.0"
}

subprojects {
	apply(plugin = "org.jetbrains.dokka")

	task("javadocJar", Jar::class) {
		dependsOn(tasks.dokkaGeneratePublicationHtml)
		from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
		archiveClassifier = "javadoc"
	}
}
