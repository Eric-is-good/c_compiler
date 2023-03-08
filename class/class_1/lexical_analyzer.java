import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class lexical_analyzer {
    /*
     * 1表示关键字
     * 2表示标识符
     * 3表示常数
     * 4表示运算符
     * 5表示界符
     * 6表示字符串
     * */

    //关键字
    static String[] keyWord = {"int","if","else","while","for","read","write","string"};
    //运算符
    static String[] operation = {"+","-","*","/","<","<=",">",">=","!=","==","="};
    //界符
    static String[] symbol = {"(", ")", ",", ";", ":", "{", "}"};

    static ArrayList<String> keyWords = null;
    static ArrayList<String> operations = null;
    static ArrayList<String> symbols = null;

    //指向当前所读到字符串的位置的指针
    static int p, lines;


    public static void main(String[] args) throws FileNotFoundException {
        init();
        File file = new File("c_code.c");
        lines = 1;
        try (Scanner input = new Scanner(file)) {
            while (input.hasNextLine()) {
                String str = input.nextLine();
                analyze(str);
                lines++;
            }
        }

    }

    //初始化把数组转换为ArrayList
    public static void init() {
        keyWords = new ArrayList<>();
        operations = new ArrayList<>();
        symbols = new ArrayList<>();
        Collections.addAll(keyWords, keyWord);
        Collections.addAll(operations, operation);
        Collections.addAll(symbols, symbol);
    }

    public static void analyze(String str) {

        p = 0;
        char ch;
        str = str.trim();        // 去掉首尾空格
        for (; p < str.length(); p++) {
            ch = str.charAt(p);
            if (ch == ' ') {
                continue;
            }else if (Character.isDigit(ch)) {
                digitCheck(str);
            } else if (Character.isLetter(ch) || ch == '_') {
                letterCheck(str);
            } else if (ch == '"') {
                stringCheck(str);
            } else {
                symbolCheck(str);   // 识别界符和非法字符
            }
        }
    }

    /*数字的识别
     * 1、识别退出：
     *   1.1、遇到空格符
     *   1.2、遇到运算符或者界符
     * 2、错误情况：
     *   2.1、两个及以上小数点
     *   2.2、掺杂字母
     * */
    public static void digitCheck(String str) {
        String token = String.valueOf(str.charAt(p++));
        //判断数字的小数点是否有且是否大于1
        int flag = 0;
        boolean err = false;
        char ch;
        for (; p < str.length(); p++) {
            ch = str.charAt(p);
            if (ch == ' ' || (!Character.isLetterOrDigit(ch) && ch != '.')) {
                break;
            } else if (err) {
                token += ch;
            } else {
                token += ch;
                if (ch == '.') {
                    if (flag == 1) {
                        err = true;
                    } else {
                        flag++;
                    }
                } else if (Character.isLetter(ch)) {
                    err = true;
                }
            }
        }
        if (token.charAt(token.length() - 1) == '.') {
            err = true;
        }
        if (err) {
            System.out.println(lines + "line" + ": " + token + " is wrong");
        } else {
            System.out.println("(" + 3 + "," + token + ")");
        }
        if (p != str.length() - 1 || (p == str.length() - 1 && !Character.isDigit(str.charAt(p)))) {
            p--;
        }
    }

    //标识符，关键字的识别
    public static void letterCheck(String str) {
        String token = String.valueOf(str.charAt(p++));
        char ch;
        for (; p < str.length(); p++) {
            ch = str.charAt(p);
            if (!Character.isLetterOrDigit(ch) && ch != '_') {
                break;
            } else {
                token += ch;
            }
        }
        if (keyWords.contains(token)) {
            System.out.println("(" + 1 + "," + token + ")");
        } else {
            System.out.println("(" + 2 + "," + token + ")");
        }
        if (p != str.length() - 1 || (p == str.length() - 1 && (!Character.isLetterOrDigit(str.charAt(p)) && str.charAt(p) != '_'))) {
            p--;
        }
    }

    //符号的识别
    public static void symbolCheck(String str) {
        String token = String.valueOf(str.charAt(p++));
        char ch;
        if (symbols.contains(token)) {
            System.out.println("(" + 5 + "," + token + ")");
            p--;
        } else {
            if (operations.contains(token)) {
                if (p < str.length()) {
                    ch = str.charAt(p);
                    if (operations.contains(token + ch)) {
                        token += ch;
                        p++;
                        if (p < str.length()) {
                            ch = str.charAt(p);
                            if (operations.contains(token + ch)) {
                                token += ch;
                                System.out.println("(" + 4 + "," + token + ")");
                            } else {
                                p--;
                                System.out.println("(" + 4 + "," + token + ")");
                            }
                        } else {
                            System.out.println("(" + 4 + "," + token + ")");
                        }
                    } else {
                        p--;
                        System.out.println("(" + 4 + "," + token + ")");
                    }
                }
            } else {
                p--;
                System.out.println(lines + "line" + ": " + token + " is wrong");
            }
        }
    }

    //字符串检查
    public static void stringCheck(String str) {
        String token = String.valueOf(str.charAt(p++));
        char ch;
        for (; p < str.length(); p++) {
            ch = str.charAt(p);
            token += ch;
            if (ch == '"') {
                break;
            }
        }
        if (token.charAt(token.length() - 1) != '"') {
            System.out.println(lines + "line" + ": " + token + " is wrong");
        } else {
            System.out.println("(" + 6 + "," + token + ")");
        }
    }
}

