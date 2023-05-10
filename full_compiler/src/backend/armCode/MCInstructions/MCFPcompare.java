package backend.armCode.MCInstructions;

import backend.armCode.MCFPInstruction;
import backend.operand.ExtensionRegister;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represent the VCMP. <br/>
 * One parameter constructor will compare to zero as default.
 */
public class MCFPcompare extends MCFPInstruction {

    private ExtensionRegister operand1;
    private ExtensionRegister operand2;

    @Override
    public HashSet<Register> getUse() {
        return new HashSet<>();
    }

    @Override
    public HashSet<Register> getDef() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {}

    @Override
    public void replaceDef(HashMap<Register, Register> map) {}

    @Override
    public HashSet<ExtensionRegister> getExtUse() {
        var set = new HashSet<ExtensionRegister>();
        set.add(operand1);
        set.add(operand2);
        return set;
    }

    @Override
    public HashSet<ExtensionRegister> getExtDef() {
        return new HashSet<>();
    }

    @Override
    public void replaceExtReg(ExtensionRegister old, ExtensionRegister brand_new) {
        if (operand1 == old) operand1 = brand_new;
        if (operand2 == old) operand2 = brand_new;
    }

    @Override
    public String emit() {
        if (operand2 == null) {
            return "VCMP" + emitCond() + ".F32 " + operand1.emit() + ", #0.0";
        }
        else {
            return "VCMP" + emitCond() + ".F32 " + operand1.emit() + ", " + operand2.emit();
        }
    }

    public ExtensionRegister getOperand1() {return operand1;}
    public void setOperand1(ExtensionRegister operand1) {this.operand1 = operand1;}

    public ExtensionRegister getOperand2() {return operand2;}
    public void setOperand2(ExtensionRegister operand2) {this.operand2 = operand2;}

    /**
     * Compare to ZERO only!
     */
    public MCFPcompare(ExtensionRegister operand1) {super(TYPE.VCMP);this.operand1 = operand1;}
    public MCFPcompare(ExtensionRegister operand1, ExtensionRegister operand2) {super(TYPE.VCMP);this.operand1 = operand1;this.operand2 = operand2;}
}
