<template>
	<xn-form-container title="详情" :width="1000" v-model:open="open" :destroy-on-close="true" @close="onClose">
		<a-descriptions bordered>
			<a-descriptions-item label="标题">{{formData.title}}</a-descriptions-item>
			<a-descriptions-item label="类型">
				<a-tag :bordered="false" color="success" v-if="formData.type === 'NOTICE'">
					{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', formData.type) }}
				</a-tag>
				<a-tag :bordered="false" color="processing" v-else-if="formData.type === 'ANNOUNCEMENT'">
					{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', formData.type) }}
				</a-tag>
				<a-tag :bordered="false" color="warning" v-else-if="formData.type === 'WARNING'">
					{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', formData.type) }}
				</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="封面图">
				<div v-if="formData.image">
					<a-image :src="formData.image" style="width: 100px; height: 50px;margin-bottom: -10px;margin-top: -10px;" />
				</div>
				<span v-else>未上传</span>
			</a-descriptions-item>
			<a-descriptions-item label="内容"><div v-html="formData.content"></div></a-descriptions-item>
		</a-descriptions>
		<a-descriptions bordered :column="2" class="mt-2">
			<a-descriptions-item label="摘要">{{ formData.digest }}</a-descriptions-item>
			<a-descriptions-item label="备注"><span>{{ formData.remark }}</span></a-descriptions-item>
			<a-descriptions-item label="排序"><span>{{ formData.sortCode }}</span></a-descriptions-item>
			<a-descriptions-item label="发布位置">
				<div v-if="formData.place">
					<a-tag v-for="textValue in JSON.parse(formData.place)" :key="textValue" color="processing">
						{{ $TOOL.dictTypeData('BIZ_NOTICE_PLACE', textValue) }}
					</a-tag>
				</div>
			</a-descriptions-item>
			<a-descriptions-item label="创建人"><span>{{ formData.createUserName }}</span></a-descriptions-item>
			<a-descriptions-item label="创建时间"><span>{{ formData.createTime }}</span></a-descriptions-item>
			<a-descriptions-item label="修改人"><span>{{ formData.updateUserName }}</span></a-descriptions-item>
			<a-descriptions-item label="修改时间"><span>{{ formData.updateTime }}</span></a-descriptions-item>
		</a-descriptions>
        <template #footer>
            <a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
            <a-button type="primary" @click="onClose" :loading="submitLoading">确定</a-button>
        </template>
    </xn-form-container>
</template>

<script setup name="bizNoticeDetail">
    import bizIndexApi from '@/api/biz/bizIndexApi'
	import { message } from 'ant-design-vue'
    // 抽屉状态
    const open = ref(false)
    const emit = defineEmits({ successful: null })
    // 表单数据
    const formData = ref({})
    const submitLoading = ref(false)
    // 打开抽屉
    const onOpen = (id) => {
        open.value = true
        if (id) {
			const param = {
				id: id
			}
			bizIndexApi.bizIndexNoticeDetail(param).then((data) => {
				formData.value = Object.assign({}, data)
			})
        } else {
			message.warning('未查到该信息')
		}
    }
    // 关闭抽屉
    const onClose = () => {
        formData.value = {}
        open.value = false
    }
    // 抛出函数
    defineExpose({
        onOpen
    })
</script>
