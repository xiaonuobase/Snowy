<template>
  <a-select
    :mode="model"
    showSearch
    :value="selectValue"
    :filter-option="false"
    :placeholder="placeholder"
    :not-found-content="fetching ? undefined : null"
    @search="fetchUser"
    @change="handleChange"
  >
    <a-spin v-if="fetching" slot="notFoundContent" size="small" />
    <a-select-option v-for="d in data" :key="d.value">
      {{ d.text }}
    </a-select-option>
  </a-select>
</template>
<script>
import debounce from 'lodash/debounce'
import { getUserPage } from '@/api/modular/system/userManage'

export default {
  name: 'UserSelect',
  props: {
    placeholder: {
      type: String
    },
    value: {
      type: String
    },
    multiple: {
      type: Boolean,
      default: false
    }
  },
  data() {
    const multiple = this.multiple
    this.fetchUser = debounce(this.fetchUser, 800)
    return {
      data: [],
      fetching: false,
      selectValue: multiple ? [] : undefined,
      model: multiple ? 'multiple' : 'default'
    }
  },
  methods: {
    fetchUser(key) {
      console.log('fetching user', key)
      this.data = []
      this.fetching = true

      const params = {
        pageNo: 1,
        pageSize: 10,
        searchValue: key
      }
      this.userFetching = true

      getUserPage(params).then((res) => {
        this.data = res.data.rows.map(user => ({
          text: `${user.name} ${user.account}`,
          value: user.id
        }))
      }).finally(() => {
        this.fetching = false
      })
    },
    handleChange(value) {
      Object.assign(this, {
        selectValue: value,
        fetching: false
      })
      this.$emit('change', value)
    }
  }
}
</script>
