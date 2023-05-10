package backend.armCode.MCInstructions;

import backend.armCode.MCInstruction;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class is an aggregation of MLA, MLS. <br/>
 * &emsp; - MLA/MLS Rd, Rm, Rs, Rn     @ Rd := low32(Rn +/- Rm * Rs) <br/>
 * &emsp; - SMMLA/SMMLS Rd, Rm, Rs, Rn     @ Rd := Rn +/- high32(Rm * Rs) <br/>
 * @see <a href="https://developer.arm.com/documentation/ddi0406/latest/">
 *     ARM Architecture Reference Manual ARMv7 edition </a> <br/>
 *     A8.6.94 MLA, A8.6.95 MLS
 */
public class MCFma extends MCInstruction {

    private Register accumulate;
    private Register multiple_1;
    private Register multiple_2;
    private Register dst;

    @Override
    public HashSet<Register> getUse() {
        var set = new HashSet<Register>();
        set.add(multiple_1);
        set.add(multiple_2);
        set.add(accumulate);
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        var set = new HashSet<Register>();
        set.add(dst);
        return set;
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {
        multiple_1 = map.getOrDefault(multiple_1, multiple_1);
        multiple_2 = map.getOrDefault(multiple_2, multiple_2);
        accumulate = map.getOrDefault(accumulate, accumulate);
    }

    @Override
    public void replaceDef(HashMap<Register, Register> map) {
        dst = map.getOrDefault(dst, dst);
    }

    @Override
    public String emit() {
        return type.name() + emitCond() + " " + dst.emit() + ", " + multiple_1.emit() + ", " + multiple_2.emit() + ", " + accumulate.emit();
    }

    //<editor-fold desc="Getter & Setter & Constructor">
    public Register getAccumulate() {return accumulate;}
    public Register getMultiple_2() {return multiple_2;}
    public Register getDst() {return dst;}
    public Register getMultiple_1() {return multiple_1;}

    public void setAccumulate(Register accumulate) {this.accumulate = accumulate;}
    public void setMultiple_1(Register multiple_1) {this.multiple_1 = multiple_1;}
    public void setMultiple_2(Register multiple_2) {this.multiple_2 = multiple_2;}
    public void setDst(Register dst) {this.dst = dst;}

    public MCFma(TYPE type, Register dst, Register multiple_1, Register multiple_2, Register accumulate) {
        super(type);
        this.accumulate = accumulate;
        this.multiple_1 = multiple_1;
        this.multiple_2 = multiple_2;
        this.dst = dst;
    }
    //</editor-fold>
}
