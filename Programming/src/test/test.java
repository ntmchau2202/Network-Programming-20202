package test;

public class test {
    public static void main(String[] args) {
        ABC a = new ABC();
        System.out.println(a.val);
        ABC b = a;
        b.val = "helo";
        System.out.println(a.val);
        System.out.println(b.val);
    }
}
