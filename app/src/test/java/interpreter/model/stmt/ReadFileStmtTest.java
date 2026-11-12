package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReadFileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private IExp file;

    private IExp fileWith(String name, String content) throws Exception {
        Path f = dir.resolve(name);
        Files.writeString(f, content);
        return new ValueExp(new StringValue(f.toString()));
    }

    @BeforeEach
    void setUp() throws Exception {
        file = fileWith("numbers.in", "7\n8\n");
        state = TestUtil.emptyState();
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void tearDown() throws Exception {
        TestUtil.closeFiles(state);
    }

    @Test
    void readsTheNumbersInOrderThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(8), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void anEmptyFileGivesZero() throws Exception {
        IExp empty = fileWith("empty.in", "");
        new OpenRFileStmt(empty).execute(state);
        new ReadFileStmt(empty, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void surroundingWhitespaceIsIgnored() throws Exception {
        IExp spaced = fileWith("spaced.in", "  42 \n");
        new OpenRFileStmt(spaced).execute(state);
        new ReadFileStmt(spaced, "x").execute(state);
        assertEquals(new IntValue(42), state.getSymTable().lookup("x"));
    }

    @Test
    void undeclaredVariableThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(file, "nope").execute(state));
        assertEquals("readFile: variable nope was not declared", ex.getMessage());
    }

    @Test
    void variableOfAnotherTypeThrows() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        new OpenRFileStmt(file).execute(state);
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(file, "b").execute(state));
        assertEquals("readFile: variable b is not an int", ex.getMessage());
    }

    @Test
    void nonStringExpressionThrows() {
        assertThrows(StatementException.class,
                () -> new ReadFileStmt(new ValueExp(new IntValue(1)), "x").execute(state));
    }

    @Test
    void fileThatIsNotOpenThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(file, "x").execute(state));
        assertTrue(ex.getMessage().contains("is not open"));
    }

    @Test
    void lineThatIsNotAnIntegerThrows() throws Exception {
        IExp bad = fileWith("bad.in", "abc\n");
        new OpenRFileStmt(bad).execute(state);
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(bad, "x").execute(state));
        assertTrue(ex.getMessage().contains("is not an integer"));
    }

    @Test
    void failedReadKeepsTheOldValue() throws Exception {
        state.getSymTable().update("x", new IntValue(5));
        IExp bad = fileWith("bad.in", "abc\n");
        new OpenRFileStmt(bad).execute(state);
        assertThrows(StatementException.class,
                () -> new ReadFileStmt(bad, "x").execute(state));
        assertEquals(new IntValue(5), state.getSymTable().lookup("x"));
    }

    @Test
    void toStringShowsTheExpressionAndTheVariable() {
        assertEquals("readFile(f, x)", new ReadFileStmt(new VarExp("f"), "x").toString());
    }
}