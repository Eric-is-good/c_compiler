package backend.armCode.MCInstructions;

import backend.armCode.MCInstruction;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class MCSmull extends MCInstruction {

    private Register low;
    private Register high;
    private Register Rm;
    private Register Rn;

    @Override
    public HashSet<Register> getUse() {
        var set = new HashSet<Register>();
        set.add(Rm);
        set.add(Rn);
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        var set = new HashSet<Register>();
        set.add(high);
        set.add(low);
        return set;
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {
        Rm = map.getOrDefault(Rm, Rm);
        Rn = map.getOrDefault(Rn, Rn);
    }

    @Override
    public void replaceDef(HashMap<Register, Register> map) {
        high = map.getOrDefault(high, high);
        low = map.getOrDefault(low, low);
    }

    @Override
    public String emit() {
        return "SMULL" + emitCond() + " " + low.emit() + ", " + high.emit()
                + ", " + Rm.emit() + ", " + Rn.emit();
    }

    public Register getLow() {return low;}
    public Register getHigh() {return high;}
    public Register getRm() {return Rm;}
    public Register getRn() {return Rn;}

    public MCSmull(Register low, Register high, Register rm, Register rn) {
        super(TYPE.SMULL);
        this.high = high;
        this.low = low;
        Rm = rm;
        Rn = rn;
    }
}
