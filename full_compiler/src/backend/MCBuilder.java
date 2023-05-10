package backend;

import backend.armCode.MCBasicBlock;
import backend.armCode.MCFunction;
import backend.armCode.MCInstruction;
import backend.armCode.MCInstructions.*;
import backend.operand.*;
import ir.Module;
import ir.Type;
import ir.Value;
import ir.types.ArrayType;
import ir.types.PointerType;
import ir.values.*;
import ir.values.constants.ConstFloat;
import ir.values.constants.ConstInt;
import ir.values.instructions.*;
import passes.ir.analysis.IRelationAnalysis;
import passes.ir.analysis.RelationAnalysis;

import java.math.BigInteger;
import java.util.*;

/**
 * This class is used to CodeGen, or translate LLVM IR into ARM assemble
 * (both are in memory). As the emitter, it's a Singleton.
 */
public class MCBuilder {

    class IRBlockInfo extends passes.ir.BasicBlock implements IRelationAnalysis<IRBlockInfo> {

        public LinkedList<IRBlockInfo> predecessors = new LinkedList<>();
        public IRBlockInfo trueBlock;
        public IRBlockInfo falseBlock;

        @Override
        public void addEntryBlock(IRBlockInfo entryBlock) {
            predecessors.add(entryBlock);
        }

        @Override
        public void setExitBlocks(List<IRBlockInfo> exitBlocks) {
            switch (exitBlocks.size()) {
                case 1 -> trueBlock = exitBlocks.get(0);
                case 2 -> {
                    trueBlock = exitBlocks.get(0);
                    falseBlock = exitBlocks.get(1);
                }
            }
        }

        public IRBlockInfo(BasicBlock rawBasicBlock) {
            super(rawBasicBlock);
        }
    }

    //<editor-fold desc="Singleton Pattern">
    static private final MCBuilder builder = new MCBuilder();

    private MCBuilder() {}

    static public MCBuilder get() {return builder;}
    //</editor-fold>

    //<editor-fold desc="Fields">
    private Module IRModule;

    private ARMAssemble target;

    /* Current MC function & basic block */
    private Function curIRFunc;
    private MCFunction curFunc;
    private MCBasicBlock curMCBB;

    /**
     * This class records the map between values and virtual registers.
     */
    private HashMap<Value, VirtualRegister> valueMap;
    private HashMap<Value, VirtualExtRegister> floatValueMap;
    private Map<BasicBlock, IRBlockInfo> blockInfo;

    /**
     * Contains the floats that can be encoded in VMOV instruction.
     * @see FPImmediate
     */
    private static final HashSet<Float> immFloat = new HashSet<>();
    static {
        float t = 2.0f;
        for (int n=0; n<=7; n++) {
            t /= 2;
            for (int m=16; m<=31; m++) {
                float tmp = m * t;
                immFloat.add(tmp);
                immFloat.add(-tmp);
            }
        }
    }
    //</editor-fold>


    /**
     * Load LLVM IR module. Separate this process to support multi-module codegen.
     * (if possible?)
     * @param m IR module
     */
    public void loadModule(Module m) {IRModule = m;}


    /**
     * Translate LLVM IR to ARM assemble target. <br/>
     * <ul>
     *     <li>Map IR global variable into target global variable list</li>
     *     <li>Map IR function into target function list and Map IR BasicBlock into target function BasicBlock</li>
     *     <li>Travel the BasicBlock and then translate into ARM instruction</li>
     *     <li>Handle PHI instruction</li>
     *     <li>Calculate loop info of function & block ( TO BE FINISHED )</li>
     * </ul>
     * @return generated ARM assemble target
     */
    public ARMAssemble codeGeneration() {
        target = new ARMAssemble();
        mapGlobalVariable(IRModule, target);
        mapFunction(IRModule, target);

        return target;
    }

    private void mapGlobalVariable(Module IRModule, ARMAssemble target) {
        for (GlobalVariable gv : IRModule.getGlobalVariables())
            target.addGlobalVariable(gv);
    }

    /**
     * Map IR Function into MC Function and Add into assemble target
     * <ul>
     *     <li>Create MC Function</li>
     *     <li>Create MC BasicBlock for each BasicBlock in IR Function</li>
     *     <li>Translate BasicBlock</li>
     * </ul>
     */
    private void mapFunction(Module IRModule, ARMAssemble target) {
        for (Function IRfunc : IRModule.externFunctions) {
            target.useExternalFunction(IRfunc);
        }

        for (Function IRfunc : IRModule.functions) {
            curIRFunc = IRfunc;
            valueMap = new HashMap<>();
            floatValueMap = new HashMap<>();
            blockInfo = new HashMap<>();
            IRfunc.forEach(bb -> blockInfo.put(bb, new IRBlockInfo(bb)));
            RelationAnalysis.analysisBasicBlocks(blockInfo);
            curFunc = target.createFunction(IRfunc);

            /* This loop is to create the MC basic block in the same order of IR */
            for (BasicBlock IRBB : IRfunc)
                curFunc.createBB(IRBB);

            for (BasicBlock IRBB : IRfunc){
                curMCBB = curFunc.findMCBB(IRBB);
                for (Instruction IRinst : IRBB) {
                    translate(IRinst);
                }
            }

            translatePhi();

            /* Allocate function variable stack in front of procedure */
            MCBasicBlock entry = curFunc.getEntryBlock();
            int variableSize = curFunc.getLocalVariable();
            if (canEncodeImm(variableSize)) {
                entry.prependInst(new MCBinary(MCInstruction.TYPE.SUB, RealRegister.get(13), RealRegister.get(13), new Immediate(variableSize)));
            } else {
                VirtualRegister vr = curFunc.createVirReg(variableSize);
                entry.prependInst(new MCBinary(MCInstruction.TYPE.SUB, RealRegister.get(13), RealRegister.get(13), vr));
                entry.prependInst(new MCMove(vr, new Immediate(variableSize), true));
            }

            /* Adjust parameter loads' offset */
            curFunc.getParamCal().forEach(move -> {
                int new_offset =
                        ((Immediate) move.getSrc()).getIntValue()
                        + curFunc.getLocalVariable();
                move.setSrc(new Immediate(new_offset));
                if (!canEncodeImm(new_offset))
                    move.setExceededLimit();
            });
        }
    }

