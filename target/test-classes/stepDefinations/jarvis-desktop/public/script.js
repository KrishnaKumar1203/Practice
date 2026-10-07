const responseDiv = document.getElementById('response');
const inputBox = document.getElementById('input');
const micButton = document.getElementById('mic');
const sendButton = document.getElementById('sendBtn');
const statusSpan = document.getElementById('status');

let lastInputWasVoice = false; // Track if last input was voice

// Scroll helper
function scrollToBottom() {
  responseDiv.scrollTop = responseDiv.scrollHeight;
}

// Text-to-speech (only for voice input)
function speak(text) {
  if (!window.speechSynthesis) return;
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = 'en-US';
  window.speechSynthesis.cancel(); // Cancel any ongoing speech
  window.speechSynthesis.speak(utterance);
}

// Send user input to backend
async function send(message) {
  const userMessage = message || inputBox.value.trim();
  if (!userMessage) return;

  // Append user message and typing indicator
  responseDiv.innerHTML += `<div><strong>You:</strong> ${userMessage}</div>`;
  const typingHTML = `<div id="typing"><em>Jarvis is typing...</em></div>`;
  responseDiv.innerHTML += typingHTML;
  scrollToBottom();

  inputBox.value = '';

  try {
    const backendURL = "http://localhost:5000/chat"; // adjust your backend URL if needed

    const res = await fetch(backendURL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ prompt: userMessage })
    });

    const data = await res.json();
    const reply = data.reply || "⚠️ No response received.";

    // Remove typing indicator
    document.getElementById('typing')?.remove();

    // Show reply with markdown
    const html = marked.parse(reply);
    responseDiv.innerHTML += `<div><strong>Jarvis:</strong> ${html}</div>`;
    document.querySelectorAll('pre code').forEach(block => hljs.highlightElement(block));

    // SPEAK reply only if input was voice
    if (lastInputWasVoice) {
      speak(reply);
    }

  } catch (error) {
    console.error("Frontend error:", error);
    document.getElementById('typing')?.remove();
    responseDiv.innerHTML += `<div><strong>Jarvis:</strong> ⚠️ Error occurred. Please try again.</div>`;
  }

  scrollToBottom();

  // Reset voice input flag, so next input defaults to text unless mic clicked again
  lastInputWasVoice = false;
}

// Send button (text input)
function handleSend() {
  lastInputWasVoice = false;
  send();
}

if (sendButton) {
  sendButton.addEventListener("click", handleSend);
} else {
  console.warn("Send button not found with ID 'sendBtn'");
}

inputBox.addEventListener("keydown", (event) => {
  if (event.key === "Enter") {
    event.preventDefault();
    handleSend();
  }
});

// Voice input via Web Speech API
const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
if (SpeechRecognition) {
  const recognition = new SpeechRecognition();
  recognition.lang = 'en-US';
  recognition.interimResults = false;
  recognition.maxAlternatives = 1;

  micButton.addEventListener('click', () => {
    recognition.start();
    statusSpan.textContent = "🎙️ Listening...";
  });

  recognition.onresult = (event) => {
    const transcript = event.results[0][0].transcript;
    inputBox.value = transcript;
    statusSpan.textContent = "";
    lastInputWasVoice = true;
    send(transcript);
  };

  recognition.onerror = (event) => {
    statusSpan.textContent = "⚠️ Microphone error: " + event.error;
  };

  recognition.onend = () => {
    // If ended without any errors, clear status (you can also choose to keep or update here)
    if (statusSpan.textContent === "🎙️ Listening...") {
      statusSpan.textContent = "";
    }
  };
} else {
  micButton.disabled = true;
  micButton.title = "Speech recognition not supported in this browser";
  statusSpan.textContent = "Speech recognition not supported!";
}
