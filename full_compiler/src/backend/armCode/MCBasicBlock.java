package backend.armCode;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The MCBasicBlock is a sequential  ARM instruction.
 *
 */
public class MCBasicBlock implements Iterable<MCInstruction> {

    private static int count = 0;

    //<editor-fold desc="Fields">
    private final LinkedList<MCInstruction> instructionList;
    private String label;

    private MCFunction belongFunc;

    private ArrayList<MCBasicBlock> predecessors;
    private MCBasicBlock falseSuccessor;
    private MCBasicBlock trueSuccessor;
    //</editor-fold>


    //<editor-fold desc="Useful methods">
    //<editor-fold desc="Instructions">
    public void prependInst(MCInstruction inst) {
        instructionList.addFirst(inst);
        inst.setBelongBB(this);
        inst.setBelongFunc(belongFunc);
    }

    public void appendInst(MCInstruction inst) {
        instructionList.addLast(inst);
        inst.setBelongBB(this);
        inst.setBelongFunc(belongFunc);
    }

    public void insertAt(int index, MCInstruction inst) {
        instructionList.add(index, inst);
        inst.setBelongBB(this);
        inst.setBelongFunc(belongFunc);
    }

    public void removeInst(MCInstruction inst) {instructionList.remove(inst);}
    public void removeLast() {instructionList.removeLast();}
    public void removeSecondLast() {instructionList.remove(instructionList.size()-2);}
    public void removeAt(int index) {instructionList.remove(index);}

    public int getIndex(MCInstruction inst) {return instructionList.indexOf(inst);}

    public MCInstruction getFirstInst() {return instructionList.getFirst();}
    public MCInstruction getLastInst() {return instructionList.getLast();}
    public MCInstruction getSecondLastInst() {return instructionList.get(instructionList.size()-2);}

    public Iterator<MCInstruction> iterator() {return instructionList.iterator();}
    //</editor-fold>

    public void addPredecessor(MCBasicBlock BB) {predecessors.add(BB);}

    public void removeSelf() {belongFunc.removeBB(this);}

    public void appendAndRemove(MCBasicBlock bb) {
        instructionList.addAll(bb.getInstructionList());
        for (var inst : bb)
            inst.setBelongBB(this);
        bb.removeSelf();
    }
    //</editor-fold>

    public String emit() {
        return "." + label;
    }

    //<editor-fold desc="Getter & Setter">
    public LinkedList<MCInstruction> getInstructionList() {return instructionList;}

    public String getName() {return label;}
    public void setName(String label) {this.label = label;}

    public MCFunction getBelongFunc() {return belongFunc;}
    public void setBelongFunc(MCFunction belongFunc) {this.belongFunc = belongFunc;}

    public ArrayList<MCBasicBlock> getPredecessors() {return predecessors;}

    public MCBasicBlock getFalseSuccessor() {return falseSuccessor;}
    public void setFalseSuccessor(MCBasicBlock falseSuccessor) {this.falseSuccessor = falseSuccessor;}

    public MCBasicBlock getTrueSuccessor() {return trueSuccessor;}
    public void setTrueSuccessor(MCBasicBlock trueSuccessor) {this.trueSuccessor = trueSuccessor;}
    //</editor-fold>


    //<editor-fold desc="Constructor">
    public MCBasicBlock(MCFunction belongingFunction) {
        this.belongFunc = belongingFunction;
        instructionList = new LinkedList<>();
        predecessors = new ArrayList<>();
        label = "BLOCK_" + count;
        count++;
    }
    public MCBasicBlock(MCFunction belongingFunction, String label) {
        this.belongFunc = belongingFunction;
        instructionList = new LinkedList<>();
        predecessors = new ArrayList<>();
        this.label = label;
    }
    //</editor-fold>

}
