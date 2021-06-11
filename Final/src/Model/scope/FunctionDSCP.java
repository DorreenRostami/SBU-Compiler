package Model.scope;

import Model.error.SemanticError;

import java.util.ArrayList;

public class FunctionDSCP extends DSCP {

    public ArrayList<argPair> arguments = new ArrayList<>();

    public FunctionDSCP(String type) {
        super(type); //return type

    }

    public FunctionDSCP(String type, String accessMode) {
        super(type, accessMode); //return type
    }

    public void addArgument(String id, String type) throws SemanticError {
        for (argPair i : arguments) {
            if (id.equals(i.getName()))
                throw new SemanticError("argument " + id + " already declared");
        }
        this.arguments.add(new argPair(id, type));
    }

    public String getArgumentType(String id) {
        for (int i = 0; i < arguments.size(); i++) {
            if (id.equals(arguments.get(i).getName()))
                return arguments.get(i).getType();
        }
        return null;
    }

    public String getArgumentType(int i) {
        return this.arguments.get(i).getType();
    }


    public String getArgumentName(int i) {
        return this.arguments.get(i).getName();
    }

    public int getArgumentSize() {
        int ans = 0;
        for (argPair a : arguments) {
            ans += 4;
            if (a.getType().equals("DOUBLE")) {
                ans += 4;
            }
        }
        return ans;
    }

}

class argPair {
    String name;
    String type;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public argPair(String name, String type) {
        this.name = name;
        this.type = type;
    }
}