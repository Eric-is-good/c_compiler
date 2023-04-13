import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import paser.miniCLexer;
import paser.miniCParser;
import paser.miniCVisitor;

import java.io.IOException;


public class Hello {
    public static void main(String[] args) throws IOException {
        CharStream input = CharStreams.fromString("int main(int args[]){\n" +
                "        int a;\n" +
                "        a = 6;\n" +
                "        a = a+10;\n" +
                "        if(a>9){\n" +
                "            return a;\n" +
                "        }else{\n" +
                "            int b;\n" +
                "            b = a * a;\n" +
                "        }\n" +
                "    }");


        // 从文件读入
//        CharStream input = CharStreams.fromFileName("test_c/main.c");

        // 词法分析->Token流->生成语法分析器对象
        miniCLexer lexer = new miniCLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        miniCParser parser = new miniCParser(tokens);

        // 真正启动语法分析，并将语法树输出
        ParseTree tree = parser.program();
        System.out.println(tree.toStringTree(parser));
    }
}

