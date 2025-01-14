plugins {
	`kotlin-dsl`
	general

	val env = System.getenv()

	if (env.contains("GITHUB_ACTIONS") && !env.contains("NO_SIGNING")) {
		`signed-plugin`
	}

	id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
	google()
	gradlePluginPortal()
	mavenCentral()
}

gradlePlugin {
	website = "https://docs.kordex.dev/.html"
	vcsUrl = "https://github.com/Kord-Extensions/i18n"

	plugins {
		create("kordex-i18n") {
			description = "Gradle plugin which generates files based on your translation bundles, " +
				"for use with the Kord Extensions i18n framework."

			displayName = "KordEx: i18n"

			tags = setOf(
				"kordEx",
				"build",
				"kotlin",
				"api",
				"generator",
				"i18n", "l10n",
				"internationalization", "localization"
			)

			id = "dev.kordex.gradle.i18n"
			implementationClass = "dev.kordex.gradle.plugins.i18n.I18nPlugin"
		}
	}
}

dependencies {
	compileOnly(kotlin("gradle-plugin", version = "2.0.20"))

	compileOnly(gradleApi())
	compileOnly(localGroovy())

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
	detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.6")

	implementation(platform("io.ktor:ktor-bom:2.3.12"))

	implementation("com.hanggrian:kotlinpoet-dsl:0.2")
	implementation("com.jcabi:jcabi-manifests:2.1.0")
	implementation("com.squareup:kotlinpoet:1.18.1")

	api(libs.bundles.ktlint)
	api(project(":i18n-generator"))
}
