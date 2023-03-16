/* Lex.java */
/* Generated By:JavaCC: Do not edit this line. Lex.java */
 package com.javacc.lexer;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.FileNotFoundException;

 public class Lex implements LexConstants {
   public static void test_lex(String args []){
       FileInputStream FileStream = null;
       try {
           FileStream = new FileInputStream("c_code.c");
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }

       Lex Parser = new Lex(FileStream);

       while (true){
           try {
               String res = Parser.next_word();
                if (res.equals("EOF")) break;
               System.out.println(res);
           }catch (Exception e){
               e.printStackTrace();
           }
       }

   }

  final public String next_word() throws ParseException {Token token;
  StringBuilder builder = new StringBuilder("Javacc Say :");
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case INTEGER_LITERAL:
      case CONSTANT:
      case MAIN:
      case INT:
      case RETURN:
      case IF:
      case ELSE:
      case VOID:
      case DOUBLE:
      case FLOAT:
      case WHILE:
      case DO:
      case FOR:
      case CHAR:
      case STRING:
      case BOOL:
      case BREAK:
      case SWITCH:
      case CASE:
      case DEFAULTS:
      case PLUS:
      case MINUS:
      case MULTIPLY:
      case DIVIDE:
      case GD:
      case LD:
      case SQRT:
      case EQ:
      case GE:
      case LE:
      case EQQ:
      case NE:
      case OR:
      case AND:
      case COMMA:
      case SEMICOLON:
      case LB:
      case RB:
      case BLB:
      case BRB:
      case LBB:
      case RBB:
      case COLON:
      case IDENTIFIER:
      case STRING_LITERAL:{
        ;
        break;
        }
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case IDENTIFIER:{
        token = jj_consume_token(IDENTIFIER);
        break;
        }
      case CONSTANT:{
        token = jj_consume_token(CONSTANT);
        break;
        }
      case INTEGER_LITERAL:{
        token = jj_consume_token(INTEGER_LITERAL);
        break;
        }
      case MAIN:{
        token = jj_consume_token(MAIN);
        break;
        }
      case INT:{
        token = jj_consume_token(INT);
        break;
        }
      case RETURN:{
        token = jj_consume_token(RETURN);
        break;
        }
      case IF:{
        token = jj_consume_token(IF);
        break;
        }
      case ELSE:{
        token = jj_consume_token(ELSE);
        break;
        }
      case VOID:{
        token = jj_consume_token(VOID);
        break;
        }
      case DOUBLE:{
        token = jj_consume_token(DOUBLE);
        break;
        }
      case FLOAT:{
        token = jj_consume_token(FLOAT);
        break;
        }
      case WHILE:{
        token = jj_consume_token(WHILE);
        break;
        }
      case DO:{
        token = jj_consume_token(DO);
        break;
        }
      case FOR:{
        token = jj_consume_token(FOR);
        break;
        }
      case CHAR:{
        token = jj_consume_token(CHAR);
        break;
        }
      case STRING:{
        token = jj_consume_token(STRING);
        break;
        }
      case BOOL:{
        token = jj_consume_token(BOOL);
        break;
        }
      case BREAK:{
        token = jj_consume_token(BREAK);
        break;
        }
      case SWITCH:{
        token = jj_consume_token(SWITCH);
        break;
        }
      case CASE:{
        token = jj_consume_token(CASE);
        break;
        }
      case DEFAULTS:{
        token = jj_consume_token(DEFAULTS);
        break;
        }
      case PLUS:{
        token = jj_consume_token(PLUS);
        break;
        }
      case MINUS:{
        token = jj_consume_token(MINUS);
        break;
        }
      case MULTIPLY:{
        token = jj_consume_token(MULTIPLY);
        break;
        }
      case DIVIDE:{
        token = jj_consume_token(DIVIDE);
        break;
        }
      case GD:{
        token = jj_consume_token(GD);
        break;
        }
      case LD:{
        token = jj_consume_token(LD);
        break;
        }
      case SQRT:{
        token = jj_consume_token(SQRT);
        break;
        }
      case EQ:{
        token = jj_consume_token(EQ);
        break;
        }
      case GE:{
        token = jj_consume_token(GE);
        break;
        }
      case LE:{
        token = jj_consume_token(LE);
        break;
        }
      case EQQ:{
        token = jj_consume_token(EQQ);
        break;
        }
      case NE:{
        token = jj_consume_token(NE);
        break;
        }
      case OR:{
        token = jj_consume_token(OR);
        break;
        }
      case AND:{
        token = jj_consume_token(AND);
        break;
        }
      case COMMA:{
        token = jj_consume_token(COMMA);
        break;
        }
      case SEMICOLON:{
        token = jj_consume_token(SEMICOLON);
        break;
        }
      case LB:{
        token = jj_consume_token(LB);
        break;
        }
      case RB:{
        token = jj_consume_token(RB);
        break;
        }
      case BLB:{
        token = jj_consume_token(BLB);
        break;
        }
      case BRB:{
        token = jj_consume_token(BRB);
        break;
        }
      case LBB:{
        token = jj_consume_token(LBB);
        break;
        }
      case RBB:{
        token = jj_consume_token(RBB);
        break;
        }
      case COLON:{
        token = jj_consume_token(COLON);
        break;
        }
      case STRING_LITERAL:{
        token = jj_consume_token(STRING_LITERAL);
        break;
        }
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
builder.append(" " + token.image);
        {if ("" != null) return builder.toString();}
    }
    jj_consume_token(0);
{if ("" != null) return "EOF" ;}
    throw new Error("Missing return statement in function");
}

  /** Generated Token Manager. */
  public LexTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[2];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
	   jj_la1_init_0();
	   jj_la1_init_1();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0xfffff880,0xfffff880,};
	}
	private static void jj_la1_init_1() {
	   jj_la1_1 = new int[] {0x13fffff,0x13fffff,};
	}

  /** Constructor with InputStream. */
  public Lex(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Lex(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new LexTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public Lex(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new LexTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new LexTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public Lex(LexTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(LexTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 2; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[57];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 2; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		   if ((jj_la1_1[i] & (1<<j)) != 0) {
			 la1tokens[32+j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 57; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

 }
