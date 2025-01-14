/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.generator

import dev.kordex.i18n.files.FileFormat
import dev.kordex.i18n.files.PropertiesFormat
import dev.kordex.i18n.messages.MessageFormat
import dev.kordex.i18n.messages.formats.ICUFormatV1
import dev.kordex.i18n.registries.FileFormatRegistry
import dev.kordex.i18n.registries.MessageFormatRegistry
import picocli.CommandLine
import picocli.CommandLine.Model.OptionSpec
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.system.exitProcess

public val extraFileFormats: List<String> = (
	System.getProperties()["fileFormats"]
		?: System.getenv()["FILE_FORMATS"]
	)
	?.toString()
	?.split(',')
	?: emptyList()

public val extraMessageFormats: List<String> = (
	System.getProperties()["messageFormats"]
		?: System.getenv()["MESSAGE_FORMATS"]
	)
	?.toString()
	?.split(',')
	?: emptyList()

public fun main(vararg args: String) {
	if (extraFileFormats.isNotEmpty()) {
		println("Loading ${extraFileFormats.size} extra file format/s...")

		extraFileFormats.forEach { specifier ->
			try {
				@Suppress("UNCHECKED_CAST")
				val clazz = Class.forName(specifier).kotlin as KClass<out FileFormat>

				val obj = clazz.objectInstance
					?: clazz.createInstance()

				FileFormatRegistry.register(obj)

				println("\t$specifier -> Loaded successfully")
			} catch (_: ClassNotFoundException) {
				println("\t$specifier -> Failed: Type not found, perhaps try appending 'Kt'")
			} catch (_: ClassCastException) {
				println("\t$specifier -> Failed: Type doesn't extend FileFormat")
			}
		}

		println()
	}

	if (extraMessageFormats.isNotEmpty()) {
		println("Loading ${extraMessageFormats.size} extra message format/s...")

		extraMessageFormats.forEach { specifier ->
			try {
				@Suppress("UNCHECKED_CAST")
				val clazz = Class.forName(specifier).kotlin as KClass<out MessageFormat>

				val obj = clazz.objectInstance
					?: clazz.createInstance()

				MessageFormatRegistry.register(obj)

				println("\t$specifier -> Loaded successfully")
			} catch (_: ClassNotFoundException) {
				println("\t$specifier -> Failed: Type not found, perhaps try appending 'Kt'")
			} catch (_: ClassCastException) {
				println("\t$specifier -> Failed: Type doesn't extend FileFormat")
			}
		}

		println()
	}

	val spec = CommandLine.Model.CommandSpec.create()

	spec.name("i18n-generator")
	spec.version(VERSION)

	spec.parser()
		.abbreviatedOptionsAllowed(true)
		.allowOptionsAsOptionParameters(true)

	spec.usageMessage()
		.description(
			"%nCommand-line tool for generating translations classes from translation bundle files.%n",

			"Use '-DfileFormats' or the 'FILE_FORMATS' environmental variable to specify third-party file formats, " +
				"represented by a comma-delimited list of fully-qualified names.%n",
			"Use '-DmessageFormats' or the 'MESSAGE_FORMATS' environmental variable to specify third-party message " +
				"formats, represented by a comma-delimited list of fully-qualified names.%n",

			"All specified file formats must implement the FileFormat type, and message formats must implement the " +
				"MessageFormat type. For more information, please see the documentation.%n",

			"Lists of available file formats and message formats can be found at the bottom of this " +
				"help message. %n"
		)
		.footer(
			"%nAvailable file formats: " + FileFormatRegistry.getFormats().sorted().joinToString(),
			"Available message formats: " + MessageFormatRegistry.getFormats().sorted().joinToString()
		)

	spec.version(null)
	spec.mixinStandardHelpOptions(true)

	spec.addOption<String>("-b", "--bundle") {
		paramLabel("BUNDLE")
		required(true)

		description("Name of the relevant translations bundle, which will be included in the output.")
	}

	spec.addOption<File>("-i", "--input-path") {
		paramLabel("INPUT")
		required(true)

		description(
			"Input path, pointing to the directory containing your translation bundle, " +
				"relative to the bundle you specified. For example, if your bundle is 'kordex.strings' and you're " +
				"using the 'properties' file format, you should provide the path to a directory containing " +
				"'kordex/strings.properties'.",
			"",
			"In most situations, this should be the 'translations' directory in your project's " +
				"'src/main/resources' directory."
		)
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

	spec.addOption<String>("-f", "--file-format") {
		paramLabel("FILE-FORMAT")
		defaultValue("properties")

		description(
			"Translations file format identifier. Defaults to '${PropertiesFormat.identifiers.first()}'."
		)
	}

	spec.addOption<String>("-m", "--message-format") {
		paramLabel("MESSAGE-FORMAT")
		defaultValue(ICUFormatV1.identifier)

		description(
			"Message format identifier. Defaults to ${ICUFormatV1.identifier}."
		)
	}

	spec.addOption<File>("--editorconfig") {
		paramLabel("PATH")
		defaultValue(".editorconfig")

		description("Path to a .editorconfig file to use when formatting generated code. Defaults to '.editorconfig'.")
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
	val inputPath: File = result.matchedOption("i").getValue()
	val classPackage: String = result.matchedOption("p").getValue()
	val internal: Boolean = result.matchedOptionValue("in", false)
	val fileFormat: String = result.matchedOptionValue("f", PropertiesFormat.identifiers.first())
	val messageFormat: String = result.matchedOptionValue("m", ICUFormatV1.identifier)
	val editorConfig: File = result.matchedOptionValue("editorconfig", File(".editorconfig"))

	val className: String = result.matchedOptionValue("c", "Translations")
	val outputDir: File = result.matchedOptionValue("o", File("output"))

	if (!inputPath.exists()) {
		exitError("Unable to find ${inputPath.absolutePath}")
	}

	if (!outputDir.exists()) {
		println("Creating output directory: ${outputDir.absolutePath}")
		outputDir.mkdirs()
	}

	println("Loading translations...")

	val fileFormatObj = FileFormatRegistry.getOrError(fileFormat)
	val loader = URLClassLoader(arrayOf(inputPath.toURI().toURL()))

	@Suppress("DEPRECATION")
	val resourceBundle = ResourceBundle.getBundle(
		bundle.replace(".", "/"),
		Locale("dummy"),
		loader,
		fileFormatObj.control,
	)

	println("Found ${resourceBundle.keys.toList().size} translation keys.")
	println("Generating class \"$className\" for bundle \"$bundle\"...")

	val translationsClass = TranslationsClass(
		resourceBundle = resourceBundle,
		bundle = bundle,
		className = className,
		publicVisibility = !internal,
		classPackage = classPackage,
		fileFormat = FileFormatRegistry.getOrError(fileFormat),
		messageFormat = messageFormat,
		editorConfig = Path.of(editorConfig.toURI())
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
