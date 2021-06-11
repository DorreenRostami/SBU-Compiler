package Model.cg;

import java.io.IOException;
import java.io.Writer;

import Model.AST.expression.*;
import Model.error.SemanticError;
import Model.scope.*;

public class CodeGen {
    public static String dataSeg = ".data\n";
    public static String textSeg = ".text\n.globl main\n\n";
    public static int regi = 1;
    public static int regd = 2;
    public static int label = 0;
    public static int startlabel = 0;
    public static int endlabel = 0;
    public static int maxendlabel = 0;
    public static int stringCount = 0;
    public static int istruelabel = 0;
    /*public static String scope = null;
    public static String r_scope = null;*/

    static {
        dataSeg += "  trueStr: .asciiz \"true\"\n  falseStr: .asciiz \"false\"\n  newline: .asciiz \"\\n\"\n";

        textSeg += "printBool:\n  li $v0, 4\n  beqz $a0, printFalse\n";
        textSeg += "  la $a0, trueStr($zero)\n  syscall\n  jr $ra\n";
        textSeg += "printFalse:\n  la $a0, falseStr($zero)\n  syscall\n  jr $ra\n\n";

        textSeg += "ReadInteger:\n  li $v0, 6 \n  syscall\n";
        textSeg += "  li.s $f10, 0.0 \n";
        textSeg += "  add.s $f4, $f0, $f10 \n";
        try {
            CodeGen.cgen("dtoi");
            CodeGen.cgen("itod");
        } catch (SemanticError semanticError) {}
        //compare f4 and f0
        textSeg += "  c.eq.s $f0, $f4\n  bc1t isInt\n  li $t2, 0\nisInt:\n  jr $ra\n\n";

        textSeg += "ITOB:\n  bne $t2, $zero, notZero\n  move $t2, $zero\nnotZero:\n  jr $ra\n\n";
    }

