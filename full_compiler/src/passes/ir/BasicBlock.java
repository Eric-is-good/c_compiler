package passes.ir;

public class BasicBlock {
    protected final ir.values.BasicBlock rawBasicBlock;

    public BasicBlock(ir.values.BasicBlock rawBasicBlock){
        this.rawBasicBlock = rawBasicBlock;
    }

    public ir.values.BasicBlock getRawBasicBlock(){
        return rawBasicBlock;
    }

}
