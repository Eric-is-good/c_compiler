package backend.operand;

/**
 * This class represent a floating point immediate number in ARM assemble. <br/>
 * ARM limits the VMOV immediate can ONLY be <b>+/- m * 2^(-n)</b>, <br/>
 * which 16 <= m <= 31, 0 <= n <= 7
 * @see <a href="https://developer.arm.com/documentation/ddi0406/latest/">
 *     ARM Architecture Reference Manual ARMv7 edition </a> A7.5.1 Page: A7-25
 */
public class FPImmediate extends MCOperand{

    private float floatValue;

    public float getFloatValue() {return floatValue;}
    public void setFloatValue(float floatValue) {this.floatValue = floatValue;}

    public String emit() {return "#" + floatValue;}

    public FPImmediate(float floatValue) {
        super(TYPE.FP);
        this.floatValue = floatValue;
    }
}
