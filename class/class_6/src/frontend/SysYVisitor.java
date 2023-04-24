// Generated from K:/Cbias/third-party/ANTLR\SysY.g4 by ANTLR 4.9.2
package frontend;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SysYParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SysYVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SysYParser#compUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompUnit(SysYParser.CompUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(SysYParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#constDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDecl(SysYParser.ConstDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#bType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBType(SysYParser.BTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code scalarConstDef}
	 * labeled alternative in {@link SysYParser#constDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalarConstDef(SysYParser.ScalarConstDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrConstDef}
	 * labeled alternative in {@link SysYParser#constDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrConstDef(SysYParser.ArrConstDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code scalarConstInitVal}
	 * labeled alternative in {@link SysYParser#constInitVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalarConstInitVal(SysYParser.ScalarConstInitValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrConstInitVal}
	 * labeled alternative in {@link SysYParser#constInitVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrConstInitVal(SysYParser.ArrConstInitValContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(SysYParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code scalarVarDef}
	 * labeled alternative in {@link SysYParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalarVarDef(SysYParser.ScalarVarDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrVarDef}
	 * labeled alternative in {@link SysYParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrVarDef(SysYParser.ArrVarDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code scalarInitVal}
	 * labeled alternative in {@link SysYParser#initVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalarInitVal(SysYParser.ScalarInitValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrInitval}
	 * labeled alternative in {@link SysYParser#initVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrInitval(SysYParser.ArrInitvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDef(SysYParser.FuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncType(SysYParser.FuncTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcFParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncFParams(SysYParser.FuncFParamsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code scalarFuncFParam}
	 * labeled alternative in {@link SysYParser#funcFParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalarFuncFParam(SysYParser.ScalarFuncFParamContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrFuncFParam}
	 * labeled alternative in {@link SysYParser#funcFParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrFuncFParam(SysYParser.ArrFuncFParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(SysYParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#blockItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockItem(SysYParser.BlockItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(SysYParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(SysYParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blkStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlkStmt(SysYParser.BlkStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code condStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondStmt(SysYParser.CondStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(SysYParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(SysYParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code contStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContStmt(SysYParser.ContStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code retStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRetStmt(SysYParser.RetStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(SysYParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond(SysYParser.CondContext ctx);
	/**
	 * Visit a parse tree produced by the {@code scalarLVal}
	 * labeled alternative in {@link SysYParser#lVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalarLVal(SysYParser.ScalarLValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrLVal}
	 * labeled alternative in {@link SysYParser#lVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrLVal(SysYParser.ArrLValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code primExpr1}
	 * labeled alternative in {@link SysYParser#primaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimExpr1(SysYParser.PrimExpr1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code primExpr2}
	 * labeled alternative in {@link SysYParser#primaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimExpr2(SysYParser.PrimExpr2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code primExpr3}
	 * labeled alternative in {@link SysYParser#primaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimExpr3(SysYParser.PrimExpr3Context ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(SysYParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#intConst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntConst(SysYParser.IntConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#floatConst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatConst(SysYParser.FloatConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code primUnaryExp}
	 * labeled alternative in {@link SysYParser#unaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimUnaryExp(SysYParser.PrimUnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fcallUnaryExp}
	 * labeled alternative in {@link SysYParser#unaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFcallUnaryExp(SysYParser.FcallUnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code oprUnaryExp}
	 * labeled alternative in {@link SysYParser#unaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOprUnaryExp(SysYParser.OprUnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#unaryOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOp(SysYParser.UnaryOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcRParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncRParams(SysYParser.FuncRParamsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprRParam}
	 * labeled alternative in {@link SysYParser#funcRParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprRParam(SysYParser.ExprRParamContext ctx);
	/**
	 * Visit a parse tree produced by the {@code strRParam}
	 * labeled alternative in {@link SysYParser#funcRParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrRParam(SysYParser.StrRParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#mulExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExp(SysYParser.MulExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#addExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExp(SysYParser.AddExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#relExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExp(SysYParser.RelExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#eqExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExp(SysYParser.EqExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#lAndExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLAndExp(SysYParser.LAndExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#lOrExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLOrExp(SysYParser.LOrExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#constExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstExp(SysYParser.ConstExpContext ctx);
}