    /**
     * Instruction selection, using Macro expansion
     * @param IRinst IR instruction to be translated
     */
    private void translate(Instruction IRinst) {
        switch (IRinst.getTag()) {
            case RET    -> translateRet((TerminatorInst.Ret) IRinst);
            case ADD    -> translateAddSub((BinaryOpInst) IRinst, MCInstruction.TYPE.ADD);
            case SUB    -> translateAddSub((BinaryOpInst) IRinst, MCInstruction.TYPE.SUB);
            case MUL    -> translateMul((BinaryOpInst) IRinst);
            case DIV    -> translateSDiv((BinaryOpInst) IRinst);
            case ALLOCA -> translateAlloca((MemoryInst.Alloca) IRinst);
            case STORE  -> translateStore((MemoryInst.Store) IRinst);
            case LOAD   -> translateLoad((MemoryInst.Load) IRinst);
            case CALL   -> translateCall((CallInst) IRinst);
            case BR     -> translateBr((TerminatorInst.Br) IRinst);
            case GEP    -> translateGEP((GetElemPtrInst) IRinst);
            case FADD   -> translateFloatBinary((BinaryOpInst) IRinst, MCInstruction.TYPE.VADD);
            case FSUB   -> translateFloatBinary((BinaryOpInst) IRinst, MCInstruction.TYPE.VSUB);
            case FMUL   -> translateFloatBinary((BinaryOpInst) IRinst, MCInstruction.TYPE.VMUL);
            case FDIV   -> translateFloatBinary((BinaryOpInst) IRinst, MCInstruction.TYPE.VDIV);
            case FNEG   -> translateFloatNeg((UnaryOpInst) IRinst);
            case FPTOSI -> translateConvert((CastInst) IRinst, true);
            case SITOFP -> translateConvert((CastInst) IRinst, false);
        }
        if (PrintInfo.printIR && !IRinst.isAlloca() && !IRinst.isPhi() && !IRinst.isIcmp() && !IRinst.isFcmp() && !IRinst.isZext()) {
            curMCBB.getLastInst().val = IRinst;
        }
    }


    //<editor-fold desc="Tools">
    /**
     * Allocate a container or find the virtual register for a IR value.<br/><br/>
     * What's a container? I consider a MC operand as a container. IR
     * values are stored in the immediate position, or register. <br/><br/>
     * This function is the key of the codegen, which manages and determines all the containers of all instructions. <br/><br/>
     * The IF order in the function matters! ValueMap as the watershed,
     * all the IF before it will generate a bunch of instructions in the front of current instruction EACH TIME when called with the same argument,
     * while the IF after it will be executed ONLY ONCE and generate code in the front of entry block to DOM all the node! <br/><br/>
     * Therefore, the IF before valueMap can be optimized to insert the code in first node before, which DOMs all the use of it, which may decrease the number of redundant code,
     * while IF after valueMap can be optimized to delay the code to the last node DOMing all the use of it, which may shorten the live range of variable in code.
     * The optimization above may need the GCM pass. <br/><br/>
     * @param value the value to handle
     * @param forceAllocReg force allocate a virtual register if true
     * @return the corresponding container (ONLY core register or integer immediate)
     */
    private MCOperand findContainer(Value value, boolean forceAllocReg) {
        // TODO: GCM maybe?
        if (value instanceof Constant) {
            int val = value instanceof ConstInt
                    ? ((ConstInt) value).getVal()
                    : Float.floatToRawIntBits(((ConstFloat) value).getVal());
            MCOperand temp = createConstInt(val);
            if (temp.isVirtualReg())
                return temp;
            /* temp is immediate */
            else {
                if (forceAllocReg){
                    /* TODO: If force to allocate a register, should we create a new one or attempt to find one hold in VR? */
                        /* Create new one: more MOV instruction is created */
                        /* Find old one: Expand the live range of one VR, may cause SPILLING, and must follow the CFG */
                    /* For now, considering the same constant may NOT be too many, try to create new one */
//                    if (valueMap.containsKey(value))
//                        return valueMap.get(value);
//                    else {
                        VirtualRegister vr = curFunc.createVirReg(val);
                        valueMap.put(value, vr);
                        curMCBB.appendInst(new MCMove(vr, temp));
                        return vr;
//                    }
                }
                else
                    return temp;
            }
        }
        else if (value instanceof GlobalVariable) {
            VirtualRegister vr = curFunc.createVirReg(value);
            valueMap.put(value, vr);
            curMCBB.appendInst(new MCMove(vr, target.findGlobalVar((GlobalVariable) value)));
            return vr;
        }
        else if (valueMap.containsKey(value)) {
            return valueMap.get(value);
        }
        else if (value instanceof Instruction) {
            var inst = ((Instruction) value);
            VirtualRegister vr;
            /* This is used to translate the ZExt instruction */
            /* Considering that all data used in competition is 32 bits, */
            /* ignore the ZExt instruction */
            /* and use the origin instruction's container */
            if (inst.isZext())
                vr = ((VirtualRegister) findContainer(inst.getOperandAt(0)));
            else if (inst.isBitcast())
                vr = (VirtualRegister) findContainer(inst.getOperandAt(0));
            else
                vr = curFunc.createVirReg(inst);

            valueMap.put(inst, vr);
            return vr;
        }
        else if (value instanceof Function.FuncArg && curIRFunc.getArgs().contains(value)) {
            // TODO: better way: 在spill的时候选择load源地址，不过运行时间没有区别
            VirtualRegister vr = curFunc.createVirReg(value);
            valueMap.put(value, vr);
            MCBasicBlock entry = curFunc.getEntryBlock();

            if (curFunc.getAPVCR().contains(value)) {
                entry.prependInst(new MCMove(vr, RealRegister.get(curFunc.getAPVCR().indexOf(value))));
            }
            else if (curFunc.getAPVER().contains(value)) {
                entry.prependInst(new MCFPmove(vr, RealExtRegister.get(curFunc.getAPVER().indexOf(value))));
            }
            else {
                int offsetVal = curFunc.getACTM().indexOf(value)*4;
                var offset = curFunc.createVirReg(offsetVal);
                entry.prependInst(new MCload(vr, RealRegister.get(13), offset));
                var move = new MCMove(offset, new Immediate(offsetVal), !canEncodeImm(offsetVal));
                entry.prependInst(move);
                if (PrintInfo.printIR)
                    move.val = value;
                curFunc.addParamCal(move);
            }

            return vr;
        }
        else
            return null;
    }

