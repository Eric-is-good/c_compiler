import frontend.Visitor;
import frontend.SysYLexer;
import frontend.SysYParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Hello {
    public static void main(String[] args) throws IOException {
        // 从文件读入
        CharStream input = CharStreams.fromFileName("test_sy/1.sy");

        // 词法分析->Token流->生成语法分析器对象
        SysYLexer lexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SysYParser parser = new SysYParser(tokens);

        // 语法分析，并将语法树输出
        ParseTree tree = parser.compUnit();
        System.out.println(tree.toStringTree(parser));

        // 语义分析
        Visitor visitor = new Visitor();
        visitor.visit(tree);


    }
}
