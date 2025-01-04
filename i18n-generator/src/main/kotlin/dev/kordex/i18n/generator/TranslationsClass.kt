/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("StringLiteralDuplication")

package dev.kordex.i18n.generator

import com.hanggrian.kotlinpoet.TypeSpecBuilder
import com.hanggrian.kotlinpoet.addObject
import com.hanggrian.kotlinpoet.buildFileSpec
import com.hanggrian.kotlinpoet.buildPropertySpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import dev.kordex.i18n.files.FileFormat
import dev.kordex.i18n.files.PropertiesFormat
import dev.kordex.i18n.messages.formats.ICUFormatV1
import java.io.File
import java.nio.file.Path
import java.util.*

public val DELIMITERS: Array<String> = arrayOf("_", "-", ".")
public val MESSAGE_FORMAT_VERSIONS: Array<Int> = arrayOf(1, 2)

/**
 * Representation of a generated translations object.
 * Handles code generation and formatting based on the given parameters.
 *
 * Usually, you can create an instance of this class and then immediately call [writeTo].
 * Nothing else needs to be done for most use-cases.
 *
 * @param bundle Name for your translation bundle, representing its location in your resources under `translations`.
 *               For example, `core.strings` represents translations in `translations/core`, with the filenames
 *               starting with `string.`.
 *
 * @param className Name given to the generated translations object. Defaults to `Translations`.
 * @param classPackage Package to place the generated translations object in.
 *
 * @param editorConfig An optional path to an editorconfig file, used to autoformat the generated code.
 *                     Set to `null` to use the default formatting settings instead.
 *                     Defaults to `.editorconfig` in the current working directory.
 *
 * @param fileFormat FileFormat object representing the file format required to load your bundle's files.
 *                   Defaults to Java Properties format.
 *
 * @para messageFormat Message format identifier, representing the message format your bundle uses.
 *                     Defaults to ICU Message Format version 1.
 *
 * @param publicVisibility Whether to use `public` (`true`) or `internal` (`false`) visibility modifiers in the
 *                         generated code.
 *                         Defaults to (`true`).
 *
 * @param resourceBundle Resource bundle object containing your bundle's base translations.
 */
