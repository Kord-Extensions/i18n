plugins {
	id("org.jetbrains.dokka") version "2.0.0"
}

val projectVersion: String by project

subprojects {
	apply(plugin = "org.jetbrains.dokka")
	this.version = projectVersion
}
