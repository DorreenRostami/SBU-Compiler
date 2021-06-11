package Model.error;

public class SemanticError extends Exception {
    public SemanticError(String errorMessage) {
        super(errorMessage);
    }
}