package Model.scope;

import java.util.HashMap;

public class Spaghetti {
    public static final HashMap<String, SymbolTable> map = new HashMap<>();
    public static SymbolTable current;
    public static SymbolTable global;
    public static String currIDwoAccessMode;

    static {
        global = new SymbolTable("global", null);
        current = global;
    }

    public static void enterScope(String name) {
        if (map.containsKey(name))
            current = map.get(name);
        else {
            SymbolTable temp = new SymbolTable(name, current);
            map.put(name, temp);
            current = temp;
        }
    }

    public static void exitScope() {
        current = current.parent;
    }

    public static void addEntry(String entry, DSCP dscp) {
        current.addEntry(entry, dscp);
    }

    public static DSCP getDSCP(String entry) {
        return current.getVarDSCP(entry);
    }

    public static DSCP getParentDSCP(String entry) {
        return current.parent.getVarDSCP(entry);
    }

    public static void setAccessMode(String entry, String accessMode) {
        current.setAccessMode(entry, accessMode);
    }

    public static FunctionDSCP getFunctionDSCP(String id) {
        return map.get(id).getFuncDSCP();
    }

    public static String getScope(String id) {
        return current.getScope(id);
    }

    public static String getParentScope(String id) {
        return current.parent.getScope(id);
    }
}
