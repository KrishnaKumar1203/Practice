// server.js
require('dotenv').config();
const express = require('express');
const axios = require('axios');
const cors = require('cors');
const path = require('path');
const franc = require('franc'); // Language detector (fixed for v5.0.0)

const app = express();
app.use(express.json());
app.use(cors());
app.use(express.static(path.join(__dirname, 'public')));

const LM_API_URL = process.env.LM_API_URL;
const LM_API_KEY = process.env.LM_API_KEY;
const MODEL_NAME = process.env.MODEL_NAME;

app.post('/chat', async (req, res) => {
  const userPrompt = req.body.prompt;

  try {
    const response = await axios.post(`${LM_API_URL}/chat/completions`, {
      model: MODEL_NAME,
      messages: [
        {
          role: "system",
          content: `You are Jarvis, a helpful and intelligent AI assistant created by Krishna.
Never mention your model name. Never explain your thoughts. Always answer clearly and cleanly.`
        },
        {
          role: "user",
          content: userPrompt
        }
      ],
      temperature: 0.7
    }, {
      headers: {
        'Authorization': `Bearer ${LM_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    let reply = response.data.choices[0].message.content;

    // Replace branding and filler
    reply = reply
      .replace(/DeepSeek-R1(-Lite)?(-Preview)?/gi, 'Jarvis')
      .replace(/DeepSeek/gi, 'Jarvis')
      .replace(/Chinese Company DeepSeek/gi, 'Krishna')
      .replace(/(?:Hmm[.,!]?|Let me think[.,!]?|I'm trying to think|Maybe I should|Wait, but|Let me consider)[\s,:-]*/gi, '')
      .trim();

    // Wrap Java code block if it contains Java but not already wrapped
    const hasJavaCode = /public\s+class\s+\w+/i.test(reply) && !reply.includes('```java');
    if (hasJavaCode) {
      reply = `\`\`\`java\n${reply}\n\`\`\``;
    }

    // Remove repeated lines
    const lines = reply.split('\n');
    const seen = new Set();
    const uniqueLines = lines.filter(line => {
      const trimmed = line.trim();
      if (seen.has(trimmed)) return false;
      seen.add(trimmed);
      return true;
    });

    reply = uniqueLines.join('\n');

    // Optional: log language
    const langCode = franc(reply);
    console.log(`🔍 Detected language: ${langCode}`);

    res.json({ reply });

  } catch (err) {
    console.error("Error:", err.response?.data || err.message);
    res.status(500).json({ error: '❌ Failed to get response from LM Studio' });
  }
});

// Health check
app.get('/', (req, res) => {
  res.send('✅ Jarvis backend is up and running!');
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`✅ Server running at http://localhost:${PORT}`);
});
