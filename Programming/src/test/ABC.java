package test;

public class ABC {
    public CAB val;
    
    public ABC(CAB val) {
    	this.val = val;
    }
   
    public void info() {
    	System.out.println(val.val1);
    	System.out.println(val.val2);
    }
    
    public static void main(String[] args) {
    	ABC newABC = new ABC(new CAB("hello", "world"));
    	CAB smt = newABC.val;
    	smt = new CAB("haha", "world");
//    	smt.val1 = "haha";
    	newABC.info();
    	
    }
}
