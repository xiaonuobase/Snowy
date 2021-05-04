<template>
  <a-list
    size="large"
    rowKey="id"
    :loading="loading"
    itemLayout="vertical"
    :dataSource="data"
  >
    <a-list-item :key="item.id" slot="renderItem" slot-scope="item">
      <a-list-item-meta>
        <a slot="title" href="https://vue.ant.design/">{{ item.title }}</a>
        <template slot="description">
          <span>
            <a-tag>Ant Design</a-tag>
            <a-tag>设计语言</a-tag>
            <a-tag>蚂蚁金服</a-tag>
          </span>
        </template>
      </a-list-item-meta>
      <article-list-content :description="item.description" :owner="item.owner" :avatar="item.avatar" :href="item.href" :updateAt="item.updatedAt" />
    </a-list-item>
    <div slot="footer" v-if="data.length > 0" style="text-align: center; margin-top: 16px;">
      <a-button @click="loadMore" :loading="loadingMore">加载更多</a-button>
    </div>
  </a-list>
</template>

<script>
import { ArticleListContent } from '@/components'

export default {
  name: 'Article',
  components: {
    ArticleListContent
  },
  data () {
    return {
      loading: true,
      loadingMore: false,
      data: []
    }
  },
  mounted () {
    this.getList()
  },
  methods: {
    getList () {
      this.data = [
        {
          updatedAt: '2021-05-01 12:00:00',
          title: '小诺',
          owner: '俞宝山',
          description: 'snowy是小诺团队产品',
          href: 'https://xiaonuo.vip',
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/ZiESqWwCXBRQoaPONSJe.png',
          name: '曲丽丽',
          id: 'member1'
        },
        {
          updatedAt: '2021-05-01 12:00:00',
          title: '小诺',
          owner: '徐玉祥',
          description: 'snowy是小诺团队产品',
          href: 'https://xiaonuo.vip',
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/tBOxZPlITHqwlGjsJWaF.png',
          name: '王昭君',
          id: 'member2'
        },
        {
          updatedAt: '2021-05-01 12:00:00',
          title: '小诺',
          owner: '董夏雨',
          description: 'snowy是小诺团队产品',
          href: 'https://xiaonuo.vip',
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/sBxjgqiuHMGRkIjqlQCd.png',
          name: '董娜娜',
          id: 'member3'
        }
      ]
      this.loading = false
    },
    loadMore () {
      this.loadingMore = false
    }
  }
}
</script>

<style scoped>

</style>
