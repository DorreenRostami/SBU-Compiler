package Model.AST.expression;

public class Constant extends Expression{
    public Constant(Object value, String type) {
        super(type);
        this.value = value;
    }
}
