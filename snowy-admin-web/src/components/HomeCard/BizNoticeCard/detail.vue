<template>
	<xn-form-container title="通知公告详情" :width="900" v-model:open="open" :destroy-on-close="true" @close="onClose">
		<div class="notice-detail-container">
			<!-- 文章头部 -->
			<div class="notice-header">
				<h1 class="notice-title">{{ formData.title }}</h1>
				<div class="notice-meta">
					<span class="meta-item">
						<a-tag :bordered="false" :color="$TOOL.dictTypeColor('BIZ_NOTICE_TYPE', formData.type)">
							{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', formData.type) }}
						</a-tag>
					</span>
					<span class="meta-item" v-if="formData.createUserName">
						<UserOutlined />
						{{ formData.createUserName }}
					</span>
					<span class="meta-item" v-if="formData.createTime">
						<ClockCircleOutlined />
						{{ $TOOL.parseTime(formData.createTime, '{y}-{m}-{d} {h}:{i}') }}
					</span>
				</div>
			</div>

			<!-- 摘要 -->
			<div v-if="formData.digest" class="notice-digest">
				{{ formData.digest }}
			</div>

			<!-- 封面图 -->
			<div v-if="formData.image" class="notice-cover">
				<a-image :src="formData.image" :preview="true" />
			</div>

			<!-- 正文内容 -->
			<div class="notice-content" v-html="formData.content"></div>

			<!-- 附加信息 -->
			<div class="notice-footer">
				<a-divider />
				<div class="footer-info">
					<div v-if="formData.place" class="info-row">
						<span class="info-label">发布位置：</span>
						<a-tag
							v-for="textValue in JSON.parse(formData.place)"
							:key="textValue"
							:color="$TOOL.dictTypeColor('BIZ_NOTICE_PLACE', textValue)"
						>
							{{ $TOOL.dictTypeData('BIZ_NOTICE_PLACE', textValue) }}
						</a-tag>
					</div>
					<div v-if="formData.remark" class="info-row">
						<span class="info-label">备注：</span>
						<span>{{ formData.remark }}</span>
					</div>
					<div v-if="formData.updateUserName && formData.updateTime" class="info-row meta-secondary">
						<span>最后更新：{{ formData.updateUserName }} · {{ $TOOL.parseTime(formData.updateTime, '{y}-{m}-{d} {h}:{i}') }}</span>
					</div>
				</div>
			</div>
		</div>
		<template #footer>
			<a-button type="primary" @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="bizNoticeDetail">
	import { UserOutlined, ClockCircleOutlined } from '@ant-design/icons-vue'
	import bizIndexApi from '@/api/biz/bizIndexApi'
	import { message } from 'ant-design-vue'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	// 表单数据
	const formData = ref({})
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

<style lang="less" scoped>
	.notice-detail-container {
		max-width: 100%;
		padding: 20px 0;
	}

	.notice-header {
		text-align: center;
		padding-bottom: 20px;
		border-bottom: 1px solid #f0f0f0;
		margin-bottom: 20px;
	}

	.notice-title {
		font-size: 28px;
		font-weight: 600;
		line-height: 1.4;
		margin: 0 0 16px 0;
		color: rgba(0, 0, 0, 0.88);
	}

	.notice-meta {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 24px;
		color: rgba(0, 0, 0, 0.45);
		font-size: 14px;
	}

	.meta-item {
		display: flex;
		align-items: center;
		gap: 6px;
	}

	.notice-digest {
		background: #f5f5f5;
		padding: 16px 20px;
		border-left: 4px solid #1890ff;
		margin-bottom: 24px;
		font-size: 15px;
		line-height: 1.6;
		color: rgba(0, 0, 0, 0.65);
	}

	.notice-cover {
		text-align: center;
		margin-bottom: 24px;

		:deep(.ant-image) {
			max-width: 100%;
			max-height: 400px;
			border-radius: 4px;
			overflow: hidden;
		}

		:deep(img) {
			max-width: 100%;
			max-height: 400px;
			height: auto;
			border-radius: 4px;
			object-fit: cover;
		}
	}

	.notice-content {
		font-size: 16px;
		line-height: 1.8;
		color: rgba(0, 0, 0, 0.88);
		margin-bottom: 32px;

		:deep(p) {
			margin-bottom: 16px;
		}

		:deep(img) {
			max-width: 100%;
			height: auto;
			display: block;
			margin: 16px auto;
		}

		:deep(h1),
		:deep(h2),
		:deep(h3),
		:deep(h4),
		:deep(h5),
		:deep(h6) {
			margin-top: 24px;
			margin-bottom: 16px;
			font-weight: 600;
		}

		:deep(ul),
		:deep(ol) {
			padding-left: 28px;
			margin-bottom: 16px;
		}

		:deep(blockquote) {
			border-left: 4px solid #e8e8e8;
			padding-left: 16px;
			margin: 16px 0;
			color: rgba(0, 0, 0, 0.65);
		}

		:deep(code) {
			background: #f5f5f5;
			padding: 2px 6px;
			border-radius: 3px;
			font-family: 'Courier New', Courier, monospace;
		}

		:deep(pre) {
			background: #f5f5f5;
			padding: 12px;
			border-radius: 4px;
			overflow-x: auto;
			margin-bottom: 16px;
		}
	}

	.notice-footer {
		margin-top: 32px;
	}

	.footer-info {
		.info-row {
			margin-bottom: 12px;
			font-size: 14px;
			line-height: 1.6;

			&.meta-secondary {
				color: rgba(0, 0, 0, 0.45);
				margin-top: 16px;
			}
		}

		.info-label {
			font-weight: 500;
			color: rgba(0, 0, 0, 0.65);
			margin-right: 8px;
		}
	}
</style>
