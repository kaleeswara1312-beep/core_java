package Exercises;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
class A {

}
class Student extends A implements Runnable {
    int id;
    String name;

    
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}

public class Collection1 {
    public static void main(String[] args) {
        // ArrayList ar = new ArrayList<>(10);

        // System.out.println(ar.isEmpty());
        // ar.add("Test");

        // System.out.println(ar.size());
        // System.out.println(ar.add("Checking"));
        // System.out.println(ar.add(2));
        // System.out.println(ar.addAll(ar));
        // System.out.println(ar);
        // System.out.println(ar.contains("Test"));
        // System.out.println(ar.clone());

        // for(Object s : ar){
        // if( s instanceof Integer test){
        // System.out.println(test*2);
        // }
        // }

        // LinkedList li = new LinkedList<>();
        // li.add("Kali");

        // System.out.println(li.getFirst());
        // System.out.println(li.getFirst());

        // LinkedList<String> li = new LinkedList<>();
        // li.add("Kali");
        // li.add("Indra");
        // li.add("kumar");

        // System.out.println(li);

        // Collections.sort(li);
        // System.out.println(li);
        // System.out.println(li.getFirst());

        // Iterator<String> it = li.iterator();

        // while (it.hasNext()) {
        // String s = it.next();
        // System.out.println(s);
        // }

        // Stack<Integer> s = new Stack<>();
        // s.push(1);
        // s.push(2);
        // s.push(3);
        // System.out.println(s.peek());

        // s.push(2);
        // s.pop();
        // System.out.println(s);

        // PriorityQueue<Integer> pq = new PriorityQueue<>();
        // pq.add(20);
        // pq.add(2);
        // pq.add(34);
        // pq.add(1);

        // pq.poll();
        // System.out.println(pq.remove(20));

        // ArrayDeque<Integer> ar = new ArrayDeque<>();
        // ar.add(2);
        // ar.addFirst(34);
        // ar.add(1);

        // ar.removeFirst();
        // System.out.println(ar.peek());
        // System.out.println(ar);

        Student s = new Student(1, "Kali");
        Student s1 = new Student(1, "Kali");
        Set<Student> stu = new HashSet<>();
        stu.add(s);
        stu.add(s);
        
        System.out.println(stu);
    }
}
