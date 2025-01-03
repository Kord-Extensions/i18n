/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fixtures

@Suppress("ConstPropertyName")
object TestConstants {
	const val bundle = "test.strings"
	const val prefixedBundle = "translations.$bundle"

	val baseName = bundle.replace(".", "/")
	val prefixedBaseName = prefixedBundle.replace(".", "/")
}
