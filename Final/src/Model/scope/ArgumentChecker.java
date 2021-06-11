package Model.scope;

import Model.AST.expression.*;
import Model.cg.CodeGen;
import Model.cg.SemanticStack;
import Model.error.SemanticError;

public class ArgumentChecker {
    private static String id;
    private static int counter;
    private static FunctionDSCP fdscp;
    private static int size;
    private static int argsp;

    public static void callReset(String i) {
        id = i;
        counter = 0;
        fdscp = Spaghetti.getFunctionDSCP(id);
        size = fdscp.getArgumentSize();
        argsp = size + 4;
        if (size > 0)
            CodeGen.textSeg += "  sub $sp, $sp, " + argsp + "\n";
    }

    public static void declReset(String i) {
        id = i;
        counter = 0;
        fdscp = Spaghetti.getFunctionDSCP(id);
        size = fdscp.getArgumentSize();
        argsp = size + 4;
    }

    public static void checkArgument(Expression e) throws SemanticError {
        if (counter >= fdscp.arguments.size())
            throw new SemanticError("Argument count mismatch");
        String type = fdscp.getArgumentType(counter);
        String etype = e.getType();
        if (type.equals(etype)) {
            if (e instanceof DSCP) {
                String id = (String) SemanticStack.pop();
                if (etype.equals("INT") || etype.equals("BOOL"))
                    CodeGen.textSeg += "  lw $t2, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
                else if (etype.equals("DOUBLE"))
                    CodeGen.textSeg += "  l.d $f4, " + id + "_" + Spaghetti.getScope(id) + "($zero) \n";
            }
            else if (e instanceof Constant) {
                if (etype.equals("INT") || etype.equals("BOOL"))
                    CodeGen.textSeg += "  li $t2, " + e.getValue() + "\n";
                else if (etype.equals("DOUBLE"))
                    CodeGen.textSeg += "  li.d $f4, " + e.getValue() + "\n";
            }
            if (type.equals("INT") || type.equals("BOOL")) {
                argsp -= 4;
                CodeGen.textSeg += "  sw $t2, " + argsp + "($sp)\n";
            }
            else if (type.equals("DOUBLE")) {
                argsp -= 8;
                CodeGen.textSeg += "  s.d $f4, " + argsp + "($sp)\n";
            }
            counter++;
            return;
        }
        throw new SemanticError("Argument type mismatch");
    }

    public static void popArguments() throws SemanticError {
        for (; counter < fdscp.arguments.size(); counter++) {
            String type = fdscp.getArgumentType(counter);
            if (type.equals("INT") || type.equals("BOOL")) {
                argsp -= 4;
                CodeGen.textSeg += "  lw $t2, " + argsp + "($sp)\n";
            }
            else if (type.equals("DOUBLE")) {
                argsp -= 8;
                CodeGen.textSeg += "  l.d $f4, " + argsp + "($sp)\n";
            }
            String name = fdscp.getArgumentName(counter);
            SemanticStack.push(name);
            CodeGen.cgen("=");
        }
        if (size > 0)
            CodeGen.textSeg += "  add $sp, $sp, " + size + "\n";
    }

    public static void completeArgs() throws SemanticError {
        if (counter != fdscp.arguments.size())
            throw new SemanticError("Argument count mismatch");
        String type = fdscp.getType();
        CodeGen.textSeg += "  sw $ra, 0($sp)\n";
        CodeGen.textSeg += "  jal " + id + "\n";
        CodeGen.textSeg += "  lw $ra, 0($sp)\n";
        CodeGen.textSeg += "  add $sp, $sp, 4\n";
        if (type.equals("INT") || type.equals("BOOL"))
            CodeGen.textSeg += "  move $t2, $v0\n";
        else if (type.equals("DOUBLE"))
            CodeGen.textSeg += "  l.d $f4, 0($sp)\n";
    }
}
