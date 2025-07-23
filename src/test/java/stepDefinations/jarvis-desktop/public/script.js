const responseDiv = document.getElementById('response');
const inputBox = document.getElementById('input');
const micButton = document.getElementById('mic');
const sendButton = document.getElementById('sendBtn'); // Make sure your HTML has button with id="sendBtn"

// Auto-scroll to latest message
function scrollToBottom() {
  responseDiv.scrollTop = responseDiv.scrollHeight;
}

// Text-to-Speech (Jarvis speaks)
function speak(text) {
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = 'en-US';
  speechSynthesis.speak(utterance);
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
    const backendURL = "http://192.168.1.9:5000/chat"; // Update if hosted elsewhere

    const res = await fetch(backendURL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ prompt: userMessage })
    });

    const data = await res.json();
    const reply = data.reply || "⚠️ No response received.";

    // Replace typing indicator with Jarvis response
    document.getElementById('typing')?.remove();
    responseDiv.innerHTML += `<div><strong>Jarvis:</strong> ${reply}</div>`;
    speak(reply);
  } catch (error) {
    console.error("Frontend error:", error);
    document.getElementById('typing')?.remove();
    responseDiv.innerHTML += `<div><strong>Jarvis:</strong> ⚠️ Error occurred. Please try again.</div>`;
  }

  scrollToBottom();
}

// Handle Send button click
function handleSend() {
  send();
}

// Attach listener to Send button
if (sendButton) {
  sendButton.addEventListener("click", handleSend);
} else {
  console.warn("Send button not found with ID 'sendBtn'");
}

// Handle Enter key in input box
inputBox.addEventListener("keydown", function (event) {
  if (event.key === "Enter") {
    event.preventDefault();
    handleSend();
  }
});

// Voice Input using Web Speech API
const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
if (SpeechRecognition) {
  const recognition = new SpeechRecognition();
  recognition.lang = 'en-US';
  recognition.interimResults = false;
  recognition.maxAlternatives = 1;

  micButton.addEventListener('click', () => {
    recognition.start();
  });

  recognition.onresult = (event) => {
    const transcript = event.results[0][0].transcript;
    inputBox.value = transcript;
    send(transcript);
  };

  recognition.onerror = (event) => {
    alert("🎤 Microphone error: " + event.error);
  };
} else {
  micButton.disabled = true;
  micButton.title = "Speech recognition not supported in this browser";
}
