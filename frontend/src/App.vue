<template>
  <router-view />
  <AiAssistant />
</template>

<script setup>
import AiAssistant from '@/components/AiAssistant.vue'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
}
</style>