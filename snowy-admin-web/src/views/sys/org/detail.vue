<template>
	<a-card :bordered="false">
		<xn-card-list
			:data-source="cardListData"
			:page="page"
		/>
	</a-card>
</template>

<script setup name="orgDetail">
	import orgApi from '@/api/sys/orgApi'

	const cardListData = ref([])
	const page = ref({
		current: 0,
		size: 0,
		total: 0
	})
	onMounted(() => {
		orgApi.orgPage().then((data) => {
			page.value.current = data.current
			page.value.size = data.size
			page.value.total = data.total
			// 如果自己的数据跟组件对应不上，使用map进行转换即可
			cardListData.value = data.records.map((m) => {
				let color = '#1890FF'
				let a = 0;
				if (a === 1) {
					color = '#f60808'
					a++
				}
				if (a === 4) {
					color = 'green'
					a++
				}
				return {
					title: '高可用服务器',
					subTitle: '使用部门：' + m.name,
					img: 'https://img1.baidu.com/it/u=3581489623,785459337&fm=253&fmt=auto&app=138&f=JPG?w=500&h=360',
					contents: [
						{
							label: '标题',
							value: m.id
						}
					],
					badge: {
						text: m.category,
						color: color
					},
					record: []
				}
			})
		})
	})
</script>
