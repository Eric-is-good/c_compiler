package passes.ir.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/// Variable name in this class is strange since it is translated from a code written in C++.
/// https://www.luogu.com.cn/record/42013604
public class DomAnalysis<BasicBlock extends IDomAnalysis<BasicBlock>> {

    private DomAnalysis() {
    }

    class NodeAttachment {
        public Integer dfn;
        public BasicBlock sdom, idom;
        public ArrayList<BasicBlock> processList = new ArrayList<>();
    }

    private int currentDfn = 0;

    private BasicBlock functionEntry;

    private final Map<Integer, BasicBlock> dfn2bbMap = new HashMap<>();

    private final Map<BasicBlock, NodeAttachment> attachmentMap = new HashMap<>();

    private void dfsForDfn(BasicBlock basicBlock){
        currentDfn += 1;
        attachmentMap.get(basicBlock).dfn = currentDfn;
        dfn2bbMap.put(currentDfn, basicBlock);
        attachmentMap.get(basicBlock).sdom = basicBlock;
        for (BasicBlock exitBlock : basicBlock.getExitBlocks()) {
            if(attachmentMap.get(exitBlock).dfn!=null) continue;
            dfsForDfn(exitBlock);
        }
    }

    private void generateDomTree(){
        var B = new WeightedUnion(attachmentMap);
        for(int i=currentDfn;i>=2;i--){
            BasicBlock nw = dfn2bbMap.get(i);
            for (BasicBlock x : attachmentMap.get(nw).processList) {
                B.getRoot(x);
                if(attachmentMap.get(x).sdom!=attachmentMap.get(B.getAttachment(x).value).sdom){
                    attachmentMap.get(x).idom = B.getAttachment(x).value;
                }else{
                    attachmentMap.get(x).idom = attachmentMap.get(x).sdom;
                }
            }
            for (BasicBlock toj : nw.getEntryBlocks()) {
                if(attachmentMap.get(toj).dfn<attachmentMap.get(nw).dfn){
                    if(attachmentMap.get(toj).dfn<attachmentMap.get(attachmentMap.get(nw).sdom).dfn){
                        attachmentMap.get(nw).sdom = toj;
                    }
                }else{
                    B.getRoot(toj);
                    if(attachmentMap.get(attachmentMap.get(B.getAttachment(toj).value).sdom).dfn<
                            attachmentMap.get(attachmentMap.get(nw).sdom).dfn){
                        attachmentMap.get(nw).sdom = attachmentMap.get(B.getAttachment(toj).value).sdom;
                    }
                }
            }
            B.getAttachment(nw).value = nw;
            attachmentMap.get(attachmentMap.get(nw).sdom).processList.add(nw);
            for (BasicBlock toj : nw.getExitBlocks()) {
                if(attachmentMap.get(toj).dfn>attachmentMap.get(nw).dfn){
                    B.union(nw,toj);
                }
            }
        }
        for (BasicBlock unnamed : attachmentMap.get(functionEntry).processList) {
            attachmentMap.get(unnamed).idom = functionEntry;
        }
        for(int i=2;i<=currentDfn;i++){
            BasicBlock nw = dfn2bbMap.get(i);
            if(attachmentMap.get(nw).sdom!=attachmentMap.get(nw).idom){
                attachmentMap.get(nw).idom = attachmentMap.get(attachmentMap.get(nw).idom).idom;
            }
            nw.setDomFather(attachmentMap.get(nw).idom);
            attachmentMap.get(nw).idom.addDomSon(nw);
        }
    }

    private void __analysis__(Map<ir.values.BasicBlock, BasicBlock> basicBlockMap) {
        // Get function entry
        for (BasicBlock wrappedBB : basicBlockMap.values()) {
            if (wrappedBB.getEntryBlocks().size() == 0) {
                functionEntry = wrappedBB;
            }
            attachmentMap.put(wrappedBB, new NodeAttachment());
        }
        assert functionEntry != null;
        // Calculate dom tree
        dfsForDfn(functionEntry);
        generateDomTree();
    }

    public static <BasicBlock extends IDomAnalysis<BasicBlock>>
    void analysis(Map<ir.values.BasicBlock, BasicBlock> basicBlockMap) {
        RelationAnalysis.analysisBasicBlocks(basicBlockMap);
        (new DomAnalysis<BasicBlock>()).__analysis__(basicBlockMap);
    }

    class WeightedUnion {
        class NodeAttachment {
            public DomAnalysis<BasicBlock>.NodeAttachment domAttachment;
            public BasicBlock unionFather, value;

            public NodeAttachment(BasicBlock basicBlock, DomAnalysis<BasicBlock>.NodeAttachment domAttachment) {
                this.domAttachment = domAttachment;
                this.unionFather = basicBlock;
                this.value = basicBlock;
            }

        }

        private final Map<BasicBlock, NodeAttachment> attachmentMap = new HashMap<>();

        public NodeAttachment getAttachment(BasicBlock basicBlock){
            return attachmentMap.get(basicBlock);
        }

        public WeightedUnion(Map<BasicBlock, DomAnalysis<BasicBlock>.NodeAttachment> domAttachmentMap) {
            domAttachmentMap.forEach((basicBlock, nodeAttachment) -> {
                attachmentMap.put(basicBlock, new NodeAttachment(basicBlock, nodeAttachment));
            });
        }

        public BasicBlock getRoot(BasicBlock basicBlock) {
            var nodeAttachment = attachmentMap.get(basicBlock);
            if (nodeAttachment.unionFather != basicBlock) {
                BasicBlock temp = nodeAttachment.unionFather;
                nodeAttachment.unionFather = getRoot(nodeAttachment.unionFather);
                if (attachmentMap.get(attachmentMap.get(attachmentMap.get(temp      ).value).domAttachment.sdom).domAttachment.dfn <
                    attachmentMap.get(attachmentMap.get(attachmentMap.get(basicBlock).value).domAttachment.sdom).domAttachment.dfn) {
                    nodeAttachment.value = attachmentMap.get(temp).value;
                }
            }
            return nodeAttachment.unionFather;
        }

        public void union(BasicBlock u, BasicBlock v){
            getRoot(v);
            attachmentMap.get(v).unionFather = u;
        }

    }
}
