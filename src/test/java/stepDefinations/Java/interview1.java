package stepDefinations.Java;

import java.util.HashMap;
import java.util.HashSet;

public class interview1 {

    public static void main(String[] args) {
       
        String []Name ={"Krishna Kumar"};
        int n=Name[0].length();
        int i,j;
        int Count=0;
        HashSet<Character> Process= new HashSet<>();
        HashMap<Character,Integer> map = new HashMap<>();
        System.out.println("The length of the array is "+n);
        for(int c=0;c<n;c++)
        {
            map.put(Name[0].charAt(c),c);
        }
        System.out.println("The map is "+map);
        for( i=0;i<n;i++)
        {if(Process.contains(Name[0].charAt(i)))
            {
                continue;
            }
            for( j=0;j<n;j++)
            {
                
                if(Name[0].charAt(i)==Name[0].charAt(j))
                {
                    Count++;
                    Process.add(Name[0].charAt(i));
                }
            }
            if(Count>1)
            {
                System.out.println("The character "+Name[0].charAt(i)+" is duplicate and present "+Count+" times");
            }
            Count =0;
 
        }

    }
}