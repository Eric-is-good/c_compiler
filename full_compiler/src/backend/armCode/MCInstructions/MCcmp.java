package backend.armCode.MCInstructions;

import backend.armCode.MCInstruction;
import backend.operand.MCOperand;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class MCcmp extends MCInstruction {

    private Register operand1;
    private MCOperand operand2;

    public Register getOperand1() {return operand1;}
    public void setOperand1(Register operand1) {this.operand1 = operand1;}

    public MCOperand getOperand2() {return operand2;}
    public void setOperand2(MCOperand operand2) {this.operand2 = operand2;}

    @Override
    public HashSet<Register> getUse() {
        var set = new HashSet<Register>();
        set.add(operand1);
        if (operand2.isVirtualReg() || operand2.isRealReg())
            set.add(((Register) operand2));
        if (shift != null && shift.getOperand().isVirtualReg())
            set.add(((Register) shift.getOperand()));
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {
        operand1 = map.getOrDefault(operand1, operand1);
        if (operand2.isVirtualReg())
            operand2 = map.getOrDefault(operand2, (Register) operand2);
        if (shift != null && shift.getOperand().isVirtualReg())
            shift.setRegister(map.get(shift.getOperand()));
    }

    @Override
    public void replaceDef(HashMap<Register, Register> map) {}

    @Override
    public String emit() {
        return "CMP " + operand1.emit() + ", " + operand2.emit() + emitShift();
    }

    public MCcmp(Register operand1, MCOperand operand2) {super(TYPE.CMP);this.operand1 = operand1;this.operand2 = operand2;}
}
