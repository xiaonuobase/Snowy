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
		v-model:visible="modalVisible"
		:width="580"
		@cancel="modalVisible = false"
		@ok="submit"
		destroy-on-close
		append-to-body
	>
		<div class="sc-cron">
			<a-tabs v-model:activeKey="activeKey">
				<a-tab-pane key="1">
					<template #tab>
						<div class="cron-num">
							<h2>秒</h2>
							<a-tag :color="activeKey === '1' ? `var(--primary-color)` : ''" style="margin-right: 0px">{{
								value_second
							}}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="value.second.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="value.second.type == 1">
							<a-input-number
								v-model:value="value.second.range.start"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
							<span style="padding: 0 15px">-</span>
							<a-input-number
								v-model:value="value.second.range.end"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
						</a-form-item>
						<a-form-item label="间隔" v-if="value.second.type == 2">
							<a-input-number
								v-model:value="value.second.loop.start"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
							秒开始，每
							<a-input-number
								v-model:value="value.second.loop.end"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
							秒执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="value.second.type == 3">
							<a-select
								v-model:value="value.second.appoint"
								multiple
								style="width: 100%"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option
									v-for="(item, index) in data.second"
									:key="index"
									:label="item"
									:value="item"
								></a-select-option>
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="2">
					<template #tab>
						<div class="cron-num">
							<h2>分钟</h2>
							<a-tag :color="activeKey === '2' ? `var(--primary-color)` : ''" style="margin-right: 0px">{{
								value_minute
							}}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="value.minute.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="value.minute.type == 1">
							<a-input-number
								v-model:value="value.minute.range.start"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
							<span style="padding: 0 15px">-</span>
							<a-input-number
								v-model:value="value.minute.range.end"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
						</a-form-item>
						<a-form-item label="间隔" v-if="value.minute.type == 2">
							<a-input-number
								v-model:value="value.minute.loop.start"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
							分钟开始，每
							<a-input-number
								v-model:value="value.minute.loop.end"
								:min="0"
								:max="59"
								controls-position="right"
							></a-input-number>
							分钟执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="value.minute.type == 3">
							<a-select
								v-model:value="value.minute.appoint"
								multiple
								style="width: 100%"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option
									v-for="(item, index) in data.minute"
									:key="index"
									:label="item"
									:value="item"
								></a-select-option>
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="3">
					<template #tab>
						<div class="cron-num">
							<h2>小时</h2>
							<a-tag :color="activeKey === '3' ? `var(--primary-color)` : ''" style="margin-right: 0px">{{
								value_hour
							}}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="value.hour.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="value.hour.type == 1">
							<a-input-number
								v-model:value="value.hour.range.start"
								:min="0"
								:max="23"
								controls-position="right"
							></a-input-number>
							<span style="padding: 0 15px">-</span>
							<a-input-number
								v-model:value="value.hour.range.end"
								:min="0"
								:max="23"
								controls-position="right"
							></a-input-number>
						</a-form-item>
						<a-form-item label="间隔" v-if="value.hour.type == 2">
							<a-input-number
								v-model:value="value.hour.loop.start"
								:min="0"
								:max="23"
								controls-position="right"
							></a-input-number>
							小时开始，每
							<a-input-number
								v-model:value="value.hour.loop.end"
								:min="0"
								:max="23"
								controls-position="right"
							></a-input-number>
							小时执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="value.hour.type == 3">
							<a-select
								v-model:value="value.hour.appoint"
								multiple
								style="width: 100%"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option
									v-for="(item, index) in data.hour"
									:key="index"
									:label="item"
									:value="item"
								></a-select-option>
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="4">
					<template #tab>
						<div class="cron-num">
							<h2>日</h2>
							<a-tag :color="activeKey === '4' ? `var(--primary-color)` : ''" style="margin-right: 0px">{{
								value_day
							}}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="value.day.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
								<a-radio-button value="4">本月最后一天</a-radio-button>
								<a-radio-button value="5">不指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="value.day.type == 1">
							<a-input-number
								v-model:value="value.day.range.start"
								:min="1"
								:max="31"
								controls-position="right"
							></a-input-number>
							<span style="padding: 0 15px">-</span>
							<a-input-number
								v-model:value="value.day.range.end"
								:min="1"
								:max="31"
								controls-position="right"
							></a-input-number>
						</a-form-item>
						<a-form-item label="间隔" v-if="value.day.type == 2">
							<a-input-number
								v-model:value="value.day.loop.start"
								:min="1"
								:max="31"
								controls-position="right"
							></a-input-number>
							号开始，每
							<a-input-number
								v-model:value="value.day.loop.end"
								:min="1"
								:max="31"
								controls-position="right"
							></a-input-number>
							天执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="value.day.type == 3">
							<a-select
								v-model:value="value.day.appoint"
								multiple
								style="width: 100%"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option
									v-for="(item, index) in data.day"
									:key="index"
									:label="item"
									:value="item"
								></a-select-option>
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="5">
					<template #tab>
						<div class="cron-num">
							<h2>月</h2>
							<a-tag :color="activeKey === '5' ? `var(--primary-color)` : ''" style="margin-right: 0px">{{
								value_month
							}}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="value.month.type">
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="value.month.type == 1">
							<a-input-number
								v-model:value="value.month.range.start"
								:min="1"
								:max="12"
								controls-position="right"
							></a-input-number>
							<span style="padding: 0 15px">-</span>
							<a-input-number
								v-model:value="value.month.range.end"
								:min="1"
								:max="12"
								controls-position="right"
							></a-input-number>
						</a-form-item>
						<a-form-item label="间隔" v-if="value.month.type == 2">
							<a-input-number
								v-model:value="value.month.loop.start"
								:min="1"
								:max="12"
								controls-position="right"
							></a-input-number>
							月开始，每
							<a-input-number
								v-model:value="value.month.loop.end"
								:min="1"
								:max="12"
								controls-position="right"
							></a-input-number>
							月执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="value.month.type == 3">
							<a-select
								v-model:value="value.month.appoint"
								multiple
								style="width: 100%"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option
									v-for="(item, index) in data.month"
									:key="index"
									:label="item"
									:value="item"
								></a-select-option>
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="6">
					<template #tab>
						<div class="cron-num">
							<h2>周</h2>
							<a-tag :color="activeKey === '6' ? `var(--primary-color)` : ''" style="margin-right: 0px">{{
								value_week
							}}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form>
							<a-form-item label="类型">
								<a-radio-group v-model:value="value.week.type">
									<a-radio-button value="0">任意值</a-radio-button>
									<a-radio-button value="1">范围</a-radio-button>
									<a-radio-button value="2">间隔</a-radio-button>
									<a-radio-button value="3">指定</a-radio-button>
									<a-radio-button value="4">本月最后一周</a-radio-button>
									<a-radio-button value="5">不指定</a-radio-button>
								</a-radio-group>
							</a-form-item>
							<a-form-item label="范围" v-if="value.week.type == 1">
								<a-select v-model:value="value.week.range.start" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									></a-select-option>
								</a-select>
								<span style="padding: 0 15px">-</span>
								<a-select v-model:value="value.week.range.end" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									></a-select-option>
								</a-select>
							</a-form-item>
							<a-form-item label="间隔" v-if="value.week.type == 2">
								第
								<a-input-number
									v-model:value="value.week.loop.start"
									:min="1"
									:max="4"
									controls-position="right"
								></a-input-number>
								周的星期
								<a-select v-model:value="value.week.loop.end" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									></a-select-option>
								</a-select>
								执行一次
							</a-form-item>
							<a-form-item label="指定" v-if="value.week.type == 3">
								<a-select
									v-model:value="value.week.appoint"
									multiple
									style="width: 100%"
									mode="multiple"
									placeholder="请选择"
								>
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									></a-select-option>
								</a-select>
							</a-form-item>
							<a-form-item label="最后一周" v-if="value.week.type == 4">
								<a-select v-model:value="value.week.last" placeholder="请选择">
									<a-select-option
										v-for="(item, index) in data.week"
										:key="index"
										:label="item.label"
										:value="item.value"
									></a-select-option>
								</a-select>
							</a-form-item>
						</a-form>
					</a-form>
				</a-tab-pane>
				<a-tab-pane key="7">
					<template #tab>
						<div class="cron-num">
							<h2>年</h2>
							<a-tag :color="activeKey === '7' ? `var(--primary-color)` : ''" style="margin-right: 0px">{{
								value_year
							}}</a-tag>
						</div>
					</template>
					<a-form>
						<a-form-item label="类型">
							<a-radio-group v-model:value="value.year.type">
								<a-radio-button value="-1">忽略</a-radio-button>
								<a-radio-button value="0">任意值</a-radio-button>
								<a-radio-button value="1">范围</a-radio-button>
								<a-radio-button value="2">间隔</a-radio-button>
								<a-radio-button value="3">指定</a-radio-button>
							</a-radio-group>
						</a-form-item>
						<a-form-item label="范围" v-if="value.year.type == 1">
							<a-input-number v-model:value="value.year.range.start" controls-position="right"></a-input-number>
							<span style="padding: 0 15px">-</span>
							<a-input-number v-model:value="value.year.range.end" controls-position="right"></a-input-number>
						</a-form-item>
						<a-form-item label="间隔" v-if="value.year.type == 2">
							<a-input-number v-model:value="value.year.loop.start" controls-position="right"></a-input-number>
							年开始，每
							<a-input-number v-model:value="value.year.loop.end" :min="1" controls-position="right"></a-input-number>
							年执行一次
						</a-form-item>
						<a-form-item label="指定" v-if="value.year.type == 3">
							<a-select
								v-model:value="value.year.appoint"
								multiple
								style="width: 100%"
								mode="multiple"
								placeholder="请选择"
							>
								<a-select-option
									v-for="(item, index) in data.year"
									:key="index"
									:label="item"
									:value="item"
								></a-select-option>
							</a-select>
						</a-form-item>
					</a-form>
				</a-tab-pane>
			</a-tabs>
		</div>
	</a-modal>
