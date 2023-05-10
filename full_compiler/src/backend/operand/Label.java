package backend.operand;

import java.util.ArrayList;

/**
 * This label is created for global variable, not for the basic block or other
 */
public class Label extends MCOperand {

    public enum TAG {
        Int,
        Float
    }

    private final String name;
    private final TAG tag;
    private final ArrayList initial;
    private final int size;

    public boolean isArray() {return size != 1;}

    public int getSize() {return size;}

    public ArrayList getInitial() {return initial;}

    public boolean isInt() {return tag == TAG.Int;}

    @Override
    public String emit() {
        return name;
    }

    public Label(String name, TAG tag, ArrayList initial) {
        super(TYPE.GBV);
        this.name = name;
        this.tag = tag;
        this.initial = initial;
        this.size = initial.size();
    }

    public Label(String name, TAG tag, int size) {
        super(TYPE.GBV);
        this.name = name;
        this.tag = tag;
        this.initial = null;
        this.size = size;
    }
}