    /**
     * Syntactic sugar of {@link #findContainer(Value, boolean)}. <br/>
     * Default do not force allocate a virtual register. <br/>
     */
    private MCOperand findContainer(Value value) {
        return findContainer(value, false);
    }

    /**
     * Allocate a container for float value
     * @param value the value that needs a container
     * @param forceAllocReg need to force allocate an extension register for the value
     * @return the corresponding container (ONLY extension register or float immediate)
     * @see #findContainer(Value, boolean)
     */
    private MCOperand findFloatContainer(Value value, boolean forceAllocReg) {
        if (value instanceof Constant) {
            /* Find a float container for the const float (the format is IEEE 754 FLOAT) */
            if (value instanceof ConstFloat) {
                float v = ((ConstFloat) value).getVal();
                if (canEncodeFloat(v)) {
                    if (forceAllocReg) {
                        var extVr = curFunc.createExtVirReg(value);
                        curMCBB.appendInst(new MCFPmove(extVr, new FPImmediate(v)));
                        floatValueMap.put(value, extVr);
                        return extVr;
                    } else {
                        return new FPImmediate(v);
                    }
                }
                else {
                    var vr = curFunc.createVirReg(value);
                    var extVr = curFunc.createExtVirReg(value);
                    curMCBB.appendInst(new MCMove(vr, new FPImmediate(v), true));
                    curMCBB.appendInst(new MCFPmove(extVr, vr));
                    floatValueMap.put(value, extVr);
                    return extVr;
                }
            }
            /* Find a float container for the const int (the format is IEEE 754 INT!) */
            else {
                var vr = (Register) findContainer(value, true);
                var extVr = curFunc.createExtVirReg(value);
                curMCBB.appendInst(new MCFPmove(extVr, vr));
                return extVr;
            }
        }
        else if (floatValueMap.containsKey(value)) {
            return floatValueMap.get(value);
        }
        else if (value instanceof Instruction) {
            var inst = ((Instruction) value);
            var extVr = curFunc.createExtVirReg(inst);
            floatValueMap.put(value, extVr);
            return extVr;
        }
        else if (value instanceof Function.FuncArg && curIRFunc.getArgs().contains(value)) {
            VirtualExtRegister extVr = curFunc.createExtVirReg(value);
            floatValueMap.put(value, extVr);
            MCBasicBlock entry = curFunc.getEntryBlock();

            if (curFunc.getAPVER().contains(value)) {
                entry.prependInst(new MCFPmove(extVr, RealExtRegister.get(curFunc.getAPVER().indexOf(value))));
            }
            else if (curFunc.getAPVCR().contains(value)) {
                entry.prependInst(new MCFPmove(extVr, RealRegister.get(curFunc.getAPVCR().indexOf(value))));
            }
            else {
                int offsetVal = curFunc.getACTM().indexOf(value)*4;
                var addr = curFunc.createVirReg(offsetVal);
                entry.prependInst(new MCFPload(extVr, addr));
                entry.prependInst(new MCBinary(MCInstruction.TYPE.ADD, addr, RealRegister.get(13), addr));
                var move = new MCMove(addr, new Immediate(offsetVal), !canEncodeImm(offsetVal));
                entry.prependInst(move);
                curFunc.addParamCal(move);
            }

            return extVr;
        }
        else
            return null;
    }

    /**
     * Syntactic sugar of {@link #findFloatContainer(Value, boolean)} <br/>
     * Default DO force allocate a virtual extension register
     * , because most float instruction use extension register
     * rather than immediate. <br/>
     */
    private MCOperand findFloatContainer(Value value) {
        return findFloatContainer(value, true);
    }


    /**
     * This function is used to determine whether a number can
     * be put into an immediate container. <br/><br/>
     * ARM can ONLY use 12 bits to represent an immediate, which is separated
     * into 8 bits representing number and 4 bits representing rotate right(ROR).
     * This means 'shifter_operand = immed_8 Rotate_Right (rotate_imm * 2)'. <br/>
     * @see <a href='https://www.cnblogs.com/walzer/archive/2006/02/05/325610.html'>ARM汇编中的立即数<a/> <br/>
     * ARM Architecture Reference Manual(ARMARM) P446.
     * @param n the to be determined
     * @return the result
     */
    public static boolean canEncodeImm(int n) {
        for (int ror = 0; ror < 32; ror += 2) {
            /* checkout whether the highest 24 bits is all 0. */
            if ((n & ~0xFF) == 0) {
                return true;
            }
            /* n rotate left 2 */
            n = (n << 2) | (n >>> 30);
        }
        return false;
    }

    /**
     * Float immedaite can only be +/- m * 2 ^ (-n)
     * @see FPImmediate
     */
    public static boolean canEncodeFloat(float n) {
        return immFloat.contains(n);
    }

    /**
     * Determine whether a number is the integer power of two.
     */
    private boolean isPowerOfTwo(int n) {
        return (n & (n-1)) == 0;
    }

    /**
     * Calculate the ceil result of log_2 n
     * @param n the integer power of 2
     * @return the result
     */
    private int log2(int n) {
        int ret = 0;
        while (n != 0){
            n = n >>> 1;
            ret++;
        }
        return ret-1;
    }


    /**
     * Create a container for a constant INT, may be immediate or register.
     * @param value the INT value to be translated into an immediate
     * @return the created container, maybe register or immediate.
     * @see MCBuilder#canEncodeImm(int)
     */
    private MCOperand createConstInt(int value, boolean forceAllocReg){
        if (!forceAllocReg && canEncodeImm(value))
            return new Immediate(value);
        else{
            VirtualRegister vr = curFunc.createVirReg(value);
            curMCBB.appendInst(new MCMove(vr, new Immediate(value), true));
            return vr;
        }
    }

    private MCOperand createConstInt(int value) {
        return createConstInt(value, false);
    }

