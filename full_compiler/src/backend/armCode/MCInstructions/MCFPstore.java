package backend.armCode.MCInstructions;

import backend.armCode.MCFPInstruction;
import backend.operand.ExtensionRegister;
import backend.operand.Immediate;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents the VSTR instruction.
 */
public class MCFPstore extends MCFPInstruction {

    private ExtensionRegister src;
    private Register addr;
    /**
     * The offset must be an integer number between 0 and 1020,<br/>
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
        var set = new HashSet<ExtensionRegister>();
        set.add(src);
        return set;
    }

    @Override
    public HashSet<ExtensionRegister> getExtDef() {
        return new HashSet<>();
    }

    @Override
    public void replaceExtReg(ExtensionRegister old, ExtensionRegister brand_new) {
        if (src == old) src = brand_new;
    }

    @Override
    public String emit() {
        return "VSTR" + emitCond() + ' ' + src.emit() + ", [" + addr.emit() + (offset==null ?"" :", "+offset.emit()) + "]";
    }

    public ExtensionRegister getSrc() {return src;}
    public Register getAddr() {return addr;}
    public Immediate getOffset() {return offset;}

    public void setSrc(ExtensionRegister src) {this.src = src;}
    public void setAddr(Register addr) {this.addr = addr;}
    public void setOffset(Immediate offset) {this.offset = offset;}

    public MCFPstore(ExtensionRegister src, Register addr) {super(TYPE.VSTR);this.src = src;this.addr = addr;}
    public MCFPstore(ExtensionRegister src, Register addr, Immediate offset) {super(TYPE.VSTR);this.src = src;this.addr = addr;this.offset = offset;}
}
