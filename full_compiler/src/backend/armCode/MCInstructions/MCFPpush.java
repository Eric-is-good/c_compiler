package backend.armCode.MCInstructions;

import backend.armCode.MCFPInstruction;
import backend.operand.ExtensionRegister;
import backend.operand.RealExtRegister;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class MCFPpush extends MCFPInstruction {

    private HashSet<RealExtRegister> operands;

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
        return new HashSet<>(operands);
    }

    @Override
    public HashSet<ExtensionRegister> getExtDef() {
        return new HashSet<>();
    }

    @Override
    public void replaceExtReg(ExtensionRegister old, ExtensionRegister brand_new) {
    }

    @Override
    public String emit() {
        StringBuilder ret = new StringBuilder("VPUSH {");
        for (int i=0; i<32; i++)
            if (operands.contains(RealExtRegister.get(i))){
                ret.append(RealExtRegister.get(i).emit()).append(", ");
            }
        return ret.substring(0,ret.length()- (operands.size()==0 ?0 :2)) + "}";
    }

    public MCFPpush(HashSet<RealExtRegister> operands) {
        super(TYPE.VPUSH);
        this.operands = operands;
    }
}