    /**
     * Map the IR icmp into ARM condition field.
     * @param IRinst icmp instruction
     * @return the corresponding ARM condition field
     */
    private MCInstruction.ConditionField mapToArmCond(BinaryOpInst IRinst) {
        return switch (IRinst.getTag()) {
            case EQ, FEQ -> MCInstruction.ConditionField.EQ;
            case NE, FNE -> MCInstruction.ConditionField.NE;
            case GE, FGE -> MCInstruction.ConditionField.GE;
            case LE, FLE -> MCInstruction.ConditionField.LE;
            case GT, FGT -> MCInstruction.ConditionField.GT;
            case LT, FLT -> MCInstruction.ConditionField.LT;
            default -> null;
        };
    }

    /**
     * Get the opposite ARM condition field.
     * @param cond ARM condition field to be reversed
     * @return the reversed result
     */
    private MCInstruction.ConditionField reverseCond(MCInstruction.ConditionField cond) {
        return switch (cond) {
            case EQ -> MCInstruction.ConditionField.NE;
            case NE -> MCInstruction.ConditionField.EQ;
            case GE -> MCInstruction.ConditionField.LT;
            case LE -> MCInstruction.ConditionField.GT;
            case GT -> MCInstruction.ConditionField.LE;
            case LT -> MCInstruction.ConditionField.GE;
        };
    }

    /**
     * Get the new condition field after exchanging the compare operands
     */
    private MCInstruction.ConditionField exchangeCond(MCInstruction.ConditionField cond) {
        return switch (cond) {
            case EQ -> MCInstruction.ConditionField.EQ;
            case NE -> MCInstruction.ConditionField.NE;
            case GE -> MCInstruction.ConditionField.LE;
            case LE -> MCInstruction.ConditionField.GE;
            case GT -> MCInstruction.ConditionField.LT;
            case LT -> MCInstruction.ConditionField.GT;
        };
    }
    //</editor-fold>


