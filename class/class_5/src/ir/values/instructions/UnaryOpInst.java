package ir.values.instructions;

import ir.Type;
import ir.Value;
import ir.values.Instruction;

public class UnaryOpInst extends Instruction {
    /**
     * User (Builder) needs to guarantee parameters passed correctly.
     *
     * @param type Type of operation result.
     * @param tag  Instruction category.
     * @param opd  The operand Value.
     */
    public UnaryOpInst (Type type, InstCategory tag, Value opd) {
        super(type, tag);
        // Add left and right operands.
        this.addOperandAt(0, opd);
    }

    @Override
    public String toString() {
        Value opd = this.getOperandAt(0);
        // e.g. "%4 = fneg float %3"
        return
                this.getName() + " = " // "%4 = "
                // Operation code
                + switch (this.getTag()) {
                    case FNEG -> "fneg ";
                    default -> throw new RuntimeException("Unsupported type.");
                }
                // The operand
                + opd.getType() + " " + opd.getName(); // "float %3"
    }
}
