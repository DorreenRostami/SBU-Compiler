package Model.AST.expression;

import Model.cg.SemanticStack;

public class ArithLogExpr extends Expression {
    public ArithLogExpr(Expression expr1, Expression expr2, String type) {
        super(type);
        SemanticStack.push(expr1);
        SemanticStack.push(expr2);
    }

    public ArithLogExpr(String type) {
        super(type);
    }
}
