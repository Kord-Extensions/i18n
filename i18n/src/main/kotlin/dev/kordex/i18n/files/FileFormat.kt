/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.files

import java.util.ResourceBundle

public interface FileFormat {
	public val identifiers: Set<String>
	public val control: ResourceBundle.Control
}
