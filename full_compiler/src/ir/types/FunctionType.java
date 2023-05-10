package ir.types;

import ir.Type;
import ir.values.Constant;
import ir.values.constants.ConstArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Class to represent function types, containing the prototype of a function
 * including its return type and argument type(s).
 * <br>
 * Technically, each prototype should correspond to a unique instance (singleton) of
 * FunctionType. But for convenience, FunctionType is implemented in ordinary class
 * fashion without singleton strategies.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/DerivedTypes.h#L102">
 *     LLVM IR Source: FunctionType</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#function-type">
 *     LLVM LangRef: Function Type</a>
 */
public class FunctionType extends Type {

    /**
     * Return type of the function. (VoidType / IntegerType / FloatType)
     */
    private Type retType;

    public Type getRetType() {
        return retType;
    }

    /**
     * Argument types in the function prototype.
     */
    private ArrayList<Type> argTypes;

    public ArrayList<Type> getArgTypes() {
        return argTypes;
    }


    //<editor-fold desc="Singleton">
    private FunctionType(Type retType, ArrayList<Type> argTypes) {
        this.retType = retType;
        this.argTypes = argTypes;
    }

    private static class PrototypeKey {
        public final ArrayList<Type> types = new ArrayList<>();

        public PrototypeKey(Type retType, List<Type> argTypes) {
            this.types.add(retType);
            this.types.addAll(argTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(types);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrototypeKey that = (PrototypeKey) o;
            return Objects.equals(types, that.types);
        }
    }

    private static HashMap<PrototypeKey, FunctionType> pool = new HashMap<>();

    /**
     * Retrieve a function signature.
     * @param retType Type of the value returned by the function.
     * @param argTypes List of types of function arguments.
     * @return The prototype of the Function.
     */
    public static FunctionType getType(Type retType, ArrayList<Type> argTypes) {
        var key = new PrototypeKey(retType, argTypes);
        if (pool.containsKey(key)) {
            return pool.get(key);
        }

        var newType = new FunctionType(retType, argTypes);
        pool.put(key, newType);
        return newType;
    }

    @Override
    public Constant getZero() {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>
}
