import backend.LLC;


public class Hello {
    public static void main(String[] args) throws Exception{
        /* Target code generation */
        LLC llc = new LLC();
        llc.emit("test_syys/1.ll", "test_syys/1.s");
    }

}