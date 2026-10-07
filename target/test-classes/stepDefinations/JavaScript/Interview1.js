function interview1(){
const s = " I love javaa codingggg";
const words = s.split(" ");

let maxLength = 0;
let largestWords = [];

// Find the maximum length
for (const word of words) {
    if (word.length > maxLength) {
        maxLength = word.length;
    }
}

// Collect all words with the maximum length
for (const word of words) {
    if (word.length === maxLength) {
        largestWords.push(word);
    }
}

// Print the largest word(s)
for (const word of largestWords) {
    console.log(`Largest word is: ${word} with length ${maxLength}`);
}
}
interview1();