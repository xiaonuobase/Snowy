const { test } = require('node:test')
const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const vm = require('node:vm')

const read = (file) => fs.readFileSync(path.join(__dirname, '../src', file), 'utf8')
const tick = () => new Promise((resolve) => setImmediate(resolve))
const ref = (value) => ({ value })

function selector(overrides = {}) {
	const calls = []
	let mounted
	const context = vm.createContext({
		ref,
		watch() {},
		nextTick: (fn) => fn(),
		defineEmits: () => () => {},
		defineExpose() {},
		onMounted: (fn) => {
			mounted = fn
		},
		defineProps: () => ({
			pageSize: 10,
			autoLoad: true,
			pageFunction: (params) => new Promise((resolve) => calls.push({ params, resolve })),
			...overrides
		})
	})
	const script = read('components/XnPageSelect/index.vue').match(/<script[^>]*>([\s\S]*?)<\/script>/)[1]
	vm.runInContext(script.replace(/^\s*import[^\n]*$/gm, ''), context)
	return { calls, mounted: () => mounted(), run: (code) => vm.runInContext(code, context) }
}

test('manual selectors do not issue an unfiltered request at mount', () => {
	const component = selector({ autoLoad: false })
	component.mounted()
	assert.equal(component.calls.length, 0)
	component.run('onPage({orgId:"A"})')
	assert.equal(component.calls[0].params.orgId, 'A')
})

test('late initial results cannot replace the current organization', async () => {
	const component = selector()
	component.mounted()
	component.run('onPage({orgId:"A"})')
	component.calls[1].resolve({ current: 1, total: 1, records: [{ id: 'A' }] })
	await tick()
	component.calls[0].resolve({ current: 1, total: 1, records: [{ id: 'B' }] })
	await tick()
	assert.equal(component.run('options.value[0].id'), 'A')
})

test('obsolete responses do not stop the current loading indicator', async () => {
	const component = selector()
	component.mounted()
	component.run('onPage({orgId:"A"})')
	component.calls[0].resolve({ current: 1, total: 1, records: [{ id: 'B' }] })
	await tick()
	assert.equal(component.run('spinning.value'), true)
	component.calls[1].resolve({ current: 1, total: 1, records: [{ id: 'A' }] })
	await tick()
	assert.equal(component.run('spinning.value'), false)
})

test('old pagination results cannot append to a new organization', async () => {
	const component = selector()
	component.mounted()
	component.calls[0].resolve({ current: 1, total: 2, records: [{ id: 'A1' }] })
	await tick()
	component.run('handlePagination(); handlePagination()')
	assert.equal(component.calls.length, 2)
	component.run('onPage({orgId:"B"})')
	component.calls[2].resolve({ current: 1, total: 1, records: [{ id: 'B1' }] })
	await tick()
	component.calls[1].resolve({ current: 2, total: 2, records: [{ id: 'A2' }] })
	await tick()
	assert.equal(component.run('options.value.map(x => x.id).join(",")'), 'B1')
})

test('late echo responses cannot restore the previous selection', async () => {
	let resolveEcho
	const component = selector({
		value: 'A0',
		echoFunction: () =>
			new Promise((resolve) => {
				resolveEcho = resolve
			})
	})
	component.mounted()
	component.calls[0].resolve({ current: 1, total: 1, records: [{ id: 'A1' }] })
	await tick()
	component.run('modelValue.value = "B1"; onPage({orgId:"B"})')
	component.calls[1].resolve({ current: 1, total: 1, records: [{ id: 'B1' }] })
	await tick()
	resolveEcho([{ id: 'A0' }])
	await tick()
	assert.equal(component.run('options.value.map(x => x.id).join(",")'), 'B1')
})

for (const code of [401, 1011007, 1011008, 200]) {
	test(`unlock response ${code} preserves the correct lock state`, async () => {
		const store = {
			isLocked: true,
			setIsLocked(value) {
				this.isLocked = value
			}
		}
		const context = vm.createContext({
			ref,
			store,
			Modal: { error() {} },
			tool: {},
			window: {},
			RELOGIN_CODES: [401, 1011007, 1011008],
			UNLOCK_FAIL_LIMIT_CODE: 4013,
			SAFE_AUTH_CODE: 4011,
			LOCKED_CODE: 4012,
			smCrypto: { doSm2Encrypt: (value) => value },
			useMenuStore: () => ({ refreshApiMenu() {} }),
			useDictStore: () => ({ refreshDict() {} })
		})
		const handler = read('utils/codeHandler.js')
		vm.runInContext(
			handler
				.slice(handler.indexOf('const GLOBAL_MODAL_Z_INDEX'), handler.indexOf('// 解锁连续失败超限，服务端已注销会话'))
				.replace(/export /g, ''),
			context
		)
		const request = read('utils/request.js')
		const start = request.indexOf('\t(response) => {')
		vm.runInContext(
			'const responseHandler = ' +
				request.slice(start, request.indexOf('\n\t(error) => {', start)).trim().replace(/,$/, ''),
			context
		)
		context.userCenterApi = {
			userUnlock: () =>
				Promise.resolve().then(() =>
					vm.runInContext(
						`responseHandler({config:{skipSuccessMessage:true},data:{code:${code},msg:"login expired"}})`,
						context
					)
				)
		}
		const lock = read('components/XnLockScreen/index.vue')
		vm.runInContext(
			'const password=ref("password"), inputStatus=ref(""), errorMessage=ref(""), loading=ref(false);' +
				lock.slice(lock.indexOf('\tconst handleUnlock ='), lock.indexOf('\n\t// 忘记密码')) +
				'\nhandleUnlock()',
			context
		)
		await tick()
		assert.equal(store.isLocked, code !== 200)
		assert.equal(vm.runInContext('errorMessage.value', context), code === 200 ? '' : 'login expired')
	})
}
