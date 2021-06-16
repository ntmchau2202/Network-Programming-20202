package test;

import javafx.scene.Parent;
import javafx.util.Pair;

import java.util.ArrayList;

public class ParentClass {
    private ArrayList<Integer> magicList;
    private String test;

    public ParentClass() {
        magicList = new ArrayList<>();
    }

    public String getTest() {
        return test;
    }

    public void addBadEle(int num) {
        magicList.add(num);
    }

    public void addEle(int num) {
        if (magicList.size() <= 0) {
            magicList.add(num);
        } else {
            boolean isAdded = false;
            for (int i = 0; i < magicList.size(); i++) {
                if (magicList.get(i) < num) {
                    magicList.add(i, num);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) {
                magicList.add(num);
            }
        }
    }

    public Pair<Integer, Integer> getMagicPair() {
        Pair<Integer, Integer> magicPair = null;
        for (int i = 0; i < magicList.size() - 1; i++) {
            if (magicList.get(i) - 500 < magicList.get(i + 1)) {
                magicPair = new Pair<>(magicList.get(i), magicList.get(i + 1));
                magicList.remove(i);
                // remember that we remove ele at i + 1 , but after remove ele at i, ele at (i + 1) becomes ele at i
                magicList.remove(i);
                break;
            }
        }
        return magicPair;
    }

    public void viewList() {
        System.out.println("Magic list: ");
        for (int i : magicList) {
            System.out.print(i + ", ");
        }
        System.out.println();
    }

    public ParentClass(String test) {
        this.test = test;
    }

    public static void main(String[] args) {
//        ChildClass cc = new ChildClass("hello", "world");
//
//
//        System.out.println(cc.getVal2());
//        ParentClass pc = (ParentClass) cc;
//        if (pc instanceof ChildClass) {
//            System.out.println("Yes instanceddd");
//        }
//        System.out.println(pc.getTest());
//        ChildClass cc2 = (ChildClass)pc;
//
//        System.out.println(cc2.getTest());
//        System.out.println(cc2.getVal2());
        ParentClass pc = new ParentClass();
//        pc.addBadEle(23);
//        pc.addBadEle(20);
//        pc.addBadEle(16);
//        pc.addBadEle(12);
//        pc.addBadEle(5);
        pc.viewList();
        pc.addEle(1600);
        pc.addEle(1390);

        pc.viewList();
        Pair<Integer, Integer> myPair = pc.getMagicPair();
        if (myPair == null) {
            System.out.println("no pair yet");
        } else {
            System.out.println("my pair: " + myPair.getKey() + "-" + myPair.getValue());
        }
        pc.viewList();
//        myPair = pc.getMagicPair();
//        System.out.println("my pair: " + myPair.getKey() + "-" + myPair.getValue());
//        pc.viewList();
//        myPair = pc.getMagicPair();
//        System.out.println("my pair: " + myPair.getKey() + "-" + myPair.getValue());
//        pc.viewList();
//        myPair = pc.getMagicPair();
//        System.out.println("my pair: " + myPair.getKey() + "-" + myPair.getValue());
//        pc.viewList();
    }
}
