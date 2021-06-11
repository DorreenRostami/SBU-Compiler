package Model.AST.expression;

import Model.cg.*;
import Model.error.SemanticError;
import Model.scope.*;

public class Assignment {

    public static void assign(String id, Expression e) throws SemanticError {
        //o.w if e is an ArithLogExpr => computed in $t2 or $f4,
        //o.w it's an id which was pushed to semanticStack before
        //or a Constant which has to be pushed now
        DSCP old = Spaghetti.getDSCP(id);
        if(old != null && e.getType().equals(old.getType()))
        {
            if(e instanceof Constant)
                SemanticStack.push(e);
            SemanticStack.push(id);
            CodeGen.cgen("=");
        }
        else
        {
            if(old == null)
                throw new SemanticError("variable " + id + " doesn't exist");
            else
                throw new SemanticError("type mismatch");

        }
    }
}
