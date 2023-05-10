package backend.armCode.MCInstructions;

import backend.armCode.MCFPInstruction;
import backend.operand.ExtensionRegister;
import backend.operand.Immediate;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represent the VLDR instruction.<br/>
 * Format: VLDR Fd, [Rn{, #&lt;immed&gt;}]
 */
public class MCFPload extends MCFPInstruction {

    private ExtensionRegister dst;
    private Register addr;
    /**
     * The offset must be an integer number between +/- 0 and 1020,<br/>
     * which can be divided by 4.
     */
    private Immediate offset;

    @Override
    public HashSet<Register> getUse() {
        var set = new HashSet<Register>();
        set.add(addr);
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        return new HashSet<>();
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {
        addr = map.getOrDefault(addr, addr);
    }

    @Override
    public void replaceDef(HashMap<Register, Register> map) {}

    @Override
    public HashSet<ExtensionRegister> getExtUse() {
        return new HashSet<>();
    }

    @Override
    public HashSet<ExtensionRegister> getExtDef() {
        var set = new HashSet<ExtensionRegister>();
        set.add(dst);
        return set;
    }

    @Override
    public void replaceExtReg(ExtensionRegister old, ExtensionRegister brand_new) {
        if (dst == old) dst = brand_new;
    }

    @Override
    public String emit() {
        return "VLDR" + emitCond() + ' ' + dst.emit() + ", [" + addr.emit() + (offset==null ?"" :", "+offset.emit()) + "]";
    }

    public ExtensionRegister getDst() {return dst;}
    public Register getAddr() {return addr;}
    public Immediate getOffset() {return offset;}

    public void setDst(ExtensionRegister dst) {this.dst = dst;}
    public void setAddr(Register addr) {this.addr = addr;}
    public void setOffset(Immediate offset) {this.offset = offset;}

    public MCFPload(ExtensionRegister dst, Register addr) {super(TYPE.VLDR);this.dst = dst;this.addr = addr;}
    public MCFPload(ExtensionRegister dst, Register addr, Immediate offset) {super(TYPE.VLDR);this.dst = dst;this.addr = addr;this.offset = offset;}
}