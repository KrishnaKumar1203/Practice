package stepDefinations;

import java.util.Arrays;

public class Interview {

    public Interview(){
        int a[]={6,4,3,2,9,8};
        int b = a.length;
        int c=0;
        //int d[] = new int[b];
        for (int i=0; i<b; i++){
            System.out.println("i located at = "+i);  
                c=a[i];
                a[i]=a[i+1];
                a[i+1]=c;
                i++;
              
            }
        
        for (int i = 0; i<a.length; i++){
            System.out.println(a[i]);
        }
    } 
        	
public void Interview1()    {
int a[]={1,1,0,1,0};
int b = a.length;
int c = 0;
int d[] = new int[b];
for ( int i = 0; i<a.length; i++){
    if (a[i]==0){
d[c]=0;// Place 0 at the next available position from the start
        
        c++;
    
    }else {
    d[b-1]=1; // Place 1 at the next available position from the end
        b--; // Decrement the end pointer
    }
    
}
for (int i = 0; i<d.length; i++){
    System.out.println(d[i]);
}
}
public static void main(String[] args) {
    new Interview();
    new Interview().Interview1();
}

}