    public static void cgen(String sem) throws SemanticError {
        if (sem.equals("+") || sem.equals("*") || sem.equals("/") || sem.equals("-") || sem.equals("%") ||
                sem.equals("<") || sem.equals(">") || sem.equals("<=") || sem.equals(">=") ||
                sem.equals("!") || sem.equals("&&") || sem.equals("||") || sem.equals("==") || sem.equals("!=")) {

            Expression num2 = (Expression) SemanticStack.pop();
            Expression num1 = (Expression) SemanticStack.pop();

            if (num1.getType().equals("INT") || num1.getType().equals("BOOL")) { //bool uses &&,||,! and int uses the others
                if (sem.equals("+") || sem.equals("*") || sem.equals("/") || sem.equals("-") || sem.equals("%")) {
                    if (num1 instanceof ArithLogExpr && num2 instanceof ArithLogExpr) {
                        if ("+".equals(sem)) {
                            textSeg += "  add $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("*".equals(sem)) {
                            textSeg += "  mul $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("/".equals(sem)) {
                            textSeg += "  div $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("-".equals(sem)) {
                            textSeg += "  sub $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if (sem.equals("%")) {
                            textSeg += "  div $t" + (regi - 1) + ", $t" + regi + "\n";
                            textSeg += "  mfhi $t" + (regi - 1) + " \n";
                        }
                        regi -= 1;
                    }
                    else if (num1 instanceof ArithLogExpr || num2 instanceof ArithLogExpr) {
                        if (num1 instanceof ArithLogExpr) {
                            textSeg += "  move $t0, $t" + regi + "\n";
                            if (num2 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                                /*if(scope.equals("this"))
                                    scope = Spaghetti.getParentScope(id);
                                else if(scope==null)
                                    scope = Spaghetti.getScope(id);*/
                                textSeg += "  lw $t" + regi + ", "+ id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else
                                textSeg += "  li $t" + regi + ", " + num2.getValue() + "\n";
                        }
                        else {
                            if (num1 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                                String scope = Spaghetti.getScope(id);
                                /*if(isThis){
                                    scope = Spaghetti.getParentScope(id);
                                    isThis = false;
                                }*/
                                textSeg += "  lw $t0, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else
                                textSeg += "  li $t0, " + num1.getValue() + "\n";
                        }
                        if ("+".equals(sem)) {
                            textSeg += "  add $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("*".equals(sem)) {
                            textSeg += "  mul $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("/".equals(sem)) {
                            textSeg += "  div $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("-".equals(sem)) {
                            textSeg += "  sub $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if (sem.equals("%")) {
                            textSeg += "  div $t0, $t" + regi + "\n";
                            textSeg += "  mfhi $t" + regi + " \n";
                        }
                    }
                    else {
                        regi += 1;
                        if (num2 instanceof DSCP) {
                            String id2 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id2);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id2);
                                isThis = false;
                            }*/
                            textSeg += "  lw $t1, " + id2 + "_" + Spaghetti.getScope(id2) + "($zero) \n";
                        }
                        else
                            textSeg += "  li $t1, " + num2.getValue() + "\n";
                        if (num1 instanceof DSCP) {
                            String id1 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id1);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id1);
                                isThis = false;
                            }*/
                            textSeg += "  lw $t0, " + id1 + "_" + Spaghetti.getScope(id1) + "($zero) \n";
                        }
                        else
                            textSeg += "  li $t0, " + num1.getValue() + "\n";
                        if ("+".equals(sem)) {
                            textSeg += "  add $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("*".equals(sem)) {
                            textSeg += "  mul $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("/".equals(sem)) {
                            textSeg += "  div $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("-".equals(sem)) {
                            textSeg += "  sub $t" + regi + ", $t0, $t1\n";
                        }
                        else if (sem.equals("%")) {
                            textSeg += "  div $t0, $t1 \n";
                            textSeg += "  mfhi $t" + regi + " \n";
                        }
                    }
                }
                else if (sem.equals("!")) {
                    if (num1 instanceof ArithLogExpr) {
                        textSeg += "  not $t" + regi + ", $t" + regi + "\n";
                    }
                    else {
                        regi += 1;
                        if (num1 instanceof DSCP) {
                            String id1 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id1);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id1);
                                isThis = false;
                            }*/
                            textSeg += "  lw $t0, " + id1 + "_" + Spaghetti.getScope(id1) + "($zero) \n";
                        }
                        textSeg += "  not $t" + regi + ", $t0\n";
                    }
                }
                else {
                    if (num1 instanceof ArithLogExpr && num2 instanceof ArithLogExpr) {
                        if ("==".equals(sem)) {
                            textSeg += "  seq $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("!=".equals(sem)) {
                            textSeg += "  sne $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("<".equals(sem)) {
                            textSeg += "  slt $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if (">".equals(sem)) {
                            textSeg += "  sgt $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("<=".equals(sem)) {
                            textSeg += "  sle $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if (">=".equals(sem)) {
                            textSeg += "  sge $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("&&".equals(sem)) {
                            textSeg += "  and $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        else if ("||".equals(sem)) {
                            textSeg += "  or $t" + (regi - 1) + ", $t" + (regi - 1) + ", $t" + regi + "\n";
                        }
                        regi -= 1;
                    }
                    else if (num1 instanceof ArithLogExpr || num2 instanceof ArithLogExpr) {
                        if (num1 instanceof ArithLogExpr) {
                            textSeg += "  move $t0, $t" + regi + "\n";
                            if (num2 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id);
                                isThis = false;
                            }*/
                                textSeg += "  lw $t" + regi + ", "+ id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else
                                textSeg += "  li $t" + regi + ", " + num2.getValue() + "\n";
                        }
                        else {
                            if (num1 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                                /*String scope = Spaghetti.getScope(id);
                                if(isThis){
                                    scope = Spaghetti.getParentScope(id);
                                    isThis = false;
                                }*/
                                textSeg += "  lw $t0, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else
                                textSeg += "  li $t0, " + num1.getValue() + "\n";
                        }
                        if ("==".equals(sem)) {
                            textSeg += "  seq $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("!=".equals(sem)) {
                            textSeg += "  sne $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("<".equals(sem)) {
                            textSeg += "  slt $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if (">".equals(sem)) {
                            textSeg += "  sgt $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("<=".equals(sem)) {
                            textSeg += "  sle $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if (">=".equals(sem)) {
                            textSeg += "  sge $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("&&".equals(sem)) {
                            textSeg += "  and $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                        else if ("||".equals(sem)) {
                            textSeg += "  or $t" + regi + ", $t0, $t" + regi + "\n";
                        }
                    }
                    else {
                        regi += 1;
                        if (num2 instanceof DSCP) {
                            String id2 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id2);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id2);
                                isThis = false;
                            }*/
                            textSeg += "  lw $t1, " + id2 + "_" + Spaghetti.getScope(id2) + "($zero) \n";
                        }
                        else
                            textSeg += "  li $t1, " + num2.getValue() + "\n";
                        if (num1 instanceof DSCP) {
                            String id1 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id1);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id1);
                                isThis = false;
                            }*/
                            textSeg += "  lw $t0, " + id1 + "_" + Spaghetti.getScope(id1) + "($zero) \n";
                        }
                        else
                            textSeg += "  li $t0, " + num1.getValue() + "\n";
                        if ("==".equals(sem)) {
                            textSeg += "  seq $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("!=".equals(sem)) {
                            textSeg += "  sne $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("<".equals(sem)) {
                            textSeg += "  slt $t" + regi + ", $t0, $t1\n";
                        }
                        else if (">".equals(sem)) {
                            textSeg += "  sgt $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("<=".equals(sem)) {
                            textSeg += "  sle $t" + regi + ", $t0, $t1\n";
                        }
                        else if (">=".equals(sem)) {
                            textSeg += "  sge $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("&&".equals(sem)) {
                            textSeg += "  and $t" + regi + ", $t0, $t1\n";
                        }
                        else if ("||".equals(sem)) {
                            textSeg += "  or $t" + regi + ", $t0, $t1\n";
                        }
                    }
                }
            }
            else if (num1.getType().equals("DOUBLE")) {
                if (sem.equals("+") || sem.equals("*") || sem.equals("/") || sem.equals("-")) {
                    if (num1 instanceof ArithLogExpr && num2 instanceof ArithLogExpr) { //num1 az samte chap umade o num2 az rast
                        if ("+".equals(sem)) {
                            textSeg += "  add.s $f" + (regd - 2) + ", $f" + (regd - 2) + ", $f" + regd + "\n";
                        }
                        else if ("*".equals(sem)) {
                            textSeg += "  mul.s $f" + (regd - 2) + ", $f" + (regd - 2) + ", $f" + regd + "\n";
                        }
                        else if ("/".equals(sem)) {
                            textSeg += "  div.s $f" + (regd - 2) + ", $f" + (regd - 2) + ", $f" + regd + "\n";
                        }
                        else if ("-".equals(sem)) {
                            textSeg += "  sub.s $f" + (regd - 2) + ", $f" + (regd - 2) + ", $f" + regd + "\n";
                        }
                        regd -= 2;
                    }
                    else if (num1 instanceof ArithLogExpr || num2 instanceof ArithLogExpr) { //vaqti expr taki samte raste
                        if (num1 instanceof ArithLogExpr) {
                            textSeg += "  mov.s $f0, $f" + regd + "\n";
                            if (num2 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                                /*String scope = Spaghetti.getScope(id);
                                if(isThis){
                                    scope = Spaghetti.getParentScope(id);
                                    isThis = false;
                                }*/
                                textSeg += "  l.s $f" + regd + ", "+ id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else
                                textSeg += "  li.s $f" + regi + ", " + num2.getValue() + "\n";
                        }
                        else {
                            if (num1 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                                /*String scope = Spaghetti.getScope(id);
                                if(isThis){
                                    scope = Spaghetti.getParentScope(id);
                                    isThis = false;
                                }*/
                                textSeg += "  l.s $f0, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else
                                textSeg += "  li.s $f0, " + num1.getValue() + "\n";
                        }
                        if ("+".equals(sem)) {
                            textSeg += "  add.s $f" + regd + ", $f0, $f" + regd + "\n";
                        }
                        else if ("*".equals(sem)) {
                            textSeg += "  mul.s $f" + regd + ", $f0, $f" + regd + "\n";
                        }
                        else if ("/".equals(sem)) {
                            textSeg += "  div.s $f" + regd + ", $f0, $f" + regd + "\n";
                        }
                        else if ("-".equals(sem)) {
                            textSeg += "  sub.s $f" + regd + ", $f0, $f" + regd + "\n";
                        }
                    }
                    else {
                        regd += 2;
                        if (num2 instanceof DSCP) {
                            String id2 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id2);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id2);
                                isThis = false;
                            }*/
                            textSeg += "  l.s $f2, " + id2 + "_" + Spaghetti.getScope(id2) + "($zero) \n";
                        }
                        else
                            textSeg += "  li.s $f2, " + num2.getValue() + "\n";
                        if (num1 instanceof DSCP) {
                            String id1 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id1);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id1);
                                isThis = false;
                            }*/
                            textSeg += "  l.s $f0, " + id1 + "_" + Spaghetti.getScope(id1) + "($zero) \n";
                        }
                        else
                            textSeg += "  li.s $f0, " + num1.getValue() + "\n";
                        if ("+".equals(sem)) {
                            textSeg += "  add.s $f" + regd + ", $f0, $f2\n";
                        }
                        else if ("*".equals(sem)) {
                            textSeg += "  mul.s $f" + regd + ", $f0, $f2\n";
                        }
                        else if ("/".equals(sem)) {
                            textSeg += "  div.s $f" + regd + ", $f0, $f2\n";
                        }
                        else if ("-".equals(sem)) {
                            textSeg += "  sub.s $f" + regd + ", $f0, $f2\n";
                        }
                    }
                }
                else if (sem.equals("<") || sem.equals(">") || sem.equals("<=") || sem.equals(">=") || sem.equals("==") || sem.equals("!=")) {
                    if (num1 instanceof ArithLogExpr && num2 instanceof ArithLogExpr) { //num1 az samte chap umade o num2 az rast
                        regi += 1;
                        if ("==".equals(sem)) {
                            textSeg += "  c.eq.s $f" + (regd - 2) + ", $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("!=".equals(sem)) {
                            textSeg += "  c.eq.s $f" + (regd - 2) + ", $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("<".equals(sem)) {
                            textSeg += "  c.lt.s $f" + (regd - 2) + ", $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if (">".equals(sem)) {
                            textSeg += "  c.lt.s $f" + (regd - 2) + ", $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("<=".equals(sem)) {
                            textSeg += "  c.le.s $f" + (regd - 2) + ", $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if (">=".equals(sem)) {
                            textSeg += "  c.le.s $f" + (regd - 2) + ", $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        regd = 0;
                    }
                    else if (num1 instanceof ArithLogExpr || num2 instanceof ArithLogExpr) { //vaqti expr taki samte raste
                        regi += 1;
                        if (num1 instanceof ArithLogExpr) {
                            textSeg += "  mov.s $f0, $f" + regd + "\n";
                            if (num2 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                                /*String scope = Spaghetti.getScope(id);
                                if(isThis){
                                    scope = Spaghetti.getParentScope(id);
                                    isThis = false;
                                }*/
                                textSeg += "  l.s $f" + regd + ", "+ id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else
                                textSeg += "  li.s $f" + regi + ", " + num2.getValue() + "\n";
                        }
                        else {
                            if (num1 instanceof DSCP) {
                                String id = (String) SemanticStack.pop();
                                /*String scope = Spaghetti.getScope(id);
                                if(isThis){
                                    scope = Spaghetti.getParentScope(id);
                                    isThis = false;
                                }*/
                                textSeg += "  l.s $f0, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                            }
                            else {
                                textSeg += "  li.s $f0, " + num1.getValue() + "\n";
                            }
                        }

                        if ("==".equals(sem)) {
                            textSeg += "  c.eq.s $f0, $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("!=".equals(sem)) {
                            textSeg += "  c.eq.s $f0, $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("<".equals(sem)) {
                            textSeg += "  c.lt.s $f0, $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if (">".equals(sem)) {
                            textSeg += "  c.lt.s $f0, $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("<=".equals(sem)) {
                            textSeg += "  c.le.s $f0, $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if (">=".equals(sem)) {
                            textSeg += "  c.le.s $f0, $f" + regd + "\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                    }
                    else {
                        regi += 1;
                        if (num2 instanceof DSCP) {
                            String id2 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id2);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id2);
                                isThis = false;
                            }*/
                            textSeg += "  l.s $f2, " + id2 + "_" + Spaghetti.getScope(id2) + "($zero) \n";
                        }
                        else
                            textSeg += "  li.s $f2, " + num2.getValue() + "\n";
                        if (num1 instanceof DSCP) {
                            String id1 = (String) SemanticStack.pop();
                            /*String scope = Spaghetti.getScope(id1);
                            if(isThis){
                                scope = Spaghetti.getParentScope(id1);
                                isThis = false;
                            }*/
                            textSeg += "  l.s $f0, " + id1 + "_" + Spaghetti.getScope(id1) + "($zero) \n";
                        }
                        else
                            textSeg += "  li.s $f0, " + num1.getValue() + "\n";
                        if ("==".equals(sem)) {
                            textSeg += "  c.eq.s $f0, $f2\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("!=".equals(sem)) {
                            textSeg += "  c.eq.s $f0, $f2\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("<".equals(sem)) {
                            textSeg += "  c.lt.s $f0, $f2\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if (">".equals(sem)) {
                            textSeg += "  c.lt.s $f0, $f2\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if ("<=".equals(sem)) {
                            textSeg += "  c.le.s $f0, $f2\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1t istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                        else if (">=".equals(sem)) {
                            textSeg += "  c.le.s $f0, $f2\n";
                            textSeg += "  li $t" + regi + ", 1\n  bc1f istrue" + istruelabel + "\n";
                            textSeg += "  li $t" + regi + ", 0\nistrue" + istruelabel + ":\n";
                            istruelabel++;
                        }
                    }
                }
            }
        }
        else if (sem.equals(";")) {
            regi = 1;
            regd = 2;
        }
        else if (sem.equals("=")) {
            String id = (String) SemanticStack.pop(); //left-hand id
            String type = Spaghetti.getDSCP(id).getType();
           /* String scope = Spaghetti.getScope(id);
            if(isThis){
                scope = Spaghetti.getParentScope(id);
                isThis = false;
            }*/
            if (SemanticStack.isEmpty()) {
                if (type.equals("INT") || type.equals("BOOL"))
                    textSeg += "  sw $t2, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                else if (type.equals("DOUBLE"))
                    textSeg += "  s.s $f4, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
            }
            else { //get right-hand id or constant
                Object semObj = SemanticStack.pop();
                if (semObj instanceof String) {
                    String r_id = (String) semObj;
                    if (type.equals("INT") || type.equals("BOOL")) {
                        textSeg += "  lw $t2, " + r_id + "_" + Spaghetti.getScope(r_id) + "($zero) \n";
                        textSeg += "  sw $t2, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                    }
                    else if (type.equals("DOUBLE")) {
                        textSeg += "  l.s $f4, " + r_id + "_" + Spaghetti.getScope(r_id) + "($zero) \n";
                        textSeg += "  s.s $f4, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                    }
                }
                else {
                    Constant con = (Constant) semObj;
                    if (type.equals("INT") || type.equals("BOOL")) {
                        textSeg += "  li $t2, " + con.getValue() + "\n";
                        textSeg += "  sw $t2, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                    }
                    else if (type.equals("DOUBLE")) {
                        textSeg += "  li.s $f4, " + con.getValue() + "\n";
                        textSeg += "  s.s $f4, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                    }
                }
            }
        }
        else if (sem.equals("Print")) {
            Expression e = (Expression) SemanticStack.pop();
            if (e instanceof Constant) {
                if (e.getType().equals("STRING")) {
                    String val = String.valueOf(e.getValue());
                    dataSeg += "  val" + stringCount + ": .asciiz \"" + val + "\"\n";
                    textSeg += "  li $v0, 4\n";
                    textSeg += "  la $a0, val" + stringCount + "\n  syscall\n";
                    stringCount++;
                }
                else if (e.getType().equals("BOOL")) {
                    textSeg += "  li $a0, " + e.getValue() + "\n";
                    textSeg += "  sub $sp, $sp, 4\n";
                    textSeg += "  sw $ra, 0($sp)\n";
                    textSeg += "  jal printBool\n";
                    textSeg += "  lw $ra, 0($sp)\n";
                    textSeg += "  add $sp, $sp, 4\n";
                }
                else if (e.getType().equals("INT")) {
                    Integer val = (Integer) e.getValue();
                    textSeg += "  li $a0, " + val + "\n";
                    textSeg += "  li $v0, 1\n syscall\n";
                }
                else if (e.getType().equals("DOUBLE")) {
                    Double val = (Double) e.getValue();
                    textSeg += "  li.s $f12, " + val + "\n";
                    textSeg += "  li $v0, 2\n syscall\n";
                }
            }
            else if (e instanceof DSCP) {
                String id = (String) SemanticStack.pop();
                /*String scope = Spaghetti.getScope(id);
                if(isThis){
                    scope = Spaghetti.getParentScope(id);
                    isThis = false;
                }*/
                if (e.getType().equals("STRING")) {
                    textSeg += "  la $a0, " + id + "_" + Spaghetti.getScope(id) + "($zero)\n";
                    textSeg += "  li $v0, 4\n  syscall\n";
                }
                else if (e.getType().equals("BOOL")) {
                    textSeg += "  lw $a0, " + id + "_" + Spaghetti.getScope(id) + "($zero)\n";
                    textSeg += "  sub $sp, $sp, 4\n";
                    textSeg += "  sw $ra, 0($sp)\n";
                    textSeg += "  jal printBool\n";
                    textSeg += "  lw $ra, 0($sp)\n";
                    textSeg += "  add $sp, $sp, 4\n";
                }
                else if (e.getType().equals("INT")) {
                    textSeg += "  lw $a0, " + id + "_" + Spaghetti.getScope(id) + "($zero)\n";
                    textSeg += "  li $v0, 1\n  syscall\n";
                }
                else if (e.getType().equals("DOUBLE")) {
                    textSeg += "  l.s $f12, " + id + "_" + Spaghetti.getScope(id) + "($zero)\n";
                    textSeg += "  li $v0, 2\n  syscall\n";
                }
            }
            else if(e instanceof ArithLogExpr) {
                if (e.getType().equals("BOOL")) {
                    textSeg += "  move $a0, $t2\n";
                    textSeg += "  sub $sp, $sp, 4\n";
                    textSeg += "  sw $ra, 0($sp)\n";
                    textSeg += "  jal printBool\n";
                    textSeg += "  lw $ra, 0($sp)\n";
                    textSeg += "  add $sp, $sp, 4\n";
                }
                else if (e.getType().equals("INT")) {
                    textSeg += "  move $a0, $t2\n";
                    textSeg += "  li $v0, 1\n  syscall\n";
                }
                else if (e.getType().equals("DOUBLE")) {
                    textSeg += "  move.s $f12, $f4\n";
                    textSeg += "  li $v0, 2\n  syscall\n";
                }
            }
        }
        else if(sem.equals("PrintNL")){
            textSeg += "  li $v0, 4\n  la $a0, newline\n  syscall\n";
        }
        else if (sem.equals("varDecl")) {
            String id = (String) SemanticStack.pop();
            String type = (String) SemanticStack.pop();
            dataSeg += "  " + id + "_" + Spaghetti.getScope(id);
            if (type.equals("INT") || type.equals("BOOL"))
                dataSeg += ": .word " + 0 + "\n";
            else if (type.equals("DOUBLE"))
                dataSeg += ": .float " + 0.0 + "\n";
            else if (type.equals("STRING"))
                dataSeg += ": .asciiz " + "\"\"" + "\n";
        }
        else if (sem.equals("endMain")) {
            textSeg += "\n  li $v0, 10\n  syscall\n\n";
        }
        else if (sem.equals("endFunc")) {
            textSeg += "\n  jr $ra\n\n";
        }
        else if (sem.equals("condExpr")) {
            cgen(";");
            Expression e = (Expression) SemanticStack.pop();
            if (e instanceof DSCP) {
                String id = (String) SemanticStack.pop(); //left-hand id
                String type = Spaghetti.getDSCP(id).getType();
                if (!type.equals("BOOL"))
                    throw new SemanticError("type mismatch");
                textSeg += "  lw $t2, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
            }
            else {
                if (!e.getType().equals("BOOL"))
                    throw new SemanticError("type mismatch");
                if (e instanceof Constant) //else: it's ArithLogExpr and already in t2
                    CodeGen.textSeg += "  li $t2, " + e.getValue() + "\n";
            }
        }
        else if (sem.equals("ReadInteger")) {
            textSeg += "  sub $sp, $sp, 4\n";
            textSeg += "  sw $ra, 0($sp)\n";
            textSeg += "  jal ReadInteger\n";
            textSeg += "  lw $ra, 0($sp)\n";
            textSeg += "  add $sp, $sp, 4\n";
            /*textSeg += "  li $v0, 5 \n  syscall\n";
            textSeg += "  move $t2, $v0\n";*/
        }
        else if (sem.equals("ReadLine")) {
            dataSeg += ("  buffer: .space  100 \n");
            textSeg += "  li $v0, 8\n";
            textSeg += "  la $a0, buffer\n  li $a1, 100\n";
            textSeg += "  move $t2, $a0\n  syscall\n";
        }
        else if (sem.equals("ReadDouble")) {
            textSeg += "  li $v0, 6 \n  syscall\n";
            textSeg += "  li.s $f10, 0.0 \n";
            //value of user input --> f10
            textSeg += "  add.s $f4, $f0, $f10 \n";
        }
        else if(sem.equals("dtoi")){
            textSeg += "  cvt.w.s $f4, $f4 \n";
            textSeg += "  mfc1 $t2, $f4\n";
        }
        else if(sem.equals("itod")){
            textSeg += "  mtc1 $t2, $f4\n";
            textSeg += "  cvt.s.w $f4, $f4\n";
        }
        else if(sem.equals("itob")){
            textSeg += "  sub $sp, $sp, 4\n";
            textSeg += "  sw $ra, 0($sp)\n";
            textSeg += "  jal ITOB\n";
            textSeg += "  lw $ra, 0($sp)\n";
            textSeg += "  add $sp, $sp, 4\n";
        }
    }

    public static void compile(Writer writer) throws IOException {
        writer.write(dataSeg + textSeg);
        //System.out.println(dataSeg + textSeg);
    }

}