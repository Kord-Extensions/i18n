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
import java.io.File
import java.util.*

public val DELIMITERS: Array<String> = arrayOf("_", "-", ".")
public val MESSAGE_FORMAT_VERSIONS: Array<Int> = arrayOf(1, 2)

/**
 * Representation of a generated translations object.
 *
 * Usually, you can create an instance of this class and then immediately call [writeTo].
 * Nothing else needs to be done for most use-cases.
 *
 * @param allProps Properties object containing your bundle's default translations, typically loaded from a properties
 *                 file without a locale in its filename.
 *
 * @param bundle Name for your translation bundle, representing its location in your resources under `translations`.
 *               For example, `core.strings` represents translations in `translations/core`, with the filenames
 *               starting with `string.`.
 *
 * @param className Name given to the generated translations object. Defaults to `Translations`.
 * @param classPackage Package to place the generated translations object in.
 *
 * @param publicVisibility Whether to use `public` (`true`) or `internal` (`false`) visibility modifiers in the
 *                         generated code.
 *                         Defaults to (`true`).
 *
 * @param splitToCamelCase Whether to replace common delimiters in generated names
 */
public class TranslationsClass(
	public val allProps: Properties,
	public val bundle: String,

	public val className: String = "Translations",
	public val publicVisibility: Boolean = true,

	@Deprecated("This option is provided for compatibility with old code, and will be removed in a future version.")
	public val splitToCamelCase: Boolean = true,

	public val classPackage: String,
	public val messageFormatVersion: Int = 1,
) {
	init {
		@Suppress("DEPRECATION")
		if (!splitToCamelCase) {
			System.err.println("")

			System.err.println(
				"WARNING: Configured to replace delimiters with underscores instead of converting names to " +
					"camel-case. This option will be removed in a future version."
			)

			System.err.println("")
		}

		if (messageFormatVersion !in MESSAGE_FORMAT_VERSIONS) {
			error(
				"Invalid message format version $messageFormatVersion - " +
					"must be one of ${MESSAGE_FORMAT_VERSIONS.joinToString()}"
			)
		}
	}

	/** KModifier represented by [publicVisibility]. **/
	public val visibility: KModifier = if (publicVisibility) {
		KModifier.PUBLIC
	} else {
		KModifier.INTERNAL
	}

	/** Flat list containing all translation keys in [allProps]. **/
	public val allKeys: List<String> = allProps.toList().map { (left, _) -> left.toString() }

	/**
	 * KotlinPoet [FileSpec] representing the file being generated.
	 *
	 * The [TranslationsClass] fills this automatically, and it is complete as soon as you've created one.
	 */
	public val spec: FileSpec = buildFileSpec(classPackage, className) {
		if (messageFormatVersion != 1) {
			this.addImport("dev.kordex.core.i18n.types", "MessageFormatVersion")
		}

		types.addObject(className) {
			addModifiers(visibility)
			bundle()

			addKeys(allKeys, allProps, className)
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
		spec.writeTo(outputDir)
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
		props: Properties,
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

			if (v.isEmpty() || props[keyName] != null) {
				properties.add(
					key(k.toVarName(), keyName, props.getProperty(keyName), translationsClassName)
				)
			}

			if (v.isNotEmpty()) {
				// Object
				types.addObject(k.toClassName()) {
					addModifiers(visibility)
					addKeys(v, props, translationsClassName, keyName)
				}
			}
		}
	}

	public fun TypeSpecBuilder.bundle() {
		properties.add(
			buildPropertySpec("bundle", ClassName("dev.kordex.core.i18n.types", "Bundle")) {
				addModifiers(visibility)

				if (messageFormatVersion != 1) {
					setInitializer("Bundle(%S, %L)", bundle, messageFormatVersion.toMessageFormatEnum())
				} else {
					setInitializer("Bundle(%S)", bundle)
				}
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
		if (splitToCamelCase) {
			toClassName()
				.replaceFirstChar { it.lowercase() }
		} else {
			it.replace("-", "_")
				.replace(".", "_")
				.replaceFirstChar { it.lowercase() }
		}
	}

	@Suppress("DEPRECATION", "SpreadOperator")
	public fun String.toClassName(): String =
		let {
			if (splitToCamelCase) {
				it.split(*DELIMITERS).joinToString("") { it.capitalized() }
			} else {
				it.replace("-", " ")
					.split(" ")
					.joinToString("") { it.capitalized() }
			}
		}

	public fun Int.toMessageFormatEnum(): String = when (this) {
		1 -> "MessageFormatVersion.ONE"
		2 -> "MessageFormatVersion.TWO"

		else -> error(
			"Invalid message format version $this - " +
				"must be one of ${MESSAGE_FORMAT_VERSIONS.joinToString()}"
		)
	}
}
