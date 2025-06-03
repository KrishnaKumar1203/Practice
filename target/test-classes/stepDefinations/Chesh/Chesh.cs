using System;
using System.Collections.Generic;
using System.Linq;

class Program
{
    static void Main(string[] args)
    {
        MiddleValue();
        Interview();
    }

    static void MiddleValue()
    {
        List<int> values = new List<int>();

        Console.WriteLine("Enter values one by one. Type 'done' to finish:");

        while (true)
        {
            Console.Write("Enter a number or 'done': ");
            string input = Console.ReadLine();

            if (input.ToLower() == "done")
            {
                ProcessValues(values);
                break;
            }

            if (int.TryParse(input, out int num))
            {
                values.Add(num);
            }
            else
            {
                Console.WriteLine("Invalid input. Please enter an integer or 'done' to finish.");
            }
        }
    }

    static void ProcessValues(List<int> values)
    {
        int n = values.Count;
        int centralPoint = n / 2;

        int sumLeft = values.Take(centralPoint).Sum();
        int sumRight = values.Skip(centralPoint + 1).Sum();

        Console.WriteLine("Sum of left side: " + sumLeft);
        Console.WriteLine("Sum of right side: " + sumRight);

        if (sumLeft == sumRight && centralPoint < values.Count)
        {
            Console.WriteLine("Central point is: " + values[centralPoint]);
        }
        else
        {
            Console.WriteLine("Central point is not found");
        }
    }

    static void Interview()
    {
        // Finding index of x in array
        int[] arr = { 8, 7, 6, 7, 6, 5, 4, 3, 2, 3, 4, 3 };
        int x = 3;

        int index = Array.IndexOf(arr, x);
        Console.WriteLine(index != -1 ? $"Element {x} found at index {index}" : $"Element {x} not found in the array.");

        // Merging and sorting lists
        int[] A = { 6, 2, 4, 0, 0, 0, 0 };
        int[] B = { 3, 1, 5 };

        var merged = A.Concat(B).Where(num => num != 0).OrderBy(num => num).ToList();
        Console.WriteLine("Merged and Sorted List: " + string.Join(", ", merged));

        // Reversing the list
        var reversed = merged.AsEnumerable().Reverse().ToList();
        Console.WriteLine("Reversed List: " + string.Join(", ", reversed));

        // Finding first occurrence of y in array
        int y = 3;
        int index1 = Array.IndexOf(arr, y);
        Console.WriteLine(index1 != -1 ? $"Element {y} found at index {index1}" : $"Element {y} not found");
    }
}