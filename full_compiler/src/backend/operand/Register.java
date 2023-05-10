package backend.operand;

public abstract class Register extends MCOperand {

    abstract public String getName();

    public Register(TYPE type) {super(type);}
}
