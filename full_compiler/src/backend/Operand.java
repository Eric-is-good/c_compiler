package backend;

public class Operand {
    public Integer OP = null;
    public Integer CONSTANT = null;

    public String GLOBAL_VAR = null;

    @Override
    public String toString() {
        if (OP != null) {
            return OP +"(%esp)";
        } else if(CONSTANT != null){
            return "$" + CONSTANT;
        } else if(GLOBAL_VAR != null){
            return GLOBAL_VAR;
        }
        else {
            System.out.println("Error: Operand is null");
            return null;
        }
    }
}
