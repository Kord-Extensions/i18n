/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.i18n

import dev.kordex.i18n.files.FileFormat
import dev.kordex.i18n.files.PropertiesFormat
import dev.kordex.i18n.messages.MessageFormat
import dev.kordex.i18n.messages.formats.ICUFormatV1
import org.gradle.api.Project
import org.gradle.api.internal.provider.PropertyFactory
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

public abstract class I18nExtension @Inject constructor(props: PropertyFactory) {
	internal val dummyFile = File(":dummy:")

	public abstract val classPackage: Property<String>
	public abstract val outputDirectory: Property<File>
	public abstract val translationBundle: Property<String>

	public val className: Property<String> = props.single("Translations")
	public val configureSourceSet: Property<Boolean> = props.boolean(true)
	public val basePath: Property<File> = props.single(dummyFile)
	public val editorConfig: Property<File> = props.single(dummyFile)
	public val fileFormat: Property<FileFormat> = props.single(PropertiesFormat)
	public val messageFormat: Property<String> = props.single(ICUFormatV1.identifier)
	public val publicVisibility: Property<Boolean> = props.boolean(true)

	public fun messageFormat(format: MessageFormat) {
		messageFormat.set(format.identifier)
	}

	public fun messageFormat(format: String) {
		messageFormat.set(format)
	}

	internal fun setup(target: Project) {
		if (basePath.get() == dummyFile) {
			basePath.set(target.file("src/main/resources/translations/"))
		}

		if (editorConfig.get() == dummyFile) {
			editorConfig.set(target.file(".editorconfig"))
		}
	}
}
