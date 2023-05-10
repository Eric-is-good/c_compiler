package backend.operand;

/**
 * This class represents all the operand that may be used in an instruction,
 * include immediate value, real physical register or virtual register.
 */
public abstract class MCOperand {

    public enum TYPE {
        IMM,
        FP,
        VTR,
        RLR,
        EVTR,
        ERLR,
        GBV
    }
    TYPE type;

    public boolean isImmediate     () {return type == TYPE.IMM;}
    public boolean isVirtualReg    () {return type == TYPE.VTR;}
    public boolean isRealReg       () {return type == TYPE.RLR;}
    public boolean isGlobalVar     () {return type == TYPE.GBV;}
    public boolean isFPImm         () {return type == TYPE.FP;}
    public boolean isVirtualExtReg () {return type == TYPE.EVTR;}
    public boolean isRealExtReg    () {return type == TYPE.ERLR;}

    public MCOperand(TYPE type) {this.type = type;}

    abstract public String emit();
}