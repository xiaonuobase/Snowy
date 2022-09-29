<template>
	<div class="ant-dropdown-menu s-tool-column ant-dropdown-content">
		<div class="s-tool-column-header s-tool-column-item">
			<a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange"> 列展示 </a-checkbox>
			<a @click="reset">重置</a>
		</div>
		<a-divider />
		<div class="ant-checkbox-group">
			<div>
				<draggable :list="columnsSetting" item-key="dataIndex" animation="300" @end="emitColumnChange">
					<template #item="{ element }">
						<div class="s-tool-column-item">
							<div class="s-tool-column-handle layout-items-center">
								<more-outlined />
								<more-outlined />
							</div>
							<a-checkbox v-model:checked="element.checked" @change="onChange">{{ element.title }}</a-checkbox>
						</div>
					</template>
				</draggable>
			</div>
		</div>
	</div>
</template>

<script>
	import draggable from 'vuedraggable-es'

	export default {
		components: {
			draggable
		},
		props: {
			columns: {
				type: Array,
				default: () => []
			}
		},
		data() {
			return {
				indeterminate: false,
				checkAll: true,
				columnsSetting: [],
				originColumns: []
			}
		},
		mounted() {
			this.columnsSetting = this.columns.map((value) => ({
				...value,
				checked: true
			}))
			this.originColumns = [...this.columnsSetting]
		},
		methods: {
			reset() {
				this.originColumns = [...this.columnsSetting]
				this.indeterminate = false
				this.checkAll = true
				this.emitColumnChange()
			},
			onChange() {
				const checkedList = this.columnsSetting.filter((value) => value.checked)
				this.indeterminate = Boolean(checkedList.length) && checkedList.length < this.columnsSetting.length
				this.checkAll = checkedList.length === this.columnsSetting.length
				this.emitColumnChange()
			},
			onCheckAllChange(e) {
				e.preventDefault()
				const val = e.target.checked
				Object.assign(this, {
					indeterminate: false,
					checkAll: val,
					columnsSetting: this.columns.map((value) => ({
						...value,
						checked: val
					}))
				})
				this.emitColumnChange()
			},
			emitColumnChange() {
				// eslint-disable-next-line vue/require-explicit-emits
				this.$emit('columnChange', this.columnsSetting)
			}
		}
	}
</script>

<style lang="less" scoped></style>
