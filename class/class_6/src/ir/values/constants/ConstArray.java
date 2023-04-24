package ir.values.constants;

import ir.Use;
import ir.Value;
import ir.types.ArrayType;
import ir.values.Constant;

import java.util.*;

/**
 * Class for (integer/float) constant array in initialization.
 * All the Constants in the array will be the operands of it.
 * <br>
 * In our cases, zero elements in the array will not be in the
 * operand list for the sake of memory saving. Thus, those entries
 * will be null (User::containsOperandAt -> false). Factually, since
 * ConstArray is already a Constant, opt like constant derivation will
 * no longer attempt to track down the using chain. So operands are just
 * use as a container for the elements with no actually use-def semantics.
 */
public class ConstArray extends Constant {

    //<editor-fold desc="Singleton">
    /**
     * Construct a constant array. The blanks that are not initialized with
     * Values in initList will be filled with corresponding zero Values.
     * @param arrType  ArrayType for the array.
     * @param initList ArrayList of Constants for initializing the ConstArray.
     *                 The list needs to have no zero elements at its end.
     */
    private ConstArray(ArrayType arrType, ArrayList<Constant> initList) {
        super(arrType);

        // To save memory spaces, add only none zero operands into the container.
        for (int i = 0; i < initList.size(); i++) {
            Constant elem = initList.get(i);
            if (!elem.isZero()) {
                super.addOperandAt(i, elem);
            }
        }
    }

    private static class ConstArrKey {
        public final ArrayType arrType;
        public final ArrayList<Constant> initList;

        public ConstArrKey(ArrayType arrType, ArrayList<Constant> initList) {
            this.arrType = arrType;
            this.initList = initList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConstArrKey that = (ConstArrKey) o;
            return Objects.equals(arrType, that.arrType) && Objects.equals(initList, that.initList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(arrType, initList);
        }
    }

    private static final HashMap<ConstArrKey, ConstArray> pool = new HashMap<>();

    /**
     * Retrieve a constant array with a list of initial values (Constants).
     * The blanks that are not initialized with Values in initList will be
     * filled with corresponding zero Values.
     * @param arrType  The ArrayType.
     * @param initList ArrayList of Constants in a same Type.
     * @return The ConstArray instance required.
     */
    public static ConstArray get(ArrayType arrType, ArrayList<Constant> initList) {
        // Check type.
        for (Constant elem : initList) {
            if (arrType.getElemType() != elem.getType()) {
                throw new RuntimeException(
                        "Try to get a ConstArray with different types of constants in the initialized list."
                );
            }
        }

        // Process initList: remove all zero values at the end.
        for (int i = initList.size() - 1; i >= 0; i--) {
            Constant elem = initList.get(i);
            if (!elem.isZero()) {
                break;
            }
            initList.remove(i);
        }

        // Retrieve the instance and return it.
        var key = new ConstArrKey(arrType, initList);
        if (pool.containsKey(key)) {
            return pool.get(key);
        }

        var newArr = new ConstArray(arrType, initList);
        pool.put(key, newArr);
        return newArr;
    }
    //</editor-fold>

    /**
     * Return an element in the ConstArray with given indices.
     * @param indices Indices indicating the position of the element.
     * @return The element retrieved, which can be Value object at any level of the array.
     */
    public Value getElemByIndex(Iterable<Integer> indices) {
        Value elem = this;
        for (Integer idx : indices) {
            if (elem.getType().isArrayType()) {
                elem = ((ConstArray) elem).getOperandAt(idx);
            }
            else {
                throw new RuntimeException("Depth of indies given is to large.");
            }
        }
        return elem;
    }

    @Override
    public String toString() {
        // e.g. [2 x i32] [i32 1, i32 2] or [2 x i32] zeroinitializer
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.getType()); // "[2 x i32]"

        if (this.isZero()) { // "zeroinitializer"
            strBuilder.append(" zeroinitializer");
        }
        else { // "[i32 1, i32 2]"
            strBuilder.append(" [");
            for (int i = 0; i < this.getNumOperands(); i++) {
                if (i != 0) {
                    strBuilder.append(", ");
                }
                strBuilder.append(this.getOperandAt(i));
            }
            strBuilder.append("]");
        }

        return strBuilder.toString();
    }

    /**
     * Retrieve a list of all array's elements.
     * @return The list containing all elements in the array.
     */
    public Iterable<Constant> getElements() {
        LinkedList<Constant> list = new LinkedList<>();
        for (int i = 0; i < this.getNumOperands(); i++) {
            list.add(this.getOperandAt(i));
        }
        return list;
    }

    /**
     * Retrieve the ArrayType of the array.
     * @return ArrayType of the array.
     */
    @Override
    public ArrayType getType() {
        return (ArrayType) super.getType();
    }

    @Override
    public Constant getOperandAt(int pos) {
        if (pos < 0 || pos >= this.getType().getLen()) {
            throw new RuntimeException("Index out of range of the array.");
        }

        if (!this.containsOperandAt(pos)) {
            return this.getType().getElemType().getZero();
        }
        else {
            return (Constant) super.getOperandAt(pos);
        }
    }

    @Override
    public int getNumOperands() {
        return this.getType().getLen();
    }

    @Override
    public LinkedList<Use> getOperands() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOperandAt(int pos, Value val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeOperandAt(int pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addOperandAt(int pos, Value val) {
        throw new UnsupportedOperationException();
    }
}
