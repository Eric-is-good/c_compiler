package ir.values;

import ir.Value;
import ir.types.FunctionType;
import ir.Type;
import utils.IntrusiveList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A Function represents a single function/procedure in IR.
 * A function basically consists of an argument list and a
 * list of basic blocks as function body.
 * <br>
 * Type for a Function is FunctionType, which contains a
 * prototype of the function (a return type and a list of
 * argument types).
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Function.h#L61">
 *     LLVM IR Function Source</a>
 */
public class Function extends Value implements Iterable<BasicBlock>{

    /**
     * Innerclass: Represents a FORMAL argument in a function call
     * (designating a memory block on the stack of invoked function).
     */
    public class FuncArg extends Value {

        /**
         * Position of the arg in the function parameter list.
         */
        private int pos;


        public int getPos() {
            return pos;
        }


        public FuncArg(Type type, int pos) {
            super(type);
            this.pos = pos;
        }


        @Override
        public String toString() {
            // e.g. "i32 %arg"
            return this.getType() + " " + this.getName();
        }
    }


    /**
     * List of formal arguments.
     */
    private final ArrayList<FuncArg> args = new ArrayList<>();

    /**
     * Retrieve a list of formal arguments of the function.
     * The indices of the FArgs in the list returned are their positions in the argument list.
     * @return An ArrayList of formal arguments.
     */
    public ArrayList<FuncArg> getArgs() {
        return args;
    }

    /**
     * Basic blocks in the function.
     */
    private final IntrusiveList<BasicBlock, Function> bbs = new IntrusiveList<>(this);

    /**
     * If it's an extern function whose prototype (declaration) is given
     * but definition has not been specified.
     * In our case, isExternal is only for functions supported by
     * the SysY runtime lib.
     */
    private boolean isExternal = false;

    public boolean isExternal() {
        return isExternal;
    }


    /**
     * Constructor for function declared in compile unit.
     * If the function is in runtime lib to be linked in, the isExternal flag should be
     * specified as true. Otherwise, it should be false.
     * @param type FunctionType with a return type and a list of formal argument types
     * @param isExternal For functions in runtime lib, it should be true.
     */
    public Function(FunctionType type, boolean isExternal) {
        super(type);
        this.isExternal = isExternal;

        // Add arguments into the args list.
        ArrayList<Type> ar = this.getType().getArgTypes();
        for (int i = 0; i < ar.size(); i++) {
            args.add(new FuncArg(ar.get(i), i));
        }
    }

    /**
     * Get the entry basic block of the function.
     * @return Reference to the entry block.
     */
    public BasicBlock getEntryBB() {
        return bbs.getFirst().getData();
    }

    /**
     * Add a new basic block into the function.
     * If the BB has already belonged to another Function, an exception will be thrown.
     * @param bb The block to be added.
     */
    public void addBB(BasicBlock bb) {
        if (bb.getFunc() != null) {
            throw new RuntimeException("Try to add a BB that has already belonged to another Function.");
        }

        this.bbs.insertAtEnd(bb.node);
    }

    /**
     * Removes a specified BasicBlock from the Function.
     * If the given bb doesn't exit in the Function, an exception will be thrown.
     * <br>
     * NOTICE: This method does NOT mark the BB as wasted. To completely remove a BB
     * from the process, use BB:markWasted instead.
     * @param bb The BB to be removed.
     */
    public void removeBB(BasicBlock bb) {
        if (bb.getFunc() != this) {
            throw new RuntimeException("Try to remove a BB that doesn't reside on the Function.");
        }

        bb.removeSelf();
    }

    @Override
    public String toString() {
        // e.g. "i32 @func(i32 arg1, i32 arg2)"

        StringBuilder strBuilder = new StringBuilder();
        // Name of the function.
        strBuilder.append(this.getType().getRetType())
                .append(" @")
                .append(this.getName())
                .append("(");
        // Argument list.
        for(int i = 0; i < getArgs().size(); i++) {
            // For extern function declaration, only argument types need to be
            // printed in the argument list.
            if (this.isExternal()) {
                strBuilder.append(getArgs().get(i).getType());
            }
            // For a local function definition, both types and names (register)
            // need to be printed.
            else {
                strBuilder.append(getArgs().get(i));
            }
            // The last argument needs no comma following it.
            if (i != getArgs().size() - 1) {
                strBuilder.append(", ");
            }
        }
        // A right parenthesis to close it.
        strBuilder.append(")");

        return strBuilder.toString();
    }

    /**
     * Get the prototype of the function.
     * @return The FunctionType of the function.
     */
    @Override
    public FunctionType getType() {
        return (FunctionType) super.getType();
    }

    /**
     * Wrapper of IntrusiveListIterator for iterating through BasicBlocks
     * on a Function.
     */
    private static class FunctionIterator implements Iterator<BasicBlock> {

        IntrusiveList<BasicBlock, Function>.IntrusiveListIterator iList;

        FunctionIterator(Function f) {
            iList = f.bbs.iterator();
        }

        @Override
        public boolean hasNext() {
            return iList.hasNext();
        }

        @Override
        public BasicBlock next() {
            return iList.next().getData();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<BasicBlock> iterator() {
        return new FunctionIterator(this);
    }
}
