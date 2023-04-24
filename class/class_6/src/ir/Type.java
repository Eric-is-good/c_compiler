package ir;

import ir.types.*;
import ir.values.Constant;

/**
 * Each Value instance has a type field containing type information related to it as an IR component.
 * <br>
 * The instances of the Type class are immutable: once they are created, they are never changed.
 * Only one instance of a particular type is ever created. To enforce this, all most all Type instances
 * exist in singleton fashion. Thus, "==" or Object::equals (address comparison by default) can be directly
 * used for comparing two Types (checking if two Value are in the same Type).
 * <br>
 * In the case of SysY needed only a subset of LLVM IR type system, we categorize Types as below:
 * <ul>
 *     <li>Primitive Types: VoidType, IntegerType, FloatType, LabelType</li>
 *     <li>Composed Types: PointerType, FunctionType</li>
 * </ul>
 * However, the concepts of "First Class Types" and "Single Value Types" in LLVM Language Reference
 * are still useful for understanding the type system.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Type.h#L45">
 *     LLVM IR Source: Type</a>
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/DerivedTypes.h">
 *     LLVM IR Source: Derived Types</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#type-system">
 *     LLVM LangRef: Type System</a>
 */
public abstract class Type {

    public boolean isVoidType() {
        return (this instanceof VoidType);
    }

    public boolean isLabelType() {
        return (this instanceof LabelType);
    }

    public boolean isFunctionType() {
        return (this instanceof FunctionType);
    }

    public boolean isIntegerType() {
        return (this instanceof IntegerType);
    }

    public boolean isI1() {
        return this.isIntegerType() && ((IntegerType) this).getBitWidth() == 1;
    }

    public boolean isI32() {
        return this.isIntegerType() && ((IntegerType) this).getBitWidth() == 32;
    }

    public boolean isFloatType() {
        return (this instanceof FloatType);
    }

    public boolean isPointerType() {
        return (this instanceof PointerType);
    }

    public boolean isArrayType() {
        return (this instanceof ArrayType);
    }

    public abstract Constant getZero();
}
