package backend.operand;

import ir.Value;

public class VirtualExtRegister extends ExtensionRegister{

    private final int name;

    /**
     * This field represents the IR value stored in.
     */
    private Value value;
    private float floatValue;

    @Override
    public String getName() {return Integer.toString(name);}

    public String emit() {return "EVR_" + getName();}

    public Value getValue() {
        return value;
    }

    public VirtualExtRegister(int name, Value value) {
        super(TYPE.EVTR);
        this.name = name;
        this.value = value;
    }

    public VirtualExtRegister(int name, float floatValue) {
        super(TYPE.EVTR);
        this.name = name;
        this.floatValue = floatValue;
    }
}
