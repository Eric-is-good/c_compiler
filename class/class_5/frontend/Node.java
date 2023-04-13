package frontend;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class Node {
    public ParseTree remain_tree;
    public int father_hashcode;
    public String func;

    public ArrayList<String> generate_code = new ArrayList<>();
    public enum TYPE{
        float_array,
        int_array,
        float_one,
        int_one,
        float_func,
        int_func
    }
    public TYPE type;

    public Node(ParseTree remain_tree, int father_hashcode, String func) {
        this.remain_tree = remain_tree;
        this.father_hashcode = father_hashcode;
        this.func = func;
    }
}
