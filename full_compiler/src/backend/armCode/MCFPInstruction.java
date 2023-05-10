package backend.armCode;

import backend.operand.ExtensionRegister;

import java.util.HashSet;

public abstract class MCFPInstruction extends MCInstruction{

    abstract public HashSet<ExtensionRegister> getExtUse();

    abstract public HashSet<ExtensionRegister> getExtDef();

    abstract public void replaceExtReg(ExtensionRegister old, ExtensionRegister brand_new);

    public MCFPInstruction(TYPE type) {
        super(type);
    }
}
