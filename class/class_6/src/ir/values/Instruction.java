package ir.values;

import ir.Use;
import ir.User;
import ir.Type;
import ir.Value;
import utils.IntrusiveList;

/**
 * Instruction class is the base class for all the IR instructions.
 * Various kinds of instruction inherit it and are tagged with an
 * InstCategory in cat field for distinction.
 * <br>
 * Type for an Instruction depends on its category.
 *
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Instruction.h">
 * LLVM IR Source</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#instruction-reference">
 * LLVM LangRef: Instruction</a>
 */
public abstract class Instruction extends User {

    /**
     * Each instruction instance has a field "tag" containing a InstCategory instance
     * as a tag distinguishing different instruction types.
     * <br>
     * NOTICE: The ordinal of each enum will be use for distinguishing different types
     * of operations. Be careful to move them or add new ones.
     */
    public enum InstCategory {
        /*
         Arithmetic Operations: Integer and floating point.
         */
        ADD, SUB, MUL, DIV,
        FADD, FSUB, FMUL, FDIV,
        FNEG,

        /*
        Relational Operations: Integer and floating point .
         */
        LT, GT, EQ, NE, LE, GE,
        FLT, FGT, FEQ, FNE, FLE, FGE,

        // Logical Operations
        AND, OR,
        // Terminators
        RET, BR,
        // Invocation
        CALL,
        // Memory operations
        ALLOCA, LOAD, STORE,
        // Casting operations
        ZEXT, FPTOSI, SITOFP, PTRCAST,
        // Others
        GEP, PHI;


        public boolean isArithmeticBinary() {
            return this.ordinal() <= InstCategory.FNEG.ordinal();
        }

        public boolean isRelationalBinary() {
            return InstCategory.LT.ordinal() <= this.ordinal()
                    && this.ordinal() <= InstCategory.FGE.ordinal();
        }

        public boolean isIntRelationalBinary() {
            return InstCategory.LT.ordinal() <= this.ordinal()
                    && this.ordinal() <= InstCategory.GE.ordinal();
        }

        public boolean isFloatRelationalBinary() {
            return InstCategory.FLT.ordinal() <= this.ordinal()
                    && this.ordinal() <= InstCategory.FGE.ordinal();

        }

        public boolean isTerminator() {
            return InstCategory.RET.ordinal() <= this.ordinal()
                    && this.ordinal() <= InstCategory.BR.ordinal();
        }
    }

    /**
     * An InstCategory instance indicating an instruction type.
     */
    private final InstCategory tag;

    public InstCategory getTag() {
        return tag;
    }

    //<editor-fold desc="Instruction Type Checks">
    public boolean isAdd() {
        return this.getTag() == InstCategory.ADD;
    }

    public boolean isSub() {
        return this.getTag() == InstCategory.SUB;
    }

    public boolean isMul() {
        return this.getTag() == InstCategory.MUL;
    }

    public boolean isDiv() {
        return this.getTag() == InstCategory.DIV;
    }

    public boolean isAnd() {
        return this.getTag() == InstCategory.AND;
    }

    public boolean isOr() {
        return this.getTag() == InstCategory.OR;
    }

    public boolean isRet() {
        return this.getTag() == InstCategory.RET;
    }

    public boolean isBr() {
        return this.getTag() == InstCategory.BR;
    }

    public boolean isCall() {
        return this.getTag() == InstCategory.CALL;
    }

    public boolean isAlloca() {
        return this.getTag() == InstCategory.ALLOCA;
    }

    public boolean isLoad() {
        return this.getTag() == InstCategory.LOAD;
    }

    public boolean isStore() {
        return this.getTag() == InstCategory.STORE;
    }

    public boolean isIcmp() {
        return this.getTag().isIntRelationalBinary();
    }

    public boolean isGEP() {
        return this.getTag() == InstCategory.GEP;
    }

    public boolean isFcmp() {
        return this.getTag().isFloatRelationalBinary();
    }

    public boolean isZext() {
        return this.getTag() == InstCategory.ZEXT;
    }

    public boolean isPhi() {
        return this.getTag() == InstCategory.PHI;
    }

    public boolean isBitcast() {
        return this.getTag() == InstCategory.PTRCAST;
    }
    //</editor-fold>

    /**
     * Intrusive node for the instruction list held by its parent BasicBlock.
     * This field is package-private (accessible to other IR classes).
     */
    final IntrusiveList.Node<Instruction, BasicBlock> node = new IntrusiveList.Node<>(this);

    /**
     * Retrieve the BasicBlock holding this Inst.
     *
     * @return The parent BasicBlock. Null if the Inst belongs to no BB.
     */
    public BasicBlock getBB() {
        return this.node.getParentHolder();
    }

    public Instruction(Type type, InstCategory tag) {
        super(type);
        this.tag = tag;
    }

    /**
     * If an instruction has result, a name (register) should be
     * assigned for the result yielded when naming a Module.
     * Namely, hasResult() == true means an instruction needs a name.
     * <br>
     * Most of the instructions have results, e.g.
     * <ul>
     *     <li>Binary instructions yield results.</li>
     *     <li>Alloca instruction yield an addresses as results.</li>
     *     <li>ZExt instruction yield extended results.</li>
     * </ul>
     * In contrast, Terminators and Store instructions have no results.
     */
    public boolean hasResult() {
        return !this.getType().isVoidType();
    }

    /**
     * Remove the instruction from the BasicBlock holding it.
     * <br>
     * NOTICE: The Use links of the Inst will NOT be wiped out.
     * To drop an Inst entirely from the process, use Inst::markWasted.
     */
    public void removeSelf() {
        // Remove the inst from the intrusive list.
        this.node.removeSelf();
    }

    /**
     * Drop the Inst entirely from the process, including
     * <ul>
     *     <li>Remove the instruction from the BasicBlock holding it.</li>
     *     <li>All related Use links of the Inst will be removed.</li>
     * </ul>
     * Notice that only when an Instruction is not used by other Values,
     * it is valid to be marked as wasted. An exception will be thrown during
     * an invalid marking.
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
        Completely drop the Inst.
         */
        // Remove the inst from the bb.
        this.removeSelf();
        // Update the use states:
        // - Remove all the Use links for the User using it.
        // - Remove all the Use links corresponding to its operands.
        this.getUses().forEach(Use::removeSelf);
        this.clearOperands();
    }

    /**
     * Insert this Instruction in front of the given one.
     * <br>
     * e.g. A.insertBefore(B) => ... A - B ...
     *
     * @param inst The target instruction.
     */
    public void insertBefore(Instruction inst) {
        this.node.insertBefore(inst.node);
    }

    /**
     * Insert this Instruction as the next of the given one.
     * <br>
     * e.g. A.insertAfter(B) => ... B - A ...
     *
     * @param inst The target instruction.
     */
    public void insertAfter(Instruction inst) {
        this.node.insertAfter(inst.node);
    }

    /**
     * Redirect all Use relations referring the result of this Inst as usee
     * to another given Value. Then mark the current Inst wasted.
     *
     * @param value The new value to be used.
     */
    @Override
    public void replaceSelfTo(Value value) {
        super.replaceSelfTo(value);
        markWasted();
    }
}
