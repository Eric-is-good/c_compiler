package ir.types;

import ir.Type;
import ir.values.Constant;

/**
 * The label type represents code labels.
 * Basically it's a type dedicated to BasicBlock.
 *
 * @see <a href="https://llvm.org/docs/LangRef.html#label-type">
 * LLVM LangRef: Label Type</a>
 */
public class LabelType extends Type {
    public static ir.types.LabelType type = new ir.types.LabelType();

    private LabelType() {
    }

    public static ir.types.LabelType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "label";
    }

    @Override
    public Constant getZero() {
        throw new UnsupportedOperationException();
    }
}
