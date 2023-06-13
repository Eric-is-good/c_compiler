package backend;

public class Operand {
    public Integer OP = null;
    public Integer CONSTANT = null;

    @Override
    public String toString() {
        if (OP != null) {
            return OP +"(%esp)";
        } else if(CONSTANT != null){
            return "$" + CONSTANT;
        } else {
            System.out.println("Error: Operand is null");
            return null;
        }
    }
}
