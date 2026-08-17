// abstract class

// abstract class Car {
//     public abstract void drive();

//     void fly() {
//         System.out.println("Fly...");
//     }
// }

// class NanoCar extends Car {
//     public void drive() {
//         System.out.println("Drive...");
//     }
// }

// public class Demo2 {
//     public static void main(String[] args) {
//         Car c = new NanoCar();
//         c.drive();
//         c.fly();
//     }
// }

// inner class
// class A{
//     int n = 10;

//     static class B{
//         int n2 = 20;
//     }
// }

// class Demo2{
//     public static void main(String[] args) {
//         A a = new A();
//         System.out.println(a.n);

//         A.B b = new  A.B();
//         System.out.println(b.n2);
        
//     }
// }

// Anonymous inner class
// class A{
//     int n = 10;

//     void Test(){
//         System.out.println("A class");
//     }
// }

// class Demo2{
//     public static void main(String[] args) {
//         A a = new A(){
//             void Test(){
//                 System.out.println("B class");
//             }
//         };
//         a.Test();
        
//     }
// }

// enums
enum Status{
    Success,
    Failure
}

// keyvalue enum pairs via constructor and getter/setter methods 
// enum Status {
//     UNAUTHORIZED(401),
//     PAYMENT_REQUIRED(402);

//     private int code;

//     Status(int code) {
//         this.code = code;
//     }

//     public int getCode() {
//         return code;
//     }

//     public static Status fromCode(int code) {
//         for (Status status : Status.values()) {
//             if (status.code == code) {
//                 return status;
//             }
//         }
//         return null;
//     }
// }

// class Demo2{
//     public static void main(String[] args) {
//         // Status s = Status.Failure;

//         // System.out.println(s);

//         int i = 2;
//         switch (i) {
//             case 1:
//                 System.out.println(i);
//                 break;
        
//             // default:
//             //     break;
//         }
//     }
// }

// functional interface with lambda expression

@FunctionalInterface
interface Demo{
    void show();
}


/**
 * Demo2
 */
public class Demo2 {

    public static void main(String[] args) {
        // Demo d = new Demo() {
        //     public void show(){
        //         System.out.println("showed");
        //     }
        // };

        // d.show();

        // Lambda expression with interface
        Demo d = () -> System.out.println("showed");
        d.show();
    }
}