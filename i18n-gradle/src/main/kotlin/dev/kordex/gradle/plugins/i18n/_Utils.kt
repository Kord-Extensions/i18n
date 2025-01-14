/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.gradle.plugins.i18n

import org.gradle.api.internal.provider.PropertyFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

internal inline fun <reified T> PropertyFactory.single(): Property<T> =
	property(T::class.java)

internal inline fun <reified T> PropertyFactory.list(): ListProperty<T> =
	listProperty(T::class.java)

internal fun PropertyFactory.boolean(): Property<Boolean> =
	property(Boolean::class.javaObjectType)

internal fun PropertyFactory.boolean(default: Boolean): Property<Boolean> =
	property(Boolean::class.javaObjectType).convention(default)

internal fun PropertyFactory.booleanList(): ListProperty<Boolean> =
	listProperty(Boolean::class.javaObjectType)

internal inline fun <reified T> PropertyFactory.single(default: T): Property<T> =
	property(T::class.java).convention(default)
