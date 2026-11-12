package interpreter;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;

/** Helpers shared by the tests. */
public final class TestUtil {
    private TestUtil() { }

    /** A fresh program state whose ExeStack is empty (the initial program is popped). */
    public static PrgState emptyState() {
        PrgState state = PrgState.of(new NopStmt());
        try {
            state.getExeStack().pop();
        } catch (MyException e) {
            throw new IllegalStateException(e);
        }
        return state;
    }

    /** A fresh program state with the given program on the ExeStack (as at the start of a run). */
    public static PrgState stateFor(IStmt program) {
        return PrgState.of(program);
    }

    /** Closes the readers still open in the FileTable, so the temporary folders can be deleted. */
    public static void closeFiles(PrgState state) throws Exception {
        for (var reader : state.getFileTable().toMap().values()) reader.close();
    }
}