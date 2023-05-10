package backend.armCode.MCInstructions;

import backend.armCode.MCBasicBlock;
import backend.armCode.MCFunction;
import backend.armCode.MCInstruction;
import backend.operand.ExtensionRegister;
import backend.operand.RealExtRegister;
import backend.operand.RealRegister;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.IntStream;

public class MCbranch extends MCInstruction {

    private String target;
    private boolean withLink;

    private MCFunction targetFunc;
    private MCBasicBlock targetBB;

    public boolean isBranch() {return !withLink;}

    public String emit() {
        if (withLink)
            return "BL " + target;
        else
            return "B" + emitCond() + " " + target;
    }

    @Override
    public HashSet<Register> getUse() {
        var set = new HashSet<Register>();
        if (withLink) {
            switch (targetFunc.getIRFunction().getName()) {
                case "starttime", "stoptime" -> set.add(RealRegister.get(0));
                default -> IntStream.range(0, targetFunc.getAPVCR().size()).forEach(x -> set.add(RealRegister.get(x)));
            }
            /* lr <- pc */
            set.add(RealRegister.get(15));
        }
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        var set = new HashSet<Register>();
        if (withLink) {
            IntStream.range(0, 4).forEach(x -> set.add(RealRegister.get(x)));
            /* lr <- pc */
            set.add(RealRegister.get(14));
        }
        return set;
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {}

    @Override
    public void replaceDef(HashMap<Register, Register> map) {}

    public HashSet<ExtensionRegister> getExtUse() {
        var set = new HashSet<ExtensionRegister>();
        if (withLink)
            IntStream.range(0, targetFunc.getAPVER().size()).forEach(x -> set.add(RealExtRegister.get(x)));
        return set;
    }

    public HashSet<ExtensionRegister> getExtDef() {
        var set = new HashSet<ExtensionRegister>();
        if (withLink)
            IntStream.range(0, 16).forEach(x -> set.add(RealExtRegister.get(x)));
        return set;
    }

    public MCFunction getTargetFunc() {return targetFunc;}
    public void setTargetFunc(MCFunction targetFunc) {this.targetFunc = targetFunc;}

    public MCBasicBlock getTargetBB() {return targetBB;}
    public void setTargetBB(MCBasicBlock targetBB) {this.targetBB = targetBB;}

    public MCbranch(MCFunction target) {
        super(TYPE.BRANCH);
        this.target = target.emit();
        withLink = true;
        targetFunc = target;
    }

    public MCbranch(MCBasicBlock target) {
        super(TYPE.BRANCH);
        this.target = target.emit();
        withLink = false;
        targetBB = target;
    }

    public MCbranch(MCBasicBlock target, ConditionField cond) {
        super(TYPE.BRANCH, null, cond);
        this.target = target.emit();
        withLink = false;
        targetBB = target;
    }
}