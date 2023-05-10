package backend.armCode.MCInstructions;

import backend.armCode.MCInstruction;
import backend.operand.MCOperand;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represent the arithmetical instruction of ARM,
 * including ADD, SUB, RSB, MUL, SDIV. The first operand must be a register. <br/>
 * This class is an aggregation. This is why it starts with a capital.
 */
public class MCBinary extends MCInstruction {

    private Register operand1;
    private MCOperand operand2;
    private Register destination;

    @Override
    public HashSet<Register> getUse() {
        var set = new HashSet<Register>();
        set.add(operand1);
        if (operand2.isVirtualReg() || operand2.isRealReg())
            set.add((Register) operand2);
        if (shift != null && shift.getOperand().isVirtualReg())
            set.add(((Register) shift.getOperand()));
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        var set = new HashSet<Register>();
        set.add(destination);
        return set;
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
    public void replaceDef(HashMap<Register, Register> map) {
        destination = map.getOrDefault(destination, destination);
    }

    @Override
    public String emit() {
        return type.name() + emitCond() + ' ' + destination.emit()
                + ", " + operand1.emit() + ", " + operand2.emit() + emitShift();
    }

    public Register getOperand1() {return operand1;}
    public void setOperand1(Register operand1) {this.operand1 = operand1;}

    public MCOperand getOperand2() {return operand2;}
    public void setOperand2(MCOperand operand2) {this.operand2 = operand2;}

    public Register getDestination() {return destination;}
    public void setDestination(Register destination) {this.destination = destination;}

    public MCBinary(TYPE type, Register destination, Register operand1, MCOperand operand2) {super(type);this.destination = destination;this.operand1 = operand1;this.operand2 = operand2;}
    public MCBinary(TYPE type, Register destination, Register operand1, MCOperand operand2, Shift shift, ConditionField cond) {super(type, shift, cond);this.operand1 = operand1;this.operand2 = operand2;this.destination = destination;}
}
