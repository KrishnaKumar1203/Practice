package stepDefinations;

import java.util.Arrays;
import java.util.HashMap;

public class Interview {

   public void Interview1(){

   }




    public static String noofaccurance(String s) 
    {
        HashMap<Character, Integer> charCountMap = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }

        StringBuilder r = new StringBuilder();
        for (HashMap.Entry<Character, Integer> entry : charCountMap.entrySet()) {
            r.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
        }

        return r.toString().trim();
    }

    public static void main(String[] args) {
        System.out.println(noofaccurance("KrishnaKumar")); // Output:  k= 2 r=2 no of occurence of each character
    }
}