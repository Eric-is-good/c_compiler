package passes.ir;

public class Instruction {

    protected final ir.values.Instruction rawInstruction;

    public Instruction(ir.values.Instruction instruction){
        this.rawInstruction = instruction;
    }

    public ir.values.Instruction getRawInstruction() {
        return rawInstruction;
    }

}
