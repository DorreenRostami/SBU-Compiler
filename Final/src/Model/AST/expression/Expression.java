package Model.AST.expression;

public abstract class Expression {
    public String type;
    public Object value;

    public Expression(String type){
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }
}
