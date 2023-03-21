/* LexTokenManager.java */
/* Generated By:JavaCC: Do not edit this line. LexTokenManager.java */
package com.javacc.lexer;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import com.javacc.utils.OneToken;

/** Token Manager. */
@SuppressWarnings ("unused")
public class LexTokenManager implements LexConstants {

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0){
   switch (pos)
   {
      case 0:
         if ((active0 & 0x3ffff000L) != 0L)
         {
            jjmatchedKind = 53;
            return 7;
         }
         if ((active0 & 0x200000000L) != 0L)
            return 29;
         return -1;
      case 1:
         if ((active0 & 0x3fdb7000L) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 53;
               jjmatchedPos = 1;
            }
            return 7;
         }
         if ((active0 & 0x248000L) != 0L)
            return 7;
         return -1;
      case 2:
         if ((active0 & 0x3f9f5000L) != 0L)
         {
            jjmatchedKind = 53;
            jjmatchedPos = 2;
            return 7;
         }
         if ((active0 & 0x402000L) != 0L)
            return 7;
         return -1;
      case 3:
         if ((active0 & 0x2d1c4000L) != 0L)
         {
            jjmatchedKind = 53;
            jjmatchedPos = 3;
            return 7;
         }
         if ((active0 & 0x12831000L) != 0L)
            return 7;
         return -1;
      case 4:
         if ((active0 & 0x29044000L) != 0L)
         {
            jjmatchedKind = 53;
            jjmatchedPos = 4;
            return 7;
         }
         if ((active0 & 0x4180000L) != 0L)
            return 7;
         return -1;
      case 5:
         if ((active0 & 0x20000000L) != 0L)
         {
            jjmatchedKind = 53;
            jjmatchedPos = 5;
            return 7;
         }
         if ((active0 & 0x9044000L) != 0L)
            return 7;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0){
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0(){
   switch(curChar)
   {
      case 33:
         return jjMoveStringLiteralDfa1_0(0x20000000000L);
      case 38:
         return jjMoveStringLiteralDfa1_0(0x80000000000L);
      case 40:
         return jjStopAtPos(0, 46);
      case 41:
         return jjStopAtPos(0, 47);
      case 42:
         return jjStopAtPos(0, 32);
      case 43:
         return jjStopAtPos(0, 30);
      case 44:
         return jjStopAtPos(0, 44);
      case 45:
         return jjStopAtPos(0, 31);
      case 47:
         return jjStartNfaWithStates_0(0, 33, 29);
      case 58:
         return jjStopAtPos(0, 52);
      case 59:
         return jjStopAtPos(0, 45);
      case 60:
         jjmatchedKind = 35;
         return jjMoveStringLiteralDfa1_0(0x8000000000L);
      case 61:
         jjmatchedKind = 37;
         return jjMoveStringLiteralDfa1_0(0x10000000000L);
      case 62:
         jjmatchedKind = 34;
         return jjMoveStringLiteralDfa1_0(0x4000000000L);
      case 91:
         return jjStopAtPos(0, 50);
      case 93:
         return jjStopAtPos(0, 51);
      case 94:
         return jjStopAtPos(0, 36);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x6000000L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x10800000L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x20240000L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x480000L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0xa000L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x1000L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x9000000L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 119:
         return jjMoveStringLiteralDfa1_0(0x100000L);
      case 123:
         return jjStopAtPos(0, 48);
      case 124:
         return jjMoveStringLiteralDfa1_0(0x40000000000L);
      case 125:
         return jjStopAtPos(0, 49);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 38:
         if ((active0 & 0x80000000000L) != 0L)
            return jjStopAtPos(1, 43);
         break;
      case 61:
         if ((active0 & 0x4000000000L) != 0L)
            return jjStopAtPos(1, 38);
         else if ((active0 & 0x8000000000L) != 0L)
            return jjStopAtPos(1, 39);
         else if ((active0 & 0x10000000000L) != 0L)
            return jjStopAtPos(1, 40);
         else if ((active0 & 0x20000000000L) != 0L)
            return jjStopAtPos(1, 41);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x10001000L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x20004000L);
      case 102:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(1, 15, 7);
         break;
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x900000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x90000L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000L);
      case 111:
         if ((active0 & 0x200000L) != 0L)
         {
            jjmatchedKind = 21;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_0(active0, 0x2460000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000000L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000000L);
      case 119:
         return jjMoveStringLiteralDfa2_0(active0, 0x8000000L);
      case 124:
         if ((active0 & 0x40000000000L) != 0L)
            return jjStopAtPos(1, 42);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000000L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x8121000L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x2080000L);
      case 114:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(2, 22, 7);
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x10010000L);
      case 116:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(2, 13, 7);
         return jjMoveStringLiteralDfa3_0(active0, 0x4000L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x24080000L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x40000L);
      case 100:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(3, 17, 7);
         break;
      case 101:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(3, 16, 7);
         else if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_0(3, 28, 7);
         break;
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000L);
      case 108:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(3, 25, 7);
         return jjMoveStringLiteralDfa4_0(active0, 0x100000L);
      case 110:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(3, 12, 7);
         break;
      case 114:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(3, 23, 7);
         break;
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x8000000L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000000L);
      case 101:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(4, 20, 7);
         break;
      case 107:
         if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_0(4, 26, 7);
         break;
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x40000L);
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000L);
      case 116:
         if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(4, 19, 7);
         break;
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(5, 18, 7);
         break;
      case 103:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(5, 24, 7);
         break;
      case 104:
         if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_0(5, 27, 7);
         break;
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000000L);
      case 110:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(5, 14, 7);
         break;
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 116:
         if ((active0 & 0x20000000L) != 0L)
            return jjStartNfaWithStates_0(6, 29, 7);
         break;
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 40;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 29:
                  if (curChar == 42)
                     { jjCheckNAddTwoStates(35, 36); }
                  else if (curChar == 47)
                     { jjCheckNAddStates(0, 2); }
                  break;
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 11)
                        kind = 11;
                     { jjCheckNAddTwoStates(3, 4); }
                  }
                  else if (curChar == 47)
                     { jjAddStates(3, 4); }
                  else if (curChar == 34)
                     { jjCheckNAddTwoStates(22, 23); }
                  else if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 19;
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 7)
                        kind = 7;
                     { jjCheckNAddTwoStates(1, 2); }
                  }
                  else if (curChar == 48)
                  {
                     if (kind > 7)
                        kind = 7;
                     { jjCheckNAddStates(5, 7); }
                  }
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 3:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  { jjCheckNAddTwoStates(3, 4); }
                  break;
               case 4:
                  if (curChar == 46)
                     { jjCheckNAdd(5); }
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  { jjCheckNAdd(5); }
                  break;
               case 7:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 53)
                     kind = 53;
                  jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 9:
                  if (curChar == 32)
                     { jjAddStates(8, 9); }
                  break;
               case 10:
                  if ((0x1000000400000000L & l) != 0L)
                     { jjCheckNAddStates(10, 12); }
                  break;
               case 11:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     { jjCheckNAddStates(10, 12); }
                  break;
               case 12:
                  if (curChar == 32)
                     { jjCheckNAddTwoStates(12, 13); }
                  break;
               case 13:
                  if ((0x4000000400000000L & l) != 0L && kind > 56)
                     kind = 56;
                  break;
               case 20:
                  if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if (curChar == 34)
                     { jjCheckNAddTwoStates(22, 23); }
                  break;
               case 22:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     { jjCheckNAddTwoStates(22, 23); }
                  break;
               case 23:
                  if (curChar == 34 && kind > 57)
                     kind = 57;
                  break;
               case 24:
                  if (curChar != 48)
                     break;
                  if (kind > 7)
                     kind = 7;
                  { jjCheckNAddStates(5, 7); }
                  break;
               case 26:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  { jjCheckNAddTwoStates(26, 2); }
                  break;
               case 27:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  { jjCheckNAddTwoStates(27, 2); }
                  break;
               case 28:
                  if (curChar == 47)
                     { jjAddStates(3, 4); }
                  break;
               case 30:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     { jjCheckNAddStates(0, 2); }
                  break;
               case 31:
                  if ((0x2400L & l) != 0L && kind > 5)
                     kind = 5;
                  break;
               case 32:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 33:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 34:
                  if (curChar == 42)
                     { jjCheckNAddTwoStates(35, 36); }
                  break;
               case 35:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     { jjCheckNAddTwoStates(35, 36); }
                  break;
               case 36:
                  if (curChar == 42)
                     { jjAddStates(13, 14); }
                  break;
               case 37:
                  if ((0xffff7fffffffffffL & l) != 0L)
                     { jjCheckNAddTwoStates(38, 36); }
                  break;
               case 38:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     { jjCheckNAddTwoStates(38, 36); }
                  break;
               case 39:
                  if (curChar == 47 && kind > 6)
                     kind = 6;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 7:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 53)
                     kind = 53;
                  { jjCheckNAdd(7); }
                  break;
               case 2:
                  if ((0x100000001000L & l) != 0L && kind > 7)
                     kind = 7;
                  break;
               case 8:
                  if (curChar == 101)
                     { jjAddStates(8, 9); }
                  break;
               case 11:
                  { jjAddStates(10, 12); }
                  break;
               case 14:
                  if (curChar == 100)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 15:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 16:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 17:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 22:
                  { jjAddStates(15, 16); }
                  break;
               case 25:
                  if ((0x100000001000000L & l) != 0L)
                     { jjCheckNAdd(26); }
                  break;
               case 26:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  { jjCheckNAddTwoStates(26, 2); }
                  break;
               case 30:
                  { jjAddStates(0, 2); }
                  break;
               case 35:
                  { jjCheckNAddTwoStates(35, 36); }
                  break;
               case 37:
               case 38:
                  { jjCheckNAddTwoStates(38, 36); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 11:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(10, 12); }
                  break;
               case 22:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(15, 16); }
                  break;
               case 30:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(0, 2); }
                  break;
               case 35:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjCheckNAddTwoStates(35, 36); }
                  break;
               case 37:
               case 38:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjCheckNAddTwoStates(38, 36); }
                  break;
               default : if (i1 == 0 || l1 == 0 || i2 == 0 ||  l2 == 0) break; else break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 40 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, 
