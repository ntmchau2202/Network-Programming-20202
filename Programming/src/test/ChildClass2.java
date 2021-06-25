package test;

public class ChildClass2 extends ParentClass {
    private String val2;

    public ChildClass2(String val1, String val2) {
        super(val1);
        this.val2 = val2;
    }

    public String getVal2() {
        return this.val2;
    }
}
