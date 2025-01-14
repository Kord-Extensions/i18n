/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.i18n

import dev.kordex.i18n.generator.TranslationsClass
import dev.kordex.i18n.registries.FileFormatRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.provider.MissingValueException
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.invoke
import java.net.URLClassLoader
import java.nio.charset.MalformedInputException
import java.util.*
import javax.inject.Inject

@Suppress("UnstableApiUsage")
public class I18nPlugin @Inject constructor(private val problems: Problems) : Plugin<Project> {
	override fun apply(target: Project) {
		val extension = target.extensions.create<I18nExtension>("i18n")

		target.afterEvaluate {
			if (!extension.isReady()) {
				return@afterEvaluate
			}

			extension.setup(target)

			var bundle = extension.translationBundle.get()

			if (bundle.split(".").size == 1) {
				bundle = bundle + "strings"
			}

			val fileFormatObj = extension.fileFormat.get()
			val loader = URLClassLoader(arrayOf(extension.basePath.get().toURI().toURL()))

			FileFormatRegistry.register(extension.fileFormat.get())

			@Suppress("DEPRECATION")
			val resourceBundle = try {
				ResourceBundle.getBundle(
					bundle.replace(".", "/"),
					Locale("dummy"),
					loader,
					fileFormatObj.control,
				)
			} catch (e: MissingResourceException) {
				problems.reporter.throwing {
					withException(e)

					id("dev.kordex.gradle.plugins.i18n.missing-bundle", "Can't find given bundle")
					details("Couldn't find bundle $bundle in ${extension.basePath.get()}")
					severity(Severity.ERROR)

					solution(
						"Check that `basePath` refers to your `resources/translations` directory, that a file " +
							"starting with ${bundle.replace(".", "/")} exists, and that the file ends with one of " +
							"these extensions: ${extension.fileFormat.get().identifiers.joinToString()}"
					)
				}

				return@afterEvaluate
			}

			val sourceSet = target.extensions.getByType<SourceSetContainer>().named("main")

			val outputDirectory = extension.outputDirectory.orNull
				?: target.layout.buildDirectory.file("generated/kordex/main/kotlin/").get().asFile

			val generateTask = target.tasks.create("generateTranslationsClass") {
				group = "generation"
				description = "Generate classes containing translation keys."

				inputs.dir(extension.basePath.get())

				doLast {
					try {
						@Suppress("DEPRECATION")
						val translationsClass = TranslationsClass(
							bundle = bundle,
							className = extension.className.get(),
							classPackage = extension.classPackage.get(),
							editorConfig = extension.editorConfig.orNull?.toPath(),
							fileFormat = extension.fileFormat.get(),
							messageFormat = extension.messageFormat.get(),
							publicVisibility = extension.publicVisibility.get(),
							resourceBundle = resourceBundle
						)

						translationsClass.writeTo(outputDirectory)
					} catch (e: MalformedInputException) {
						problems.reporter.throwing {
							withException(e)

							id("dev.kordex.gradle.plugins.i18n.malformed-input", "Malformed translation data")
							details("Malformed data detected in bundle $bundle, in ${extension.basePath.get()}")
							severity(Severity.ERROR)

							solution(
								"Check that your translation bundle is UTF-8 encoded, rather than ISO-8859-1. " +
									"This may require changing your IDE settings."
							)
						}

						return@doLast
					}
				}
			}

			target.tasks.getByName("compileKotlin") {
				dependsOn(generateTask)
			}

			if (extension.configureSourceSet.get()) {
				sourceSet {
					java {
						srcDir(outputDirectory)
					}

					output.dir(
						mapOf("builtBy" to generateTask),
						extension.basePath.get()
					)
				}
			}
		}
	}

	internal fun I18nExtension.isReady(): Boolean {
		try {
			classPackage.get()
			translationBundle.get()
		} catch (_: MissingValueException) {
			return false
		}

		return true
	}
}
