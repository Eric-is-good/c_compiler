package backend.armCode.MCInstructions;

import backend.armCode.MCFPInstruction;
import backend.operand.ExtensionRegister;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class is a aggregation of the BINARY data-processing <br/>
 * instruction in ARM, including the VADD, VSUB, VMUL, VDIV. <br/>
 * MCFP = Machine Code Float Point
 * @see <a href="https://developer.arm.com/documentation/ddi0406/latest/">
 *     ARM Architecture Reference Manual ARMv7 edition </a> <br/>
 *     A4.14 Page: A4-38
 */
public class MCFPBinary extends MCFPInstruction {

    private ExtensionRegister operand1;
    private ExtensionRegister operand2;
    private ExtensionRegister destination;

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
        var set = new HashSet<ExtensionRegister>();
        set.add(destination);
        return set;
    }

    @Override
    public void replaceExtReg(ExtensionRegister old, ExtensionRegister brand_new) {
        if (operand1 == old) operand1 = brand_new;
        if (operand2 == old) operand2 = brand_new;
        if (destination == old) destination = brand_new;
    }

    public String emit() {
        return type.name() + emitCond() + ".F32 " + destination.emit()
                + ", " + operand1.emit() + ", " + operand2.emit();
    }

    public ExtensionRegister getOperand1() {return operand1;}
    public ExtensionRegister getOperand2() {return operand2;}
    public ExtensionRegister getDestination() {return destination;}

    public MCFPBinary(TYPE type, ExtensionRegister destination, ExtensionRegister operand1, ExtensionRegister operand2) {
        super(type);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.destination = destination;
    }
}
