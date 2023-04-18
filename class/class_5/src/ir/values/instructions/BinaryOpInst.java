package ir.values.instructions;

import ir.Type;
import ir.Value;
import ir.values.Instruction;

/**
 * BinaryInst represents instructions with two operands,
 * e.g. binary arithmetic operations, comparison operations.
 * <br>
 * <ul>
 *     <li>Type for arithmetic instructions is the type of operation result.</li>
 *     <li>Type for comparison instructions is boolean (i1).</li>
 * </ul>
 * Technically, arithmetic/relational operations for integers and floating points are
 * supposed to be defined as several separated java classes. But in the simple case of
 * SysY, we use BinaryOpInst and UnaryOpInst to cover all these instructions for
 * convenience, leaving the improvement for future works.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/InstrTypes.h#L189">
 *     LLVM IR BinaryOperator Source</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#binary-operations">
 *     LLVM LangRef: Binary Operations</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#icmp-instruction">
 *     LLVM LangRef: icmp, fcmp</a>
 */
public class BinaryOpInst extends Instruction {

    /**
     * User needs to guarantee parameters passed correct.
     * @param type Type of operation result.
     * @param tag  Instruction category.
     * @param lOp  Left operand.
     * @param rOp  Right operand.
     */
    public BinaryOpInst(Type type, InstCategory tag, Value lOp, Value rOp) {
        super(type, tag);
        // Add left and right operands.
        this.addOperandAt(0, lOp);
        this.addOperandAt(1, rOp);
    }

    @Override
    public String toString() {
        // Build and return a string like "%1 = add i32 %2, %3"
        return
            // Result name: "%1 = "
            this.getName() + " = "
            // Operation code and type: "add i32 "
            + switch (this.getTag()) {
                // Integer arithmetics.
                case ADD -> "add i32 ";
                case SUB -> "sub i32 ";
                case MUL -> "mul i32 ";
                case DIV -> "sdiv i32 ";
                // Floating point arithmetics.
                case FADD -> "fadd float ";
                case FSUB -> "fsub float ";
                case FMUL -> "fmul float ";
                case FDIV -> "fdiv float ";
                // Integer comparisons.
                case LT -> "icmp slt " + this.getOperandAt(0).getType() + " ";
                case LE -> "icmp sle " + this.getOperandAt(0).getType() + " ";
                case GE -> "icmp sge " + this.getOperandAt(0).getType() + " ";
                case GT -> "icmp sgt " + this.getOperandAt(0).getType() + " ";
                case EQ -> "icmp eq " + this.getOperandAt(0).getType() + " ";
                case NE -> "icmp ne " + this.getOperandAt(0).getType() + " ";
                // Floating point comparisons.
                // SysY doesn't support NaN/Inf, thus no ult/ule/... are needed.
                case FLT -> "fcmp olt " + this.getOperandAt(0).getType() + " ";
                case FLE -> "fcmp ole " + this.getOperandAt(0).getType() + " ";
                case FGE -> "fcmp oge " + this.getOperandAt(0).getType() + " ";
                case FGT -> "fcmp ogt " + this.getOperandAt(0).getType() + " ";
                case FEQ -> "fcmp oeq " + this.getOperandAt(0).getType() + " ";
                case FNE -> "fcmp one " + this.getOperandAt(0).getType() + " ";
                // Error.
                default -> throw new RuntimeException("Try to print with an unsupported InstCategory.");
            }
            // Two operands: "%2, %3"
            + this.getOperandAt(0).getName() + ", " + getOperandAt(1).getName();
    }

}
