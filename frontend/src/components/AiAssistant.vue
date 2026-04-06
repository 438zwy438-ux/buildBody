·<template>
  <div class="ai-assistant">
    <Transition name="fade">
      <div v-if="isOpen" class="chat-window">
        <div class="chat-header">
          <div class="header-left">
            <el-icon :size="20" class="ai-icon">
              <ChatDotRound />
            </el-icon>
            <span class="chat-title">AI 健身助手</span>
          </div>
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
            placeholder="请输入您的健身问题..."
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
    
    <!-- 悬浮按钮区域 -->
    <Transition name="bounce">
      <div v-if="!isOpen" class="floating-container">
        <!-- 气泡提示（显示5秒后自动隐藏） -->
        <Transition name="slide-fade">
          <div v-if="showBubble" class="speech-bubble">
            <div class="bubble-content">
              <span>Hi，我是您的AI教练，有什么可以帮您？</span>
              <div class="bubble-tail"></div>
            </div>
          </div>
        </Transition>
        
        <!-- 悬浮按钮 -->
        <el-tooltip
          effect="dark"
          content="AI 健身助手"
          placement="left"
          :show-after="500"
        >
          <div
             class="floating-ball"
             @click="toggleChat"
           >
             <el-icon :size="28" class="ai-icon">
               <ChatDotRound />
             </el-icon>
             <div class="pulse-ring"></div>
           </div>
        </el-tooltip>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { Close, ChatDotRound, Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const isOpen = ref(false)
const inputMessage = ref('')
const messages = ref([])
const loading = ref(false)
const chatContentRef = ref(null)
const showBubble = ref(true)
let bubbleTimer = null

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

  const aiMessageIndex = messages.value.length
  messages.value.push({
    role: 'assistant',
    content: ''
  })

  try {
    const token = localStorage.getItem('token')
    const headers = {
      'Content-Type': 'application/json'
    }
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    const response = await fetch(`http://localhost:8080/api/ai/streamChat?message=${encodeURIComponent(userMessage)}`, {
      method: 'GET',
      headers: headers
    })

    if (!response.ok) {
      throw new Error('请求失败')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:') && line !== 'data:') {
          const content = line.substring(5).trim()
          if (content) {
            messages.value[aiMessageIndex].content += content
            scrollToBottom()
          }
        }
      }
    }
  } catch (error) {
    ElMessage.error('发送失败，请重试')
    messages.value[aiMessageIndex].content = '抱歉，发生了错误，请重试。'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  messages.value = [
    {
      role: 'assistant',
      content: '您好！我是AI健身助手，有什么健身问题可以帮您解答吗？'
    }
  ]
  
  // 5秒后自动隐藏气泡提示
  bubbleTimer = setTimeout(() => {
    showBubble.value = false
  }, 5000)
})

onUnmounted(() => {
  if (bubbleTimer) {
    clearTimeout(bubbleTimer)
  }
})
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 9999;
}

.floating-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

/* 气泡对话框样式 */
.speech-bubble {
  position: relative;
  background: white;
  border-radius: 16px;
  padding: 12px 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  max-width: 200px;
  animation: float 3s ease-in-out infinite;
}

.bubble-content {
  position: relative;
  font-size: 12px;
  color: #333;
  line-height: 1.4;
}

.bubble-tail {
  position: absolute;
  right: 20px;
  bottom: -8px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 8px solid white;
}

/* 悬浮按钮样式 */
.floating-ball {
  position: relative;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.5);
  transition: all 0.3s ease;
  color: white;
  animation: breathing 2s ease-in-out infinite;
}

.floating-ball:hover {
  transform: scale(1.15);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.7);
  animation: none;
}

/* 呼吸灯动效 */
.pulse-ring {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border: 2px solid rgba(102, 126, 234, 0.6);
  border-radius: 50%;
  animation: pulse 2s ease-out infinite;
}

.ai-icon {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
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

/* 动画关键帧 */
@keyframes breathing {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.3);
    opacity: 0;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

/* 过渡动画 */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}

.chat-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
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