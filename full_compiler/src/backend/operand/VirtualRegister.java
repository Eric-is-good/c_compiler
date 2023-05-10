package backend.operand;

import ir.Value;

public class VirtualRegister extends Register{

    private final int name;

    /**
     * This field represents the IR value stored in.
     */
    private Value value;
    private int intValue;

    @Override
    public String getName() {return Integer.toString(name);}

    public String emit() {return "VR_" + getName();}

    public Value getValue() {
        return value;
    }

    public VirtualRegister(int name, Value value) {
        super(TYPE.VTR);
        this.name = name;
        this.value = value;
    }

    public VirtualRegister(int name, int intValue) {
        super(TYPE.VTR);
        this.name = name;
        this.intValue = intValue;
    }
}
