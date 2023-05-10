package backend.armCode.MCInstructions;

import backend.armCode.MCInstruction;
import backend.operand.RealRegister;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class MCpush extends MCInstruction {
    private HashSet<RealRegister> operands;

    @Override
    public HashSet<Register> getUse() {
        return new HashSet<>(operands);
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
    public String emit() {
        StringBuilder ret = new StringBuilder("PUSH {");
        for (int i=0; i<16; i++)
            if (operands.contains(RealRegister.get(i))){
                ret.append(RealRegister.get(i).emit()).append(", ");
            }
        return ret.substring(0,ret.length()- (operands.size()==0 ?0 :2)) + "}";
    }

    public HashSet<RealRegister> getOperands() {return operands;}

    public MCpush(RealRegister op1) {super(TYPE.PUSH);operands = new HashSet<>();operands.add(op1);}
    public MCpush(RealRegister op1, RealRegister op2) {super(TYPE.PUSH);operands = new HashSet<>();operands.add(op1);operands.add(op2);}
    public MCpush(RealRegister op1, RealRegister op2, RealRegister op3) {super(TYPE.PUSH);operands = new HashSet<>();operands.add(op1);operands.add(op2);operands.add(op3);}
    public MCpush(RealRegister op1, RealRegister op2, RealRegister op3, RealRegister op4) {super(TYPE.PUSH);operands = new HashSet<>();operands.add(op1);operands.add(op2);operands.add(op3);operands.add(op4);}
    public MCpush(HashSet<RealRegister> operands) {super(TYPE.PUSH); this.operands = operands;}
}