public class TranslationsClass(
	/** Constructor parameter, see [TranslationsClass]. **/
	public val bundle: String,

	/** Constructor parameter, see [TranslationsClass]. **/
	public val className: String = "Translations",

	/** Constructor parameter, see [TranslationsClass]. **/
	public val classPackage: String,

	/** Constructor parameter, see [TranslationsClass]. **/
	public val editorConfig: Path? = Path.of(".editorconfig"),

	/** Constructor parameter, see [TranslationsClass]. **/
	public val fileFormat: FileFormat = PropertiesFormat,

	/** Constructor parameter, see [TranslationsClass]. **/
	public val messageFormat: String = ICUFormatV1.identifier,

	/** Constructor parameter, see [TranslationsClass]. **/
	public val publicVisibility: Boolean = true,

	/** Constructor parameter, see [TranslationsClass]. **/
	public val resourceBundle: ResourceBundle,
) {

	/** KModifier represented by [publicVisibility]. **/
	public val visibility: KModifier = if (publicVisibility) {
		KModifier.PUBLIC
	} else {
		KModifier.INTERNAL
	}

	/** Flat list containing all translation keys in [resourceBundle]. **/
	public val allKeys: List<String> = resourceBundle.keys.toList()

	/**
	 * KotlinPoet [FileSpec] representing the file being generated.
	 *
	 * The [TranslationsClass] fills this automatically, and it is complete as soon as you've created one.
	 */
	public val spec: FileSpec = buildFileSpec(classPackage, className) {
		types.addObject(className) {
			addModifiers(visibility)
			bundle()

			addKeys(allKeys, className)
		}
	}

	/**
	 * Write the generated translations object to an output directory, generating interim directories for the
	 * [classPackage] as necessary.
	 *
	 * You can usually call this immediately after creating your [TranslationsClass], unless you need to mess with the
	 * [spec] first.
	 *
	 * @param outputDir [File] representing the output directory.
	 */
	public fun writeTo(outputDir: File) {
		val buffer = StringBuffer()

		spec.writeTo(buffer)

		val code = buffer.toString().formatCode(editorConfig)

		val parentPath = File(outputDir, classPackage.replace('.', File.separatorChar))
		val outputFile = File(parentPath, "$className.kt")

		parentPath.mkdirs()
		outputFile.writeText(code)
	}

	/**
	 * Generate a KotlinPoet [PropertySpec] representing a single translation key object.
	 *
	 * @param varName Variable name, normalised automatically.
	 * @param keyName [String] representing the current translation key.
	 * @param keyValue [String] representing the default translation for the current key.
	 * @param translationsClassName [String] representing the name of the containing translations object.
	 */
	public fun key(varName: String, keyName: String, keyValue: String, translationsClassName: String): PropertySpec =
		buildPropertySpec(varName, ClassName("dev.kordex.core.i18n.types", "Key")) {
			addModifiers(visibility)
			setInitializer("Key(%S)\n.withBundle(%L.bundle)", keyName, translationsClassName)

			keyValue.lines().forEach {
				kdoc.addStatement(
					"%L",
					it.trim().replace("*/", "")
				)
			}
		}

	public fun TypeSpecBuilder.addKeys(
		keys: List<String>,
		translationsClassName: String,
		parent: String? = null,
	) {
		val partitioned = keys.sorted().partition()

		partitioned.forEach { (k, v) ->
			val keyName = if (parent != null) {
				"$parent.$k"
			} else {
				k
			}

			if (v.isEmpty() || resourceBundle.getStringOrNull(keyName) != null) {
				properties.add(
					key(k.toVarName(), keyName, resourceBundle.getString(keyName), translationsClassName)
				)
			}

			if (v.isNotEmpty()) {
				// Object
				types.addObject(k.toClassName()) {
					addModifiers(visibility)
					addKeys(v, translationsClassName, keyName)
				}
			}
		}
	}

	public fun TypeSpecBuilder.bundle() {
		properties.add(
			buildPropertySpec("bundle", ClassName("dev.kordex.core.i18n.types", "Bundle")) {
				addModifiers(visibility)

				setInitializer(
					"Bundle(\nname = %S, \nfileFormat = %S,\nmessageFormat = %S\n)",

					bundle, fileFormat.identifiers.first(), messageFormat
				)
			}
		)
	}

	public fun List<String>.partition(): Map<String, List<String>> =
		filterNotNull()
			.groupBy(
				keySelector = { it.substringBefore(".") },

				valueTransform = {
					if ("." in it) {
						it.substringAfter(".")
					} else {
						null
					}
				},
			)
			.mapValues { (_, value) ->
				value.filterNotNull()
			}

	public fun String.capitalized(): String =
		replaceFirstChar { it.uppercase() }

	@Suppress("DEPRECATION")
	public fun String.toVarName(): String = let {
		toClassName()
			.replaceFirstChar { it.lowercase() }
	}

	@Suppress("DEPRECATION", "SpreadOperator")
	public fun String.toClassName(): String =
		split(*DELIMITERS).joinToString("") { it.capitalized() }

	public fun Int.toMessageFormatEnum(): String = when (this) {
		1 -> "MessageFormatVersion.ONE"
		2 -> "MessageFormatVersion.TWO"

		else -> error(
			"Invalid message format version $this - " +
				"must be one of ${MESSAGE_FORMAT_VERSIONS.joinToString()}"
		)
	}

	public fun ResourceBundle.getStringOrNull(key: String): String? {
		val result = try {
			getString(key)
		} catch (_: MissingResourceException) {
			return null
		}

		if (result == key) {
			return null
		}

		return result
	}
}
