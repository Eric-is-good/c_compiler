package backend.armCode;


import backend.operand.Immediate;
import backend.operand.MCOperand;
import backend.operand.Register;
import ir.Value;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents all instructions of ARM in memory. <br/>
 * All instructions' constructor is about the text info, which will be emitted.
 * The relational info need to be set using setter.
 */
public abstract class MCInstruction {

    public Value val;

    /**
     * The type of Machine Code instruction, which will be emitted directly!
     * No more translate!
     */
    public enum TYPE {
        ADD,
        SUB,
        RSB,
        MUL,
        SDIV,
        SMULL,
        AND,
        ORR,
        MLA,
        MLS,
        SMMLA,
        SMMLS,
        MOV,
        RET,
        BRANCH,
        STORE,
        LOAD,
        PUSH,
        POP,
        CMP,
        VADD,
        VSUB,
        VMUL,
        VMLA,
        VMLS,
        VDIV,
        VNEG,
        VCMP,
        VCVT,
        VMOV,
        VMRS,
        VLDR,
        VSTR,
        VPUSH,
        VPOP
    }

    /**
     * The instance of this class represents the condition field of an instruction in ARM.
     */
    public enum ConditionField {
        EQ,
        NE,
        GE,
        GT,
        LE,
        LT
    }


    //<editor-fold desc="Fields">
    /**
     * This field indicates the type of instruction.
     */
    protected final TYPE type;
    /**
     * The conditional field. <br/>
     * All instructions have this,
     */
    private ConditionField cond;
    /**
     * The shift field. <br/>
     * But in fact, not all instructions have this field.
     * So it's not reasonable to put it here. :(
     */
    protected Shift shift;

    protected MCBasicBlock belongBasicBlock;
    protected MCFunction belongFunc;
    //</editor-fold>

    //<editor-fold desc="Functional">
    abstract public HashSet<Register> getUse();

    abstract public HashSet<Register> getDef();

    abstract public void replaceUse(HashMap<Register, Register> map);

    abstract public void replaceDef(HashMap<Register, Register> map);

    public void insertBefore(MCInstruction inst) {
        belongBasicBlock.insertAt(belongBasicBlock.getIndex(this), inst);
    }

    public void insertAfter(MCInstruction inst) {
        belongBasicBlock.insertAt(belongBasicBlock.getIndex(this)+1, inst);
    }

    public void removeSelf() {belongBasicBlock.removeInst(this);}
    //</editor-fold>


    //<editor-fold desc="Emits">
    abstract public String emit();

    protected String emitCond() {
        if (cond == null)
            return "";
        else
            return cond.name();
    }

    protected String emitShift() {
        if (shift == null)
            return "";
        else
            return ", " + shift.emit();
    }
    //</editor-fold>


    //<editor-fold desc="Getter & Setter">
    public MCBasicBlock getBelongBB() {return belongBasicBlock;}
    public MCFunction getBelongFunc() {return belongFunc;}
    public TYPE getType() {return type;}
    public Shift getShift() {return shift;}
    public ConditionField getCond() {return cond;}

    public void setBelongBB(MCBasicBlock belongBB) {this.belongBasicBlock = belongBB;}
    public void setBelongFunc(MCFunction function) {this.belongFunc = function;}
    public void setShift(Shift shift) {this.shift = shift;}
    public void setCond(ConditionField cond) {this.cond = cond;}
    //</editor-fold>


    //<editor-fold desc="Constructor">
    /* The constructor only initializes the TEXT information of the instruction.
       The relation info must be set use setter.  */
    public MCInstruction(TYPE type) {
        this.type = type;
    }
    public MCInstruction(TYPE type, Shift shift, ConditionField cond) {
        this.type = type;
        this.shift = shift;
        this.cond = cond;
    }
    //</editor-fold>


    /**
     * This class represents the shift operation of the second operand in an instruction.<br/>
     * The shift operand can be <br/>
     * &emsp;&emsp; - 5 bit immediate <br/>
     * &emsp;&emsp; - the last byte of one register <br/>
     * In fact this class should be not replaced here.
     */
    public static class Shift {

        public enum TYPE {
            ASR,//算数右移
            LSR,//逻辑右移
            LSL,//逻辑左移
            ROR,//循环右移
            RRX //扩展循环右移
        }

        //<editor-fold desc="Fields">
        private TYPE type;
        private Immediate immediate;
        private Register register;
        //</editor-fold>

        public String emit() {
            return type.name() + " " + getOperand().emit();
        }


        //<editor-fold desc="Getter & Setter">
        public TYPE getType() {return type;}
        public MCOperand getOperand() {return register==null ?immediate :register;}

        public void setType(TYPE type) {this.type = type;}
        public void setImmediate(int immediate) {this.immediate = new Immediate(immediate); this.register=null;}
        public void setRegister(Register register) {this.register = register; this.immediate=null;}
        //</editor-fold>


        //<editor-fold desc="Constructor">
        public Shift(TYPE type, int imm) {this.type = type; this.immediate = new Immediate(imm);}
        public Shift(TYPE type, Register reg) {this.type = type; this.register = reg;}
        //</editor-fold>

    }
}
