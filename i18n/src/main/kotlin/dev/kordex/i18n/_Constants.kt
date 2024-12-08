/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n

/** Empty Key object, which always points to the empty string. **/
public val EMPTY_KEY: Key = "".toKey()

/** Default bundle name suffix. **/
public const val DEFAULT_BUNDLE_SUFFIX: String = "strings"

/** String used to denote an empty translation value - `∅∅∅` (`\u2205\u2205\u2205`). **/
public const val EMPTY_VALUE_STRING: String = "∅∅∅"
