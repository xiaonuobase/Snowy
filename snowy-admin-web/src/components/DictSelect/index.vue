<template>
  <a-select 
    v-if="props.optionType === 'dropdown'"
    :model-value="props.value" 
    :placeholder="props.placeholder" 
    :options="dictOptions" 
    :disabled="props.disabled"
    :size="props.size"
    :allowClear="props.allowClear"
    @change="handleChange" 
  />

  <a-radio-group 
    v-if="props.optionType === 'radio' || props.optionType === 'button'"
    :model-value="props.value" 
    :optionType="props.optionType" 
    :options="dictOptions" 
    :disabled="props.disabled"
    :size="props.size"
    @change="handleChange"
  />
</template>

<script setup>
import tool from '@/utils/tool'


const props = defineProps({
  optionType: {
    type: String,
    default: 'dropdown',
    required: false,
    validator(value) {
        return ['dropdown', 'button', 'radio'].includes(value)
    }
  },
  dictType: {
    type: String,
    default: '',
    required: true
  },
  allowClear: {        // 仅限于下拉框有效
    type: Boolean,
    default: true,
    required: false
  },
  placeholder: {      // 仅限于下拉框有效
    type: String,
    default: '',
    required: false
  },
  size: {
    type: String,
    default: 'middle',
    required: false,
    validator(value) {
        return ['small', 'middle', 'large'].includes(value)
    }
  },
  value: {
    type: [String, null],
    default: null,
    required: true,
  },
  disabled: {
    type: Boolean,
    default: false,
    required: false
  },
})

const emit = defineEmits(['update:value', 'change'])

const dictOptions = ref([])

onMounted(() => {
  dictOptions.value = tool.dictList(props.dictType)
})


const handleChange = (e) => {
  if (props.optionType === 'dropdown') {
    emit('update:value', e)
    emit('change', e)
  }
  else{
    emit('update:value', e.target.value)
    emit('change', e.target.value)
  } 
}
</script>