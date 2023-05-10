package ir.types;

import ir.Type;
import ir.values.Constant;

import java.util.HashMap;
import java.util.Objects;

/**
 * Class to represent pointers.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/DerivedTypes.h#L631">
 *     LLVM IR Source: PointerType</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#pointer-type">
 *     LLVM LangRef: Pointer Type</a>
 */
public class PointerType extends Type {

    /**
     * Type of the pointed-to memory space. (one step forward on the pointing chain)
     * <br>
     * e.g. Root type of PtrType( [3 x i32]*** ) is PtrType( [3 x i32]** )
     */
    private Type pointeeType;

    public Type getPointeeType() {
        return pointeeType;
    }

    /**
     * Type of the ultimate element tracing up the pointing chain.
     * <br>
     * e.g. Root type of PtrType( [3 x i32]*** ) is ArrayType( [3 x i32] )
     */
    private Type rootType;

    public Type getRootType() {
        return rootType;
    }

    /**
     * The pointer nesting depth of the PointerType.
     * <br>
     * e.g. i32*** has depth of 3.
     */
    private final int depth;


    //<editor-fold desc="Singleton">
    private PointerType(Type pointeeType) {
        this.pointeeType = pointeeType;

        if(pointeeType.isPointerType()) {
            this.rootType = ((PointerType) pointeeType).getRootType();
            this.depth = ((PointerType) pointeeType).depth + 1;
        }
        else {
            this.rootType = pointeeType;
            this.depth = 1;
        }
    }

    private static class PointerKey {
        public int depth;

        public Type rootType;

        public PointerKey(Type pointeeType) {
            this.depth = pointeeType.isPointerType() ? ((PointerType) pointeeType).depth + 1 : 1;
            this.rootType = pointeeType.isPointerType() ? ((PointerType) pointeeType).getRootType() : pointeeType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PointerKey that = (PointerKey) o;
            return depth == that.depth && Objects.equals(rootType, that.rootType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(depth, rootType);
        }
    }

    private static HashMap<PointerKey, PointerType> pool = new HashMap<>();

    /**
     * @param pointeeType Type of the pointed-to memory space.
     * @return FunctionType pointing to specified-type memory.
     */
    public static PointerType getType(Type pointeeType) {
        var key = new PointerKey(pointeeType);
        if (pool.containsKey(key)) {
            return pool.get(key);
        }

        var newType = new PointerType(pointeeType);
        pool.put(key, newType);
        return newType;
    }
    //</editor-fold>

    @Override
    public String toString() {
        // e.g. "i32*" and "i1*"
        return pointeeType.toString() + "*";
    }

    @Override
    public Constant getZero() {
        throw new UnsupportedOperationException();
    }
}
