package stepDefinations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Practice {

    public static void MiddleValue() {
        Scanner scanner = new Scanner(System.in);
        List<Integer> values = new ArrayList<>();
        
        System.out.println("Enter values one by one. Type 'done' to finish:");

        while (scanner.hasNext()) {
            if (scanner.hasNextInt()) {
                values.add(scanner.nextInt());
            } else if (scanner.next().equalsIgnoreCase("done")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter an integer or 'done' to finish:");
            }
        }

        int n = values.size();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = values.get(i);
        }

        int centralpoint, central2 = 0;
        centralpoint = n / 2;

        int sumleft = 0, sumright = 0;
        for (int i = 0; i < n; i++) {
            if (i < centralpoint) {
                sumleft = sumleft + a[i];
            } else if (i > centralpoint) {
                sumright = sumright + a[i];
            } else {
                central2 = a[i];
            }
        }

        System.out.println("Sum of left side: " + sumleft);
        System.out.println("Sum of right side: " + sumright);
        if (sumleft == sumright) {
            System.out.println("Central point is: " + central2);
        } else {
            System.out.println("Central point is not found");
        }

        scanner.close();
    }
        public static void interview() {
              //   A= {6,2,4,0,0,0,0} 
        // B= {3,1,5} 
        // Output = {1,2,3,4,5,6} 
        //   Input: arr[] = {8, 7, 6, 7, 6, 5, 4, 3, 2, 3, 4, 3}      
    //   x = 3 
// Output: Element 3 found at index 7 

int[] arr = {8, 7, 6, 7, 6, 5, 4, 3, 2, 3, 4, 3};
int x = 3;

int index = Arrays.asList(Arrays.stream(arr).boxed().toArray(Integer[]::new)).indexOf(x);

if (index != -1) {
    System.out.println("Element " + x + " found at index " + index);
} else {
    System.out.println("Element " + x + " not found in the array.");
}
 
        List<Integer> A = new LinkedList<>(Arrays.asList(6, 2, 4, 0, 0, 0, 0));
        List<Integer> B = new LinkedList<>(Arrays.asList(3, 1, 5));
        
        A.addAll(B);
        A.removeIf(n -> n == 0);
        Collections.sort(A);
        
        System.out.println("Merged and Sorted List: " + A);
        Collections.reverse(A);
        System.out.println("Reversed List: " + A);
        List<Integer> arra = Arrays.asList(8, 7, 6, 7, 6, 5, 4, 3, 2, 3, 4, 3);
        int y = 3;

        int index1 = arra.indexOf(y); // Directly finds the first occurrence

        System.out.println(index != -1 ? "Element " + y + " found at index " + index1 : "Element " + y + " not found");
   
        }
    
    public static void main(String[] args) {
        MiddleValue();
        interview();
    }
}

