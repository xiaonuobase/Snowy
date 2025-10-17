<template>
  <a-select 
    v-if="props.optionType === 'dropdown'"
    v-model:value="modelValue" 
    :placeholder="props.placeholder" 
    :options="dictOptions" 
    :disabled="props.disabled"
    :size="props.size"
    :allowClear="props.allowClear"
    @change="handleChange" 
  />

  <a-radio-group 
    v-if="props.optionType === 'radio' || props.optionType === 'button'"
    v-model:value="modelValue"
    :name="props.name" 
    :optionType="props.optionType" 
    :options="dictOptions" 
    :disabled="props.disabled"
    :size="props.size"
    @change="handleChange"
  />

  <a-checkbox-group
    v-if="props.optionType === 'checkbox'"
    v-model:value="modelValue"
    :name="props.name"
    :options="dictOptions" 
    :disabled="props.disabled"
    @change="handleChange"
  />
</template>

<script setup>
import tool from '@/utils/tool'

// 定义双向数据绑定模型
const modelValue = defineModel({
  required: true,
  default: null || [],
  type: [String, Array, null],
})

const props = defineProps({
  optionType: {
    type: String,
    default: 'dropdown',
    required: false,
    validator(value) {
      return ['dropdown', 'button', 'radio', 'checkbox'].includes(value)
    }
  },
  dictType: {
    type: String,
    default: '',
    required: true
  },
  allowClear: {        // 仅限于dropdown有效
    type: Boolean,
    default: true,
    required: false
  },
  placeholder: {      // 仅限于dropdown有效
    type: String,
    default: '',
    required: false
  },
  size: {             // 仅限于dropdown和button有效
    type: String,
    default: 'middle',
    required: false,
    validator(value) {
      return ['small', 'middle', 'large'].includes(value)
    }
  },
  name: {             // 仅限于checkbox、button、radio有效
    type: String,
    default: '',
    required: false
  },
  disabled: {
    type: Boolean,
    default: false,
    required: false
  }
})

const emit = defineEmits(['change'])

const dictOptions = ref([])

onMounted(() => {
  dictOptions.value = tool.dictList(props.dictType)
})


const handleChange = (e) => {
  if (props.optionType === 'dropdown'||props.optionType === 'checkbox') {
    emit('change', e)
  }
  else{
    emit('change', e.target.value)
  } 
}
</script>