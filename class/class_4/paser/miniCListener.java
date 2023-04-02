// Generated from miniC.g4 by ANTLR 4.9
package paser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link miniCParser}.
 */
public interface miniCListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link miniCParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(miniCParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(miniCParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#declaration_list}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration_list(miniCParser.Declaration_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#declaration_list}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration_list(miniCParser.Declaration_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(miniCParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(miniCParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#var_declaration}.
	 * @param ctx the parse tree
	 */
	void enterVar_declaration(miniCParser.Var_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#var_declaration}.
	 * @param ctx the parse tree
	 */
	void exitVar_declaration(miniCParser.Var_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#var_decl_list}.
	 * @param ctx the parse tree
	 */
	void enterVar_decl_list(miniCParser.Var_decl_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#var_decl_list}.
	 * @param ctx the parse tree
	 */
	void exitVar_decl_list(miniCParser.Var_decl_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#var_decl_id}.
	 * @param ctx the parse tree
	 */
	void enterVar_decl_id(miniCParser.Var_decl_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#var_decl_id}.
	 * @param ctx the parse tree
	 */
	void exitVar_decl_id(miniCParser.Var_decl_idContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#type_specifier}.
	 * @param ctx the parse tree
	 */
	void enterType_specifier(miniCParser.Type_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#type_specifier}.
	 * @param ctx the parse tree
	 */
	void exitType_specifier(miniCParser.Type_specifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#fun_declaration}.
	 * @param ctx the parse tree
	 */
	void enterFun_declaration(miniCParser.Fun_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#fun_declaration}.
	 * @param ctx the parse tree
	 */
	void exitFun_declaration(miniCParser.Fun_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#params}.
	 * @param ctx the parse tree
	 */
	void enterParams(miniCParser.ParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#params}.
	 * @param ctx the parse tree
	 */
	void exitParams(miniCParser.ParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#param_list}.
	 * @param ctx the parse tree
	 */
	void enterParam_list(miniCParser.Param_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#param_list}.
	 * @param ctx the parse tree
	 */
	void exitParam_list(miniCParser.Param_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#param_type_list}.
	 * @param ctx the parse tree
	 */
	void enterParam_type_list(miniCParser.Param_type_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#param_type_list}.
	 * @param ctx the parse tree
	 */
	void exitParam_type_list(miniCParser.Param_type_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#param_id_list}.
	 * @param ctx the parse tree
	 */
	void enterParam_id_list(miniCParser.Param_id_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#param_id_list}.
	 * @param ctx the parse tree
	 */
	void exitParam_id_list(miniCParser.Param_id_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#param_id}.
	 * @param ctx the parse tree
	 */
	void enterParam_id(miniCParser.Param_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#param_id}.
	 * @param ctx the parse tree
	 */
	void exitParam_id(miniCParser.Param_idContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterCompound_stmt(miniCParser.Compound_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitCompound_stmt(miniCParser.Compound_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(miniCParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(miniCParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#expression_stmt}.
	 * @param ctx the parse tree
	 */
	void enterExpression_stmt(miniCParser.Expression_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#expression_stmt}.
	 * @param ctx the parse tree
	 */
	void exitExpression_stmt(miniCParser.Expression_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#selection_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSelection_stmt(miniCParser.Selection_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#selection_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSelection_stmt(miniCParser.Selection_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#iteration_stmt}.
	 * @param ctx the parse tree
	 */
	void enterIteration_stmt(miniCParser.Iteration_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#iteration_stmt}.
	 * @param ctx the parse tree
	 */
	void exitIteration_stmt(miniCParser.Iteration_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void enterReturn_stmt(miniCParser.Return_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void exitReturn_stmt(miniCParser.Return_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#break_stmt}.
	 * @param ctx the parse tree
	 */
	void enterBreak_stmt(miniCParser.Break_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#break_stmt}.
	 * @param ctx the parse tree
	 */
	void exitBreak_stmt(miniCParser.Break_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(miniCParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(miniCParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#var}.
	 * @param ctx the parse tree
	 */
	void enterVar(miniCParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#var}.
	 * @param ctx the parse tree
	 */
	void exitVar(miniCParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void enterSimple_expression(miniCParser.Simple_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void exitSimple_expression(miniCParser.Simple_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void enterOr_expression(miniCParser.Or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void exitOr_expression(miniCParser.Or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#unary_rel_expression}.
	 * @param ctx the parse tree
	 */
	void enterUnary_rel_expression(miniCParser.Unary_rel_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#unary_rel_expression}.
	 * @param ctx the parse tree
	 */
	void exitUnary_rel_expression(miniCParser.Unary_rel_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#rel_expression}.
	 * @param ctx the parse tree
	 */
	void enterRel_expression(miniCParser.Rel_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#rel_expression}.
	 * @param ctx the parse tree
	 */
	void exitRel_expression(miniCParser.Rel_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#relop}.
	 * @param ctx the parse tree
	 */
	void enterRelop(miniCParser.RelopContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#relop}.
	 * @param ctx the parse tree
	 */
	void exitRelop(miniCParser.RelopContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#add_expression}.
	 * @param ctx the parse tree
	 */
	void enterAdd_expression(miniCParser.Add_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#add_expression}.
	 * @param ctx the parse tree
	 */
	void exitAdd_expression(miniCParser.Add_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#addop}.
	 * @param ctx the parse tree
	 */
	void enterAddop(miniCParser.AddopContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#addop}.
	 * @param ctx the parse tree
	 */
	void exitAddop(miniCParser.AddopContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(miniCParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(miniCParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#mulop}.
	 * @param ctx the parse tree
	 */
	void enterMulop(miniCParser.MulopContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#mulop}.
	 * @param ctx the parse tree
	 */
	void exitMulop(miniCParser.MulopContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#unary_expression}.
	 * @param ctx the parse tree
	 */
	void enterUnary_expression(miniCParser.Unary_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#unary_expression}.
	 * @param ctx the parse tree
	 */
	void exitUnary_expression(miniCParser.Unary_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(miniCParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(miniCParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(miniCParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(miniCParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#call}.
	 * @param ctx the parse tree
	 */
	void enterCall(miniCParser.CallContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#call}.
	 * @param ctx the parse tree
	 */
	void exitCall(miniCParser.CallContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#args}.
	 * @param ctx the parse tree
	 */
	void enterArgs(miniCParser.ArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#args}.
	 * @param ctx the parse tree
	 */
	void exitArgs(miniCParser.ArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link miniCParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void enterArg_list(miniCParser.Arg_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link miniCParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void exitArg_list(miniCParser.Arg_listContext ctx);
}