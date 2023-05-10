package backend.operand;

import java.util.ArrayList;

/**
 * ARM VFP & Advanced SIMD share the same register set, <br/>
 * distinguishing from the ARM core register set (r0-r15). <br/>
 * Those registers are generally referred to as <b>extension registers</b>. <br/>
 * Considering that the competition only define 32 bits floating <br/>
 * point number, we use the s0-s31 extension register for floating point calculate.
 * @see <a href="https://developer.arm.com/documentation/ddi0406/latest/">
 *     ARM Architecture Reference Manual ARMv7 edition </a> A2.6.1 Page:A2-21
 */
public class RealExtRegister extends ExtensionRegister{

    private final int index;

    public int getIndex() {return index;}

    public String getName() {return "s"+index;}

    public String emit() {return "s"+index;}

    //<editor-fold desc="Multition Pattern">
    private RealExtRegister(int index) {
        super(TYPE.ERLR);
        this.index = index;
    }

    static private final ArrayList<RealExtRegister> exts = new ArrayList<>();

    static {
        for (int i=0; i<33; i++)
            exts.add(new RealExtRegister(i));
    }

    static public RealExtRegister get(int i) {return exts.get(i);}
    //</editor-fold>
}
