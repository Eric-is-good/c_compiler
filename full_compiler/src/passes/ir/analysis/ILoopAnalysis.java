package passes.ir.analysis;

public interface ILoopAnalysis<BasicBlock extends ILoopAnalysis<BasicBlock>> extends IDomAnalysis<BasicBlock> {

    void setLoop(LoopAnalysis<BasicBlock>.Loop loop);

    LoopAnalysis<BasicBlock>.Loop getLoop();

}
