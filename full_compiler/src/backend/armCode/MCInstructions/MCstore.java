package backend.armCode.MCInstructions;

import backend.armCode.MCInstruction;
import backend.operand.MCOperand;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The STR instruction of ARM. <br/>
 * Now just has the pre‐indexed addressing and write option.
 */
public class MCstore extends MCInstruction {

    private Register src;
    private Register addr;
    /**
     * Addressing offset. <br/>
     * In ARM, this can be <br/>
     * &nbsp; - 12 bits immediate, ranging from -4095 to 4095 <br/>
     * &nbsp; - a register
     */
    private MCOperand offset;
    /**
     * Whether write to the sp
     */
    private boolean write;

    @Override
    public HashSet<Register> getUse() {
        var set = new HashSet<Register>();
        set.add(src);
        set.add(addr);
        if (offset != null && offset.isVirtualReg())
            set.add(((Register) offset));
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        var set = new HashSet<Register>();
        if (write)
            set.add(addr);
        return set;
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {
        src = map.getOrDefault(src, src);
        addr = map.getOrDefault(addr, addr);
        if (offset != null && offset.isVirtualReg())
            offset = map.getOrDefault(offset, (Register) offset);
    }

    @Override
    public void replaceDef(HashMap<Register, Register> map) {
        if (write)
            addr = map.getOrDefault(addr, addr);
    }

    public String emit(){
        return "STR " + src.emit() + ", [" + addr.emit() + (offset==null ?"" :", "+offset.emit()) + "]" + (write?"!":"");
    }

    //<editor-fold desc="Getter & Setter">
    public Register getSrc() {return src;}
    public Register getAddr() {return addr;}
    public MCOperand getOffset() {return offset;}

    public void setSrc(Register src) {this.src = src;}
    public void setAddr(Register addr) {this.addr = addr;}
    public void setOffset(MCOperand offset) {this.offset = offset;}
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public MCstore(Register src, Register addr) {super(TYPE.STORE); this.src = src; this.addr = addr;}
    public MCstore(Register src, Register addr, MCOperand offset) {super(TYPE.STORE);this.src = src;this.addr = addr;this.offset = offset;this.write=false;}

    /**
     * Make sure that write is a non-zero number!
     */
    public MCstore(Register src, Register addr, MCOperand offset, boolean write) {super(TYPE.STORE);this.src = src;this.addr = addr;this.offset = offset;this.write=write;}
    public MCstore(Register src, Register addr, Shift shift, ConditionField cond) {super(TYPE.STORE, shift, cond); this.src = src; this.addr = addr;}
    //</editor-fold>
}
