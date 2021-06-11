package Model.cg;

import java.util.Stack;

public class SemanticStack {
    private static final Stack <Object> stack = new Stack<>();

    public static void push(Object obj){
        stack.push(obj);
    }

    public static Object pop(){
        return stack.pop();
    }

    public static Object peek(){
        return stack.peek();
    }

    public static boolean isEmpty(){
        return stack.isEmpty();
    }

    public static void printStack(){
        for (var val: stack) {
            System.out.println("-> "+val);
        }
    }
}
