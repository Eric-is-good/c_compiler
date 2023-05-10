package backend.operand;

public abstract class ExtensionRegister extends MCOperand{

    abstract public String getName();

    public ExtensionRegister(TYPE type) {super(type);}
}
