/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
import { resolve } from 'path'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import VueJSX from '@vitejs/plugin-vue-jsx'
import AutoImport from 'unplugin-auto-import/vite'
import vueSetupExtend from 'vite-plugin-vue-setup-extend'
import { visualizer } from 'rollup-plugin-visualizer'
import Less2CssVariablePlugin from 'antd-less-to-css-variable'
import viteCompression from 'vite-plugin-compression'

//  ant-design-vue 的 less 变量，通过兼容包将 v4 变量转译成 v3 版本，并通过 less-loader 注入
import { theme } from 'ant-design-vue/lib'
import convertLegacyToken from 'ant-design-vue/lib/theme/convertLegacyToken'
const { defaultAlgorithm, defaultSeed } = theme
const mapToken = defaultAlgorithm(defaultSeed)
const v3Token = convertLegacyToken.default(mapToken)

export const r = (...args) => resolve(__dirname, '.', ...args)

export default defineConfig(({ command, mode }) => {
	const envConfig = loadEnv(mode, './')
	const alias = {
		'~': `${resolve(__dirname, './')}`,
		'@/': `${resolve(__dirname, 'src')}/`
	}
	return {
		server: {
			port: envConfig.VITE_PORT,
			proxy: {
				'/api': {
					target: envConfig.VITE_API_BASEURL,
					ws: false,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/api/, '')
				}
			}
		},
		resolve: {
			alias,
			// 优化依赖解析
			dedupe: ['vue', 'ant-design-vue']
		},
		// 解决警告You are running the esm-bundler build of vue-i18n.
		define: {
			__VUE_I18N_FULL_INSTALL__: true,
			__VUE_I18N_LEGACY_API__: true,
			__VUE_I18N_PROD_DEVTOOLS__: true,
			__VUE_PROD_HYDRATION_MISMATCH_DETAILS__: true
		},
		build: {
			// 生产环境移除console
			minify: 'terser',
			terserOptions: {
				compress: {
					drop_console: true,
					drop_debugger: true,
					pure_funcs: ['console.log']
				}
			},
			// 禁用 gzip 压缩大小报告，因为压缩大型文件可能会很慢
			reportCompressedSize: true,
			// CSS代码分割，设为false将所有CSS合并到一个文件
			cssCodeSplit: true,
			// 启用源码映射用于调试
			sourcemap: command === 'serve',
			manifest: true,
			brotliSize: false,
			assetsInlineLimit: 4096,
			rollupOptions: {
				output: {
					// 静态资源分类打包
					chunkFileNames: 'static/js/[name]-[hash].js',
					entryFileNames: 'static/js/[name]-[hash].js',
					assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
					manualChunks: {
						// 第三方库分包配置保持不变
						'vue-vendor': ['vue', 'vue-router', 'pinia', 'vue-i18n'],
						'ant-design-vendor': ['ant-design-vue', '@ant-design/icons-vue', 'lodash-es', 'axios', 'dayjs'],
						'echarts-vendor': ['echarts', 'echarts-stat'],
						'editor-vendor': ['@tinymce/tinymce-vue', 'tinymce'],
						'office-vendor': ['@vue-office/docx', 'vue-pdf-embed', '@vue-office/excel']
					}
				}
			},
			// 调整chunk大小警告限制
			chunkSizeWarningLimit: 2000
		},
		plugins: [
			vue({
				script: {
					refTransform: true
				}
			}),
			viteCompression(),
			vueSetupExtend(),
			VueJSX(),
			AutoImport({
				imports: ['vue'],
				dirs: ['./src/utils/permission'],
				dts: r('src/auto-imports.d.ts')
			}),
			// 组件按需引入
			Components(
				{
					dirs: [r('src/components')],
					dts: false,
					resolvers: []
				},
				{
					dirs: [r('src/components/HomeCard')],
					dts: false,
					resolvers: []
				}
			),
			visualizer()
		],
		css: {
			preprocessorOptions: {
				less: {
					javascriptEnabled: true,
					plugins: [
						new Less2CssVariablePlugin({
							// TODO：有必要用的情况下，是否需要传入 variables，可能会造成重复引用
							variables: { ...v3Token }
						})
					],
					modifyVars: v3Token
				}
			}
		},
		optimizeDeps: {}
	}
})
