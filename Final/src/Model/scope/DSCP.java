package Model.scope;

import Model.AST.expression.Expression;

public class DSCP extends Expression {

    protected String accessMode;

    public DSCP(String type) {
        super(type);
    }

    public DSCP(String type, String accessMode) {
        super(type);
        this.accessMode = accessMode;
    }
    
    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }
}
