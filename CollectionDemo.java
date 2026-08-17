import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


class Student implements Comparable<Student>{
    int age;
    String name;

    Student(Integer age, String name){
        this.age = age;
        this.name = name;
    }

    @Override
    public int compareTo(Student o) {
         if(this.age > o.age) return 1;
         return -1;
    }
}   

public class CollectionDemo {
    public static void main(String[] args) {
        // Collection<Integer> co = new ArrayList<Integer>();
        // List<Integer> co = new ArrayList<Integer>();

        // co.add(2);
        // co.add(23);
        // co.add(25);
        // co.add(21);

        // System.out.println(co.get(3));

        // Set<Integer> co = new HashSet<Integer>();
        // Set<Integer> co = new TreeSet<Integer>();

        // co.add(2);
        // co.add(23);
        // co.add(25);
        // co.add(21);
        // co.add(21);
        // co.add(21);

        // System.out.println(co);


        // Map<String, Integer> co = new HashMap<String, Integer>();
        // // Map<String, Integer> co = new Hashtable<String, Integer>();

        // co.put("Kali",2);
        // co.put("Kali2", 23);
        // co.put("Kali3",25);
        // co.put("Kali4", 21);

        // System.out.println(co.get("Kali2"));

        
        // for(String ke : co.keySet()){
        //     System.out.println(co.get(ke));
        // }


        // Comparator & Comparable
        // Comparator<Integer> co = new Comparator<Integer>() {
        //     @Override
        //     public int compare(Integer o1, Integer o2) {
        //         if(o1%10 > 02%10)
        //             return 1;
        //         else
        //             return -1;
        //     }
        // };
        // List<Integer> li = new ArrayList<>();
        
        // li.add(34);
        // li.add(24);
        // li.add(35);
        // li.add(22);

        // Collections.sort(li, co);
        // System.out.println(li);

        // List<Student> st = new ArrayList<>();

        // st.add(new Student(1, "Kali"));
        // st.add(new Student(24, "John"));
        // st.add(new Student(35, "Bob"));
        // st.add(new Student(22, "Alex"));

        // Collections.sort(st);

        // for(Student su : st){
        //     System.out.println(su.name);
        // }

        // Stream<Integer> s = li.stream();

        // s = s.filter(x -> x > 30);

        // s.forEach(x -> System.out.println(x));

        // Parellelstream
        // List<Integer> li = new ArrayList<>(10000);
        // long startSeq = System.currentTimeMillis();
        // int sum2 = li.stream()
        //         .map(i -> i * 2)
        //         .mapToInt(i -> i)
        //         .sum();

        // long endSeq = System.currentTimeMillis();

        // long startPara = System.currentTimeMillis();
        // int sum3 = li.parallelStream()
        //         .map(i -> i * 2)
        //         .mapToInt(i -> i)
        //         .sum();

        // long endPara = System.currentTimeMillis();

        // System.out.println(sum2 + " " + sum3);
        // System.out.println("Seq : " + (endSeq - startSeq));
        // System.out.println("Para : " + (endPara - startPara));

        List<String> li = Arrays.asList("Kali", "Kumar", "King");

        String s = li.stream().filter(x -> x.contains("v")).findFirst().orElse("Not found");
        
        System.out.println(s);


    }
}
