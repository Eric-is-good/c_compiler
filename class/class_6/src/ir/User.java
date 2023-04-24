package ir;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class defines the interface that one who uses a Value must implement
 * to gain necessary extended fields for tracing all the Value instances used.
 * <br>
 * Each instance of the Value class keeps track of what User's have handles to it.
 * <ul>
 *     <li>Instructions are the largest class of Users.</li>
 *     <li>Constants may be users of other constants (think arrays and stuff)</li>
 * </ul>
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/User.h">
 *     LLVM IR Reference</a>
 */
public abstract class User extends Value {

    /**
     * Keep track of all the Values used by a (pos -> val) mapping.
     * <br>
     * To safely add operands to it, use addOperandAt().
     */
    private final HashMap<Integer, Use> operands = new HashMap<>();


    public User(Type type) {
        super(type);
    }


    /**
     * Retrieve the number of Value it uses.
     * @return Number of operands of the user.
     */
    public int getNumOperands() {
        return operands.size();
    }

    /**
     * Retrieve a LinkedList containing all Uses for operands used.
     * (The LinkedList does NOT guarantee the order of the operands,
     * i.e. the index of each element in the LinkedList may not be
     * the position of the operand)
     * @return A LinkedList of all Uses of the operands.
     */
    public LinkedList<Use> getOperands() {
        return new LinkedList<>(operands.values());
    }

    /**
     * Clear all operand use relations from the User.
     */
    public void clearOperands(){
        for (Use use : getOperands()) {
            use.removeSelf();
        }
    }

    /**
     * Returns true if this User contains an operand on the given position.
     * @param pos The position.
     * @return Yes or no.
     */
    public boolean containsOperandAt(int pos) {
        return operands.containsKey(pos);
    }

    /**
     * Retrieve a value used at a specified position.
     * An exception will be thrown if no operand exists on the given position.
     * @param pos The position of the target operands.
     * @return Reference of the target operand at the given position.
     */
    public Value getOperandAt(int pos) {
        if (!operands.containsKey(pos)) {
            throw new RuntimeException("Operand index (position) doesn't exist.");
        }
        return operands.get(pos).getUsee();
    }

    /**
     * At a new operand at a given position.
     * If an existed operand has already landed on that position, an Exception will be thrown.
     *
     * @param pos Given operand position.
     * @param val The value to be added as an operand.
     */
    public void addOperandAt(int pos, Value val) {
        // If not, add the given Value as a new use.
        var use = new Use(val, this, pos);
        addUseAt(pos, use);
    }


    protected void addUseAt(int pos, Use use) {
        // Check if there is an existing operand on the given position.
        // If there is, throw an exception.
        if (operands.containsKey(pos)) {
            throw new RuntimeException("Try to add an operand at an occupied position.");
        }
        this.operands.put(pos, use);
        use.getUsee().addUse(use);
    }

    /**
     * Set an operand at the specified position to be another Value given.
     * If there's no existing operand matched, an Exception will be thrown.
     * <br>
     * NOTICE: This method uses Use::setUsee to change the operand referred
     * by the Use object, other than building a new Use edge aft removing the
     * old one. This means your shallow copy of Uses retrieved from User::getOperands
     * may have its content of elements changed aft calling this method.
     *
     * @param pos Given operand position.
     * @param val The value to be set as an operand.
     */
    public void setOperandAt(int pos, Value val) {
        // Check if there is the matched operand on the given position.
        // If there is, redirect it safely.
        if (operands.containsKey(pos)) {
            operands.get(pos).setUsee(val);
        }
        // If not, throw an exception.
        else {
            throw new RuntimeException("Try to reassign a non-existent operand.");
        }
    }

    /**
     * Remove an operand at the specified position
     * If there's no existing operand matched, an Exception will be thrown.
     * @param pos Given operand position.
     */
    public void removeOperandAt(int pos) {
        if (!operands.containsKey(pos)) {
            throw new RuntimeException("Try to remove a non-existent operand.");
        }
        else {
            operands.get(pos).removeSelf(); // from both User and Usee
        }
    }

    /**
     * Remove an operand use of the specified position from User's operand field.
     * <br>
     * NOTICE: This is a unilateral removal (package-private for Use only). To safely
     * delete a user-usee relation, use User::removeOperandAt instead.
     * @param pos Given operand position.
     */
    void removeOperandRaw(int pos) {
        if (operands.remove(pos) == null) { // from User only
            throw new RuntimeException("Try to remove a non-existent operand.");
        }
    }
}
