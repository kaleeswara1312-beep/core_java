package Exercises;

import java.util.ArrayList;
import java.util.Collection;

class A{
    String name = "Kali";
    int i = 10;
    public native void check();


    void test(){
        this.check();
        System.out.println(i);
    }
}

public class Test {
    public static void main(String[] args) {

        // String name = "Kali";
        // name+= "Kumar";
        // String name2 = new String("Kali");
        // String name3 = name2.concat("Kumar");
        //     System.out.println("H1" + name);
        //     System.out.println("H2" + name3);

        // // name2 += " Kumar";

        // if (name.toString() == name3.toString()) {
        //     System.out.println("Hello World" + name2.charAt(0));
        // }

        // StringBuilder sBuilder = new StringBuilder("Kali");
        // StringBuilder sBuilder2 = new StringBuilder("Kali");

        // // sBuilder.append("Kumar");
        // if(sBuilder.equals(sBuilder2)){
        //     System.out.println("Happy");
        // }

        // String name = "Kali";
        // int i = 10;

        // A a = new A();

        // if(a.name == name){
        //     System.out.println("Happy");
        // }

        // if(a.i == i){
        //     System.out.println("Happy2");
        // }

        Collection<Integer> c = new ArrayList<>();
        A a  = new A();

        a.test();
        c.add(30);
        c.add(30);
        c.add(30);

        for(int i :  c){
            System.out.println(i+2);
        }

        System.out.println(Thread.currentThread());
        Thread th = new Thread();

        th.start();
        th.run();;
    }
}
