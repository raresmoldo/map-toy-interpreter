package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CloseRFileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private StringValue fileName;
    private IExp file;

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");
        fileName = new StringValue(f.toString());
        file = new ValueExp(fileName);
        state = TestUtil.emptyState();
    }

    @AfterEach
    void tearDown() throws Exception {
        TestUtil.closeFiles(state);
    }

    @Test
    void removesTheFileFromTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(fileName));
    }

    @Test
    void theReaderIsReallyClosed() throws MyException {
        new OpenRFileStmt(file).execute(state);
        BufferedReader reader = state.getFileTable().lookup(fileName);
        new CloseRFileStmt(file).execute(state);
        assertThrows(IOException.class, reader::readLine);
    }

    @Test
    void closingAFileThatIsNotOpenThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new CloseRFileStmt(file).execute(state));
        assertTrue(ex.getMessage().contains("is not open"));
    }

    @Test
    void closingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
    }

    @Test
    void nonStringExpressionThrows() {
        assertThrows(StatementException.class,
                () -> new CloseRFileStmt(new ValueExp(new IntValue(1))).execute(state));
    }

    @Test
    void aClosedFileCanBeOpenedAgain() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void toStringShowsTheExpression() {
        assertEquals("closeRFile(f)", new CloseRFileStmt(new VarExp("f")).toString());
    }
}