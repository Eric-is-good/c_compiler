// Generated from miniC.g4 by ANTLR 4.9
package paser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link miniCParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface miniCVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link miniCParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(miniCParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#declaration_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration_list(miniCParser.Declaration_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(miniCParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#var_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_declaration(miniCParser.Var_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#var_decl_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_decl_list(miniCParser.Var_decl_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#var_decl_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_decl_id(miniCParser.Var_decl_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#type_specifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_specifier(miniCParser.Type_specifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#fun_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_declaration(miniCParser.Fun_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(miniCParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#param_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list(miniCParser.Param_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#param_type_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_type_list(miniCParser.Param_type_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#param_id_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_id_list(miniCParser.Param_id_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#param_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_id(miniCParser.Param_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompound_stmt(miniCParser.Compound_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(miniCParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#expression_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_stmt(miniCParser.Expression_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#selection_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelection_stmt(miniCParser.Selection_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#iteration_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIteration_stmt(miniCParser.Iteration_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#return_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_stmt(miniCParser.Return_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#break_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreak_stmt(miniCParser.Break_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(miniCParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(miniCParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#simple_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_expression(miniCParser.Simple_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#or_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_expression(miniCParser.Or_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#unary_rel_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_rel_expression(miniCParser.Unary_rel_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#rel_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRel_expression(miniCParser.Rel_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#relop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelop(miniCParser.RelopContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#add_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd_expression(miniCParser.Add_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#addop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddop(miniCParser.AddopContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(miniCParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#mulop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulop(miniCParser.MulopContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#unary_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_expression(miniCParser.Unary_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(miniCParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(miniCParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCall(miniCParser.CallContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(miniCParser.ArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link miniCParser#arg_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_list(miniCParser.Arg_listContext ctx);
}