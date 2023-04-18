package ir.types;

import ir.Type;
import ir.values.Constant;

/**
 * The void type does not represent any value and has no size.
 * e.g. Functions returning void has VoidType,
 * Instructions yielding no result has VoidType.
 *
 * @see <a href="https://llvm.org/docs/LangRef.html#void-type">
 * LLVM LangRef: Void Type</a>
 */
public class VoidType extends Type {
    private static ir.types.VoidType type = new ir.types.VoidType();

    private VoidType() {
    }

    public static ir.types.VoidType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public Constant getZero() {
        return null;
    }
}
