package frontend;

import ir.Module;
import ir.Type;
import ir.Value;
import ir.types.*;
import ir.values.*;
import ir.values.constants.ConstArray;
import ir.values.constants.ConstFloat;
import ir.values.constants.ConstInt;
import ir.values.instructions.*;

import java.util.ArrayList;

/**
 * An IRBuilder object maintains one building process of the IR.
 * <br>
 * It keeps track of the current positions of module, function and basic block
 * during building an IR module.
 * <br>
 * It can be used to create different types of IR constructs and insert constructs
 * Technically, Visitor should only use methods provided by IRBuilder to build IR
 * (e.g. build constructs, retrieve Constants), other than directly invoking
 * constructors of Values, which might lead to unsafe behaviors and chaos code.
 * <br>
 */
public class IRBuilder {

    //<editor-fold desc="Three current building pointers for different levels of IR.">
    /**
     * The module currently being built.
     * In our case with only a single compile unit to work on,
     * this reference is in effect immutable.
     */
    private Module curMdl;

    /**
     * The function currently being built.
     */
    private Function curFunc;

    /**
     * The basic block currently being built.
     */
    private BasicBlock curBB;
    //</editor-fold>


    /**
     * @param m The module to build on.
     */
    public IRBuilder(Module m) {
        this.setCurModule(m);
    }


    //<editor-fold desc="Setters for building pointers.">
    public void setCurModule(Module mod) {
        curMdl = mod;
    }

    public void setCurFunc(Function func) {
        curFunc = func;
    }

    public void setCurBB(BasicBlock bb) {
        curBB = bb;
    }
    //</editor-fold>

    //<editor-fold desc="Getters for building pointers.">
    public Module getCurModule() {
        return curMdl;
    }

    public Function getCurFunc() {
        return curFunc;
    }

    public BasicBlock getCurBB() {
        return curBB;
    }
    //</editor-fold>

    /**
     * Retrieve a Constant.ConstInt Value.
     * @param i The numeric value of the integer.
     * @return The ConstInt Value with given numeric value.
     */
    public ConstInt buildConstant(int i) {
        return ConstInt.getI32(i);
    }

    /**
     * Retrieve a Constant.ConstFloat Value.
     * @param f The numeric value of the float.
     * @return The ConstInt Value with given numeric value.
     */
    public ConstFloat buildConstant(float f) {
        return ConstFloat.get(f);
    }

    /**
     * Retrieve a Constant.ConstArray Value.
     * If the initial list is shorter than needed by ArrayType, zeros will be used to filled in.
     * @param arrType The ArrayType carrying necessary info.
     * @param initList An linear (no-nested) array of (integer/float) Constants for initialization the array.
     * @return The ConstArray.
     */
    public ConstArray buildConstArr(ArrayType arrType, ArrayList<Constant> initList) {
        /*
        If the initialization list is shorter than needed,
        filled the blanks with 0 (or .0f).
         */
        Type primType = arrType.getAtomType();
        while (arrType.getAtomLen() > initList.size()) {
           initList.add(primType.getZero());
        }


        if (arrType.getElemType().isArrayType()) {
            /*
            Build the nested initList from the given linear initList.
             */
            ArrayList<Constant> nestedInitList = new ArrayList<>();
            int j = 0;
            int step = arrType.getAtomLen() / arrType.getLen();
            while(j < initList.size()) {
                nestedInitList.add(
                        buildConstArr(
                                (ArrayType) arrType.getElemType(),
                                new ArrayList<>(initList.subList(j, j + step))
                        )
                );
                j += step;
            }

            return ConstArray.get(arrType, nestedInitList);
        }
        else {
            Type elemType = arrType.getElemType();
            for (int i = 0; i < initList.size(); i++) {
                Value elem = initList.get(i);
                if (elem.getType() != elemType) {
                    if (elemType.isFloatType()) { // cast elem i32 -> float
                        int numericVal = ((ConstInt) elem).getVal();
                        initList.set(i, buildConstant((float) numericVal));
                    }
                    else if (elemType.isIntegerType()) { // cast elem float -> i32
                        float numericVal = ((ConstFloat) elem).getVal();
                        initList.set(i, buildConstant((int) numericVal));
                    }
                }
            }
            return ConstArray.get(arrType, initList);
        }

    }