</template>

<script>
	export default {
		props: {
			modelValue: { type: String, default: '' },
			shortcuts: { type: Array, default: () => [] }
		},
		data() {
			return {
				activeKey: '1',
				type: '0',
				defaultValue: '',
				modalVisible: false,

				value: {
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
							start: this.getYear()[0],
							end: this.getYear()[1]
						},
						loop: {
							start: this.getYear()[0],
							end: 1
						},
						appoint: []
					}
				},
				data: {
					second: ['0', '5', '15', '20', '25', '30', '35', '40', '45', '50', '55', '59'],
					minute: ['0', '5', '15', '20', '25', '30', '35', '40', '45', '50', '55', '59'],
					hour: [
						'0',
						'1',
						'2',
						'3',
						'4',
						'5',
						'6',
						'7',
						'8',
						'9',
						'10',
						'11',
						'12',
						'13',
						'14',
						'15',
						'16',
						'17',
						'18',
						'19',
						'20',
						'21',
						'22',
						'23'
					],
					day: [
						'1',
						'2',
						'3',
						'4',
						'5',
						'6',
						'7',
						'8',
						'9',
						'10',
						'11',
						'12',
						'13',
						'14',
						'15',
						'16',
						'17',
						'18',
						'19',
						'20',
						'21',
						'22',
						'23',
						'24',
						'25',
						'26',
						'27',
						'28',
						'29',
						'30',
						'31'
					],
					month: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
					week: [
						{
							value: '1',
							label: '周日'
						},
						{
							value: '2',
							label: '周一'
						},
						{
							value: '3',
							label: '周二'
						},
						{
							value: '4',
							label: '周三'
						},
						{
							value: '5',
							label: '周四'
						},
						{
							value: '6',
							label: '周五'
						},
						{
							value: '7',
							label: '周六'
						}
					],
					year: this.getYear()
				}
			}
		},
		computed: {
			value_second() {
				let v = this.value.second
				if (v.type == 0) {
					return '*'
				} else if (v.type == 1) {
					return v.range.start + '-' + v.range.end
				} else if (v.type == 2) {
					return v.loop.start + '/' + v.loop.end
				} else if (v.type == 3) {
					return v.appoint.length > 0 ? v.appoint.join(',') : '*'
				} else {
					return '*'
				}
			},
			value_minute() {
				let v = this.value.minute
				if (v.type == 0) {
					return '*'
				} else if (v.type == 1) {
					return v.range.start + '-' + v.range.end
				} else if (v.type == 2) {
					return v.loop.start + '/' + v.loop.end
				} else if (v.type == 3) {
					return v.appoint.length > 0 ? v.appoint.join(',') : '*'
				} else {
					return '*'
				}
			},
			value_hour() {
				let v = this.value.hour
				if (v.type == 0) {
					return '*'
				} else if (v.type == 1) {
					return v.range.start + '-' + v.range.end
				} else if (v.type == 2) {
					return v.loop.start + '/' + v.loop.end
				} else if (v.type == 3) {
					return v.appoint.length > 0 ? v.appoint.join(',') : '*'
				} else {
					return '*'
				}
			},
			value_day() {
				let v = this.value.day
				if (v.type == 0) {
					return '*'
				} else if (v.type == 1) {
					return v.range.start + '-' + v.range.end
				} else if (v.type == 2) {
					return v.loop.start + '/' + v.loop.end
				} else if (v.type == 3) {
					return v.appoint.length > 0 ? v.appoint.join(',') : '*'
				} else if (v.type == 4) {
					return 'L'
				} else if (v.type == 5) {
					return '?'
				} else {
					return '*'
				}
			},
			value_month() {
				let v = this.value.month
				if (v.type == 0) {
					return '*'
				} else if (v.type == 1) {
					return v.range.start + '-' + v.range.end
				} else if (v.type == 2) {
					return v.loop.start + '/' + v.loop.end
				} else if (v.type == 3) {
					return v.appoint.length > 0 ? v.appoint.join(',') : '*'
				} else {
					return '*'
				}
			},
			value_week() {
				let v = this.value.week
				if (v.type == 0) {
					return '*'
				} else if (v.type == 1) {
					return v.range.start + '-' + v.range.end
				} else if (v.type == 2) {
					return v.loop.end + '#' + v.loop.start
				} else if (v.type == 3) {
					return v.appoint.length > 0 ? v.appoint.join(',') : '*'
				} else if (v.type == 4) {
					return v.last + 'L'
				} else if (v.type == 5) {
					return '?'
				} else {
					return '*'
				}
			},
			value_year() {
				let v = this.value.year
				if (v.type == -1) {
					return ''
				} else if (v.type == 0) {
					return '*'
				} else if (v.type == 1) {
					return v.range.start + '-' + v.range.end
				} else if (v.type == 2) {
					return v.loop.start + '/' + v.loop.end
				} else if (v.type == 3) {
					return v.appoint.length > 0 ? v.appoint.join(',') : ''
				} else {
					return ''
				}
			}
		},
		watch: {
			'value.week.type'(val) {
				if (val != '5') {
					this.value.day.type = '5'
				}
			},
			'value.day.type'(val) {
				if (val != '5') {
					this.value.week.type = '5'
				}
			},
			modelValue() {
				this.defaultValue = this.modelValue
			}
		},
		mounted() {
			this.defaultValue = this.modelValue
		},
		methods: {
			upadteValue(v) {
				this.$emit('update:modelValue', v)
			},
			// 选择预置好的
			handleShortcuts(obj) {
				if (obj.key === 'custom') {
					this.open()
				} else {
					this.defaultValue = obj.key
					this.$emit('update:modelValue', this.defaultValue)
				}
			},
			open() {
				this.set()
				this.modalVisible = true
			},
			set() {
				this.defaultValue = this.modelValue
				let arr = (this.modelValue || '* * * * * ?').split(' ')
				//简单检查
				if (arr.length < 6) {
					arr = '* * * * * ?'.split(' ')
				}

				//秒
				if (arr[0] == '*') {
					this.value.second.type = '0'
				} else if (arr[0].includes('-')) {
					this.value.second.type = '1'
					this.value.second.range.start = Number(arr[0].split('-')[0])
					this.value.second.range.end = Number(arr[0].split('-')[1])
				} else if (arr[0].includes('/')) {
					this.value.second.type = '2'
					this.value.second.loop.start = Number(arr[0].split('/')[0])
					this.value.second.loop.end = Number(arr[0].split('/')[1])
				} else {
					this.value.second.type = '3'
					this.value.second.appoint = arr[0].split(',')
				}
				//分
				if (arr[1] == '*') {
					this.value.minute.type = '0'
				} else if (arr[1].includes('-')) {
					this.value.minute.type = '1'
					this.value.minute.range.start = Number(arr[1].split('-')[0])
					this.value.minute.range.end = Number(arr[1].split('-')[1])
				} else if (arr[1].includes('/')) {
					this.value.minute.type = '2'
					this.value.minute.loop.start = Number(arr[1].split('/')[0])
					this.value.minute.loop.end = Number(arr[1].split('/')[1])
				} else {
					this.value.minute.type = '3'
					this.value.minute.appoint = arr[1].split(',')
				}
				//小时
				if (arr[2] == '*') {
					this.value.hour.type = '0'
				} else if (arr[2].includes('-')) {
					this.value.hour.type = '1'
					this.value.hour.range.start = Number(arr[2].split('-')[0])
					this.value.hour.range.end = Number(arr[2].split('-')[1])
				} else if (arr[2].includes('/')) {
					this.value.hour.type = '2'
					this.value.hour.loop.start = Number(arr[2].split('/')[0])
					this.value.hour.loop.end = Number(arr[2].split('/')[1])
				} else {
					this.value.hour.type = '3'
					this.value.hour.appoint = arr[2].split(',')
				}
				//日
				if (arr[3] == '*') {
					this.value.day.type = '0'
				} else if (arr[3] == 'L') {
					this.value.day.type = '4'
				} else if (arr[3] == '?') {
					this.value.day.type = '5'
				} else if (arr[3].includes('-')) {
					this.value.day.type = '1'
					this.value.day.range.start = Number(arr[3].split('-')[0])
					this.value.day.range.end = Number(arr[3].split('-')[1])
				} else if (arr[3].includes('/')) {
					this.value.day.type = '2'
					this.value.day.loop.start = Number(arr[3].split('/')[0])
					this.value.day.loop.end = Number(arr[3].split('/')[1])
				} else {
					this.value.day.type = '3'
					this.value.day.appoint = arr[3].split(',')
				}
				//月
				if (arr[4] == '*') {
					this.value.month.type = '0'
				} else if (arr[4].includes('-')) {
					this.value.month.type = '1'
					this.value.month.range.start = Number(arr[4].split('-')[0])
					this.value.month.range.end = Number(arr[4].split('-')[1])
				} else if (arr[4].includes('/')) {
					this.value.month.type = '2'
					this.value.month.loop.start = Number(arr[4].split('/')[0])
					this.value.month.loop.end = Number(arr[4].split('/')[1])
				} else {
					this.value.month.type = '3'
					this.value.month.appoint = arr[4].split(',')
				}
				//周
				if (arr[5] == '*') {
					this.value.week.type = '0'
				} else if (arr[5] == '?') {
					this.value.week.type = '5'
				} else if (arr[5].includes('-')) {
					this.value.week.type = '1'
					this.value.week.range.start = arr[5].split('-')[0]
					this.value.week.range.end = arr[5].split('-')[1]
				} else if (arr[5].includes('#')) {
					this.value.week.type = '2'
					this.value.week.loop.start = Number(arr[5].split('#')[1])
					this.value.week.loop.end = arr[5].split('#')[0]
				} else if (arr[5].includes('L')) {
					this.value.week.type = '4'
					this.value.week.last = arr[5].split('L')[0]
				} else {
					this.value.week.type = '3'
					this.value.week.appoint = arr[5].split(',')
				}
				//年
				if (!arr[6]) {
					this.value.year.type = '-1'
				} else if (arr[6] == '*') {
					this.value.year.type = '0'
				} else if (arr[6].includes('-')) {
					this.value.year.type = '1'
					this.value.year.range.start = Number(arr[6].split('-')[0])
					this.value.year.range.end = Number(arr[6].split('-')[1])
				} else if (arr[6].includes('/')) {
					this.value.year.type = '2'
					this.value.year.loop.start = Number(arr[6].split('/')[1])
					this.value.year.loop.end = Number(arr[6].split('/')[0])
				} else {
					this.value.year.type = '3'
					this.value.year.appoint = arr[6].split(',')
				}
			},
			getYear() {
				let v = []
				let y = new Date().getFullYear()
				for (let i = 0; i < 11; i++) {
					v.push(y + i)
				}
				return v
			},
			submit() {
				let year = this.value_year ? ' ' + this.value_year : ''
				this.defaultValue =
					this.value_second +
					' ' +
					this.value_minute +
					' ' +
					this.value_hour +
					' ' +
					this.value_day +
					' ' +
					this.value_month +
					' ' +
					this.value_week +
					year
				this.$emit('update:modelValue', this.defaultValue)
				this.modalVisible = false
			}
		}
	}
</script>

<style scoped>
	.sc-cron:deep(.el-tabs__item) {
		height: auto;
		line-height: 1;
		padding: 0 7px;
		vertical-align: bottom;
	}
	.cron-num {
		text-align: center;
		width: 100%;
	}
	.cron-num h2 {
		font-size: 12px;
		font-weight: normal;
	}
	.sc-cron:deep(.el-tabs__item.is-active) .cron-num h4 {
		background: var(--el-color-primary);
		color: #fff;
	}

	[data-theme='dark'] .cron-num h4 {
		background: var(--el-color-white);
	}
</style>
