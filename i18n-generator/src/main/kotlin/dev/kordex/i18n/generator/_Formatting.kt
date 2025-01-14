/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.kordex.i18n.generator

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.api.Code
import com.pinterest.ktlint.rule.engine.api.EditorConfigDefaults
import com.pinterest.ktlint.rule.engine.api.KtLintRuleEngine
import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.propertyTypes
import java.nio.file.Path
import java.util.*

private val ruleProviders = ServiceLoader.load(
	RuleSetProviderV3::class.java,
	TranslationsClass::class.java.classLoader,
)
	.flatMap { it.getRuleProviders() }
	.toSet()

public fun String.formatCode(editorConfig: Path? = null): String {
	val ruleEngine = KtLintRuleEngine(
		ruleProviders = ruleProviders,

		editorConfigDefaults  = EditorConfigDefaults.load(
			path = editorConfig,
			propertyTypes = ruleProviders.propertyTypes()
		)
	)

	val code = Code.fromSnippet(this)

	return ruleEngine.format(code) {
		AutocorrectDecision.ALLOW_AUTOCORRECT
	}
}
