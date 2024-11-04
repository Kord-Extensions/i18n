/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.generator

import picocli.CommandLine
import picocli.CommandLine.Model.OptionSpec
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.Properties
import kotlin.system.exitProcess

public fun main(vararg args: String) {
	val spec = CommandLine.Model.CommandSpec.create()

	spec.name("i18n-generator")
	spec.version(VERSION)

	spec.parser()
		.abbreviatedOptionsAllowed(true)
		.allowOptionsAsOptionParameters(true)

	spec.usageMessage()
		.description(
			"%nCommand-line tool for generating Kord Extensions translations classes from translation bundle " +
				"properties files.%n"
		)
		.footer("%nAvailable encodings: " + Charset.availableCharsets().keys.joinToString())

	spec.version(null)
	spec.mixinStandardHelpOptions(true)

	spec.addOption<String>("-b", "--bundle") {
		paramLabel("BUNDLE")
		required(true)

		description("Name of the relevant translations bundle, which will be included in the output.")
	}

	spec.addOption<File>("-i", "--input-file") {
		paramLabel("INPUT")
		required(true)

		description("Input file, a properties file representing a set of translations from a translation bundle.")
	}

	spec.addOption<String>("-p", "--class-package") {
		paramLabel("PACKAGE")
		required(true)

		description("Package containing the generated class.")
	}

	spec.addOption<String>("-c", "--class-name") {
		paramLabel("CLASS")
		defaultValue("Translations")

		description("Generated class name. Defaults to \"Translations\".")
	}

	spec.addOption<String>("-e", "--encoding") {
		paramLabel("ENCODING")
		defaultValue("UTF-8")

		Charset.availableCharsets().keys

		description("Character encoding used to load the bundle file. Defaults to UTF-8.")
	}

	spec.addOption<File>("-o", "--output-dir") {
		paramLabel("DIRECTORY")
		defaultValue("output")

		description(
			"Output directory for the generated files, which will include the package structure.%n  " +
				"Defaults to \"./output\"."
		)
	}

	spec.addOption<Boolean>("-in", "--internal") {
		paramLabel("INTERNAL")
		defaultValue("false")

		description(
			"Generate objects with internal visibility, rather than public."
		)
	}

	spec.addOption<Boolean>("-ncc", "--no-camel-case") {
		paramLabel("CAMEL CASE")
		defaultValue("false")

		description(
			"Replace common delimiters in names with underscores instead of camel-casing them. " +
				"This option is provided for compatibility, and will be removed in the future.."
		)
	}

	val commandLine = CommandLine(spec)

	commandLine.setExecutionStrategy(::run)

	exitProcess(commandLine.execute(*args))
}

private fun run(result: CommandLine.ParseResult): Int {
	val helpExitCode = CommandLine.executeHelpRequest(result)

	if (helpExitCode != null) {
		return helpExitCode
	}

	var bundle: String = result.matchedOption("b").getValue()
	val inputFile: File = result.matchedOption("i").getValue()
	val classPackage: String = result.matchedOption("p").getValue()
	val encoding: String = result.matchedOptionValue("e", "UTF-8")
	val internal: Boolean = result.matchedOptionValue("in", false)
	val noCamelCase: Boolean = result.matchedOptionValue("ncc", false)

	val className: String = result.matchedOptionValue("c", "Translations")
	val outputDir: File = result.matchedOptionValue("o", File("output"))

	if ("." !in bundle) {
		bundle = "$bundle.strings"
	}

	if (!inputFile.exists()) {
		exitError("Unable to find ${inputFile.absolutePath}")
	}

	if (!outputDir.exists()) {
		println("Creating output directory: ${outputDir.absolutePath}")
		outputDir.mkdirs()
	}

	println("Loading properties...")

	val props = Properties()

	props.load(
		Files.newBufferedReader(
			inputFile.toPath(),
			charset(encoding)
		)
	)

	println("Found ${props.size} translation keys.")
	println("Generating class \"$className\" for bundle \"$bundle\"...")

	val translationsClass = TranslationsClass(
		allProps = props,
		bundle = bundle,
		className = className,
		publicVisibility = !internal,
		splitToCamelCase = !noCamelCase,
		classPackage = classPackage,
	)

	translationsClass.writeTo(outputDir)

	println("Written to directory: ${outputDir.absolutePath}")

	return 0
}

private fun exitError(message: String): Nothing {
	println("ERROR: $message")
	exitProcess(1)
}

public inline fun <reified T> CommandLine.Model.CommandSpec.addOption(
	name: String,
	vararg names: String,
	body: OptionSpec.Builder.() -> Unit
): OptionSpec {
	val builder = OptionSpec.builder(name, *names)

	body(builder)
	builder.type(T::class.java)

	val build = builder.build()

	addOption(build)

	return build
}
