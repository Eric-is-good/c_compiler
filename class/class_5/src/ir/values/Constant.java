package ir.values;

import ir.User;
import ir.Type;

/**
 * A constant is a value that is immutable at runtime. All Constants are implemented
 * in Singleton fashions, guaranteeing each Constant in the same numeric value has
 * at most one existing instance (for the convenience of comparison using addresses).
 * <ul>
 *     <li>Integer and floating point values </li>
 *     <li>Arrays </li>
 *     <li>(Being referred in Global Variables)</li>
 * </ul>
 * Constant class has an operand list (inheriting from User), which is factually dedicated to ConstArray.
 * <br>
 * Type for a Constant is the type of that constant value.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Constant.h#L41">
 *     LLVM IR Source</a>
 */
public abstract class Constant extends User {

    public Constant(Type type) {
        super(type);
    }

    /**
     * Return true if the Constant has zero value of its corresponding type.
     * @return Yes or no.
     */
    public boolean isZero() {
        return this == this.getType().getZero();
    }
}
