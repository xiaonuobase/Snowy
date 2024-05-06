<template>
	<a-input :value="defaultValue" @update:value="upadteValue" placeholder="请输入CRON定时规则">
		<template #addonAfter>
			<a-dropdown>
				<down-outlined />
				<template #overlay>
					<a-menu @click="handleShortcuts">
						<a-menu-item key="0 * * * * ?">每分钟</a-menu-item>
						<a-menu-item key="0 0 * * * ?">每小时</a-menu-item>
						<a-menu-item key="0 0 0 * * ?">每天零点</a-menu-item>
						<a-menu-item key="0 0 0 1 * ?">每月一号零点</a-menu-item>
						<a-menu-item key="0 0 0 L * ?">每月最后一天零点</a-menu-item>
						<a-menu-item key="0 0 0 ? * 1">每周星期日零点</a-menu-item>
						<a-menu-divider v-if="shortcuts" />
						<a-menu-item v-for="item in shortcuts" :key="item.value">{{ item.text }}</a-menu-item>
						<a-menu-divider />
						<a-menu-item key="custom"><UserOutlined />自定义</a-menu-item>
					</a-menu>
				</template>
			</a-dropdown>
		</template>
	</a-input>
	<a-modal
		title="CRON规则生成器"
		v-model:open="modalVisible"
		:width="580"
		@cancel="modalVisible = false"
		@ok="submit"
		destroy-on-close
		append-to-body
	>
		<div>
			<a-tabs v-model:activeKey="activeKey">
				<a-tab-pane key="1">
					<template #tab>
						<div class="cron-num">
							<h2>秒</h2>
							<a-tag :color="activeKey === '1' ? `var(--primary-color)` : ''" class="xn-mr">{{ value_second }}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="dateValue.second.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="dateValue.second.type === '1'">
							<a-input-number
								v-model:value="dateValue.second.range.start"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
							<span class="xn-pd">-</span>
							<a-input-number
								v-model:value="dateValue.second.range.end"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
						</a-form-item>
						<a-form-item label="间隔" v-if="dateValue.second.type === '2'">
							<a-input-number
								v-model:value="dateValue.second.loop.start"
								:min="0"
								:max="59"
								controls-position="right"
							/>
							秒开始，每
							<a-input-number v-model:value="dateValue.second.loop.end" :min="0" :max="59" controls-position="right" />
							秒执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="dateValue.second.type === '3'">
							<a-select
								v-model:value="dateValue.second.appoint"
								multiple
								class="xn-wd"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option v-for="(item, index) in data.second" :key="index" :label="item" :value="item" />
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="2">
					<template #tab>
						<div class="cron-num">
							<h2>分钟</h2>
							<a-tag :color="activeKey === '2' ? `var(--primary-color)` : ''" class="xn-mr">{{ value_minute }}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="dateValue.minute.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="dateValue.minute.type === '1'">
							<a-input-number
								v-model:value="dateValue.minute.range.start"
								:min="0"
								:max="59"
								controls-position="right"
							/>
							<span class="xn-pd">-</span>
							<a-input-number v-model:value="dateValue.minute.range.end" :min="0" :max="59" controls-position="right" />
						</a-form-item>
						<a-form-item label="间隔" v-if="dateValue.minute.type === '2'">
							<a-input-number
								v-model:value="dateValue.minute.loop.start"
								:min="0"
								:max="59"
								controls-position="right"
							/>
							分钟开始，每
							<a-input-number v-model:value="dateValue.minute.loop.end" :min="0" :max="59" controls-position="right" />
							分钟执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="dateValue.minute.type === '3'">
							<a-select
								v-model:value="dateValue.minute.appoint"
								multiple
								class="xn-wd"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option v-for="(item, index) in data.minute" :key="index" :label="item" :value="item" />
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="3">
					<template #tab>
						<div class="cron-num">
							<h2>小时</h2>
							<a-tag :color="activeKey === '3' ? `var(--primary-color)` : ''" class="xn-mr">{{ value_hour }}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="dateValue.hour.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="dateValue.hour.type === '1'">
							<a-input-number v-model:value="dateValue.hour.range.start" :min="0" :max="23" controls-position="right" />
							<span class="xn-pd">-</span>
							<a-input-number v-model:value="dateValue.hour.range.end" :min="0" :max="23" controls-position="right" />
						</a-form-item>
						<a-form-item label="间隔" v-if="dateValue.hour.type === '2'">
							<a-input-number v-model:value="dateValue.hour.loop.start" :min="0" :max="23" controls-position="right" />
							小时开始，每
							<a-input-number v-model:value="dateValue.hour.loop.end" :min="0" :max="23" controls-position="right" />
							小时执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="dateValue.hour.type === '3'">
							<a-select
								v-model:value="dateValue.hour.appoint"
								multiple
								class="xn-wd"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option v-for="(item, index) in data.hour" :key="index" :label="item" :value="item" />
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="4">
					<template #tab>
						<div class="cron-num">
							<h2>日</h2>
							<a-tag :color="activeKey === '4' ? `var(--primary-color)` : ''" class="xn-mr">{{ value_day }}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="dateValue.day.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
								<a-radio-button value="4">本月最后一天</a-radio-button>
								<a-radio-button value="5">不指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="dateValue.day.type === '1'">
							<a-input-number v-model:value="dateValue.day.range.start" :min="1" :max="31" controls-position="right" />
							<span class="xn-pd">-</span>
							<a-input-number v-model:value="dateValue.day.range.end" :min="1" :max="31" controls-position="right" />
						</a-form-item>
						<a-form-item label="间隔" v-if="dateValue.day.type === '2'">
							<a-input-number v-model:value="dateValue.day.loop.start" :min="1" :max="31" controls-position="right" />
							号开始，每
							<a-input-number v-model:value="dateValue.day.loop.end" :min="1" :max="31" controls-position="right" />
							天执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="dateValue.day.type === '3'">
							<a-select
								v-model:value="dateValue.day.appoint"
								multiple
								class="xn-wd"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option v-for="(item, index) in data.day" :key="index" :label="item" :value="item" />
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="5">
					<template #tab>
						<div class="cron-num">
							<h2>月</h2>
							<a-tag :color="activeKey === '5' ? `var(--primary-color)` : ''" class="xn-mr">{{ value_month }}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="dateValue.month.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="dateValue.month.type === '1'">
							<a-input-number
								v-model:value="dateValue.month.range.start"
								:min="1"
								:max="12"
								controls-position="right"
							/>
							<span class="xn-pd">-</span>
							<a-input-number v-model:value="dateValue.month.range.end" :min="1" :max="12" controls-position="right" />
						</a-form-item>
						<a-form-item label="间隔" v-if="dateValue.month.type === '2'">
							<a-input-number v-model:value="dateValue.month.loop.start" :min="1" :max="12" controls-position="right" />
							月开始，每
							<a-input-number v-model:value="dateValue.month.loop.end" :min="1" :max="12" controls-position="right" />
							月执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="dateValue.month.type === '3'">
							<a-select
								v-model:value="dateValue.month.appoint"
								multiple
								class="xn-wd"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option v-for="(item, index) in data.month" :key="index" :label="item" :value="item" />
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="6">
					<template #tab>
						<div class="cron-num">
							<h2>周</h2>
							<a-tag :color="activeKey === '6' ? `var(--primary-color)` : ''" class="xn-mr">{{ value_week }}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form>
							<a-form-item label="类型">
								<a-radio-group v-model:value="dateValue.week.type">
									<a-radio-button value="0">任意值</a-radio-button>
									<a-radio-button value="1">范围</a-radio-button>
									<a-radio-button value="2">间隔</a-radio-button>
									<a-radio-button value="3">指定</a-radio-button>
									<a-radio-button value="4">本月最后一周</a-radio-button>
									<a-radio-button value="5">不指定</a-radio-button>
								</a-radio-group>
							</a-form-item>
							<a-form-item label="范围" v-if="dateValue.week.type === '1'">
								<a-select v-model:value="dateValue.week.range.start" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									/>
								</a-select>
								<span class="xn-pd">-</span>
								<a-select v-model:value="dateValue.week.range.end" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									/>
								</a-select>
							</a-form-item>
							<a-form-item label="间隔" v-if="dateValue.week.type === '2'">
								第
								<a-input-number v-model:value="dateValue.week.loop.start" :min="1" :max="4" controls-position="right" />
								周的星期
								<a-select v-model:value="dateValue.week.loop.end" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									/>
								</a-select>
								执行一次
							</a-form-item>
							<a-form-item label="指定" v-if="dateValue.week.type === '3'">
								<a-select
									v-model:value="dateValue.week.appoint"
									multiple
									class="xn-wd"
									mode="multiple"
									placeholder="请选择"
								>
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									/>
								</a-select>
							</a-form-item>
							<a-form-item label="最后一周" v-if="dateValue.week.type === '4'">
								<a-select v-model:value="dateValue.week.last" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									/>
								</a-select>
							</a-form-item>
						</a-form>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="7">
					<template #tab>
						<div class="cron-num">
							<h2>年</h2>
							<a-tag :color="activeKey === '7' ? `var(--primary-color)` : ''" class="xn-mr">{{ value_year }}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="dateValue.year.type">
								<a-radio-button value="-1">忽略</a-radio-button>
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="dateValue.year.type === '1'">
							<a-input-number v-model:value="dateValue.year.range.start" controls-position="right" />
							<span class="xn-pd">-</span>
							<a-input-number v-model:value="dateValue.year.range.end" controls-position="right" />
						</a-form-item>
						<a-form-item label="间隔" v-if="dateValue.year.type === '2'">
							<a-input-number v-model:value="dateValue.year.loop.start" controls-position="right" />
							年开始，每
							<a-input-number v-model:value="dateValue.year.loop.end" :min="1" controls-position="right" />
							年执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="dateValue.year.type === '3'">
							<a-select
								v-model:value="dateValue.year.appoint"
								multiple
								class="xn-wd"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option v-for="(item, index) in data.year" :key="index" :label="item" :value="item" />
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
			</a-tabs>
		</div>
	</a-modal>
