package ir.values;

import ir.Value;
import ir.types.LabelType;
import utils.IntrusiveList;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A basic block is simply a container of instructions that execute sequentially.
 * Basic blocks are Values because they are referenced by instructions such as branches.
 * A well-formed basic block is formed of a list of non-terminating instructions followed
 * by a single terminator instruction.
 * <br>
 * To ensure BasicBlocks are always well-defined, the last Inst of a BasicBlock should always
 * be a Terminator. And a terminator instruction can only be the last instruction of a block.
 * <br>
 * Type for a BasicBlock is Type.LabelType because the basic block represents a label
 * to which a branch can jump.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/BasicBlock.h#L58">
 *     LLVM IR BasicBlock Source</a>
 */
public class BasicBlock extends Value implements Iterable<Instruction>{

    /**
     * All the Instructions in the BB.
     */
    private final IntrusiveList<Instruction, BasicBlock> instructions = new IntrusiveList<>(this);

    /**
     * Retrieve a LinkedList of Instructions contained by the BasicBlock.
     * <br>
     * NOTICE: This method cost O(n) time to conduct scanning and shallow copying.
     * To simply loop through the block without removal/adding of Instructions,
     * use BasicBlock::iterator instead.
     * @return A LinkedList with all Instruction current in the block.
     */
    public LinkedList<Instruction> getInstructions() {
        LinkedList<Instruction> list = new LinkedList<>();
        for (IntrusiveList.Node<Instruction, BasicBlock> node : instructions) {
            list.add(node.getData());
        }
        return list;
    }


    /**
     * Intrusive node for the BasicBlock list held by its parent Function.
     * This field is package-private (accessible to other IR classes).
     */
    final IntrusiveList.Node<BasicBlock, Function> node = new IntrusiveList.Node<>(this);

    /**
     * Retrieve the Function holding this BB.
     * @return The parent Function. Null if the BB belongs to no Function.
     */
    public Function getFunc() {
        return this.node.getParentHolder();
    }

    public BasicBlock() {
        super(LabelType.getType());
    }

    public BasicBlock(String info) {
        this();
        this.setInfo(info);
    }

    /**
     * Retrieve the size of the BB.
     * @return Number of Instructions in the BB.
     */
    public int size() {
        return instructions.size();
    }

    /**
     * Returns true if the BasicBlock contains no Instruction.
     * @return true if the BasicBlock contains no Instruction.
     */
    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    /**
     * Remove the BB from the Function holding it.
     * <br>
     * NOTICE:
     * This method is for simply removing the BB from the Function,
     * while all Instructions in the block are still activated.
     * To drop the BB entirely from the process (i.e. mark all
     * Instructions inside as wasted), use BB::markWasted.
     */
    public void removeSelf() {
        this.node.removeSelf();
    }

    /**
     * Drop the BB entirely from the process, including
     * <ul>
     *     <li>Remove the BB from the Function holding it.</li>
     *     <li>All Instructions inside will be marked as wasted.</li>
     * </ul>
     * Notice that only when a BB is not used by other Values, as well as each
     * instruction in the block is not used by any Values outside the block,
     * the BB is valid to be marked as wasted.
     */
    public void markWasted() {
        /*
        Security check:
        Usee is not allowed to remove Use relations proactively.
        i.e. If a Value is still a usee in any User-Usee relations, it cannot call
        markWasted to drop itself from the process.
         */
        if (!this.getUses().isEmpty()) {
            throw new RuntimeException("Try to mark as wasted an Inst still being used.");
        }

        /*
        Completely drop the BB.
         */
        var instList = this.getInstructions();

        // Remove all operands of each Instruction in the block,
        // to destruct possible internal user-usee relations inside the block.
        for (Instruction inst : instList) {
            inst.clearOperands();
        }

        // Remove the bb from the function.
        this.removeSelf();
        // Mark all instructions inside as wasted.
        // If any Instruction is still used by other Values outside the block, this block
        // is not supposed to be marked wasted, in which inst.markWasted will throw an exception.
        for (Instruction inst : instList) {
            inst.markWasted();
        }
    }

    /**
     * Returns true if this BB contains the specified Instruction.
     * @param inst The Instruction to be looked up.
     * @return true if this BB contains the specified Instruction. Otherwise, return false.
     */
    public boolean contains(Instruction inst) {
        return this.instructions.contains(inst);
    }

    /**
     * Removes a specified instruction from the BasicBlock.
     * If the given Inst doesn't exit in the BB, an exception will be thrown.
     * <br>
     * NOTICE: The Use links of the Inst will NOT be wiped out.
     * To drop an Inst entirely from the process, use Inst::markWasted.
     * @param inst The Instruction to be removed.
     */
    public void removeInst(Instruction inst) {
        if (inst.getBB() != this) {
            throw new RuntimeException("Try to remove an Instruction that doesn't reside on the BasicBlock.");
        }

        inst.removeSelf();
    }

    /**
     * Retrieve the last instruction in the basic block.
     * @return The last instruction. Null if it's an empty block.
     */
    public Instruction getLastInst() {
        return this.instructions.isEmpty() ? null : this.instructions.getLast().getData();
    }

    /**
     * Insert an instruction at the end of the basic block.
     * If the Inst has already belonged to another BB, an exception will be thrown.
     * @param inst The instruction to be inserted.
     */
    public void insertAtEnd(Instruction inst) {
        // Security checks.
        if (inst.getBB() != null) {
            throw new RuntimeException("Try to insert an Inst that has already belonged to another BB.");
        }
        if (this.instructions.size() != 0 && this.getLastInst().getTag().isTerminator()) {
            throw new RuntimeException("Try to insert an Inst to a BB which has already ended with a Terminator.");
        }
        // Insertion.
        this.instructions.insertAtEnd(inst.node);
    }

    /**
     * Insert an instruction at the beginning of the basic block.
     * If the Inst has already belonged to another BB, an exception will be thrown.
     * @param inst The instruction to be inserted.
     */
    public void insertAtFront(Instruction inst) {
        if (inst.getBB() != null) {
            throw new RuntimeException("Try to insert an Inst that has already belonged to another BB.");
        }
        // Insertion
        this.instructions.insertAtFront(inst.node);
    }

    /**
     * Wrapper of IntrusiveListIterator for iterating through Instructions
     * on a BasicBlock.
     */
    private static class BasicBlockIterator implements Iterator<Instruction> {

        IntrusiveList<Instruction, BasicBlock>.IntrusiveListIterator iList;

        BasicBlockIterator(BasicBlock bb) {
            iList = bb.instructions.iterator();
        }

        @Override
        public boolean hasNext() {
            return iList.hasNext();
        }

        @Override
        public Instruction next() {
            return iList.next().getData();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Instruction> iterator() {
        return new BasicBlockIterator(this);
    }

    /**
     * Get LabelType for a BB.
     * @return A LabelType.
     */
    @Override
    public LabelType getType() {
        return (LabelType) super.getType();
    }
}
