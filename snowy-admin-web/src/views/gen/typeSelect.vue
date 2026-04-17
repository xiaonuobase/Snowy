<template>
	<a-modal v-model:open="visible" title="选择生成类型" :width="700" :footer="null" @cancel="handleCancel">
		<a-row :gutter="[16, 16]" style="padding: 20px 0">
			<a-col :span="12" v-for="item in typeList" :key="item.value">
				<a-card
					hoverable
					:class="{ 'type-card-selected': selectedType === item.value }"
					@click="selectType(item.value)"
				>
					<template #title>
						<div style="display: flex; align-items: center; gap: 8px">
							<component :is="item.icon" style="font-size: 20px; color: #1677ff" />
							<span>{{ item.label }}</span>
							<a-tag v-if="item.tag" :color="item.tagColor">{{ item.tag }}</a-tag>
						</div>
					</template>
					<div style="color: #666; font-size: 13px; min-height: 40px">{{ item.description }}</div>
				</a-card>
			</a-col>
		</a-row>
		<div style="text-align: right; padding-top: 10px">
			<a-button @click="handleCancel" style="margin-right: 8px">取消</a-button>
			<a-button type="primary" :disabled="!selectedType" @click="handleConfirm">确定</a-button>
		</div>
	</a-modal>
</template>

<script setup>
	import { ref } from 'vue'
	import { TableOutlined, ApartmentOutlined, LayoutOutlined, ProfileOutlined } from '@ant-design/icons-vue'

	const visible = ref(false)
	const selectedType = ref('')
	const emit = defineEmits(['confirm'])

	const typeList = [
		{
			value: 'TABLE',
			label: '单表格',
			icon: TableOutlined,
			description: '单张数据库表，生成标准CRUD列表页面',
			tag: '单表',
			tagColor: 'blue'
		},
		{
			value: 'TREE',
			label: '单树表',
			icon: ApartmentOutlined,
			description: '同一张表，左侧树形结构、右侧表格列表，适用于含父子关系的数据（如组织管理）',
			tag: '单表',
			tagColor: 'blue'
		},
		{
			value: 'LEFT_TREE_TABLE',
			label: '左树右表',
			icon: LayoutOutlined,
			description: '两张表，左侧一张表以树形呈现，右侧另一张表以列表呈现（如用户管理）',
			tag: '双表',
			tagColor: 'green'
		},
		{
			value: 'MASTER_DETAIL',
			label: '主子表',
			icon: ProfileOutlined,
			description: '两张表，主表列表展开显示子表记录，适用于一对多关系（如订单-明细）',
			tag: '双表',
			tagColor: 'green'
		}
	]

	const onOpen = () => {
		selectedType.value = ''
		visible.value = true
	}

	const selectType = (type) => {
		selectedType.value = type
	}

	const handleConfirm = () => {
		visible.value = false
		emit('confirm', selectedType.value)
	}

	const handleCancel = () => {
		visible.value = false
	}

	defineExpose({ onOpen })
</script>

<style scoped>
	.type-card-selected {
		border-color: #1677ff;
		box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.2);
	}
</style>
