<template>
  <a-card :bordered="false" :bodyStyle="tstyle">
    <slot name="content"></slot>
  </a-card>
</template>

<script>
    export default {
        name: 'XCard',
      data() {
        return {
          tstyle: { 'padding-bottom': '0px', 'margin-bottom': '10px' }
        }
      }
    }
</script>
