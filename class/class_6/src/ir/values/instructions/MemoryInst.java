package ir.values.instructions;

import ir.Type;
import ir.Value;
import ir.types.PointerType;
import ir.types.VoidType;
import ir.values.BasicBlock;
import ir.values.Instruction;

/**
 * This class contains nested classes representing instructions that are memory-related,
 * including memory accessing and addressing instructions.
 * In the latest version of LLVM IR, these instructions inherit from Instruction (Store)
 * or Instruction.UnaryInstruction (Load, Alloca, ZExt).
 */
public abstract class MemoryInst extends Instruction {

    public MemoryInst(Type type, InstCategory tag) {
        super(type, tag);
    }

    /**
     * An instruction for writing to memory.
     * A Store has two arguments (operands): a value to store
     * and an address at which to store it, and does NOT yield
     * any result (yields void, need no name).
     * <br>
     * Thus, Type for Store is VoidType.
     * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Instructions.h#L304">
     *     LLVM IR Source: StoreInst</a>
     * @see <a href="https://llvm.org/docs/LangRef.html#store-instruction">
     *     LLVM LangRef: Store Instruction</a>
     */
    public static class Store extends MemoryInst {

        /**
         * @param val The Value to be stored (written) back to memory.
         * @param addr The address where the content to be written.
         */
        public Store(Value val, Value addr) {
            super(VoidType.getType(), InstCategory.STORE);
            this.addOperandAt(0, val);
            this.addOperandAt(1, addr);
        }


        @Override
        public String toString() {
            Value val = this.getOperandAt(0);
            Value addr = this.getOperandAt(1);
            // e.g. store i32 3, i32* %ptr
            return "store " // store
                    + val.getType() + " " + val.getName() + ", " // i32 3,
                    + addr.getType() + " " + addr.getName(); // i32* %ptr
        }
    }

    /**
     * Represents a ‘load’ instruction used to read from memory,
     * which yields the result of loaded memory block.
     * The argument (operand) to the load instruction specifies
     * the memory address from which to load.
     * <br>
     * Type for Load is the type of the memory block loaded in.
     * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Instructions.h#L175">
     *     LLVM IR Source: LoadInst</a>
     * @see <a href="https://llvm.org/docs/LangRef.html#load-instruction">
     *     LLVM LangRef: Load Instruction</a>
     */
    public static class Load extends MemoryInst {

        /**
         * @param loadedType  The type of the memory block loaded in.
         * @param addr Value specifying the memory address from which to load. (loadedType*)
         */
        public Load(Type loadedType, Value addr) {
            super(loadedType, InstCategory.LOAD);
            this.addOperandAt(0, addr);
        }


        @Override
        public String toString() {
            Value op = this.getOperandAt(0);
            // e.g. %val = load i32, i32* %ptr
            return this.getName() + " = load " // %val = load
                    + this.getType() + ", " // i32,
                    + op.getType() + " " + op.getName(); // , i32* %ptr
        }
    }

    /**
     * Alloca is an instruction to allocate memory on the stack,
     * yielding an address (pointer) where the memory allocated
     * lands as result.
     * <br>
     * Type for Alloca is PointerType.
     * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/Instructions.h#L62">
     *     LLVM IR Source: AllocaInst</a>
     * @see <a href="https://llvm.org/docs/LangRef.html#alloca-instruction">
     *     LLVM LangRef: Alloca Instrucstion</a>
     */
    public static class Alloca extends MemoryInst {
        /**
         * The type of memory space allocated.
         */
        private final Type allocatedType;

        public Type getAllocatedType() {
            return allocatedType;
        }



        /**
         * @param allocatedType The type of memory space allocated.
         */
        public Alloca(Type allocatedType) {
            super(PointerType.getType(allocatedType), InstCategory.ALLOCA);
            this.allocatedType = allocatedType;
        }

        @Override
        public String toString() {
            return this.getName() + " = alloca " + this.allocatedType;
        }
    }

}
