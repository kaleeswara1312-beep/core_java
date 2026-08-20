package Exercises;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@FunctionalInterface
interface Test {
    String myName(String name);

    default void myAge() {

    }

    static void myAddress() {

    }
}

class Student {
    public String myName(String name) {
        return name;
    }
}

class Employee {
    private String name;

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

public class Java8 {
    public static void main(String[] args) {
        // Student s = new Student();
        // Test t = s::myName;

        // System.out.println(t.myName("Kali"));

        // List<String> li = Arrays.asList("Kali", "Kumar", "Kunal", null);

        // Predicate<String> p = new Predicate<String>() {
        // public boolean test(String t){
        // if(t == "Kali") return true;

        // return false;
        // }
        // };
        // System.out.println(li.stream().filter(p::test).collect(Collectors.toList()).size());
        // System.out.println("Kali");
        // li.forEach(System.out::println);
        Optional<String> op2 = Optional.ofNullable(null);

        // print value
        System.out.println("Optional 2: "
                + op2.orElse("Test"));

        LocalDate lD = LocalDate.now();
        System.out.println(lD);

        LocalDateTime lDT = LocalDateTime.now();
        System.out.println(lDT);

        Date date = new Date();

        int year = date.getYear();
        System.out.println(year);

    }
}
