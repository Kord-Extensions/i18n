plugins {
	id("dev.yumi.gradle.licenser")
	id("io.gitlab.arturbosch.detekt")

	kotlin("jvm")
	kotlin("plugin.serialization")
}

repositories {
	mavenCentral()
}

detekt {
	buildUponDefaultConfig = true
	config.from(rootProject.file("detekt.yml"))

	autoCorrect = true
}

dependencies {
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.6")
}

license {
	rule(rootProject.file("codeformat/HEADER"))
}

kotlin {
	explicitApi()
}