    /**
     * Insert a function into the module, with function name assigned.
     * Argument list and return type parts will leave to the child node
     * visiting.
     * @param name Function name (identifier).
     * @param type Value type.
     * @return Reference of the function created and inserted.
     */
    public Function buildFunction(String name, FunctionType type, boolean isExternal) {
        Function func = new Function(type, isExternal);
        func.setName(name);
        // Add the function to the current module.
        if(isExternal) {
            getCurModule().externFunctions.add(func);
        }
        else {
            getCurModule().functions.add(func);
            // Set the pointer.
            this.setCurFunc(func);
        }
        return func;
    }

    /**
     * Insert a new basic block at the end of current function.
     *
     * @param bbInfo Descriptive Information of the basic block to be created.
     * @return Reference of the basic block created.
     */
    public BasicBlock buildBB(String bbInfo) {
        BasicBlock bb = this.buildBB();
        bb.setInfo(bbInfo);
        return bb;
    }

    /**
     * Insert a new basic block at the end of current function.
     *
     * @return Reference of the basic block created.
     */
    public BasicBlock buildBB() {
        BasicBlock bb = new BasicBlock();
        curFunc.addBB(bb);
        // Set the pointer.
        this.setCurBB(bb);
        return bb;
    }

    /**
     * Insert a Ret terminator with no return value (return void)
     * at the end of current basic block.
     * @return The terminator object inserted.
     */
    public TerminatorInst.Ret buildRet() {
        // Security checks.
        if (!getCurFunc().getType().getRetType().isVoidType()) {
            throw new RuntimeException("Try to return void with Ret inst in a non-void function.");
        }
        // Construct, insert, and return.
        TerminatorInst.Ret ret = new TerminatorInst.Ret();
        getCurBB().insertAtEnd(ret);
        return ret;
    }

    /**
     * Insert a Ret terminator with a specified return value
     * at the end of current basic block.
     * @param retVal The Value instance specified as return value.
     * @return The terminator object inserted.
     */
    public TerminatorInst.Ret buildRet(Value retVal) {
        // Security checks.
        if (retVal.getType() != getCurFunc().getType().getRetType()) {
            throw new RuntimeException(
                    "The type of retVal doesn't match with the return type defined in the function prototype.");
        }
        // Construct, insert, and return.
        TerminatorInst.Ret ret = new TerminatorInst.Ret(retVal);
        getCurBB().insertAtEnd(ret);
        return ret;
    }

    /**
     * Insert a conditional branching instruction at the end of current basic block.
     * @param cond The condition.
     * @param trueBlk The basic block to jump to when condition is true.
     * @param falseBlk The basic block to jump to when condition is false.
     * @return The conditional branching inserted.
     */
    public TerminatorInst.Br buildBr(Value cond, BasicBlock trueBlk, BasicBlock falseBlk) {
        // Create and insert a block.
        TerminatorInst.Br condBr = new TerminatorInst.Br(cond, trueBlk, falseBlk);
        getCurBB().insertAtEnd(condBr);

        return condBr;
    }

    /**
     * Insert an unconditional branching instruction at the end of current basic block.
     * @param blk The basic block to jump to.
     * @return The unconditional branching inserted.
     */
    public TerminatorInst.Br buildBr(BasicBlock blk) {
        /*
        Security Check.
         */
        if (getCurBB().getLastInst() != null && getCurBB().getLastInst().isBr()) {
            throw new RuntimeException("Cannot insert a Br after another Br.");
        }
        // Create and insert a block.
        TerminatorInst.Br uncondBr = new TerminatorInst.Br(blk);
        getCurBB().insertAtEnd(uncondBr);

        return uncondBr;
    }

