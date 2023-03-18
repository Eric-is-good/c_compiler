import com.javacc.errlexer.LexError;
import com.javacc.lexer.Lex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.javacc.lexer.LexConstants.tokenImage;

public class lexer {
    public static void main(String[] args) {
        String file_name = "c_code.c";
        boolean good_file = new lexer().Check_no_err(file_name);

        if(good_file){
            new lexer().print_parser(file_name);
        }
    }

    public void print_parser(String file_name){
        FileInputStream FileStream = null;
        try {
            FileStream = new FileInputStream(file_name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Lex Parser = new Lex(FileStream);

        while (true){
            try {
                com.javacc.utils.OneToken token = Parser.next_word();
                if (token.kind == 0) break;
                System.out.println(token.image + " " + tokenImage[token.kind]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean Check_no_err(String file_name) {
        FileInputStream FileStream = null;
        try {
            FileStream = new FileInputStream(file_name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LexError ParserErr = new LexError(FileStream);

        while (true){
            try {
                String res = ParserErr.err_check();
                if (res.equals("EOF")) {System.out.println("Check ok !");return true;}
                else if (res.equals("false")) {System.out.println("Check false ! (" + ParserErr.token.image + ")"
                        + " at line " + ParserErr.token.beginLine + " column " + ParserErr.token.beginColumn);return false;}
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }




}
