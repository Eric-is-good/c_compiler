package frontend;

import ir.Value;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Scope keeps track of the current scope in the source during traversal of
 * the parse tree with Visitor, maintaining all symbol tables needed by functions
 * and nested code blocks in the source (as well as other semantic info of that
 * environment for looking up).
 * <br>
 * Each Scope can trace a compiling process for a single compile unit (a module).
 * Thus, in our case we only need one instance of Scope.
 * <br>
 * This class is NOT similar to the LLVM Clang Scope, which represents only one layer
 * of the scoping hierarchy (a single symbol table in the stack).
 */
public class Scope {

    /**
     * A list as a stack containing all symbol tables.
     * The top element is the symbol table of the current scope.
     */
    private final ArrayList<HashMap<String, Value>> tables = new ArrayList<>();


    public Scope() {
        // Push the first symbol table (the top/global scope) for the module as initialization.
        tables.add(new HashMap<>());
    }


    /**
     * Peek the top of the symbol table stack, which is the current scope (during parse
     * tree traversal / visiting)
     * @return The symbol table of current scope.
     */
    private HashMap<String, Value> curTab() {
        return tables.get(tables.size() - 1);
    }

    /**
     * If the current scope is the top scope.
     * @return True if currently under the global scope. Otherwise, return false.
     */
    public boolean isGlobal() {
        return tables.size() == 1;
    }

    /**
     * Push a new symbol table onto the stack when scoping.
     */
    public void scopeIn() {
        tables.add(new HashMap<>());
    }

    /**
     * Pop out the top symbol table from the stack when current scope exits.
     */
    public void scopeOut() {
        if (this.isGlobal()) {
            throw new RuntimeException("Try to exit the top (global) scope.\n");
        }
        else {
            tables.remove(tables.size() - 1);
        }
    }

    /**
     * Add a new name-value pair into the symbol table of current scope.
     * @param name The name of the pair.
     * @param val The value of the pair.
     */
    public void addDecl(String name, Value val) {
        // Check name repetition. (Security check)
        if(this.duplicateDecl(name)) {
            throw new RuntimeException(String.format(
                    "Try to add an declaration with an existing name \"%s\" into current symbol table.",
                    name
            ));
        }
        // If it's a new name.
        curTab().put(name, val);
    }

    /**
     * Find the variable (value) with the given name that can be used in current scope.
     * The variable found may be defined in an outer scope.
     * NOTICE: Use duplicateDecl() for duplication check when defining / declaring
     * a new variable name.
     * @param name The name to be used for searching.
     * @return The object with the name. Return null if no matched object is found.
     */
    public Value getValByName(String name) {
        // Search the object from current layer of scope to the outer ones.
        for (int i = tables.size() - 1; i >= 0; i--) {
            Value val = tables.get(i).get(name);
            if(val != null)
                return val;
        }
        return null;
    }

    /**
     * Check if the given name has been declared in current scope (in current layer of
     * symbol table).
     * @param name The name (identifier) to be checked.
     * @return True if the name has already been in current layer of symbol table. False
     * otherwise.
     */
    public boolean duplicateDecl(String name) {
        return curTab().get(name) != null;
    }

}