    /**
     * Insert a Call terminator invoking a given function with given list of arguments
     * at the end of current basic block.
     * The new basic block will not be automatically created by this method and
     * needs to be manually built and managed by the user.
     * @param func Function Value carrying information about return type and FORMAL arguments.
     * @param args The ACTUAL arguments to be referenced by the Call.
     * @return The call instruction inserted.
     */
    public CallInst buildCall(Function func, ArrayList<Value> args) {
        CallInst call = new CallInst(func, args);
        getCurBB().insertAtEnd(call);
        return call;
    }

    /**
     * Insert a Load instruction at the end of current basic block.
     * @param loadedType The type of the memory block loaded in.
     * @param addr loadedType*
     * @return The Load instruction inserted.
     */
    public MemoryInst.Load buildLoad(Type loadedType, Value addr) {
        MemoryInst.Load inst = new MemoryInst.Load(loadedType, addr);
        getCurBB().insertAtEnd(inst);
        return inst;
    }

    /**
     * Insert a Store instruction at the end of current basic block.
     * @param val The Value to be stored (written) back to memory.
     * @param addr The address where the content to be written.
     * @return The Store instruction inserted.
     */
    public MemoryInst.Store buildStore(Value val, Value addr) {
        MemoryInst.Store inst = new MemoryInst.Store(val, addr);
        getCurBB().insertAtEnd(inst);
        return inst;
    }

    /**
     * Insert an Alloca instruction at the FRONT of ENTRY basic block of the current Function.
     * @param allocatedType The type of memory space allocated.
     * @return The Alloca instruction inserted.
     */
    public MemoryInst.Alloca buildAlloca(Type allocatedType) {
        var entryBlk = getCurFunc().getEntryBB();
        MemoryInst.Alloca inst = new MemoryInst.Alloca(allocatedType);
        entryBlk.insertAtFront(inst);
        return inst;
    }

    /**
     * Insert a ZExt instruction at current position of basic block.
     * @param srcVal The Value to be extended.
     * @return The ZExt instruction inserted.
     */
    public CastInst.ZExt buildZExt(Value srcVal) {
        // Security checks.
        if (!srcVal.getType().isI1()) {
            throw new RuntimeException("A non-i1 src Value is given.");
        }
        // Construct, insert, and return.
        CastInst.ZExt zext = new CastInst.ZExt(srcVal);
        getCurBB().insertAtEnd(zext);
        return zext;
    }


    /**
     * Insert a Fptosi instruction at current position of basic block.
     * @param srcVal The Value to be converted.
     * @param destType The destination IntegerType (i32/i1).
     * @return The fptosi instruction inserted.
     */
    public CastInst.Fptosi buildFptosi(Value srcVal, IntegerType destType) {
        // Security checks.
        if (!srcVal.getType().isFloatType()) {
            throw new RuntimeException("A non-floatingPoint src Value is given.");
        }
        // Construct, insert, and return.
        CastInst.Fptosi fptosi = new CastInst.Fptosi(srcVal, destType);
        getCurBB().insertAtEnd(fptosi);
        return fptosi;
    }

    /**
     * Insert a Sitofp instruction at current position of basic block.
     * @param srcVal The Value to be converted.
     * @return The sitofp instruction inserted.
     */
    public CastInst.Sitofp buildSitofp(Value srcVal) {
        // Security checks.
        // TODO: move all this security checks to ir constructors?
        if (!srcVal.getType().isIntegerType()) {
            throw new RuntimeException("A non-integer src Value is given.");
        }
        // Construct, insert, and return.
        CastInst.Sitofp sitofp = new CastInst.Sitofp(srcVal);
        getCurBB().insertAtEnd(sitofp);
        return sitofp;
    }

    /**
     * Insert a Sitofp instruction at current position of basic block.
     * @param srcVal The Value to be converted.
     * @return The sitofp instruction inserted.
     */
    public CastInst.Bitcast buildBitcast(Value srcVal, PointerType dstType) {
        // Security checks.
        if (!srcVal.getType().isPointerType()) {
            throw new RuntimeException("A non-pointer src Value is given.");
        }
        // Construct, insert, and return.
        CastInst.Bitcast bitcast = new CastInst.Bitcast(srcVal, dstType);
        getCurBB().insertAtEnd(bitcast);
        return bitcast;
    }


