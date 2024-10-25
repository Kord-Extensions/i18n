/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
import java.util.Properties

public class TranslationsClass(
	public val allProps: Properties,
	public val bundle: String,
	public val className: String,
	public val publicVisibility: Boolean = true,

	classPackage: String
) {
	public val visibility: KModifier = if (publicVisibility) {
		KModifier.PUBLIC
	} else {
		KModifier.INTERNAL
	}
	public val allKeys: List<String> = allProps.toList().map { (left, _) -> left.toString() }

	public val spec: FileSpec = buildFileSpec(classPackage, className) {
		types.addObject(className) {
			addModifiers(visibility)
			bundle()

			addKeys(allKeys, allProps, className)
		}
	}

	public fun writeTo(outputDir: File) {
		spec.writeTo(outputDir)
	}

	public fun key(name: String, value: String, property: String, translationsClassName: String): PropertySpec =
		buildPropertySpec(name.replace("-", "_"), ClassName("dev.kordex.core.i18n.types", "Key")) {
			addModifiers(visibility)
			setInitializer("Key(%S)\n.withBundle(%L.bundle)", value, translationsClassName)

			property.lines().forEach {
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
		val paritioned = keys.partition()

		paritioned.forEach { (k, v) ->
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
				val objName = k
					.replace("-", " ")
					.split(" ")
					.map { it.capitalized() }
					.joinToString("")

				types.addObject(objName) {
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
				setInitializer("Bundle(%S)", bundle)
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

	public fun String.toVarName(): String =
		replace("-", "_")
			.replace(".", "_")
			.replaceFirstChar { it.lowercase() }
}
