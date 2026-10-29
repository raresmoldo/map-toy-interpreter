package interpreter;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;

public final class TestUtil {
    private TestUtil() { }

    /** A fresh program state whose ExeStack is empty (the initial program is popped) */
    public static PrgState emptyState() {
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(),
                new MyList<>(), new NopStmt());
        try {
            state.getExeStack().pop();
        } catch (MyException e) {
            throw new IllegalStateException(e);
        }
        return state;
    }

    /** A fresh program state with the given program on the ExeStack (as at the start of a run). */
    public static PrgState stateFor(IStmt program) {
        return new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), program);
    }
}