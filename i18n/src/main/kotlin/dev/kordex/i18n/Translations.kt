/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n

import dev.kordex.i18n.registries.ClassLoaderRegistry
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

public object Translations {
	private val logger: KLogger = KotlinLogging.logger { }

	/** `Pair<"bundle name", Locale>` to a corresponding resource bundle object. **/
	private val bundleLocaleCache: MutableMap<Pair<String, Locale>, ResourceBundle> = mutableMapOf()

	/** Check whether the given [Key] exists. **/
	public fun hasKey(keyObj: Key): Boolean {
		val (key, bundle, locale) = keyObj

		return try {
			val (rootBundle, _) = getBundles(
				bundle ?: I18n.defaultBundle,
				locale ?: I18n.defaultLocale,
			)

			rootBundle.keySet().contains(key)
		} catch (e: MissingResourceException) {
			logger.trace(e) { "Failed to find $key" }

			false
		}
	}

	/** Get the untranslated string for the given [Key]. **/
	public fun get(keyObj: Key): String {
		val (key, bundle, locale) = keyObj

		val (baseBundle, overrideBundle) = getBundles(
			bundle ?: I18n.defaultBundle,
			locale ?: I18n.defaultLocale,
		)

		val result = overrideBundle?.getStringOrNull(key)
			?: baseBundle.getString(key)

		logger.trace { "Result: $key -> $result" }

		return result
	}

	/**
	 * Get the translation for the given [Key], formatting it using the given [replacements].
	 *
	 * **This function formats using an indexed array, which some message formats may not support.**
	 */
	public fun translateOrdinal(
		key: Key,
		replacements: Array<Any?> = arrayOf(),
	): String {
		if (key == EMPTY_KEY) {
			return ""
		}

		val bundle = key.bundle
			?: I18n.defaultBundle

		val locale = key.locale
			?: I18n.defaultLocale

		val string = getTranslatedString(key)
		val formatter = bundle.getMessageFormatter()

		return formatter.formatOrdinal(string, locale, replacements)
	}

	/**
	 * Get the translation for the given [Key], formatting it using the given [replacements].
	 *
	 * This function formats using a map of named replacements, the preferred approach.
	 */
	public fun translateNamed(
		key: Key,
		replacements: Map<String, Any?>,
	): String {
		if (key == EMPTY_KEY) {
			return ""
		}

		val bundle = key.bundle
			?: I18n.defaultBundle

		val locale = key.locale
			?: I18n.defaultLocale

		val string = getTranslatedString(key)
		val formatter = bundle.getMessageFormatter()

		return formatter.formatNamed(string, locale, replacements)
	}

	private fun getTranslatedString(key: Key): String {
		val bundle = key.bundle

		var string: String = try {
			get(key)
		} catch (_: MissingResourceException) {
			key.key
		}

		return try {
			if (string == key.key && bundle != null) {
				// Fall through to the default bundle if not found.
				logger.trace { "$key not found - falling through to ${I18n.defaultBundle}" }

				string = get(key.withBundle(I18n.defaultBundle))
			}

			string
		} catch (e: MissingResourceException) {
			logger.trace(e) {
				buildString {
					appendLine("Unable to find translation for ${key.key} in bundles:")

					if (key.bundle != null) {
						appendLine("\t${key.bundle}")
					}

					appendLine("\t${I18n.defaultBundle}")
				}
			}

			key.key
		}
	}

	private fun getResourceBundle(bundle: Bundle, locale: Locale): ResourceBundle {
		val baseName = bundle.name.replace(".", "/")
		val control = bundle.getResourceBundleControl()
		val format = control.getFormats(baseName)
			.map { it.split(".").last() }
			.first { it != "class" }

		val bundlePath = "$baseName.$format"

		val classLoaders: MutableList<Pair<String, ClassLoader>> = mutableListOf(
			"bundle" to bundle.classLoader,
			"current" to Translations::class.java.classLoader,
			"system" to ClassLoader.getSystemClassLoader(),
		)

		classLoaders.addAll(
			ClassLoaderRegistry.getCallbackClassloaders().entries.map { it.toPair() }
		)

		logger.trace {
			"Searching for $bundle with locale ${locale.toLanguageTag()} in ${classLoaders.size} class-loaders"
		}

		for ((name, loader) in classLoaders) {
			logger.trace { "Searching in class-loader $name" }

			try {
				if (loader.getResource(bundlePath) != null) {
					val result = ResourceBundle.getBundle(bundle.name, locale, loader, control)

					logger.debug { "Found $bundle with locale ${locale.toLanguageTag()} in $name class-loader" }

					return result
				}
			} catch (_: MissingResourceException) {
				// Do nothing, move on to the next loader.
				continue
			}
		}

		logger.debug {
			"Couldn't find $bundle with locale ${locale.toLanguageTag()} in ${classLoaders.size} class-loaders; " +
				"falling back to default strategy"
		}

		return ResourceBundle.getBundle(
			bundle.name,
			locale,
			bundle.getResourceBundleControl()
		)
	}

	@Suppress("DEPRECATION")  // Locale constructor, for compatibility with Java versions older than 19.
	private fun getBundles(bundle: Bundle, locale: Locale): Pair<ResourceBundle, ResourceBundle?> {
		// First, let's make sure the bundle is valid. If these throw, it isn't valid.
		bundle.getResourceBundleControl()
		bundle.getMessageFormatter()

		// Prefix using the default bundle prefix and add the default suffix if needed.
		val prefixedBundle = bundle.copy(
			name = buildString {
				append("${I18n.defaultBundlePrefix}.${bundle.name}")

				if (count { ch -> ch == '.' } < 2) {
					append(".${I18n.defaultBundleSuffix}")
				}
			}
		)

		val overrideBundle = prefixedBundle.copy(name = prefixedBundle.name + "_override")

		val prefixedBundleKey = prefixedBundle.name to locale
		val overrideBundleKey = overrideBundle.name to locale

		if (prefixedBundleKey !in bundleLocaleCache) {
			logger.trace { "Getting bundle: $prefixedBundle" }

			val localeTag = locale.toLanguageTag()
			val firstBundle = getResourceBundle(prefixedBundle, locale)

			bundleLocaleCache[prefixedBundleKey] = if (localeTag.count { it in "-_" } == 0) {
				// If the locale tag only contains one element, double it up.
				// TODO: Try to remember why I did this - Relevant KordEx commit is 13d8843f (30th Jan 2022)

				val baseCode = localeTag.split('-', '_').first()
				val secondLocale = Locale(baseCode, baseCode)
				val secondBundle = getResourceBundle(prefixedBundle, secondLocale)

				val firstKey = firstBundle.keySet().first()

				if (firstBundle.getStringOrNull(firstKey) != secondBundle.getStringOrNull(firstKey)) {
					secondBundle
				} else {
					firstBundle
				}
			} else {
				firstBundle
			}

			try {
				logger.trace { "Getting override bundle: $overrideBundle" }

				bundleLocaleCache[overrideBundleKey] = getResourceBundle(overrideBundle, locale)
			} catch (e: MissingResourceException) {
				logger.trace(e) { "No override bundle found." }
			}
		}

		return bundleLocaleCache[prefixedBundleKey]!! to bundleLocaleCache[overrideBundleKey]
	}

	private fun ResourceBundle.getStringOrNull(key: String): String? {
		return try {
			getString(key)
		} catch (_: MissingResourceException) {
			null
		}
	}
}
