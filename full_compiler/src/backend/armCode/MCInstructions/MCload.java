package backend.armCode.MCInstructions;

import backend.armCode.MCInstruction;
import backend.operand.MCOperand;
import backend.operand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class MCload extends MCInstruction {

    private Register dst;
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
        set.add(addr);
        if (offset != null && offset.isVirtualReg())
            set.add(((Register) offset));
        return set;
    }

    @Override
    public HashSet<Register> getDef() {
        var set = new HashSet<Register>();
        set.add(dst);
        if (write)
            set.add(addr);
        return set;
    }

    @Override
    public void replaceUse(HashMap<Register, Register> map) {
        addr = map.getOrDefault(addr, addr);
        if (offset !=null && offset.isVirtualReg())
            offset = map.getOrDefault(offset, (Register) offset);
    }

    @Override
    public void replaceDef(HashMap<Register, Register> map) {
        dst = map.getOrDefault(dst, dst);
        if (write)
            addr = map.getOrDefault(addr, addr);
    }

    public String emit(){
        return "LDR " + dst.emit() + ", [" + addr.emit() + (offset==null ?"" :", "+offset.emit()) + "]" + (write?"!":"");
    }

    //<editor-fold desc="Getter & Setter">
    public Register getDst() {return dst;}
    public Register getAddr() {return addr;}
    public MCOperand getOffset() {return offset;}

    public void setDst(Register dst) {this.dst = dst;}
    public void setAddr(Register addr) {this.addr = addr;}
    public void setOffset(MCOperand offset) {this.offset = offset;}
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public MCload(Register dst, Register addr) {super(TYPE.LOAD); this.dst = dst; this.addr = addr;}
    public MCload(Register dst, Register addr, MCOperand offset) {super(TYPE.LOAD); this.dst = dst; this.addr = addr; this.offset=offset; this.write=false;}
    public MCload(Register dst, Register addr, MCOperand offset, boolean write) {super(TYPE.LOAD); this.dst = dst; this.addr = addr; this.offset=offset; this.write=write;}
    //</editor-fold>
}
