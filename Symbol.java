package compiler;

public class Symbol {
    public final String content;
    public final Token token;

    public Symbol(Token token, Object content) {
        this.content = content.toString();
        this.token = token;
    }
}
