// class Calculator{
//     int add(int a, int b){
//         return a + b;
//     }
// }

// public class Demo {
//     public static void main(String[] a){
//         System.out.println("Hello World");

//         Calculator cal = new Calculator();
//         Calculator cal2 = cal;
//         int result = cal.add(1, 3);

//         System.out.println(result);
//     }
// }

// public class Demo {
//     public static void main(String[] a) {

//         // Array
//         // int arr[] = {2,3,4};

//         // System.out.println(arr[0]);

//         // int arr2[] = new int[2];

//         // // By default if we intilized the array size, it'll assign the defualt 0
//         // value upto the elements
//         // System.out.println(arr2[1]);

//         // arr2[1] = 100;

//         // System.out.println(arr2[1]);

//         // Multi-dimensional Array
//         // int arr[][] = new int[3][3];

//         // for (int i = 0; i < 3; i++) {
//         //     for (int j = 0; j < 3; j++) {
//         //         arr[i][j] = (int)(Math.random() * 10);
//         //     }
//         // }

//         // for (int i = 0; i < 3; i++) {
//         //     for (int j = 0; j < 3; j++) {
//         //         System.out.print(arr[i][j]+ " ");
//         //     }

//         //     System.out.println();
//         // }

//         // // Enhanced forloop
//         // for(int num[]: arr){
//         //     for(int n : num){
//         //         System.out.print(n+ " ");
//         //     }
//         //     System.out.println();
//         // }


//         // Jagged Array(Doesnt have the fixed size for the nested array that can be defined different array length)
//         int arr[][] = new int[3][];

//         arr[0] = new int[3];
//         arr[1] = new int[4];
//         arr[2] = new int[1];

//         for (int i = 0; i < arr.length; i++) {
//             for (int j = 0; j < arr[i].length; j++) {
//                 arr[i][j] = (int)(Math.random() * 10);
//             }
//         }

        
//         // Enhanced forloop
//         for(int num[]: arr){
//             for(int n : num){
//                 System.out.print(n+ " ");
//             }
//             System.out.println();
//         }
//     }
// }


// class Student {
//     private int id = 1;
//     static String name;
//     int mark;

//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }
    
// }

// class Demo{
//     public static void main(String[] args) {
//         Student s1 = new Student();
//         s1.id = 1;
//         s1.name = "Kali";
//         s1.mark = 80;

//         Student s2 = new Student();
//         s2.id = 2;
//         s2.name = "Kali2";
//         s2.mark = 39;

//         Student s3 = new Student();
//         s3.id = 3;
//         s3.name = "suresh";
//         s3.mark = 83;

//         Student s[] = new Student[3];
//         s[0] = s1;
//         s[1] = s2;
//         s[2] = s3;

//         for(Student su : s){
//             System.out.println(su.id+ " " + su.name + " "  + su.mark);
//         }
//     }
// }

import Tools.Calc;
import Tools.AdvCalc;

// Inheritance + super keyword usage
class A{
    A(){
        System.out.println("A");
    }

    A(int a){
        System.out.println("a " + a);
    }

    int add(int a, int b){
        return a + b;
    }   
}

class B extends A{
    B(){
        super(10);
        System.out.println("B");
    }

    B(int b){
        this();
        System.out.println("B "+ b);
    }

    int add(int a, int b) {
        return a+b+1;
    }
}

public class Demo extends B{

    public static void main(String[] args) {

        // B b = new B(23);
        // b.add(2, 4);

        // Calc cal = new Calc();
        // cal.add();

        // AdvCalc aCalc = new AdvCalc();
        // aCalc.sub();
        // aCalc.add();

        // Dynamic method dispatch during runtime
        A b = new B();
        int add = b.add(1,3);
        System.out.println("add "+ add);

        b = new A();
        int add1 = b.add(1,3);
        System.out.println("add1 "+ add1);

    }
}