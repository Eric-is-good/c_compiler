package ir;

/**
 * A Use represents the edge between a Value and one of its users.
 * i.e. A Use is a user-usee link in a "using graph" of Values.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Use.h">
 *     LLVM IR Reference</a>
 */
public class Use {
    /**
     * Reference to value being used.
     */
    private Value usee;

    public Value getUsee() {
        return usee;
    }

    public void setUsee(Value usee) {
        this.usee.removeUseRaw(this);
        this.usee = usee;
        this.usee.addUse(this);
    }

    /**
     * Reference to the user value.
     */
    private User user;

    public User getUser() {
        return user;
    }

    /**
     * The position number of the value as an operand of the user.
     * It starts from 0.
     */
    private int pos;

    public int getOperandPos() {
        return pos;
    }

    /**
     * Construct a new Use edge.
     * <br>
     * The constructor DOES NOT automatically insert the Use link constructed to neither usee's [uses]
     * field nor the user's [operands] field, which are left for the caller to manually handle.
     * @param value The value being used.
     * @param user  The user value.
     * @param position The position of the used value as an operand.
     */
    public Use(Value value, User user, int position) {
        this.usee = value;
        this.user = user;
        this.pos = position;
    }

    /**
     * A safe way to delete a user-usee relation.
     * i.e. to remove the Use link from its user and usee.
     * Notice that the Use itself will not be destructed (if there is still any object referring it).
     */
    public void removeSelf() {
        this.getUsee().removeUseRaw(this);
        this.getUser().removeOperandRaw(this.getOperandPos());
    }

}
