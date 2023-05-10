package ir.values.instructions;

import ir.Type;
import ir.Value;
import ir.types.FloatType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.values.Instruction;

/**
 * This class contains nested classes representing instructions
 * for conversion between Values in different IR Types.
 * <br>
 * Type for a CastInst is the destination Type of the casting.
 */
public abstract class CastInst extends Instruction {

    public CastInst(Type type, InstCategory tag) {
        super(type, tag);
    }

    /**
     * Represents Zero Extension of integer type.
     * In our case, there is only the case of extending i1 to i32,
     * thus the destination type will only be i32.
     * <br>
     * Type for ZExt is its destination type (i32).
     * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Instructions.h#L4758">
     *     LLVM IR Source: ZExtInst</a>
     * @see <a href="https://llvm.org/docs/LangRef.html#zext-to-instruction">
     *     LLVM LangRef: ZExt Instruction</a>
     */
    public static class ZExt extends CastInst {

        /**
         * Construct a ZExt instruction.
         * @param opd The operand Value in i1 to be extended.
         */
        public ZExt(Value opd) {
            super(IntegerType.getI32(), InstCategory.ZEXT);
            this.addOperandAt(0, opd);
        }

        @Override
        public String toString() {
            return this.getName()
                    + " = "
                    + "zext i1 "
                    + this.getOperandAt(0).getName()
                    + " to i32";
        }
    }

    /**
     * Convert a FloatType Value into IntegerType.
     * @see <a href="https://llvm.org/docs/LangRef.html#fptosi-to-instruction">
     *     LLVM LangRef: ‘fptosi .. to’ Instruction</a>
     */
    public static class Fptosi extends CastInst {

        /**
         * Construct a fptosi instruction.
         * @param opd      The operand Value to be cast.
         * @param destType The target IntegerType (maybe i32 or i1)
         */
        public Fptosi(Value opd, IntegerType destType) {
            super(destType, InstCategory.FPTOSI);
            this.addOperandAt(0, opd);
        }

        @Override
        public String toString() {
            // e.g. "%4 = fptosi float %3 to i32"
            Value opd = this.getOperandAt(0);
            return this.getName() + " = fptosi "// "%4 = fptosi "
                    + opd.getType() + " " + opd.getName() // "float %3"
                    + " to " + this.getType(); // " to i32"
        }
    }


    /**
     * Convert a IntegerType Value into FloatType.
     * @see <a href="https://llvm.org/docs/LangRef.html#sitofp-to-instruction">
     *     LLVM LangRef: ‘sitofp .. to’ Instruction</a>
     */
    public static class Sitofp extends CastInst {

        /**
         * Construct a sitofp instruction.
         * @param opd The operand Value to be cast.
         */
        public Sitofp(Value opd) {
            super(FloatType.getType(), InstCategory.SITOFP);
            this.addOperandAt(0, opd);
        }

        @Override
        public String toString() {
            // e.g. "%6 = sitofp i32 %5 to float"
            Value opd = this.getOperandAt(0);
            return this.getName() + " = sitofp "// "%6 = sitofp "
                    + opd.getType() + " " + opd.getName() // "i32 %5"
                    + " to " + this.getType(); // " to float"
        }
    }

    /**
     * Converts value to type ty2 without changing any bits.
     * In SysY, this instruction is only for convert float* to i32*.
     * @see <a href="https://llvm.org/docs/LangRef.html#bitcast-to-instruction">
     *     LLVM LangRef: ‘bitcast .. to’ Instruction</a>
     */
    public static class Bitcast extends CastInst {

        /**
         * Construct an addrspacecast instruction.
         * @param opd The operand Value to be cast.
         * @param dstType The target type to be cast to.
         */
        public Bitcast(Value opd, Type dstType) {
            super(dstType, InstCategory.PTRCAST);
            this.addOperandAt(0, opd);
        }

        @Override
        public String toString() {
            // e.g. "%6 = sitofp i32 %5 to float"
            Value opd = this.getOperandAt(0);
            return this.getName() + " = bitcast "// "%6 = sitofp "
                    + opd.getType() + " " + opd.getName() // "i32 %5"
                    + " to " + this.getType(); // " to float"
        }
    }
}
