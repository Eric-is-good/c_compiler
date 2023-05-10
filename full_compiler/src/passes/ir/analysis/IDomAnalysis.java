package passes.ir.analysis;

import java.util.Set;

public interface IDomAnalysis<BasicBlock extends IDomAnalysis<BasicBlock>> extends IRelationAnalysis<BasicBlock> {
    void setDomFather(BasicBlock father);
    void addDomSon(BasicBlock son);

    BasicBlock getDomFather();

    Set<BasicBlock> getDomSons();
    void setDomDepth(int depth);

    int getDomDepth();

}
