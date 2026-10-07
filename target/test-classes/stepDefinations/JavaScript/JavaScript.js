const readline = require('readline');

function middleValue() {
    let values = [];
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });

    console.log("Enter values one by one. Type 'done' to finish:");

    function askQuestion() {
        rl.question("Enter a number or 'done': ", (input) => {
            if (input.toLowerCase() === 'done') {
                rl.close();
                processValues();
            } else {
                let num = parseInt(input);
                if (!isNaN(num)) {
                    values.push(num);
                    askQuestion();
                } else {
                    console.log("Invalid input. Please enter an integer or 'done' to finish:");
                    askQuestion();
                }
            }
        });
    }

    function processValues() {
        let n = values.length;
        let centralPoint = Math.floor(n / 2);

        let sumLeft = values.slice(0, centralPoint).reduce((acc, val) => acc + val, 0);
        let sumRight = values.slice(centralPoint + 1).reduce((acc, val) => acc + val, 0);

        console.log("Sum of left side:", sumLeft);
        console.log("Sum of right side:", sumRight);

        if (sumLeft === sumRight) {
            console.log("Central point is:", values[centralPoint]);
        } else {
            console.log("Central point is not found");
        }
    }

    askQuestion();
}

function interview() {
    // Finding index of x in array
    let arr = [8, 7, 6, 7, 6, 5, 4, 3, 2, 3, 4, 3];
    let x = 3;

    let index = arr.indexOf(x);
    console.log(index !== -1 ? `Element ${x} found at index ${index}` : `Element ${x} not found in the array.`);

    // Merging and sorting lists
    let A = [6, 2, 4, 0, 0, 0, 0];
    let B = [3, 1, 5];

    let merged = [...A, ...B].filter(num => num !== 0).sort((a, b) => a - b);
    console.log("Merged and Sorted List:", merged);

    // Reversing the list
    let reversed = [...merged].reverse();
    console.log("Reversed List:", reversed);

    // Finding first occurrence of y in array
    let y = 3;
    let index1 = arr.indexOf(y);
    console.log(index1 !== -1 ? `Element ${y} found at index ${index1}` : `Element ${y} not found`);
}

// Calling functions
middleValue();
interview();