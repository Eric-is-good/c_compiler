package backend.operand;

/**
 * This class represent an immediate value of ARM assemble.
 */
public class Immediate extends MCOperand {

    private int intValue;

    //<editor-fold desc="Getter & Setter">
    public int getIntValue() {return intValue;}
    public void setIntValue(int intValue) {this.intValue = intValue;}
    //</editor-fold>


    public String emit() {
        return "#" + intValue;
    }


    //<editor-fold desc="Constructor">
    public Immediate(int intValue) {
        super(TYPE.IMM);
        this.intValue = intValue;
    }
    //</editor-fold>
}
