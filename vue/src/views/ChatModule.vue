<template>
  <div class="chat-container">
    <div class="chat-header">
      <h1>Large language model interaction</h1>
    </div>

    <!-- Left-side dropdown to select model version -->
    <div class="model-selector">
      <label for="model">Select Model Version:</label>
      <select v-model="selectedVersion" class="version-selector">
        <!-- ChatGPT Versions -->
        <option v-if="selectedModel === 'chatgpt'" value="o3-mini-high">o3-mini-high</option>
        <option v-if="selectedModel === 'chatgpt'" value="o3-mini">o3-mini</option>
        <option v-if="selectedModel === 'chatgpt'" value="o1">o1</option>
        <option v-if="selectedModel === 'chatgpt'" value="4o">4o</option>

        <!-- Deepseek Versions -->
        <option v-if="selectedModel === 'deepseek'" value="R1">R1</option>
        <option v-if="selectedModel === 'deepseek'" value="V3">V3</option>

        <!-- Gemini Versions -->
        <option v-if="selectedModel === 'gemini'" value="2.0 Pro">2.0 Pro</option>
        <option v-if="selectedModel === 'gemini'" value="2.0 Flash">2.0 Flash</option>
        <option v-if="selectedModel === 'gemini'" value="2.0 Flash-Lite">2.0 Flash-Lite</option>

        <!-- Grok Versions -->
        <option v-if="selectedModel === 'grok'" value="grok-3">Grok-3</option>
        <option v-if="selectedModel === 'grok'" value="grok-2">Grok-2</option>
      </select>
    </div>

    <div class="chat-box">
      <div v-for="(message, index) in chatMessages" :key="index" class="chat-message">
        <!-- User Information -->
        <div v-if="message.from === 'user'" class="message-content message-user">
          <span>{{ message.content }}</span>
        </div>

        <!-- rebot Information -->
        <div v-if="message.from === 'bot'" class="message-content message-bot">
          <span>{{ message.content }}</span>
        </div>
      </div>
    </div>

    <div class="input-container">
      <div class="model-selector">
        <label>
          <input type="radio" value="chatgpt" v-model="selectedModel" /> ChatGPT
        </label>
        <label>
          <input type="radio" value="deepseek" v-model="selectedModel" /> Deepseek
        </label>
        <label>
          <input type="radio" value="grok" v-model="selectedModel" /> Grok
        </label>
        <label>
          <input type="radio" value="gemini" v-model="selectedModel" /> Gemini
        </label> 
      </div>

      <textarea v-model="userQuestion" placeholder="Please enter a question" class="input-field"></textarea>
      <button @click="submitQuestion" class="submit-button">Submit a question</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      selectedModel: 'chatgpt',  
      selectedVersion: 'o3-mini-high', 
      userQuestion: '',
      response: null,
      chatMessages: [],
    };
  },
  methods: {
    async submitQuestion() {
      if (this.userQuestion.trim()) {
        // Add user message to the chat
        this.chatMessages.push({
          from: 'user',
          content: this.userQuestion,
        });
        
        // Clear input field
        const question = this.userQuestion;
        this.userQuestion = '';

        try {
          const res = await axios.post('http://localhost:9090/ask', question, {
            params: {
              LLM: this.selectedModel,    // Send selected model
              model: this.selectedVersion, // Send selected version
            },
            headers: {
              'Content-Type': 'application/json',
            },
          });

          // Add bot response to the chat
          this.chatMessages.push({
            from: 'bot',
            content: res.data,
          });

          // Trigger typing effect for the bot's response
          this.simulateTypingEffect(res.data);
        } catch (error) {
          console.error('请求错误:', error);
          this.chatMessages.push({
            from: 'bot',
            content: '处理您的问题时出错。',
          });
        }
      }
    },
    // Simulate typing effect
    simulateTypingEffect(text) {
      let index = 0;
      const botMessage = this.chatMessages[this.chatMessages.length - 1];
      botMessage.content = '';

      const typingInterval = setInterval(() => {
        botMessage.content += text[index];
        index++;

        if (index === text.length) {
          clearInterval(typingInterval);
        }
      }, 100); // Adjust typing speed here (100ms per character)
    },
  },
};
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  justify-content: flex-start;
}

.chat-header {
  background-color: #f0f0f0;
  padding: 10px;
  text-align: center;
}

.chat-box {
  width: 100%;
  margin: 0 auto;
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #f9f9f9;
  max-height: 400px;
  overflow-y: scroll;
}

.chat-message {
  margin-bottom: 15px;
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
}

.message-content {
  display: flex;
  align-items: center;
  max-width: 100%;
  padding: 10px;
  border-radius: 10px;
  position: relative;
  margin-bottom: 10px;
  word-wrap: break-word;
}

.message-user {
  background-color: #4CAF50;
  color: white;
  margin-left: auto;
  flex-direction: row-reverse;
}

.message-bot {
  background-color: #e0e0e0;
  color: black;
  margin-right: auto;
  flex-direction: row;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-left: 10px;
  margin-right: 10px;
}

.user-avatar {
  margin-left: 10px;
}

.bot-avatar {
  margin-right: 10px;
}

.input-container {
  padding: 10px;
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
}

.model-selector {
  margin-bottom: 10px;
}

.version-selector {
  margin-left: 20px;
  margin-top: 5px;
  font-size: 20px;
}

.input-field {
  width: 100%;
  height: 60px;
  margin-bottom: 10px;
  padding: 10px;
  font-size: 16px;
  resize: none;
  border-radius: 5px;
  border: 1px solid #ddd;
}

.submit-button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
}

.submit-button:hover {
  background-color: #45a049;
}
.model-selector label {
  font-size: 20px;
}
</style>
