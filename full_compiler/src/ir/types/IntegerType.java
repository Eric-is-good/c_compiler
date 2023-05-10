package ir.types;

import ir.Type;
import ir.values.constants.ConstInt;

/**
 * Class to represent integer types, including the built-in integer types: i1 and i32.
 *
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/DerivedTypes.h#L40">
 * LLVM LangRef: Integer Type</a>
 */
public class IntegerType extends Type {

    /**
     * Field to represent the bit width of the integer type (1 or 32).
     */
    private int bitWidth;

    /**
     * Singleton for i32. Can be retrieved only by getI32().
     */
    private static final ir.types.IntegerType i32 = new ir.types.IntegerType(32);

    /**
     * Singleton for i32. Can be retrieved only by getI1().
     */
    private static final ir.types.IntegerType i1 = new ir.types.IntegerType(1);


    private IntegerType(int bitWidth) {
        this.bitWidth = bitWidth;
    }


    /**
     * @return The bit width of the integer type (1 or 32).
     */
    public int getBitWidth() {
        return bitWidth;
    }

    /**
     * Get the singleton i32 type.
     *
     * @return i32 type.
     */
    public static ir.types.IntegerType getI32() {
        return i32;
    }

    /**
     * Get the singleton i1 type.
     *
     * @return i1 type.
     */
    public static ir.types.IntegerType getI1() {
        return i1;
    }

    @Override
    public String toString() {
        switch (bitWidth) {
            case 32 -> {
                return "i32";
            }
            case 1 -> {
                return "i1";
            }
            default -> {
                throw new RuntimeException("Unsupported bit width.");
            }
        }
    }

    @Override
    public ConstInt getZero() {
        switch (bitWidth) {
            case 32 -> {
                return ConstInt.getI32(0);
            }
            case 1 -> {
                return ConstInt.getI1(0);
            }
            default -> {
                throw new RuntimeException("Unsupported bit width.");
            }
        }
    }
}
