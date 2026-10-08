// server.js
require('dotenv').config();
const express = require('express');
const axios = require('axios');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
const franc = require('franc');
const { default: MDBReader } = require('mdb-reader'); // ✅ Added for Access DB reading

const app = express();
app.use(express.json());
app.use(cors());
app.use(express.static(path.join(__dirname, 'public')));

const LM_API_URL = process.env.LM_API_URL;
const LM_API_KEY = process.env.LM_API_KEY;
const MODEL_NAME = process.env.MODEL_NAME;

// --- Load Memory from Text File ---
let memory = '';
const memoryFilePath = 'memory.txt';
try {
  if (fs.existsSync(memoryFilePath)) {
    memory = fs.readFileSync(memoryFilePath, 'utf-8') + '\n';
    console.log("🧠 Memory loaded from text file.");
  } else {
    console.log("🧠 No memory file found. Starting fresh.");
  }
} catch (err) {
  console.error("❌ Error reading memory file:", err);
}

// --- Load Memory from Access DB ---
function loadChatHistoryFromAccess() {
  try {
    const dbPath = path.join(__dirname, 'DataBase', 'memory.accdb');
    const buffer = fs.readFileSync(dbPath);
    const reader = new MDBReader(buffer);

    const tableName = 'chat_history';
    if (!reader.getTableNames().includes(tableName)) {
      console.warn(`⚠ Table '${tableName}' not found in Access DB`);
      return '';
    }

    const chatTable = reader.getTable(tableName);
    const rows = chatTable.getData();

    let dbMemory = '';
    rows.forEach(row => {
      dbMemory += `User: ${row.user_text}\nJarvis: ${row.ai_response}\n\n`;
    });

    return dbMemory;
  } catch (err) {
    console.error("❌ Failed to load Access chat history:", err);
    return '';
  }
}

// Append DB memory to main memory
const memoryFromDB = loadChatHistoryFromAccess();
if (memoryFromDB) {
  memory += memoryFromDB;
  console.log("🧠 Memory loaded from Access database.");
}

// --- Chat Endpoint ---
app.post('/chat', async (req, res) => {
  const userPrompt = req.body.prompt;

  try {
const messages = [
  {
    role: "system",
    content: `You are Jarvis, a helpful and intelligent AI assistant created by Krishna.
Never mention your model name. Never explain your thoughts. Always answer clearly and cleanly.
`
  },
  {
    role: "system",
    content: `Background knowledge from past chats (use only if relevant to the user's current question):\n${memory}`
  },
  {
    role: "user",
    content: userPrompt
  }
];

    const response = await axios.post(`${LM_API_URL}/chat/completions`, {
      model: MODEL_NAME,
      messages: messages,
      temperature: 0.7
    }, {
      headers: {
        'Authorization': `Bearer ${LM_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    let reply = response.data.choices[0].message.content;

    // --- Identity Override ---
    const identityQuestions = [
      /who\s+are\s+you/i,
      /your\s+name\??/i,
      /what\s+is\s+your\s+name/i,
      /tell\s+me\s+about\s+yourself/i,
      /are\s+you\s+jarvis/i,
      /who\s+created\s+you/i,
    ];
    if (identityQuestions.some(regex => regex.test(userPrompt))) {
      reply = "I am Jarvis, created by Krishna.";
    }

    // --- Custom Replacements ---
    reply = reply
      .replace(/DeepSeek-R1(-Lite)?(-Preview)?/gi, 'Jarvis')
      .replace(/DeepSeek/gi, 'Jarvis')
      .replace(/Chinese Company DeepSeek/gi, 'Krishna')
      .replace(/(?:Hmm[.,!]?|Let me think[.,!]?|I'm trying to think|Maybe I should|Wait, but|Let me consider)[\s,:-]*/gi, '')
      .trim();

    const hasJavaCode = /public\s+class\s+\w+/i.test(reply) && !reply.includes('```java');
    if (hasJavaCode) {
      reply = `\`\`\`java\n${reply}\n\`\`\``;
    }

    const lines = reply.split('\n');
    const seen = new Set();
    reply = lines.filter(line => {
      const trimmed = line.trim();
      if (seen.has(trimmed)) return false;
      seen.add(trimmed);
      return true;
    }).join('\n');

    // Language detection
    const langCode = franc(reply);
    console.log(`🔍 Detected language: ${langCode}`);

    // Save memory if user says "save this information"
    if (/save\s+(this\s+)?information/i.test(userPrompt)) {
      const logToSave = `User: ${userPrompt}\nJarvis: ${reply}\n\n`;
      fs.appendFile(memoryFilePath, logToSave, (err) => {
        if (err) {
          console.error("❌ Error saving memory:", err);
        } else {
          console.log("✅ Memory updated in text file.");
        }
      });
      reply = "I've saved this information for the future.";
    }

    res.json({ reply });

  } catch (err) {
    console.error("❌ Error:", err.response?.data || err.message);
    res.status(500).json({ error: '❌ Failed to get response from LM Studio' });
  }
});

// --- Health Check ---
app.get('/', (req, res) => {
  res.send('✅ Jarvis backend is up and running!');
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`✅ Server running at http://localhost:${PORT}`);
});
