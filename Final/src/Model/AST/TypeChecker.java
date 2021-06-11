package Model.AST;

import Model.error.SemanticError;

public class TypeChecker {
    public static boolean hasArithCompOp(String type1, String type2) throws SemanticError // -, *, /, >, >=, <, <=
    {
        if (type1.equals(type2) && (type1.equals("INT") || type1.equals("DOUBLE")))
            return true;
        else
            throw new SemanticError("type mismatch");
    }

    public static boolean hasModOp(String type1, String type2) throws SemanticError // %
    {
        if (type1.equals(type2) && (type1.equals("INT")))
            return true;
        else
            throw new SemanticError("type mismatch");
    }

    public static boolean hasSumOp(String type1, String type2) throws SemanticError //+
    {
        if (type1.equals(type2) && !type1.equals("BOOL"))
            return true;
        else
            throw new SemanticError("type mismatch");
    }

    public static boolean hasEqualOp(String type1, String type2) throws SemanticError // ==, !=
    {
        if (type1.equals(type2))
            return true;
        else
            throw new SemanticError("type mismatch");
    }

    public static boolean hasLogicOp(String type1, String type2) throws SemanticError // &&, ||, !
    {
        if (type1.equals(type2) && type1.equals("BOOL"))
            return true;
        else
            throw new SemanticError("type mismatch");
    }

    public static boolean hasPrintOp(String type) throws SemanticError {
        if (type.equals("BOOL") || type.equals("INT") || type.equals("STRING") || type.equals("DOUBLE"))
            return true;
        else
            throw new SemanticError("type mismatch");
    }

    public static boolean canReturn(String t1, String t2) throws SemanticError {
        if(t1.equals(t2))
            return true;
        throw new SemanticError("type mismatch");
    }
}
