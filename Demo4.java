/**
 * Thread
 */

// class A extends Thread {
// public void run() {
// for (int i = 0; i < 100; i++) {
// System.out.println("show A");
// }
// }
// }

// class B extends Thread {
// public void run() {
// for (int i = 0; i < 100; i++) {
// System.out.println("show B");
// }
// }
// }

// public class Demo4 {

// public static void main(String[] args) {
// System.out.println("Hello");

// A a = new A();
// a.start();
// B b = new B();
// b.start();
// }
// }

// Race condition

class Increment {
    int count = 0;

    public synchronized void increment() {
        count++;
    }
}

public class Demo4 {

    public static void main(String[] args) throws InterruptedException {
        Increment in = new Increment();

        Runnable a = () -> {
            for (int i = 0; i < 1000; i++) {
                in.increment();
            }
        };
        Runnable b = () -> {
            for (int i = 0; i < 1000; i++) {
                in.increment();
            }
        };
        Thread th = new Thread(a);
        Thread th2 = new Thread(b);

        th.start();
        th2.start();

        th.join();
        th2.join();

        Thread.sleep(100);
        System.out.println(in.count);

    }
}