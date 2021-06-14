package test;

public class ChildClass extends ParentClass {
    private String val2;

    public ChildClass(String val1, String val2) {
        super(val1);
        this.val2 = val2;
    }

    public String getVal2() {
        return this.val2;
    }
}