    //<editor-fold desc="Translate functions">
    /**
     * Translate IR Call instruction into ARM instruction. <br/>
     * r0-r3 & s0-s15 are caller-saved registers, via which the first 4 int & 16 float arguments will be passed. <br/>
     * The other arguments will be copied to memory, pushed in the reversed order. <br/>
     * Function stack (from high to low): parameter, context, spilled nodes, local variables <br/>
     * @param IRinst IR call instruction
     * @see <a href='https://web.eecs.umich.edu/~prabal/teaching/resources/eecs373/ARM-AAPCS-EABI-v2.08.pdf'>Procedure Call Standard for the ARM Architecture</a> <br/>
     * Charpter 5 The Base Procedure Call Standard & Charpter 6 The Standard Variants
     */
    private void translateCall(CallInst IRinst) {
        var callee = (Function) IRinst.getOperandAt(0);
        var MCCalee = target.findMCFunc(callee);
        var args = callee.getArgs();
        var APVCR = MCCalee.getAPVCR();
        var APVER = MCCalee.getAPVER();
        var ACTM = MCCalee.getACTM();

        /* Generate Instruction */
        int stackSize = ACTM.size();
        for (int i = IRinst.getNumOperands()-1; i > 0; i--) {
            var param = IRinst.getOperandAt(i);
            var crspArg = args.get(i-1);
            if (ACTM.contains(crspArg)) {
                if (param.getType().isIntegerType() || param.getType().isPointerType() || param instanceof ConstFloat)
                    curMCBB.appendInst(new MCstore(
                            (Register) findContainer(param, true),
                            RealRegister.get(13),
                            new Immediate((ACTM.indexOf(crspArg)-stackSize)*4)
                    ));
                else
                    curMCBB.appendInst(new MCFPstore(
                            (ExtensionRegister) findFloatContainer(param),
                            RealRegister.get(13),
                            new Immediate((ACTM.indexOf(crspArg)-stackSize)*4)
                    ));
            }
            else if (APVER.contains(crspArg))
                curMCBB.appendInst(new MCFPmove(RealExtRegister.get(APVER.indexOf(crspArg)), findFloatContainer(param, false)));
            else if (APVCR.contains(crspArg))
                curMCBB.appendInst(new MCMove(RealRegister.get(APVCR.indexOf(crspArg)), findContainer(param)));
        }

        /* SUB sp here, instead of using PUSH, to avoid segmentation fault if spilling happens in parameter passing */
        if (stackSize > 0)
            curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.SUB, RealRegister.get(13), RealRegister.get(13), createConstInt(stackSize * 4)));
        /* Branch */
        curMCBB.appendInst(new MCbranch(target.findMCFunc((Function) IRinst.getOperandAt(0))));
        /* Stack balancing */
        if (stackSize > 0)
            curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, RealRegister.get(13), RealRegister.get(13), createConstInt(stackSize * 4)));
        /* Save result */
        if (callee.getType().getRetType().isFloatType())
            curMCBB.appendInst(new MCFPmove((VirtualExtRegister) findFloatContainer(IRinst), RealExtRegister.get(0)));
        else if (!callee.getType().getRetType().isVoidType())
            curMCBB.appendInst(new MCMove((Register) findContainer(IRinst), RealRegister.get(0)));

        curFunc.setUseLR();
    }

    private void translateRet(TerminatorInst.Ret IRinst) {
        if (IRinst.getNumOperands() != 0) {
            var returnValue = IRinst.getOperandAt(0);
            if (returnValue.getType().isFloatType()){
                MCOperand ret = findFloatContainer(returnValue, false);
                curMCBB.appendInst(new MCFPmove(RealExtRegister.get(0),  ret));
            }
            else
                curMCBB.appendInst(new MCMove(RealRegister.get(0), findContainer(returnValue)));
        }
        curMCBB.appendInst(new MCReturn());
    }

    /**
     * Translate the IR alloca instruction into ARM. <br/>
     * IR Alloca will be translated into "VirReg := sp + offset_before".
     * And sp will be added in front of a procedure,
     * meaning that the stack of function is allocated at beginning,
     * and do NOT change in the procedure until a function call then balanced. <br/>
     * @param IRinst IR instruction to be translated
     */
    private void translateAlloca(MemoryInst.Alloca IRinst) {
        // TODO: calculate address when used
        int offset = 0;
        Type allocated = IRinst.getAllocatedType();
        if (allocated.isIntegerType() || allocated.isFloatType() || allocated.isPointerType()) {
            offset = 4;
        }
        else if (allocated.isArrayType()) {
            offset = ((ArrayType) allocated).getAtomLen() * 4;
        }
        curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, (Register) findContainer(IRinst), RealRegister.get(13), createConstInt(curFunc.getLocalVariable())));

        curFunc.addLocalVariable(offset);
    }

    /**
     * Translate the IR store instruction into ARM. <br/>
     * @param IRinst IR instruction to be translated
     */
    private void translateStore(MemoryInst.Store IRinst) {
        Value source = IRinst.getOperandAt(0);
        Register addr = ((Register) findContainer(IRinst.getOperandAt(1)));
        if (source.getType().isIntegerType() || source.getType().isPointerType() || source instanceof ConstFloat){
            Register src = ((Register) findContainer(source, true));
            curMCBB.appendInst(new MCstore(src, addr));
        }
        else {
            ExtensionRegister src = (ExtensionRegister) findFloatContainer(source);
            curMCBB.appendInst(new MCFPstore(src, addr));
        }
    }

    /**
     * Translate IR load, just like its name.
     * @param IRinst IR instruction to be translated
     */
    private void translateLoad(MemoryInst.Load IRinst) {
        if (IRinst.getType().isFloatType())
            curMCBB.appendInst(new MCFPload((ExtensionRegister) findFloatContainer(IRinst), (Register) findContainer(IRinst.getOperandAt(0))));
        else
            curMCBB.appendInst(new MCload((Register) findContainer(IRinst), ((Register) findContainer(IRinst.getOperandAt(0)))));
    }

    /**
     * Translate IR br instruction into ARM branch and a lot of condition calculate in front.
     * @param IRinst IR instruction to be translated
     */
    private void translateBr(TerminatorInst.Br IRinst) {
        if (IRinst.isCondJmp()) {
            if (IRinst.getOperandAt(0) instanceof ConstInt) {
                int cond = ((ConstInt) IRinst.getOperandAt(0)).getVal();
                if (cond == 0)
                    curMCBB.appendInst(new MCbranch(curFunc.findMCBB((BasicBlock) IRinst.getOperandAt(2))));
                else
                    curMCBB.appendInst(new MCbranch(curFunc.findMCBB((BasicBlock) IRinst.getOperandAt(1))));
            }
            else {
                MCInstruction.ConditionField cond = dealCmpOpr((BinaryOpInst) IRinst.getOperandAt(0), false);
                curMCBB.appendInst(new MCbranch(curFunc.findMCBB((BasicBlock) IRinst.getOperandAt(1)), cond));
                curMCBB.appendInst(new MCbranch(curFunc.findMCBB((BasicBlock) IRinst.getOperandAt(2)), reverseCond(cond)));
            }
        }
        else {
            curMCBB.appendInst(new MCbranch(curFunc.findMCBB((BasicBlock) IRinst.getOperandAt(0))));
        }
    }

    /**
     * Translate all the compare instruction <br/>
     * ZEXT will be translated to a CMP with saveResult true
     * @param cmp the conditional instruction, including ICMP, FCMP, ZEXT
     * @param saveResult whether to save the compare result to a register
     * @return the corresponding ARM condition field of the compare
     */
    private MCInstruction.ConditionField dealCmpOpr(Instruction cmp, boolean saveResult) {
        if (cmp.isIcmp())
            return translateIcmp((BinaryOpInst) cmp, saveResult);
        else if (cmp.isFcmp())
            return translateFcmp((BinaryOpInst) cmp, saveResult);
        else if (cmp.isZext())
            return dealCmpOpr((BinaryOpInst) cmp.getOperandAt(0), true);
        else
            return null;
    }

    /**
     * Translate the IR icmp into a lot of ARM calculation.
     * @param icmp IR instruction to be translated
     * @param saveResult whether to save the compare result to a register
     * @return the corresponding ARM condition field of icmp
     */
    private MCInstruction.ConditionField translateIcmp(BinaryOpInst icmp, boolean saveResult) {
        Value value1 = icmp.getOperandAt(0);
        Value value2 = icmp.getOperandAt(1);

        /* If there is cmp or zext instruction in operands */
        if (value1 instanceof Instruction)
            dealCmpOpr((Instruction) value1, true);
        if (value2 instanceof Instruction)
            dealCmpOpr((Instruction) value2, true);

        /* Translate */
        Register operand1;
        MCOperand operand2;
        MCInstruction.ConditionField armCond = mapToArmCond(icmp);
        if (value1 instanceof ConstInt && !(value2 instanceof ConstInt)){
            operand1 = (Register) findContainer(value2);
            operand2 = findContainer(value1);
            armCond = exchangeCond(armCond);
        }
        else {
            operand1 = (Register) findContainer(value1, true);
            operand2 = findContainer(value2);
        }
        curMCBB.appendInst(new MCcmp(operand1, operand2));

        /* Save result */
        if (saveResult) {
            curMCBB.appendInst(new MCMove((Register) findContainer(icmp), createConstInt(1), null, armCond));
            curMCBB.appendInst(new MCMove((Register) findContainer(icmp), createConstInt(0), null, reverseCond(armCond)));
        }

        if (PrintInfo.printIR)
            curMCBB.getLastInst().val = icmp;

        return armCond;
    }

    /**
     * Translate Fcmp into ARM VCMP
     * @param fcmp IR FCMP instruction
     * @param saveResult whether to save the compare result to a register
     * @return the corresponding ARM condition field of fcmp
     */
    private MCInstruction.ConditionField translateFcmp(BinaryOpInst fcmp, boolean saveResult) {
        Value value1 = fcmp.getOperandAt(0);
        Value value2 = fcmp.getOperandAt(1);

        /* If there is cmp or zext instruction in operands */
        if (value1 instanceof Instruction)
            dealCmpOpr((Instruction) value1, true);
        if (value2 instanceof Instruction)
            dealCmpOpr((Instruction) value2, true);

        /* Translate */
        ExtensionRegister operand1 = (ExtensionRegister) findFloatContainer(value1);
        ExtensionRegister operand2 = (ExtensionRegister) findFloatContainer(value2);
        MCInstruction.ConditionField armCond = mapToArmCond(fcmp);
        curMCBB.appendInst(new MCFPcompare(operand1, operand2));
        curMCBB.appendInst(new MCFPmove());

        /* Save result */
        if (saveResult) {
            curMCBB.appendInst(new MCMove((Register) findContainer(fcmp), createConstInt(1), null, armCond));
            curMCBB.appendInst(new MCMove((Register) findContainer(fcmp), createConstInt(0), null, reverseCond(armCond)));
        }

        if (PrintInfo.printIR)
            curMCBB.getLastInst().val = fcmp;

        return armCond;
    }

    /**
     * Translate IR binary expression instruction into ARM instruction. <br/>
     * NOTE: The first operand must be a REGISTER!!
     * @param IRinst IR instruction to be translated
     * @param type Operation type, ADD/SUB
     */
    private void translateAddSub(BinaryOpInst IRinst, MCInstruction.TYPE type) {
        Value value1 = IRinst.getOperandAt(0);
        Value value2 = IRinst.getOperandAt(1);

        /* If there is icmp instruction in operands */
        if (value1 instanceof CastInst.ZExt)
            translateIcmp((BinaryOpInst) ((CastInst.ZExt) value1).getOperandAt(0), true);
        if (value2 instanceof CastInst.ZExt)
            translateIcmp((BinaryOpInst) ((CastInst.ZExt) value2).getOperandAt(0), true);

        MCOperand operand1 = findContainer(value1);
        MCOperand operand2 = findContainer(value2);
        Register dst = (Register) findContainer(IRinst);

        /* Translate */
        if (operand1.isImmediate()) {
            if (operand2.isImmediate()) {
                /* This case should not happen, so no optimization here */
                VirtualRegister register = (VirtualRegister) findContainer(IRinst.getOperandAt(0), true);
                curMCBB.appendInst(new MCBinary(type, dst, register, operand2));
            }
            else {
                if (type != MCInstruction.TYPE.SUB) {
                    if (((ConstInt) value1).getVal() == 0)
                        curMCBB.appendInst(new MCMove(dst, operand2));
                    else
                        curMCBB.appendInst(new MCBinary(type, dst, (Register) operand2, operand1));
                }
                else
                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.RSB, dst, (Register) operand2, operand1));
            }
        }
        else {
            if (operand2.isImmediate() && ((ConstInt) value2).getVal() == 0)
                curMCBB.appendInst(new MCMove(dst, operand1));
            else
                curMCBB.appendInst(new MCBinary(type, dst, (Register) operand1, operand2));
        }
    }

    /**
     * Translate MUL in IR
     */
    private void translateMul(BinaryOpInst IRinst) {
        Value operand1 = IRinst.getOperandAt(0);
        Value operand2 = IRinst.getOperandAt(1);
        boolean op1IsConst = operand1 instanceof ConstInt;
        boolean op2IsConst = operand2 instanceof ConstInt;
        Register dst = (Register) findContainer(IRinst);

        /* If both is not const, that's fine */
        if (!op1IsConst && !op2IsConst) {
            Register mul1 = (Register) findContainer(operand1, true);
            Register mul2 = (Register) findContainer(operand2, true);

            curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.MUL, dst, mul1, mul2));
        }
        /* If both const, fold */
        else if (op1IsConst && op2IsConst) {
            int result = ((ConstInt) operand1).getVal() * ((ConstInt) operand2).getVal();
            if (canEncodeImm(result)){
                curMCBB.appendInst(new MCMove(dst, new Immediate(result)));
            }
            else {
                curMCBB.appendInst(new MCMove(dst, new Immediate(result), true));
            }
        }
        /* If there is one const */
        else {
            Value v = operand1;
            Value c = operand2;
            if (operand1 instanceof Constant) {
                v = operand2;
                c = operand1;
            }
            Register mul = (Register) findContainer(v);

            int intVal = ((ConstInt) c).getVal();
            int abs = intVal>0 ?intVal :-intVal;

            /* Optimization: 2^n = LSL n, 2^n-1 = RSB LSL n, 2^n+1 = ADD LSL n */
            if (isPowerOfTwo(abs)) {
                MCInstruction.Shift shift = new MCInstruction.Shift(MCInstruction.Shift.TYPE.LSL, log2(abs));
                curMCBB.appendInst(new MCMove(dst, mul, shift, null));
                if (intVal < 0) {
                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.RSB, dst, dst, createConstInt(0)));
                }
            }
            else if (isPowerOfTwo(abs + 1)) {
                MCInstruction.TYPE type = intVal<0 ?MCInstruction.TYPE.SUB : MCInstruction.TYPE.RSB;
                MCInstruction.Shift shift = new MCInstruction.Shift(MCInstruction.Shift.TYPE.LSL, log2(abs+1));
                curMCBB.appendInst(new MCBinary(type, dst, mul, mul, shift, null));
            }
            else if (isPowerOfTwo(abs - 1)) {
                MCInstruction.Shift shift = new MCInstruction.Shift(MCInstruction.Shift.TYPE.LSL, log2(abs));
                curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, dst, mul, mul, shift, null));
                if (intVal < 0) {
                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.RSB, dst, dst, createConstInt(0)));
                }
            }
            else {
                Register mul1 = (Register) findContainer(operand1, true);
                Register mul2 = (Register) findContainer(operand2, true);
                curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.MUL, dst, mul1, mul2));
            }
        }
    }

    /**
     * Translate SDIV <br/>
     * If divisor is constant int, use integer division
     * @see <a href='https://ridiculousfish.com/blog/posts/labor-of-division-episode-i.html'>Labor of Division (Episode I)</a> <br/>
     * <a href='https://gmplib.org/~tege/divcnst-pldi94.pdf'>Division by Invariant Integers using Multiplication</a>
     */
    private void translateSDiv(BinaryOpInst IRinst) {
        Value v1 = IRinst.getOperandAt(0);
        Value v2 = IRinst.getOperandAt(1);

        Register dst = (Register) findContainer(IRinst);
        Register dividend  = (Register) findContainer(v1, true);

        if (v2 instanceof ConstInt) {
            int divisor = ((ConstInt) v2).getVal();
            int abs = divisor>0 ?divisor :-divisor;
            if (isPowerOfTwo(abs)) {
                int x = log2(abs);
                var tmp = curFunc.createVirReg(0);

                MCInstruction.Shift shift1 = new MCInstruction.Shift(MCInstruction.Shift.TYPE.ASR, x-1);
                curMCBB.appendInst(new MCMove(tmp, dividend, shift1, null));

                MCInstruction.Shift shift2 = new MCInstruction.Shift(MCInstruction.Shift.TYPE.LSR, 32-x);
                curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, tmp, dividend, tmp, shift2, null));

                MCInstruction.Shift shift3 = new MCInstruction.Shift(MCInstruction.Shift.TYPE.ASR, x);
                curMCBB.appendInst(new MCMove(dst, tmp, shift3, null));
            }
            else if (abs == 1) {
                curMCBB.appendInst(new MCMove(dst, dividend));
            }
            /* Integer division */
            else {
//                var divisorR = findContainer(v2, true);
//
//                curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.SDIV, dst, dividend, divisorR));

                int l = log2(abs) + 1;
                int sh = l;
                var tmpInt = new BigInteger("1");
                /* (1 << (32+l)) / abs */
                long low = tmpInt.shiftLeft(32+l).divide(BigInteger.valueOf(abs)).longValue();
                /* ((1L << (32+l)) + (1L << (l+1))) / abs */
                long high = tmpInt.shiftLeft(32+l).add(tmpInt.shiftLeft(l+1)).divide(BigInteger.valueOf(abs)).longValue();
                while (((low/2) < (high/2)) && sh > 0) {
                    low = low / 2;
                    high = high / 2;
                    sh--;
                }

                if (high < (1L << 31)) {
                    var tmp1 = curFunc.createVirReg(0);
                    var tmp2 = curFunc.createVirReg(0);
                    curMCBB.appendInst(new MCSmull(dst, tmp1, (Register) createConstInt((int) high, true), dividend));
                    MCInstruction.Shift shift1 = new MCInstruction.Shift(MCInstruction.Shift.TYPE.ASR, sh);
                    curMCBB.appendInst(new MCMove(tmp2, tmp1, shift1, null));
                    MCInstruction.Shift shift2 = new MCInstruction.Shift(MCInstruction.Shift.TYPE.ASR, 31);
                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.SUB, dst, tmp2, dividend, shift2, null));
                }
                else {
                    high = high - (1L << 32);
                    assert high < 0;
                    var tmp = curFunc.createVirReg(0);
                    var tmp1 = curFunc.createVirReg(0);
                    curMCBB.appendInst(new MCFma(MCInstruction.TYPE.SMMLA, tmp, (Register) createConstInt((int) high, true), dividend, dividend));
//                    curMCBB.appendInst(new MCSmull(dst, tmp, (Register) createConstInt((int) high, true), dividend));
//                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, tmp1, tmp, dividend));
                    curMCBB.appendInst(new MCMove(tmp1, tmp, new MCInstruction.Shift(MCInstruction.Shift.TYPE.ASR, sh), null));
                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.SUB, dst, tmp1, dividend, new MCInstruction.Shift(MCInstruction.Shift.TYPE.ASR, 31), null));
                }
            }
            if (divisor < 0)
                curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.RSB, dst, dst, createConstInt(0)));
        }
        else {
            var divisor = findContainer(v2, true);

            curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.SDIV, dst, dividend, divisor));
        }
    }

    private void translateFloatBinary(BinaryOpInst IRinst, MCInstruction.TYPE type) {
        curMCBB.appendInst(new MCFPBinary(
                type,
                (ExtensionRegister) findFloatContainer(IRinst),
                (ExtensionRegister) findFloatContainer(IRinst.getOperandAt(0)),
                (ExtensionRegister) findFloatContainer(IRinst.getOperandAt(1))
        ));
    }

    private void translateFloatNeg(UnaryOpInst IRinst) {
        curMCBB.appendInst(new MCFPneg(
                (ExtensionRegister) findFloatContainer(IRinst),
                (ExtensionRegister) findFloatContainer(IRinst.getOperandAt(0))
        ));
    }

    /**
     * Translate sitofp & fptosi. <br/>
     * sitofp needs to insert VMOV from core register, while fptosi needs to VMOV from extReg back
     */
    private void translateConvert(CastInst IRinst, boolean f2i) {
        var operand = IRinst.getOperandAt(0);
        if (!f2i) {
            curMCBB.appendInst(new MCFPmove((ExtensionRegister) findFloatContainer(operand),(Register) findContainer(operand)));
        }
        curMCBB.appendInst(new MCFPconvert(
                f2i,
                (ExtensionRegister) findFloatContainer(IRinst),
                (ExtensionRegister) findFloatContainer(operand)
        ));
        if (f2i) {
            curMCBB.appendInst(new MCFPmove((Register) findContainer(IRinst),(ExtensionRegister) findFloatContainer(IRinst)));
        }
    }

    /**
     * Translate IR GEP. Calculate the address of an element. <br/>
     * Each index's base address is the last address resolution.
     */
    private void translateGEP(GetElemPtrInst IRinst) {
        // 不能确定的elementType是否是一层指针
        Type elemetType = ((PointerType) IRinst.getOperandAt(0).getType()).getPointeeType();
        /* The number of GEP */
        int operandNum = IRinst.getNumOperands() - 1;
        /* The length of each dimension */
        List<Integer> lengths = null;
        if (elemetType.isArrayType())
            lengths = ((ArrayType) elemetType).getDimLens();

        /* Prepare, dst = baseAddr + totalOffset */
        Register baseAddr = (Register) findContainer(IRinst.getOperandAt(0));
        int totalOffset = 0;
        Register dst = (Register) findContainer(IRinst);

        for (int i=1; i<=operandNum; i++) {
            /* offset size of this level = index * scale */
            MCOperand index = findContainer(IRinst.getOperandAt(i));
            int scale = 4;
            if (lengths != null)
                for (int j=i-1; j<lengths.size(); j++)
                    scale *= lengths.get(j);

            /* If index is an immediate, calculate address until next variable or the last operand */
            if (index.isImmediate()) {
                int offset = scale * ((Immediate) index).getIntValue();
                totalOffset += offset;
                if (i == operandNum) {
                    if (totalOffset == 0)
                        curMCBB.appendInst(new MCMove(dst, baseAddr));
                    else
                        curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, dst, baseAddr, createConstInt(totalOffset)));
                }
            }
            /* If index is a variable */
            else {
                /* If the index before is immediate, calculate here */
                if (totalOffset != 0) {
                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, dst, baseAddr, createConstInt(totalOffset)));
                    totalOffset = 0;
                    baseAddr = dst;
                }

                /* Handle the variable calculation */
                /* If the scale is the integer power of two, use the {@link MCInstruction#shift} */
                if (isPowerOfTwo(scale)) {
                    /* Considering the size of array, 5 bits immediate is enough */
                    var shift = new MCInstruction.Shift(MCInstruction.Shift.TYPE.LSL, log2(scale));
                    curMCBB.appendInst(new MCBinary(MCInstruction.TYPE.ADD, dst, baseAddr, index, shift, null));
                }
                else {
                    /* Create register for scale */
                    VirtualRegister vr = curFunc.createVirReg(scale);
                    curMCBB.appendInst(new MCMove(vr, new Immediate(scale), !canEncodeImm(scale)));
                    curMCBB.appendInst(new MCFma(MCInstruction.TYPE.MLA, dst, ((Register) index), vr, baseAddr));
                }

                baseAddr = dst;
            }
        }
    }

    /**
     * SSA destruction, and then inserted to the end of each predecessor
     */
    private void translatePhi() {
        for (BasicBlock IRBB : curIRFunc) {
            var info = blockInfo.get(IRBB);
            if (info.predecessors.size() == 0) continue;

            var phis = new ArrayList<PhiInst>();
            for (Instruction inst : IRBB)
                if (inst.isPhi())
                    phis.add((PhiInst) inst);
                else
                    break;

            if (phis.isEmpty()) continue;

            for (var pre : info.predecessors) {
                /* Build the map between phi operands */
                var preIRBB = pre.getRawBasicBlock();
                var phiMap = new HashMap<MCOperand, MCOperand>();

                phis.forEach(phi -> {
                    var operand = phi.findValue(preIRBB);
                    if (operand instanceof Constant) {
                        if (operand instanceof ConstFloat)
                            phiMap.put(findFloatContainer(phi), new FPImmediate(((ConstFloat) operand).getVal()));
                        else
                            phiMap.put(findContainer(phi), new Immediate(((ConstInt) operand).getVal()));
                    }
                    else {
                        if (phi.getType().isFloatType())
                            phiMap.put(findFloatContainer(phi), findFloatContainer(operand));
                        else
                            phiMap.put(findContainer(phi), findContainer(operand));
                    }
                });

                /* Generate code */
                /* Store the moves in reversed order for insert */
                var moves = new LinkedList<MCInstruction>();

                while (!phiMap.isEmpty()) {
                    var k = phiMap.keySet().iterator().next();

                    /* Build the use stack */
                    var useStack = new Stack<MCOperand>();
                    var dealing = k;
                    var isInt = dealing.isVirtualReg() || dealing.isImmediate();
                    while (true) {
                        if (!phiMap.containsKey(dealing))
                            break;
                        else if (useStack.contains(dealing))
                            break;
                        else {
                            useStack.push(dealing);
                            dealing = phiMap.get(dealing);
                        }
                    }

                    /* Dealing phi in a loop, in which all the operands are registers, NOT immediate */
                    if (phiMap.containsKey(dealing)) {
                        MCOperand tmp = isInt ?curFunc.createVirReg(null) :curFunc.createExtVirReg(null);
                        MCOperand dst = tmp;
                        MCOperand src;
                        while (useStack.contains(dealing)) {
                            src = useStack.pop();
                            var  mov = isInt ?new MCMove((Register) dst, src) :new MCFPmove(dst, src);
                            moves.addLast(mov);
                            dst = src;
                            phiMap.remove(src);
                        }
                        var mov = isInt ?new MCMove((Register) dealing, tmp) :new MCFPmove(dealing, tmp);
                        if (PrintInfo.printIR && dealing.isVirtualReg())
                            mov.val = ((VirtualRegister) dealing).getValue();
                        else if (PrintInfo.printIR && dealing.isVirtualExtReg())
                            mov.val = ((VirtualExtRegister) dealing).getValue();
                        moves.addLast(mov);
                    }
                    /* Dealing nested phi, the ONLY immediate can be here is dealing itself */
                    MCOperand dst;
                    MCOperand src = dealing;
                    while (!useStack.isEmpty()) {
                        dst = useStack.pop();
                        MCInstruction  mov;
                        if (src.isImmediate()) {
                            mov = new MCMove((Register) dst, src, !canEncodeImm(((Immediate) src).getIntValue()));
                        }
                        else if (src.isFPImm()) {
                            if (canEncodeFloat(((FPImmediate) src).getFloatValue())) {
                                mov = new MCFPmove((ExtensionRegister) dst,(FPImmediate) src);
                            }
                            else {
                                var tmpVr = curFunc.createVirReg(null);
                                moves.addFirst(new MCFPmove((ExtensionRegister) dst, tmpVr));
                                mov = new MCMove(tmpVr, src, true);
                            }
                        }
                        else
                            mov = isInt ?new MCMove((Register) dst, src) :new MCFPmove(dst, src);
                        if (PrintInfo.printIR && dst.isVirtualReg())
                            mov.val = ((VirtualRegister) dst).getValue();
                        else if (PrintInfo.printIR && dst.isVirtualExtReg())
                            mov.val = ((VirtualExtRegister) dst).getValue();
                        moves.addFirst(mov);
                        src = dst;
                        phiMap.remove(dst);
                    }
                }

                /* Insert */
                var preList = curFunc.findMCBB(preIRBB).getInstructionList();
                var inst = preList.getLast();
                if (preList.size() > 1) {
                    var secondLast = preList.get(preList.size()-2);
                    if (secondLast instanceof MCbranch && ((MCbranch) secondLast).isBranch())
                        inst = secondLast;
                }
                moves.forEach(inst::insertBefore);
            }
        }
    }
    //</editor-fold>

}