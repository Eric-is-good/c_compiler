package frontend;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Visitor extends SysYBaseVisitor<Integer>{

    public ArrayList<Integer> tree_nodes = new ArrayList<>();   // hashcode 树的拓扑排序
    public HashMap<Integer,ParseTree> full_tree = new HashMap<>();   // 因为 tree_nodes 没有叶子节点
    public ArrayList<Integer> leaf = new ArrayList<>();         // 叶子节点
    public Queue<ParseTree> tree_nodes_q = new LinkedList<>();


    public HashMap<Integer,Node> hash_tree = new HashMap<>();   // hash 树
    public ArrayList<TableElement> Table = new ArrayList<>();   // 符号表

    public Integer GetNodes(ParseTree ctx){
        if(ctx.getChildCount()==0) return 0;
        tree_nodes_q.offer(ctx.getChild(0).getParent());
        while(!tree_nodes_q.isEmpty()){
            ParseTree tree_node = tree_nodes_q.poll();
            if(tree_node.getChildCount() != 0){
                for(int i=0;i<tree_node.getChildCount();i++){
                    tree_nodes_q.offer(tree_node.getChild(i));
                }
            }
            tree_nodes.add(tree_node.hashCode());
            full_tree.put(tree_node.hashCode(), tree_node);
        }
        return 0;
    }

    public Integer GetLeaf(){
        for (int i:tree_nodes){
            if(! hash_tree.containsKey(i)){
                leaf.add(i);
            }
        }
        return 0;
    }

    @Override
    public Integer visitCompUnit(SysYParser.CompUnitContext ctx) {
        GetNodes(ctx);
        GetLeaf();

        hash_tree.put(ctx.hashCode(), new Node(ctx,-1,"CompUnit"));
        return super.visitCompUnit(ctx);
    }

    @Override
    public Integer visitDecl(SysYParser.DeclContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"Decl"));
        return super.visitDecl(ctx);
    }

    @Override
    public Integer visitConstDecl(SysYParser.ConstDeclContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ConstDecl"));
        return super.visitConstDecl(ctx);
    }

    @Override
    public Integer visitBType(SysYParser.BTypeContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"BType"));
        return super.visitBType(ctx);
    }

    @Override
    public Integer visitScalarConstDef(SysYParser.ScalarConstDefContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ScalarConstDef"));
        return super.visitScalarConstDef(ctx);
    }

    @Override
    public Integer visitArrConstDef(SysYParser.ArrConstDefContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ArrConstDef"));
        return super.visitArrConstDef(ctx);
    }

    @Override
    public Integer visitScalarConstInitVal(SysYParser.ScalarConstInitValContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ScalarConstInitVal"));
        return super.visitScalarConstInitVal(ctx);
    }

    @Override
    public Integer visitArrConstInitVal(SysYParser.ArrConstInitValContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ArrConstInitVal"));
        return super.visitArrConstInitVal(ctx);
    }

    @Override
    public Integer visitVarDecl(SysYParser.VarDeclContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"VarDecl"));
        return super.visitVarDecl(ctx);
    }

    @Override
    public Integer visitScalarVarDef(SysYParser.ScalarVarDefContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ScalarVarDef"));
        return super.visitScalarVarDef(ctx);
    }

    @Override
    public Integer visitArrVarDef(SysYParser.ArrVarDefContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ArrVarDef"));
        return super.visitArrVarDef(ctx);
    }

    @Override
    public Integer visitScalarInitVal(SysYParser.ScalarInitValContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ScalarInitVal"));
        return super.visitScalarInitVal(ctx);
    }

    @Override
    public Integer visitArrInitval(SysYParser.ArrInitvalContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ArrInitval"));
        return super.visitArrInitval(ctx);
    }

    @Override
    public Integer visitFuncDef(SysYParser.FuncDefContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"FuncDef"));
        return super.visitFuncDef(ctx);
    }

    @Override
    public Integer visitFuncType(SysYParser.FuncTypeContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"FuncType"));
        return super.visitFuncType(ctx);
    }

    @Override
    public Integer visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"FuncFParams"));
        return super.visitFuncFParams(ctx);
    }

    @Override
    public Integer visitScalarFuncFParam(SysYParser.ScalarFuncFParamContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ScalarFuncFParam"));
        return super.visitScalarFuncFParam(ctx);
    }

    @Override
    public Integer visitArrFuncFParam(SysYParser.ArrFuncFParamContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ArrFuncFParam"));
        return super.visitArrFuncFParam(ctx);
    }

    @Override
    public Integer visitBlock(SysYParser.BlockContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"Block"));
        return super.visitBlock(ctx);
    }

    @Override
    public Integer visitBlockItem(SysYParser.BlockItemContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"BlockItem"));
        return super.visitBlockItem(ctx);
    }

    @Override
    public Integer visitAssignStmt(SysYParser.AssignStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"AssignStmt"));
        return super.visitAssignStmt(ctx);
    }

    @Override
    public Integer visitExprStmt(SysYParser.ExprStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ExprStmt"));
        return super.visitExprStmt(ctx);
    }

    @Override
    public Integer visitBlkStmt(SysYParser.BlkStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"BlkStmt"));
        return super.visitBlkStmt(ctx);
    }

    @Override
    public Integer visitCondStmt(SysYParser.CondStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"CondStmt"));
        return super.visitCondStmt(ctx);
    }

    @Override
    public Integer visitWhileStmt(SysYParser.WhileStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"WhileStmt"));
        return super.visitWhileStmt(ctx);
    }

    @Override
    public Integer visitBreakStmt(SysYParser.BreakStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"BreakStmt"));
        return super.visitBreakStmt(ctx);
    }

    @Override
    public Integer visitContStmt(SysYParser.ContStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ContStmt"));
        return super.visitContStmt(ctx);
    }

    @Override
    public Integer visitRetStmt(SysYParser.RetStmtContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"RetStmt"));
        return super.visitRetStmt(ctx);
    }

    @Override
    public Integer visitExpr(SysYParser.ExprContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"Expr"));
        return super.visitExpr(ctx);
    }

    @Override
    public Integer visitCond(SysYParser.CondContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"Cond"));
        return super.visitCond(ctx);
    }

    @Override
    public Integer visitScalarLVal(SysYParser.ScalarLValContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ScalarLVal"));
        return super.visitScalarLVal(ctx);
    }

    @Override
    public Integer visitArrLVal(SysYParser.ArrLValContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ArrLVal"));
        return super.visitArrLVal(ctx);
    }

    @Override
    public Integer visitPrimExpr1(SysYParser.PrimExpr1Context ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"PrimExpr1"));
        return super.visitPrimExpr1(ctx);
    }

    @Override
    public Integer visitPrimExpr2(SysYParser.PrimExpr2Context ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"PrimExpr2"));
        return super.visitPrimExpr2(ctx);
    }

    @Override
    public Integer visitPrimExpr3(SysYParser.PrimExpr3Context ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"PrimExpr3"));
        return super.visitPrimExpr3(ctx);
    }

    @Override
    public Integer visitNumber(SysYParser.NumberContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"Number"));
        return super.visitNumber(ctx);
    }

    @Override
    public Integer visitIntConst(SysYParser.IntConstContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"IntConst"));
        return super.visitIntConst(ctx);
    }

    @Override
    public Integer visitFloatConst(SysYParser.FloatConstContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"FloatConst"));
        return super.visitFloatConst(ctx);
    }

    @Override
    public Integer visitPrimUnaryExp(SysYParser.PrimUnaryExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"PrimUnaryExp"));
        return super.visitPrimUnaryExp(ctx);
    }

    @Override
    public Integer visitFcallUnaryExp(SysYParser.FcallUnaryExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"FcallUnaryExp"));
        return super.visitFcallUnaryExp(ctx);
    }

    @Override
    public Integer visitOprUnaryExp(SysYParser.OprUnaryExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"OprUnaryExp"));
        return super.visitOprUnaryExp(ctx);
    }

    @Override
    public Integer visitUnaryOp(SysYParser.UnaryOpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"UnaryOp"));
        return super.visitUnaryOp(ctx);
    }

    @Override
    public Integer visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"FuncRParams"));
        return super.visitFuncRParams(ctx);
    }

    @Override
    public Integer visitExprRParam(SysYParser.ExprRParamContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ExprRParam"));
        return super.visitExprRParam(ctx);
    }

    @Override
    public Integer visitStrRParam(SysYParser.StrRParamContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"StrRParam"));
        return super.visitStrRParam(ctx);
    }

    @Override
    public Integer visitMulExp(SysYParser.MulExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"MulExp"));
        return super.visitMulExp(ctx);
    }

    @Override
    public Integer visitAddExp(SysYParser.AddExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"AddExp"));
        return super.visitAddExp(ctx);
    }

    @Override
    public Integer visitRelExp(SysYParser.RelExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"RelExp"));
        return super.visitRelExp(ctx);
    }

    @Override
    public Integer visitEqExp(SysYParser.EqExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"EqExp"));
        return super.visitEqExp(ctx);
    }

    @Override
    public Integer visitLAndExp(SysYParser.LAndExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"LAndExp"));
        return super.visitLAndExp(ctx);
    }

    @Override
    public Integer visitLOrExp(SysYParser.LOrExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"LOrExp"));
        return super.visitLOrExp(ctx);
    }

    @Override
    public Integer visitConstExp(SysYParser.ConstExpContext ctx) {
        hash_tree.put(ctx.hashCode(), new Node(ctx,ctx.getParent().hashCode(),"ConstExp"));
        return super.visitConstExp(ctx);
    }
}
