const responseDiv = document.getElementById('response');
const inputBox = document.getElementById('input');
const micButton = document.getElementById('mic');

// Send user message to backend and get response
async function send(message) {
  const userMessage = message || inputBox.value.trim();
  if (!userMessage) return;

  responseDiv.innerHTML += `<br><strong>You:</strong> ${userMessage}`;


  try {
 const backendURL = "http://192.168.1.9:5000/chat"; // Always use PC IP here for all devices



    const res = await fetch(backendURL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ prompt: userMessage }) // "prompt" must match what your backend expects
    });

    const data = await res.json();
    const reply = data.reply;

    responseDiv.innerHTML += `<br><strong>Jarvis:</strong> ${reply}`;
    speak(reply); // Use TTS
  } catch (e) {
    console.error("Frontend error:", e);
    responseDiv.innerHTML += `<br><strong>Jarvis:</strong> ⚠️ Something went wrong`;
  }

  inputBox.value = '';
}

// Trigger on Send button
function handleSend() {
  send();
}

// Text-to-Speech for Jarvis's reply
function speak(text) {
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = 'en-US';
  speechSynthesis.speak(utterance);
}

// Voice input (Speech-to-Text)
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
    alert("Microphone error: " + event.error);
  };
} else {
  micButton.disabled = true;
  micButton.title = "Speech recognition not supported in this browser";
}