    /**
     * Insert an Add instruction at current position of basic block.
     * ADD/FADD will be automatically determined according to the types
     * of operands given.
     * @param lOp Left operand.
     * @param rOp Right operand.
     * @return The binary instruction inserted.
     */
    public BinaryOpInst buildAdd(Value lOp, Value rOp) {
        // Security checks.
        if (lOp.getType() != rOp.getType()) {
            throw new RuntimeException("Unmatched types: [lOp] " + lOp.getType() + ", [rOp] " + rOp.getType());
        }

        // Constructor the Add instruction (for integers and floats respectively)
        BinaryOpInst instAdd;
        Type type = lOp.getType();
        if (type.isI32()) {
            instAdd = new BinaryOpInst(IntegerType.getI32(), Instruction.InstCategory.ADD, lOp, rOp);
        }
        else if(type.isFloatType()) {
            instAdd = new BinaryOpInst(FloatType.getType(), Instruction.InstCategory.FADD, lOp, rOp);
        }
        else {
            throw new RuntimeException("Unsupported type: " + type);
        }

        // Insert and return the inst.
        getCurBB().insertAtEnd(instAdd);
        return instAdd;
    }

    /**
     * Insert a subtraction instruction at current position of basic block.
     * SUB/FSUB will be automatically determined according to the types
     * of operands given.
     * @param lOp Left operand.
     * @param rOp Right operand.
     * @return The binary instruction inserted.
     */
    public BinaryOpInst buildSub(Value lOp, Value rOp) {
        // Security checks.
        if (lOp.getType() != rOp.getType()) {
            throw new RuntimeException("Unmatched types: [lOp] " + lOp.getType() + ", [rOp] " + rOp.getType());
        }

        // Constructor the Sub instruction (for integers and floats respectively)
        BinaryOpInst instSub;
        Type type = lOp.getType();
        if (type.isI32()) {
            instSub = new BinaryOpInst(IntegerType.getI32(), Instruction.InstCategory.SUB, lOp, rOp);
        }
        else if(type.isFloatType()) {
            instSub = new BinaryOpInst(FloatType.getType(), Instruction.InstCategory.FSUB, lOp, rOp);
        }
        else {
            throw new RuntimeException("Unsupported type: " + type);
        }

        // Insert and return the inst.
        getCurBB().insertAtEnd(instSub);
        return instSub;
    }

    /**
     * Insert a multiplication instruction at current position of basic block.
     * MUL/FMUL will be automatically determined according to the types
     * of operands given.
     * @param lOp Left operand.
     * @param rOp Right operand.
     * @return The binary instruction inserted.
     */
    public BinaryOpInst buildMul(Value lOp, Value rOp) {
        // Security checks.
        if (lOp.getType() != rOp.getType()) {
            throw new RuntimeException("Unmatched types: [lOp] " + lOp.getType() + ", [rOp] " + rOp.getType());
        }

        // Constructor the Mul instruction (for integers and floats respectively)
        BinaryOpInst instMul;
        Type type = lOp.getType();
        if (type.isI32()) {
            instMul = new BinaryOpInst(IntegerType.getI32(), Instruction.InstCategory.MUL, lOp, rOp);
        }
        else if(type.isFloatType()) {
            instMul = new BinaryOpInst(FloatType.getType(), Instruction.InstCategory.FMUL, lOp, rOp);
        }
        else {
            throw new RuntimeException("Unsupported type: " + type);
        }

        // Insert and return the inst.
        getCurBB().insertAtEnd(instMul);
        return instMul;
    }

    /**
     * Insert a division instruction at current position of basic block.
     * DIV/FDIV will be automatically determined according to the types
     * of operands given.
     * @param lOp Left operand.
     * @param rOp Right operand.
     * @return The binary instruction inserted.
     */
    public BinaryOpInst buildDiv(Value lOp, Value rOp) {
        // Security checks.
        if (lOp.getType() != rOp.getType()) {
            throw new RuntimeException("Unmatched types: [lOp] " + lOp.getType() + ", [rOp] " + rOp.getType());
        }

        // Constructor the Div instruction (for integers and floats respectively)
        BinaryOpInst instDiv;
        Type type = lOp.getType();
        if (type.isI32()) {
            instDiv = new BinaryOpInst(IntegerType.getI32(), Instruction.InstCategory.DIV, lOp, rOp);
        }
        else if(type.isFloatType()) {
            instDiv = new BinaryOpInst(FloatType.getType(), Instruction.InstCategory.FDIV, lOp, rOp);
        }
        else {
            throw new RuntimeException("Unsupported type: " + type);
        }

        // Insert and return the inst.
        getCurBB().insertAtEnd(instDiv);
        return instDiv;
    }

