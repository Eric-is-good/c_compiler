package backend.operand;

import java.util.ArrayList;

/**
 * This class represents a real physical register in the CPU.<br/>
 * In ARM, there's 16 registers, in which only 0 to 12 can be used. <br/>
 * The r13 register is used as Stack Pointer,  <br/>
 * the r14 used as Link Register,  <br/>
 * the r15 used as Program Counter,  <br/>
 * the 16th used as CPSR.
 */
public class RealRegister extends Register{

    private final int index;

    public int getIndex() {return index;}

    @Override
    public String getName() {
        return switch (index){
            case 15 -> "pc";
            case 14 -> "lr";
            case 13 -> "sp";
            default -> "r" + index;
        };
    }

    public String emit() {
        return getName();
    }

    //<editor-fold desc="Multition Pattern">
    private RealRegister(int index) {
        super(TYPE.RLR);
        this.index = index;
    }

    static private final ArrayList<RealRegister> regs = new ArrayList<>();

    static {
        for (int i=0; i<16; i++)
            regs.add(new RealRegister(i));
    }

    static public RealRegister get(int i) {return regs.get(i);}
    //</editor-fold>
}