"\155\141\151\156", "\151\156\164", "\162\145\164\165\162\156", "\151\146", "\145\154\163\145", 
"\166\157\151\144", "\144\157\165\142\154\145", "\146\154\157\141\164", "\167\150\151\154\145", 
"\144\157", "\146\157\162", "\143\150\141\162", "\163\164\162\151\156\147", 
"\142\157\157\154", "\142\162\145\141\153", "\163\167\151\164\143\150", "\143\141\163\145", 
"\144\145\146\141\165\154\164", "\53", "\55", "\52", "\57", "\76", "\74", "\136", "\75", "\76\75", "\74\75", 
"\75\75", "\41\75", "\174\174", "\46\46", "\54", "\73", "\50", "\51", "\173", "\175", 
"\133", "\135", "\72", null, null, null, null, null, };
protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}
static final int[] jjnextStates = {
   30, 31, 33, 29, 34, 25, 27, 2, 9, 10, 11, 12, 13, 37, 39, 22, 
   23, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default :
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(Exception e)
   {
      jjmatchedKind = 0;
      jjmatchedPos = -1;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

void SkipLexicalActions(Token matchedToken)
{
   switch(jjmatchedKind)
   {
      default :
         break;
   }
}
void MoreLexicalActions()
{
   jjimageLen += (lengthOfMatch = jjmatchedPos + 1);
   switch(jjmatchedKind)
   {
      default :
         break;
   }
}
void TokenLexicalActions(Token matchedToken)
{
   switch(jjmatchedKind)
   {
      default :
         break;
   }
}
private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

    /** Constructor. */
    public LexTokenManager(SimpleCharStream stream){

      if (SimpleCharStream.staticFlag)
            throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");

    input_stream = stream;
  }

  /** Constructor. */
  public LexTokenManager (SimpleCharStream stream, int lexState){
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Reinitialise parser. */
  
  public void ReInit(SimpleCharStream stream)
  {


    jjmatchedPos =
    jjnewStateCnt =
    0;
    curLexState = defaultLexState;
    input_stream = stream;
    ReInitRounds();
  }

  private void ReInitRounds()
  {
    int i;
    jjround = 0x80000001;
    for (i = 40; i-- > 0;)
      jjrounds[i] = 0x80000000;
  }

  /** Reinitialise parser. */
  public void ReInit(SimpleCharStream stream, int lexState)
  
  {
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Switch to specified lex state. */
  public void SwitchTo(int lexState)
  {
    if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
    else
      curLexState = lexState;
  }


/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};

/** Lex State array. */
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, 
};
static final long[] jjtoToken = {
   0x33ffffffffff881L, 
};
static final long[] jjtoSkip = {
   0x7eL, 
};
static final long[] jjtoSpecial = {
   0x0L, 
};
static final long[] jjtoMore = {
   0x0L, 
};
    protected SimpleCharStream  input_stream;

    private final int[] jjrounds = new int[40];
    private final int[] jjstateSet = new int[2 * 40];
    private final StringBuilder jjimage = new StringBuilder();
    private StringBuilder image = jjimage;
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;
}
