package ir.values.instructions;

import ir.Value;
import ir.values.Function;
import ir.values.Instruction;

import java.util.ArrayList;

/**
 * This class represents a function call.
 * <br>
 * Type for a CallInst is the return type of the function invoked,
 * which might be VoidType.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Instructions.h#L1475>
 *     LLVM IR Source: CallInst</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#call-instruction">
 *     LLVM LangRef: Call Instruction</a>
 */
public class CallInst extends Instruction {

    /**
     * A call instruction in IR.
     * @param func Function Value carrying information about return type and FORMAL arguments.
     * @param args The ACTUAL arguments to be referenced by the Call.
     */
    public CallInst(Function func, ArrayList<Value> args) {
        // Operands of Call is the Function invoked and all argument Values passed
        super(func.getType().getRetType(), InstCategory.CALL);

        // The function Value is the 1st operand of the Call instruction.
        this.addOperandAt(0, func);
        // All arguments as operands.
        for (int i = 0; i < args.size(); i++) {
            this.addOperandAt(i+1, args.get(i));
        }
    }

    @Override
    public String toString() {
        // e.g. %res = call i32 @func(i32 %arg) ; with return value
        // e.g. call void @func(i32 %arg)            ; without return value
        StringBuilder strBuilder = new StringBuilder();

        // "%res = " if the function call yields a result.
        if (this.hasResult()) {
            strBuilder.append(this.getName()).append(" = ");
        }
        // "call i32 " or "call void"
        // + "@func(i32 %arg)"
        strBuilder.append("call ").append(this.getType()).append(" ")
                .append("@").append(this.getOperandAt(0).getName())
                .append("(");
        for(int i = 1; i < this.getNumOperands(); i++) {
            Value opd = this.getOperandAt(i);
            strBuilder.append(opd.getType())
                    .append(" ")
                    .append(opd.getName());
            if (i != this.getNumOperands() - 1) { // The last argument need no comma following it.
                strBuilder.append(", ");
            }
        }
        strBuilder.append(")");

        return strBuilder.toString();
    }
}