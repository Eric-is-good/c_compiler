package ir.values.instructions;

import ir.Type;
import ir.Use;
import ir.Value;
import ir.values.BasicBlock;
import ir.values.Instruction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The ‘phi’ instruction is used to implement the φ node in the SSA graph representing the function.
 * @see <a href="https://llvm.org/docs/LangRef.html#phi-instruction">
 *     LLVM IR LangRef: phi instruction</a>
 */
public class PhiInst extends Instruction {

    public PhiInst(Type type){
        super(type, InstCategory.PHI);
    }

    int nextMappingId = 0;
    Map<BasicBlock, Integer> operandMapping = new HashMap<>();

    public boolean hasEntry(){
        return getNumOperands()!=0;
    }

    public Value findValue(BasicBlock basicBlock) {
        return getOperandAt(operandMapping.get(basicBlock));
    }

    public void addMapping(BasicBlock basicBlock, Value value){
        if(operandMapping.containsKey(basicBlock)){
            var currentValue = getOperandAt(operandMapping.get(basicBlock));
            if(!Objects.equals(currentValue,value)){
                throw new RuntimeException("Trying to assign a different value to an entry block already exists in PHI.");
            }
            return;
        }
        this.addOperandAt(nextMappingId, value);
        this.addOperandAt(nextMappingId+1, basicBlock);
        operandMapping.put(basicBlock, nextMappingId);
        nextMappingId += 2;
    }

    public Set<BasicBlock> getEntries() {
        return operandMapping.keySet();
    }

    public void removeMapping(BasicBlock basicBlock){
        if(!operandMapping.containsKey(basicBlock)){
            throw new RuntimeException("Trying to remove a mapping that does not exists");
        }
        int id = operandMapping.get(basicBlock);
        this.removeOperandAt(id);
        this.removeOperandAt(id+1);
        operandMapping.remove(basicBlock);
    }

    public void setPhiMapping(Map<BasicBlock, Value> phiMapping){
        operandMapping.keySet().forEach(this::removeMapping);
        phiMapping.forEach(this::addMapping);
    }

    public boolean canDerive(){
        Value stdValue = null;
        for (Integer id : operandMapping.values()) {
            var value = getOperandAt(id);
            if(value==this) continue;
            if(stdValue==null){
                stdValue = value;
            }else{
                if(!Objects.equals(stdValue,value)) return false;
            }
        }
        if(stdValue==null) return false;
        return true;
    }

    public Value deriveConstant(){
        Value stdValue = null;
        for (Integer id : operandMapping.values()) {
            var value = getOperandAt(id);
            if(value==this) continue;
            if(stdValue==null){
                stdValue = value;
            }else{
                if(!Objects.equals(stdValue,value)){
                    System.out.println(stdValue);
                    System.out.println(value);
                    throw new RuntimeException(String.format("Value of PHI cannot be determined! Conflict values are: (1) %s (2) %s.",stdValue,value));
                }
            }
        }
        return stdValue;
    }

    @Override
    public void markWasted() {
        super.markWasted();
        operandMapping.clear();
    }

    @Override
    public void clearOperands() {
        super.clearOperands();
        operandMapping.clear();
    }

    @Override
    public void addOperandAt(int pos, Value val) {
        if(val instanceof BasicBlock){
            var use = new Use(val, this, pos){
                @Override
                public void setUsee(Value newVal) {
                    var oldBlock = (BasicBlock) this.getUsee();
                    var newBlock = (BasicBlock) newVal;
                    Integer pos = operandMapping.get(oldBlock);
                    operandMapping.remove(oldBlock);
                    super.setUsee(newBlock);
                    operandMapping.put(newBlock, pos);
                }
            };
            super.addUseAt(pos, use);
        }else{
            super.addOperandAt(pos,val);
        }
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(this.getName());
        builder.append(" = ");
        builder.append("phi ");
        builder.append(this.getType().toString());

        boolean isFirstBranch = true;
        for (Integer id : operandMapping.values()) {
            builder.append(isFirstBranch ? " " : ", ");
            builder.append(String.format("[%s, %s]",
                    getOperandAt(id).getName(),
                    "%"+getOperandAt(id+1).getName()
                    ));
            isFirstBranch = false;
        }

        return builder.toString();
    }
}
