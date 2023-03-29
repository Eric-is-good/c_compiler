grammar miniC ;

/*
 * Parser Rules
 */

program : declaration_list ;
declaration_list : declaration_list declaration | declaration ;
declaration : var_declaration | fun_declaration ;
var_declaration : type_specifier var_decl_list ';' ;
var_decl_list : var_decl_list ',' var_decl_id | var_decl_id ;
var_decl_id : ID | (ID '[' NUM ']') ;
type_specifier : 'int' | 'void' | 'bool'  ;
fun_declaration : type_specifier ID '(' params ')' statement ;
params : param_list? ;
param_list : param_list ';' param_type_list | param_type_list ;
param_type_list : type_specifier param_id_list ;
param_id_list : param_id_list ',' param_id | param_id ;
param_id : ID | (ID '[' ']') ;
compound_stmt : '{' var_declaration* statement* '}' ;
//local_declarations : local_declarations var_declaration | EMPTY;
//statement_list : statement_list statement | EMPTY;
statement : expression_stmt | compound_stmt | selection_stmt | iteration_stmt | return_stmt| break_stmt ;
expression_stmt : expression ';' | ';' ;
selection_stmt : 'if' '(' expression ')' statement | 'if' '(' expression ')' statement 'else' statement ;
iteration_stmt : 'while' '(' expression ')' statement ;
return_stmt : 'return' ';' | 'return' expression ';' ;
break_stmt : 'break' ';' ;
expression : var '=' expression | var '+=' expression | var '−=' expression | simple_expression ;
var : ID | (ID '[' expression ']') ;
simple_expression : simple_expression '|' or_expression | or_expression ;
or_expression : or_expression '&' unary_rel_expression | unary_rel_expression ;
unary_rel_expression : '!' unary_rel_expression | rel_expression ;
rel_expression : add_expression relop add_expression | add_expression ;
relop : '<=' | '<' | '>' | '>=' | '==' | '!=' ;
add_expression : add_expression addop term | term ;
addop : '+' | '−' ;
term : term mulop unary_expression | unary_expression ;
mulop : '*' | '/' | '%' ;
unary_expression : '-' unary_expression | factor ;
factor : '(' expression ')' | var | call | constant ;
constant : NUM | 'true' | 'false' ;
call : ID '(' args ')' ;
args : arg_list? ;
arg_list : arg_list ',' expression | expression ;
/*
 * Lexer Rules
 */

fragment LETTER : [a-z] | [A-Z] ;
fragment DIGIT : [0-9] ;
ID : LETTER+DIGIT* ;
NUM : DIGIT+  ;
WHITESPACE  : (' ' | '\t') -> skip ;
NEWLINE : ('\r'? '\n' | '\r') -> skip ;
