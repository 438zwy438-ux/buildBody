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
              <div v-if="msg.role === 'user'" class="message-text">{{ msg.content }}</div>
              <div v-else class="message-text markdown-content" v-html="renderMarkdown(msg.content)"></div>
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
        <!-- 气泡提示（每10秒显示一次） -->
        <Transition name="slide-fade">
          <div v-if="showBubble" class="speech-bubble">
            <div class="bubble-content">
              <span>你好朋友，我是你的专属健身ai客服需要帮忙吗</span>
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
             <img src="/images/ai图标.jpg" alt="AI助手" class="ai-icon-img" />
             <div class="pulse-ring"></div>
           </div>
        </el-tooltip>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { Close, ChatDotRound, Promotion, Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'

const isOpen = ref(false)
const inputMessage = ref('')
const messages = ref([])
const loading = ref(false)
const chatContentRef = ref(null)
const showBubble = ref(true)
let bubbleTimer = null
let bubbleHideTimer = null

const renderMarkdown = (content) => {
  return marked(content)
}

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    showBubble.value = false
    if (bubbleHideTimer) {
      clearTimeout(bubbleHideTimer)
    }
    scrollToBottom()
  } else {
    showBubble.value = true
    startBubbleHideTimer()
  }
}

const startBubbleHideTimer = () => {
  if (bubbleHideTimer) {
    clearTimeout(bubbleHideTimer)
  }
  bubbleHideTimer = setTimeout(() => {
    showBubble.value = false
  }, 5000)
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
    content: '',
    images: []
  })

  try {
    const token = localStorage.getItem('token')
    const headers = {
      'Content-Type': 'application/json'
    }
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    const response = await fetch(`/api/ai/streamChat?message=${encodeURIComponent(userMessage)}`, {
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
            try {
              const jsonData = JSON.parse(content)
              if (jsonData.content) {
                messages.value[aiMessageIndex].content += jsonData.content
                
                const imageUrls = extractImageUrls(messages.value[aiMessageIndex].content)
                messages.value[aiMessageIndex].images = imageUrls.map(url => ({ url }))
                
                scrollToBottom()
              }
            } catch (e) {
              messages.value[aiMessageIndex].content += content
              scrollToBottom()
            }
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

const extractImageUrls = (markdown) => {
  const imageRegex = /!\[.*?\]\((.*?)\)/g
  const urls = []
  let match
  while ((match = imageRegex.exec(markdown)) !== null) {
    urls.push(match[1])
  }
  return urls
}

onMounted(() => {
  messages.value = [
    {
      role: 'assistant',
      content: '您好！我是AI健身助手，有什么健身问题可以帮您解答吗？'
    }
  ]
  
  // 启动定时器，每10秒显示一次气泡提示
  startBubbleTimer()
  
  // 5秒后自动隐藏第一次显示的气泡提示
  bubbleHideTimer = setTimeout(() => {
    showBubble.value = false
  }, 5000)
})

const startBubbleTimer = () => {
  if (bubbleTimer) {
    clearInterval(bubbleTimer)
  }
  bubbleTimer = setInterval(() => {
    if (!isOpen.value) {
      showBubble.value = true
      startBubbleHideTimer()
    }
  }, 10000)
}

onUnmounted(() => {
  if (bubbleTimer) {
    clearInterval(bubbleTimer)
  }
  if (bubbleHideTimer) {
    clearTimeout(bubbleHideTimer)
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
  overflow: hidden;
}

.floating-ball:hover {
  transform: scale(1.15);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.7);
  animation: none;
}

.ai-icon-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  display: block;
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

.markdown-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3) {
  margin: 12px 0 8px 0;
  font-weight: 600;
}

.markdown-content :deep(h1) {
  font-size: 1.4em;
}

.markdown-content :deep(h2) {
  font-size: 1.2em;
}

.markdown-content :deep(h3) {
  font-size: 1.1em;
}

.markdown-content :deep(p) {
  margin: 8px 0;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin: 8px 0;
  padding-left: 20px;
}

.markdown-content :deep(li) {
  margin: 4px 0;
}

.markdown-content :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.markdown-content :deep(pre) {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
}

.markdown-content :deep(pre) code {
  background: none;
  padding: 0;
}

.markdown-content :deep(strong) {
  font-weight: 600;
}

.markdown-content :deep(em) {
  font-style: italic;
}

.markdown-content :deep(blockquote) {
  border-left: 4px solid #667eea;
  padding-left: 12px;
  margin: 8px 0;
  color: #666;
}

.markdown-content :deep(img) {
  width: 200px !important;
  height: auto !important;
  border-radius: 12px;
  margin: 8px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: block;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #e8e8e8;
  background: #f8f8f8;
  object-fit: cover;
  max-width: none !important;
}

.markdown-content :deep(img):hover {
  transform: scale(1.02);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.message-images {
  display: flex;
   gap: 12px;
  margin-top: 12px;
}

.message-image {
  width: 200px;
  height: auto;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  background: #f8f8f8;
  display: block;
}

.message-image:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
  font-size: 24px;
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