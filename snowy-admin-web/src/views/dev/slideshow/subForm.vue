<template>
	<a-form :model="formData" ref="formRef" name="basic" autocomplete="off">
		<a-table :columns="columns" :dataSource="formData" size="middle">
			<template #bodyCell="{ text, record, index, column }">
				<template v-if="column.dataIndex === 'whetherToClick'">
					<a-form-item :validate-status="validateStatus(record, 'whetherToClick')">
						<a-radio-group
							v-model:value="record.whetherToClick"
							placeholder="请选择跳转方式"
							:options="whetherToClickOptions"
						/>
					</a-form-item>
				</template>
				<template v-if="column.dataIndex === 'skipMode'">
					<a-form-item :validate-status="validateStatus(record, 'skipMode')">
						<a-select
							v-model:value="record.skipMode"
							placeholder="请选择跳转方式"
							:disabled="record.whetherToClick === 'DISABLE'"
							:options="skipModeOptions"
						/>
					</a-form-item>
				</template>
				<template v-if="column.dataIndex === 'url'">
					<a-form-item :validate-status="validateStatus(record, 'url')">
						<a-input
							v-model:value="formData[index].url"
							:disabled="record.whetherToClick === 'DISABLE'"
							placeholder="请输入URL或路由地址"
						/>
					</a-form-item>
				</template>
			</template>
		</a-table>
	</a-form>
</template>

<script name="subForm" setup>
	import tool from '@/utils/tool'
	import { remove, isEmpty, cloneDeep } from 'lodash-es'
	const formRef = ref()
	const formData = ref([])
	const skipModeOptions = ref(tool.dictList('SKIP_MODE'))
	const whetherToClickOptions = ref(tool.dictList('WHETHER_TO_CLICK'))
	const props = defineProps({
		dataArray: {
			type: Object,
			default: []
		},
		place: {
			type: Object,
			default: []
		}
	})
	const columns = ref([
		{
			title: '位置',
			dataIndex: 'label',
			width: '20%'
		},
		{
			title: '点击事件',
			dataIndex: 'whetherToClick',
			width: '25%'
		},
		{
			title: '跳转方式',
			dataIndex: 'skipMode',
			width: '20%'
		},
		{
			title: 'URL',
			dataIndex: 'url'
		}
	])
	// 每行的校验
	const validateStatus = (record, name) => {
		if (record[name]) {
			return 'success'
		} else {
			return 'error'
		}
	}
	// 返回多的数据，用于增减行
	const dataFiltrate = (newVal, oldVal) => {
		let result = ''
		oldVal.forEach((data) => {
			if (!newVal.some((item) => item === data)) {
				result = data
			}
		})
		return result
	}
	watch(
		() => props.place,
		(newVal, oldVal) => {
			if (!isEmpty(props.dataArray) && isEmpty(formData.value)) {
				formData.value = cloneDeep(props.dataArray)
			} else {
				if (typeof newVal === 'object') {
					if (!isEmpty(formData.value)) {
						formData.value.forEach(() => {
							// 如果包含
							if (!newVal.some((item) => item === item.key)) {
								// 需要减少的
								if (formData.value.length > newVal.length) {
									const deleteData = dataFiltrate(newVal, oldVal)
									remove(formData.value, (item) => item.key === deleteData)
								}
								// 需要增加的
								if (formData.value.length < newVal.length) {
									const deleteData = dataFiltrate(oldVal, newVal)
									// 如果没有，就不增加
									if (!formData.value.some((item) => item === deleteData)) {
										const obj = {
											key: deleteData,
											label: tool.dictTypeData('DEV_SLIDESHOW_PLACE', deleteData),
											whetherToClick: 'DISABLE',
											skipMode: 'URL',
											url: ''
										}
										formData.value.push(obj)
									}
								}
							}
						})
					} else {
						newVal.forEach((item) => {
							const obj = {
								key: item,
								label: tool.dictTypeData('DEV_SLIDESHOW_PLACE', item),
								whetherToClick: 'DISABLE',
								skipMode: 'URL',
								url: ''
							}
							formData.value.push(obj)
						})
					}
				}
			}
		},
		{ immediate: true, deep: true }
	)
	// 获取值，校验不通过返回false
	const getData = () => {
		if (isEmpty(formData.value)) {
			return false
		} else {
			let result = true
			formData.value.forEach((item) => {
				if (item.whetherToClick === 'ENABLE' && isEmpty(item.url)) {
					result = false
				}
			})
			if (result === false) {
				return false
			} else {
				return formData.value
			}
		}
	}
	defineExpose({
		getData
	})
</script>
<style lang="less" scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
</style>
