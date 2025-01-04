/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.files

import dev.akkinoc.util.YamlResourceBundle
import java.util.ResourceBundle

public object YamlFormat : FileFormat {
	override val identifiers: Set<String> = setOf("yaml", "yml")
	override val control: ResourceBundle.Control = YamlResourceBundle.Control

	override fun toString(): String = "YamlFormat(${identifiers.joinToString(", ")})"
}
