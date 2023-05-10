package passes.ir.analysis;

import java.util.List;
import java.util.Set;

public interface IRelationAnalysis<BasicBlock extends IRelationAnalysis<BasicBlock>> {
    ir.values.BasicBlock getRawBasicBlock();
    void addEntryBlock(BasicBlock entryBlock);

    void setExitBlocks(List<BasicBlock> exitBlocks);

    default Set<BasicBlock> getEntryBlocks(){
        throw new UnsupportedOperationException();
    }

    default Set<BasicBlock> getExitBlocks(){
        throw new UnsupportedOperationException();
    }

}
