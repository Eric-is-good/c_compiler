package ir.values.constants;

import ir.types.FloatType;
import ir.values.Constant;

import java.util.HashMap;

/**
 * Class ConstFloat instantiates the IR of float integer constant in source.
 */
public class ConstFloat extends Constant {
    /**
     * The arithmetic value of the constant float.
     */
    private final float val;


    public float getVal() {
        return val;
    }

    //<editor-fold desc="Singleton">
    private ConstFloat(float val) {
        super(FloatType.getType());
        this.val = val;
        this.setName("0x" + Long.toHexString(Double.doubleToLongBits(val)));
    }

    // Instance pools.
    private static final HashMap<Float, ConstFloat> pool = new HashMap<>();

    /**
     * Retrieve an IR Constant instance of given float.
     * @param val Numeric value of the float.
     * @return Corresponding ConstFloat instance created.
     */
    public static ConstFloat get(float val) {
        if (pool.containsKey(val)) {
            return pool.get(val);
        }
        else {
            var newInstance = new ConstFloat(val);
            pool.put(val, newInstance);
            return newInstance;
        }
    }
    //</editor-fold>

    @Override
    public String toString() {
        return this.getType() + " " + this.getName();
    }

    /**
     * Retrieve the FloatType of the ConstFloat.
     * @return the FloatType of the ConstFloat
     */
    @Override
    public FloatType getType() {
        return (FloatType) super.getType();
    }
}
