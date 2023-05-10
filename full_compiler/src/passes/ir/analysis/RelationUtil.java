package passes.ir.analysis;

import ir.Value;
import ir.values.BasicBlock;
import ir.values.Instruction;
import ir.values.instructions.PhiInst;
import ir.values.instructions.TerminatorInst;

import java.util.ArrayList;
import java.util.Collection;

public class RelationUtil {
    public static Collection<BasicBlock> getFollowingBB(BasicBlock basicBlock){
        var lastInst = basicBlock.getLastInst();
        var ret = new ArrayList<BasicBlock>();
        if(!(lastInst instanceof TerminatorInst.Br)) return ret;
        var br = (TerminatorInst.Br) lastInst;
        if(br.isCondJmp()){
            ret.add((BasicBlock) br.getOperandAt(1));
            ret.add((BasicBlock) br.getOperandAt(2));
        }else{
            ret.add((BasicBlock) br.getOperandAt(0));
        }
        return ret;
    }

    public static void removeEntry(BasicBlock basicBlock, BasicBlock entry) {
        for (Instruction instruction : basicBlock) {
            if (instruction instanceof PhiInst) {
                var phiInst = (PhiInst) instruction;
                phiInst.removeMapping(entry);
            } else {
                break; // PHI must be in the front of a bb
            }
        }
    }

    public static void replaceEntry(BasicBlock basicBlock, BasicBlock oldBlock, BasicBlock newBlock){
        for (Instruction instruction : basicBlock) {
            if (instruction instanceof PhiInst) {
                var phiInst = (PhiInst) instruction;
                Value value = phiInst.findValue(oldBlock);
                phiInst.removeMapping(oldBlock);
                phiInst.addMapping(newBlock, value);
            } else {
                break; // PHI must be in the front of a bb
            }
        }
    }

    public static void freeBlock(BasicBlock basicBlock){
        for (Instruction instruction : basicBlock.getInstructions()) {
            instruction.clearOperands();
        }
    }
}