</template>

<script setup name="XnCron">
	import { data, getYear } from './data.js'
	const activeKey = ref('1')
	const defaultValue = ref('')
	const modalVisible = ref(false)
	const dateValue = ref({
		second: {
			type: '0',
			range: {
				start: 1,
				end: 2
			},
			loop: {
				start: 0,
				end: 1
			},
			appoint: []
		},
		minute: {
			type: '0',
			range: {
				start: 1,
				end: 2
			},
			loop: {
				start: 0,
				end: 1
			},
			appoint: []
		},
		hour: {
			type: '0',
			range: {
				start: 1,
				end: 2
			},
			loop: {
				start: 0,
				end: 1
			},
			appoint: []
		},
		day: {
			type: '0',
			range: {
				start: 1,
				end: 2
			},
			loop: {
				start: 1,
				end: 1
			},
			appoint: []
		},
		month: {
			type: '0',
			range: {
				start: 1,
				end: 2
			},
			loop: {
				start: 1,
				end: 1
			},
			appoint: []
		},
		week: {
			type: '5',
			range: {
				start: '2',
				end: '3'
			},
			loop: {
				start: 0,
				end: '2'
			},
			last: '2',
			appoint: []
		},
		year: {
			type: '-1',
			range: {
				start: getYear()[0],
				end: getYear()[1]
			},
			loop: {
				start: getYear()[0],
				end: 1
			},
			appoint: []
		}
	})

	const props = defineProps({
		modelValue: {
			type: String,
			default: () => ''
		},
		shortcuts: {
			type: Array,
			default: () => []
		}
	})
	const setRules = (v) => {
		switch (v.type) {
			case '0':
				return '*'
			case '1':
				return v.range.start + '-' + v.range.end
			case '2':
				return v.loop.start + '/' + v.loop.end
			case '3':
				return v.appoint.length > 0 ? v.appoint.join(',') : '*'
			case '4':
				return 'L'
			case '5':
				return '?'
			default:
				return '*'
		}
	}
	const value_second = computed(() => {
		let v = dateValue.value.second
		return setRules(v)
	})
	const value_minute = computed(() => {
		let v = dateValue.value.minute
		return setRules(v)
	})
	const value_hour = computed(() => {
		let v = dateValue.value.hour
		return setRules(v)
	})
	const value_day = computed(() => {
		let v = dateValue.value.day
		return setRules(v)
	})
	const value_month = computed(() => {
		let v = dateValue.value.month
		return setRules(v)
	})
	const value_week = computed(() => {
		let v = dateValue.value.week
		return setRules(v)
	})
	const value_year = computed(() => {
		let v = dateValue.value.year
		switch (v.type) {
			case '-1':
				return ''
			case '0':
				return '*'
			case '1':
				return v.range.start + '-' + v.range.end
			case '2':
				return v.loop.start + '/' + v.loop.end
			case '3':
				return v.appoint.length > 0 ? v.appoint.join(',') : ''
			default:
				return ''
		}
	})

	// 监听周
	watch(dateValue.value.week, (newValue) => {
		if (newValue.type !== '5') {
			dateValue.value.day.type = '5'
		}
	})
	// 监听监听日
	watch(dateValue.value.day, (newValue) => {
		if (newValue.type !== '5') {
			dateValue.value.week.type = '5'
		}
	})
	watch(props, (newValue) => {
		if (newValue.modelValue === '') {
			defaultValue.value = ''
		}
		if (newValue.modelValue) {
			defaultValue.value = newValue.modelValue
		}
	})
	onMounted(() => {
		defaultValue.value = props.modelValue
	})
	const emit = defineEmits(['update:modelValue'])
	const upadteValue = (v) => {
		emit('update:modelValue', v)
	}
	const handleShortcuts = (obj) => {
		if (obj.key === 'custom') {
			open()
		} else {
			defaultValue.value = obj.key
			emit('update:modelValue', defaultValue.value)
		}
	}
	const open = () => {
		set()
		modalVisible.value = true
	}
	const set = () => {
		defaultValue.value = props.modelValue
		let arr = (props.modelValue || '* * * * * ?').split(' ')
		//简单检查
		if (arr.length < 6) {
			arr = '* * * * * ?'.split(' ')
		}

		//秒
		if (arr[0] === '*') {
			dateValue.value.second.type = '0'
		} else if (arr[0].includes('-')) {
			dateValue.value.second.type = '1'
			dateValue.value.second.range.start = Number(arr[0].split('-')[0])
			dateValue.value.second.range.end = Number(arr[0].split('-')[1])
		} else if (arr[0].includes('/')) {
			dateValue.value.second.type = '2'
			dateValue.value.second.loop.start = Number(arr[0].split('/')[0])
			dateValue.value.second.loop.end = Number(arr[0].split('/')[1])
		} else {
			dateValue.value.second.type = '3'
			dateValue.value.second.appoint = arr[0].split(',')
		}
		//分
		if (arr[1] === '*') {
			dateValue.value.minute.type = '0'
		} else if (arr[1].includes('-')) {
			dateValue.value.minute.type = '1'
			dateValue.value.minute.range.start = Number(arr[1].split('-')[0])
			dateValue.value.minute.range.end = Number(arr[1].split('-')[1])
		} else if (arr[1].includes('/')) {
			dateValue.value.minute.type = '2'
			dateValue.value.minute.loop.start = Number(arr[1].split('/')[0])
			dateValue.value.minute.loop.end = Number(arr[1].split('/')[1])
		} else {
			dateValue.value.minute.type = '3'
			dateValue.value.minute.appoint = arr[1].split(',')
		}
		//小时
		if (arr[2] === '*') {
			dateValue.value.hour.type = '0'
		} else if (arr[2].includes('-')) {
			dateValue.value.hour.type = '1'
			dateValue.value.hour.range.start = Number(arr[2].split('-')[0])
			dateValue.value.hour.range.end = Number(arr[2].split('-')[1])
		} else if (arr[2].includes('/')) {
			dateValue.value.hour.type = '2'
			dateValue.value.hour.loop.start = Number(arr[2].split('/')[0])
			dateValue.value.hour.loop.end = Number(arr[2].split('/')[1])
		} else {
			dateValue.value.hour.type = '3'
			dateValue.value.hour.appoint = arr[2].split(',')
		}
		//日
		if (arr[3] === '*') {
			dateValue.value.day.type = '0'
		} else if (arr[3] === 'L') {
			dateValue.value.day.type = '4'
		} else if (arr[3] === '?') {
			dateValue.value.day.type = '5'
		} else if (arr[3].includes('-')) {
			dateValue.value.day.type = '1'
			dateValue.value.day.range.start = Number(arr[3].split('-')[0])
			dateValue.value.day.range.end = Number(arr[3].split('-')[1])
		} else if (arr[3].includes('/')) {
			dateValue.value.day.type = '2'
			dateValue.value.day.loop.start = Number(arr[3].split('/')[0])
			dateValue.value.day.loop.end = Number(arr[3].split('/')[1])
		} else {
			dateValue.value.day.type = '3'
			dateValue.value.day.appoint = arr[3].split(',')
		}
		//月
		if (arr[4] === '*') {
			dateValue.value.month.type = '0'
		} else if (arr[4].includes('-')) {
			dateValue.value.month.type = '1'
			dateValue.value.month.range.start = Number(arr[4].split('-')[0])
			dateValue.value.month.range.end = Number(arr[4].split('-')[1])
		} else if (arr[4].includes('/')) {
			dateValue.value.month.type = '2'
			dateValue.value.month.loop.start = Number(arr[4].split('/')[0])
			dateValue.value.month.loop.end = Number(arr[4].split('/')[1])
		} else {
			dateValue.value.month.type = '3'
			dateValue.value.month.appoint = arr[4].split(',')
		}
		//周
		if (arr[5] === '*') {
			dateValue.value.week.type = '0'
		} else if (arr[5] === '?') {
			dateValue.value.week.type = '5'
		} else if (arr[5].includes('-')) {
			dateValue.value.week.type = '1'
			dateValue.value.week.range.start = arr[5].split('-')[0]
			dateValue.value.week.range.end = arr[5].split('-')[1]
		} else if (arr[5].includes('#')) {
			dateValue.value.week.type = '2'
			dateValue.value.week.loop.start = Number(arr[5].split('#')[1])
			dateValue.value.week.loop.end = arr[5].split('#')[0]
		} else if (arr[5].includes('L')) {
			dateValue.value.week.type = '4'
			dateValue.value.week.last = arr[5].split('L')[0]
		} else {
			dateValue.value.week.type = '3'
			dateValue.value.week.appoint = arr[5].split(',')
		}
		//年
		if (!arr[6]) {
			dateValue.value.year.type = '-1'
		} else if (arr[6] === '*') {
			dateValue.value.year.type = '0'
		} else if (arr[6].includes('-')) {
			dateValue.value.year.type = '1'
			dateValue.value.year.range.start = Number(arr[6].split('-')[0])
			dateValue.value.year.range.end = Number(arr[6].split('-')[1])
		} else if (arr[6].includes('/')) {
			dateValue.value.year.type = '2'
			dateValue.value.year.loop.start = Number(arr[6].split('/')[1])
			dateValue.value.year.loop.end = Number(arr[6].split('/')[0])
		} else {
			dateValue.value.year.type = '3'
			dateValue.value.year.appoint = arr[6].split(',')
		}
	}

	const submit = () => {
		let year = value_year.value ? ' ' + value_year.value : ''
		defaultValue.value =
			value_second.value +
			' ' +
			value_minute.value +
			' ' +
			value_hour.value +
			' ' +
			value_day.value +
			' ' +
			value_month.value +
			' ' +
			value_week.value +
			year
		emit('update:modelValue', defaultValue.value)
		modalVisible.value = false
	}
</script>

<style scoped>
	.cron-num {
		text-align: center;
		width: 100%;
	}
	.cron-num h2 {
		font-size: 12px;
		font-weight: normal;
	}
	.xn-mr {
		margin-right: 0px;
	}
	.xn-pd {
		padding: 0 15px;
	}
</style>
