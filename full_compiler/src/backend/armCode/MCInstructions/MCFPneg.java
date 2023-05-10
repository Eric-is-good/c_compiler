package backend.armCode.MCInstructions;

import backend.armCode.MCFPInstruction;
import backend.operand.ExtensionRegister;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class MCFPneg extends MCFPInstruction {

    private ExtensionRegister destination;
    private ExtensionRegister operand;

    @Override
    public HashSet<Register> getDef() {
        return new HashSet<>();
    }

    @Override
    public HashSet<Register> getUse() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {}

    @Override
    public void replaceDef(HashMap<Register, Register> map) {}

    @Override
    public HashSet<ExtensionRegister> getExtUse() {
        var set = new HashSet<ExtensionRegister>();
        set.add(operand);
        return set;
    }

    @Override
    public HashSet<ExtensionRegister> getExtDef() {
        var set = new HashSet<ExtensionRegister>();
        set.add(destination);
        return set;
    }

    @Override
    public void replaceExtReg(ExtensionRegister old, ExtensionRegister brand_new) {
        if (destination == old) destination = brand_new;
        if (operand == old) operand = brand_new;
    }

    @Override
    public String emit() {
        return "VNEG" + emitCond() + ".F32 " + destination.emit()
                + ", " + operand.emit();
    }

    public ExtensionRegister getDestination() {return destination;}
    public ExtensionRegister getOperand() {return operand;}

    public MCFPneg(ExtensionRegister destination, ExtensionRegister operand) {
        super(TYPE.VNEG);
        this.destination = destination;
        this.operand = operand;
    }
}
