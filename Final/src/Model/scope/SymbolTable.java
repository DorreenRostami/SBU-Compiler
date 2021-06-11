package Model.scope;

import java.util.HashMap;

import Model.error.*;

public class SymbolTable {

    public HashMap<String, DSCP> table;
    public String scopeName;
    public SymbolTable parent;

    public SymbolTable(String scopeName, SymbolTable parent) {
        this.table = new HashMap<>();
        this.scopeName = scopeName;
        this.parent = parent;
    }

    public void addEntry(String entry, DSCP dscp) {
        table.put(entry, dscp);
    }

    public DSCP getVarDSCP(String entry) {
        SymbolTable temp = this;
        if (temp.table.get(scopeName) instanceof FunctionDSCP) {
            FunctionDSCP funcd = (FunctionDSCP) temp.table.get(scopeName);
            if (funcd != null) {
                String t = funcd.getArgumentType(entry);
                if (t != null)
                    return new DSCP(t);
            }
        }
        while (!temp.table.containsKey(entry)) {
            if (parent != null)
                temp = temp.parent;
            else
                return null;
        }
        return temp.table.get(entry);
    }

    public FunctionDSCP getFuncDSCP() {
        return (FunctionDSCP) this.table.get(scopeName);
    }

    public void setAccessMode(String entry, String accessMode) {
        SymbolTable temp = this;
        while (!temp.table.containsKey(entry))
            temp = temp.parent;

        DSCP dscp = temp.table.get(entry);
        dscp.setAccessMode(accessMode);
        temp.table.replace(entry, dscp);
    }

    public String getScope(String id) {
        SymbolTable temp = this;
        if (temp.table.get(scopeName) instanceof FunctionDSCP) {
            FunctionDSCP funcd = (FunctionDSCP) temp.table.get(scopeName);
            if (funcd != null) {
                String t = funcd.getArgumentType(id);
                if (t != null)
                    return temp.scopeName;
            }
        }
        while (!temp.table.containsKey(id))
            temp = temp.parent;
        return temp.scopeName;
    }

    public void addArgToDSCP(String id, String type) throws SemanticError {
        this.getFuncDSCP().addArgument(id, type);
    }

    public String getReturnType() {
        return this.getFuncDSCP().getType();
    }
}