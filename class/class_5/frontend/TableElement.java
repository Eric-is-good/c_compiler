package frontend;

public class TableElement {
    public int index;
    public String ele_name;
    public Node.TYPE ele_type;
    public float f_value;
    public int i_value;

    public TableElement(int index, String ele_name, Node.TYPE ele_type) {
        this.index = index;
        this.ele_name = ele_name;
        this.ele_type = ele_type;
    }
}