    /**
     * Insert a comparison (relational) instruction at current position of basic block.
     * ICMP/FCMP will be automatically determined according to the types
     * @param opr The string of the operator.
     * @param lOp Left operand.
     * @param rOp Right operand.
     * @return The binary instruction inserted.
     */
    public BinaryOpInst buildComparison(String opr, Value lOp, Value rOp) {
        // Security checks.
        if (lOp.getType() != rOp.getType()) {
            throw new RuntimeException("Unmatched types: [lOp] " + lOp.getType() + ", [rOp] " + rOp.getType());
        }


        BinaryOpInst inst = null;
        if (lOp.getType().isIntegerType()) {
            switch (opr) {
                case "<=" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.LE, lOp, rOp);
                case ">=" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.GE, lOp, rOp);
                case "<" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.LT, lOp, rOp);
                case ">" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.GT, lOp, rOp);
                case "==" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.EQ, lOp, rOp);
                case "!=" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.NE, lOp, rOp);
                default -> {}
            }
        }
        // Floating point comparison.
        else {
            switch (opr) {
                case "<=" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.FLE, lOp, rOp);
                case ">=" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.FGE, lOp, rOp);
                case "<" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.FLT, lOp, rOp);
                case ">" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.FGT, lOp, rOp);
                case "==" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.FEQ, lOp, rOp);
                case "!=" -> inst = new BinaryOpInst(IntegerType.getI1(), Instruction.InstCategory.FNE, lOp, rOp);
                default -> {}
            }
        }

        if (inst == null) {
            throw new RuntimeException("Operand '" + opr + "' cannot be recognized.");
        }
        // Insert and return the inst.
        getCurBB().insertAtEnd(inst);
        return inst;
    }

    /**
     * Insert a unary instruction at current position of basic block.
     * @param tag Instruction category.
     * @param opd The single operand of the instruction.
     * @return The unary instruction inserted.
     */
    public UnaryOpInst buildUnary(Instruction.InstCategory tag, Value opd) {
        // Analyze the type of the result returned by the binary operation.
        Type resType = null;
        switch (tag) {
            case FNEG -> resType = FloatType.getType();
        }
        // Build the binary instruction.
        UnaryOpInst unaryInst = new UnaryOpInst(resType, tag, opd);
        getCurBB().insertAtEnd(unaryInst);
        return unaryInst;
    }

    /**
     * Build a GlbVar w/o initialization.
     * @param name The name of the GlbVar.
     * @param type The type of the memory block it references.
     */
    public GlobalVariable buildGlbVar(String name, Type type) {
        GlobalVariable glbVar = new GlobalVariable(name, type);
        getCurModule().addGlobalVariable(glbVar);
        return glbVar;
    }

    /**
     * Build a Gv with initialization.
     * @param name The name of the GlbVar. If it's null, an anonymous Gv will be built.
     * @param init The initial value.
     */
    public GlobalVariable buildGlbVar(String name, Constant init) {
        GlobalVariable glbVar = new GlobalVariable(name, init);
        getCurModule().addGlobalVariable(glbVar);
        return glbVar;
    }


    /**
     * Insert a GEP instruction at current position of basic block.
     * @param ptr The Value in PointerType (the first address of an array).
     * @param indices The indices for dereference.
     * @return The GEP instruction inserted.
     */
    public GetElemPtrInst buildGEP(Value ptr, ArrayList<Value> indices) {
        GetElemPtrInst gepInst = new GetElemPtrInst(ptr, indices);
        getCurBB().insertAtEnd(gepInst);
        return gepInst;
    }
}
