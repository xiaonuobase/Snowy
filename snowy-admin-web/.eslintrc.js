module.exports = {
	root: true,
	env: {
		browser: true,
		node: true,
		es6: true,
		'vue/setup-compiler-macros': true
	},
	parser: 'vue-eslint-parser',
	extends: ['plugin:vue/vue3-recommended', 'eslint:recommended', 'plugin:prettier/recommended'],
	parserOptions: {
		ecmaVersion: 2020,
		sourceType: 'module',
		jsxPragma: 'React',
		ecmaFeatures: {
			jsx: true
		}
	},
	rules: {
		'no-unused-vars': 'off',
		'no-undef': 'off',
		'vue/script-setup-uses-vars': 'error',
		'vue/custom-event-name-casing': 'off',
		'no-use-before-define': 'off',
		'space-before-function-paren': 'off',
		'vue/attributes-order': 'off',
		'vue/one-component-per-file': 'off',
		'vue/html-closing-bracket-newline': 'off',
		'vue/max-attributes-per-line': 'off',
		'vue/multiline-html-element-content-newline': 'off',
		'vue/singleline-html-element-content-newline': 'off',
		'vue/attribute-hyphenation': 'off',
		'vue/require-default-prop': 'off',
		'vue/html-self-closing': 'off',
		'vue/v-on-event-hyphenation': 'off',
		'vue/multi-word-component-names': 'off',
		'prettier/prettier': ['error', { endOfLine: 'auto' }]
	}
}
