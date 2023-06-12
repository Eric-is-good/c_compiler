import backend.LLC;
import frontend.*;
import ir.Module;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;


public class Hello {
    public static void main(String[] args) throws Exception{
        CharStream inputFile = CharStreams.fromFileName("test_syys/1.sy");

        /* Lexical analysis */
        SysYLexer lexer = new SysYLexer(inputFile);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        /* Parsing */
        SysYParser parser = new SysYParser(tokenStream);
        ParseTree ast = parser.compUnit(); // Retrieve the parse tree (It's called AST but actually a CST).

        /* Traversal the ast to build the IR. */
        Module module = new Module();
        Visitor visitor = new Visitor(module);
        visitor.visit(ast);

        /* Emit the IR text to an output file for testing. */
        IREmitter irEmitter = new IREmitter("test_syys/1.ll");
        irEmitter.emit(module, true);

        /* Target code generation */
        LLC llc = new LLC();
        llc.emit("test_syys/1.ll", "test_syys/1.s");
    }

}