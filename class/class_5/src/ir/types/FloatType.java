package ir.types;

import ir.Type;
import ir.values.constants.ConstFloat;

/**
 * Class representing the floating point type.
 * <br>
 * LLVM IR has to support various kinds of floating point types including float,
 * double, float128, x86_fp80 etc., while in SysY float(32bit) is the only
 * type of bit pattern we need to support.
 *
 * @see <a href="https://llvm.org/docs/LangRef.html#floating-point-types">
 * LLVM LangRef: Floating Point Type</a>
 */
public class FloatType extends Type {

    //<editor-fold desc="Singleton">
    private FloatType() {
    }

    /**
     * The singleton of the FloatType.
     */
    private static final ir.types.FloatType floatType = new ir.types.FloatType();

    /**
     * Retrieve a FloatType.
     *
     * @return float type
     */
    public static ir.types.FloatType getType() {
        return floatType;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "float";
    }

    @Override
    public ConstFloat getZero() {
        return ConstFloat.get(.0f);
    }
}
