<template>
  <div slot="overlay" class="ant-dropdown-menu s-tool-column ant-dropdown-content">
    <div class="s-tool-column-header s-tool-column-item">
      <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">
        列展示
      </a-checkbox>
      <a @click="reset">重置</a>
    </div>
    <a-divider />
    <div class="ant-checkbox-group">
      <div>
        <draggable v-model="columnsSetting" animation="300" @end="emitColumnChange">
          <div class="s-tool-column-item" v-for="item in columnsSetting" :key="item.title">
            <div class="s-tool-column-handle" >
              <a-icon type="more"/>
              <a-icon type="more"/>
            </div>
            <a-checkbox v-model="item.checked" @change="onChange">{{ item.title }}</a-checkbox>
          </div>
        </draggable>
      </div>
    </div>
  </div>
</template>

<script>
  import draggable from 'vuedraggable'

  export default {
    props: {
      columns: {
        type: Array,
        default: () => ([])
      }
    },
    components: {
      draggable
    },
    data() {
      return {
        indeterminate: false,
        checkAll: true,
        columnsSetting: [],
        originColumns: []
      }
    },
    methods: {
      reset() {
        this.columnsSetting = JSON.parse(JSON.stringify(this.originColumns))
        this.indeterminate = false
        this.checkAll = true
        this.emitColumnChange()
      },
      onChange() {
        const checkedList = this.columnsSetting.filter(value => value.checked)
        this.indeterminate = !!checkedList.length && checkedList.length < this.columnsSetting.length
        this.checkAll = checkedList.length === this.columnsSetting.length
        this.emitColumnChange()
      },
      onCheckAllChange(e) {
        const val = e.target.checked
        Object.assign(this, {
          indeterminate: false,
          checkAll: val,
          columnsSetting: this.columns.map(value => ({ ...value, checked: val }))
        })
        this.emitColumnChange()
      },
      emitColumnChange() {
        this.$emit('columnChange', this.columnsSetting)
      }
    },
    mounted() {
      this.columnsSetting = this.columns.map(value => ({ ...value, checked: true }))
      this.originColumns = JSON.parse(JSON.stringify(this.columnsSetting))
    }
  }
</script>

<style lang="less" scoped>

</style>
