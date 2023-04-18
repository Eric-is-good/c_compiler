package ir.values.instructions;

import ir.Type;
import ir.Value;
import ir.types.ArrayType;
import ir.types.PointerType;
import ir.values.Instruction;

import java.util.ArrayList;

/**
 * The Get Element Pointer (GEP) IR instruction.
 * <br>
 * Type for a GEP is a PointerType for the pointer retrieved.
 * @see <a href=">
 *     LLVM IR Source: </a>
 * @see <a href="https://llvm.org/docs/LangRef.html#getelementptr-instruction">
 *     LLVM LangRef: ‘getelementptr’ Instruction</a>
 * @see <a href="https://llvm.org/docs/GetElementPtr.html">
 *     LLVM LangRef: The Often Misunderstood GEP Instruction</a>
 */
public class GetElemPtrInst extends Instruction {

    /**
     * A private helper method for GEP constructor to get the Type after dereference of the indices.
     * @param ptr The Value in PointerType (the first address of an array).
     * @param indices The indices for dereference.
     * @return Element Type retrieved by the indices applying on the ptr.
     */
    static private Type getGEPElemType(Value ptr, ArrayList<Value> indices) {
        if(!ptr.getType().isPointerType()) {
            throw new RuntimeException("getElemType/GEP gets a non-pointer Value argument.");
        }

        Type retType = ((PointerType) ptr.getType()).getPointeeType();
        for (int i = 1; i < indices.size(); i++) {
            if (!retType.isArrayType()) {
                throw new RuntimeException("getElemType gets indices exceeding the nesting depth of the pointer.");
            }
            retType = ((ArrayType) retType).getElemType();
        }
        return retType;
    }

    /**
     * Construct a GEP instruction.
     * The 1st operand of a GEP is the ptr (first address of the array) applied on.
     * The following operands are the Values serving as indices.
     * @param ptr     The Value in PointerType (the first address of an array).
     * @param indices The indices for dereference.
     */
    public GetElemPtrInst(Value ptr, ArrayList<Value> indices) {
        this(PointerType.getType(getGEPElemType(ptr, indices)), ptr, indices);
    }


    /**
     * This is a raw constructor of GEP allowing user to specify return type of it,
     * because ptr can be a DummyValue from which the return type cannot be inferred.
     * @param retType Return type specified manually.
     * @param ptr     The Value for GEP (can be a DummyValue).
     * @param indices The indices for dereference.
     */
    public GetElemPtrInst(Type retType, Value ptr, ArrayList<Value> indices){
        super(retType, InstCategory.GEP);
        // The 1st operand of a GEP is the ptr (the address of the array) applied on.
        this.addOperandAt(0, ptr);
        // The following operands are the Values serving as indices.
        for (int i = 0; i < indices.size(); i++) {
            this.addOperandAt(i + 1, indices.get(i));
        }
    }


    @Override
    public String toString() {
        // e.g. "ar[1]" => "%5 = getelementptr [2 x i32], [2 x i32]* %2, i64 0, i64 1"
        StringBuilder strBuilder = new StringBuilder();
        Value ptr = this.getOperandAt(0);
        PointerType ptrType = (PointerType) ptr.getType();

        // "%5 = getelementptr [2 x i32], [2 x i32]* %2, "
        strBuilder.append(this.getName())
                .append(" = getelementptr ")
                .append(ptrType.getPointeeType())
                .append(", ")
                .append(ptr.getType())
                .append(" ")
                .append(ptr.getName())
                .append(", ");
        // "i64 0, i64 1"
        for (int i = 1; i < this.getNumOperands(); i++) {
            Value opd = this.getOperandAt(i); // operand
            strBuilder.append(opd.getType())
                    .append(" ")
                    .append(opd.getName());
            if (i < this.getNumOperands() - 1) {
                strBuilder.append(", ");
            }
        }
        return strBuilder.toString();
    }

    @Override
    public PointerType getType() {
        return (PointerType) super.getType();
    }
}
