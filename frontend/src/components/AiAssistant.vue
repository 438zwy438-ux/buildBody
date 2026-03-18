<template>
  <div class="ai-assistant">
    <Transition name="fade">
      <div v-if="isOpen" class="chat-window">
        <div class="chat-header">
          <span class="chat-title">AI 智能助手</span>
          <el-button
            class="close-btn"
            type="text"
            :icon="Close"
            @click="toggleChat"
          />
        </div>
        
        <div class="chat-content" ref="chatContentRef">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-content">
              <div class="message-text">{{ msg.content }}</div>
            </div>
          </div>
          <div v-if="loading" class="message assistant">
            <div class="message-content">
              <div class="message-text typing">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题..."
            @keydown.enter.prevent="handleSend"
            :disabled="loading"
          />
          <el-button
            type="primary"
            :icon="Promotion"
            :loading="loading"
            @click="handleSend"
            class="send-btn"
          >
            发送
          </el-button>
        </div>
      </div>
    </Transition>
    
    <Transition name="bounce">
      <div
        v-if="!isOpen"
        class="floating-ball"
        @click="toggleChat"
      >
        <el-icon :size="24">
          <ChatDotRound />
        </el-icon>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { Close, ChatDotRound, Promotion } from '@element-plus/icons-vue'
import { aiChat } from '@/api/ai'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const isOpen = ref(false)
const inputMessage = ref('')
const messages = ref([])
const loading = ref(false)
const chatContentRef = ref(null)
const typingMessage = ref('')

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContentRef.value) {
      chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
    }
  })
}

const handleSend = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  const userMessage = inputMessage.value.trim()
  messages.value.push({
    role: 'user',
    content: userMessage
  })
  
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const history = messages.value.map(msg => ({
      role: msg.role,
      content: msg.content
    }))

    const response = await aiChat({
      userId: userStore.userInfo?.id || 0,
      message: userMessage,
      history: history
    })

    const assistantMessage = response.data
    
    messages.value.push({
      role: 'assistant',
      content: ''
    })
    
    await typeWriterEffect(assistantMessage, messages.value.length - 1)
  } catch (error) {
    ElMessage.error('发送失败，请重试')
  } finally {
    loading.value = false
  }
}

const typeWriterEffect = (text, messageIndex) => {
  return new Promise((resolve) => {
    let index = 0
    const speed = 30
    
    const type = () => {
      if (index < text.length) {
        messages.value[messageIndex].content += text.charAt(index)
        index++
        scrollToBottom()
        setTimeout(type, speed)
      } else {
        resolve()
      }
    }
    
    type()
  })
}

onMounted(() => {
  messages.value = [
    {
      role: 'assistant',
      content: '您好！我是AI智能助手，有什么可以帮助您的吗？'
    }
  ]
})
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 9999;
}

.floating-ball {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  color: white;
}

.floating-ball:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.6);
}

.chat-window {
  position: absolute;
  right: 0;
  bottom: 80px;
  width: 400px;
  height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chat-title {
  font-size: 16px;
  font-weight: 600;
}

.close-btn {
  color: white;
  padding: 4px;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f8f9fa;
}

.message {
  margin-bottom: 16px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 80%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.message.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-text {
  background: white;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-bottom-left-radius: 4px;
}

.typing {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}

.typing span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.chat-input {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #e8e8e8;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input :deep(.el-textarea__inner) {
  resize: none;
  border-radius: 8px;
}

.send-btn {
  height: 76px;
  min-width: 80px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.send-btn:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.bounce-enter-active {
  animation: bounce-in 0.5s;
}

.bounce-leave-active {
  animation: bounce-in 0.5s reverse;
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.chat-content::-webkit-scrollbar {
  width: 6px;
}

.chat-content::-webkit-scrollbar-track {
  background: transparent;
}

.chat-content::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

.chat-content::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}
</style>