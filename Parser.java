import java.util.*;

public class Parser {
    private Queue<Token> tokens;
    private Queue<Token> expr = new LinkedList<>();
    private Token currentToken;
    private VariableTable table = new VariableTable();
    private boolean success = true;

    public VariableTable getTable() {
        return table;
    }

    private void checkToken(String appropriateType) {
        match();
        String currentTokenType = currentToken.getType();
        if (!currentTokenType.equals(appropriateType)) {
            error(currentTokenType, appropriateType);
        }
    }

    private void error(String currentTokenType, String appropriateType) {
        System.out.printf("Ошибка: ожидается %s, найден %s\n", appropriateType, currentTokenType);
        success = false;
    }

    private void match() {
        currentToken = tokens.poll();
        expr.add(currentToken);
    }

    public boolean parse(Queue<Token> t) {
        tokens = new LinkedList<>(t);

        while (!tokens.isEmpty() && success) {
            lang();
        }

        return success;
    }

    private void lang() {
        expr();
    }

    private void expr() {
        Queue<Token> copy = new LinkedList<>(tokens);
        Token t = copy.poll();
        switch (t.getType()) {
            case "VAR":
                switch (copy.poll().getType()) {
                    case "ASSIGN_OP":
                        assignExpr();
                        break;
                    case "OPERATION_KW":
                        structureOperation();
                        break;
                    default:
                        error(t.getType(), "VAR | OPERATION_KW");
                        break;
                }
                break;
            case "IF_KW":
                ifStatement();
                break;
            case "WHILE_KW":
            case "FOR_KW":
                anyLoop();
                break;
            case "LET_KW":
                structureDeclaration();
                break;
            case "PRINT_KW":
                printSt();
                break;
            default:
                error(t.getType(), "VAR | loop");
        }
    }

    private void anyLoop() {
        String type = tokens.peek().getType();
        switch (type) {
            case "WHILE_KW":
                whileLoop();
                break;
            case "FOR_KW":
                forLoop();
                break;
            default:
                error(type, "WHILE_KW | FOR_KW");
        }
    }

    private void whileLoop() {
        whileKw();
        conditionInBr();
        body();
    }

    private void conditionInBr() {
        openBracket();
        condition();
        closeBracket();
    }

    private void condition() {
        operand();
        compOp();
        operand();
    }

    private void body() {
        Token t;
        boolean stop = false;

        openBrace();
        while (!stop && success) {
            Queue<Token> copy = new LinkedList<>(tokens);
            t = copy.poll();
            switch (t.getType()) {
                case "VAR":
                    switch (copy.poll().getType()) {
                        case "ASSIGN_OP":
                            assignExpr();
                            break;
                        case "OPERATION_KW":
                            structureOperation();
                    }
                    break;
                case "WHILE_KW":
                case "FOR_KW":
                    anyLoop();
                    break;
                case "IF_KW":
                    ifStatement();
                    break;
                case "LET_KW":
                    structureDeclaration();
                    break;
                case "CLOSE_BRACE":
                    closeBrace();
                    stop = true;
                    break;
                case "PRINT_KW":
                    printSt();
                    break;
                default:
                    error(t.getType(), "VAR | WHILE_KW | FOR_KW | IF_KW | LET_KW");
                    break;
            }
        }
    }

    private void forLoop() {
        forKw();
        forStatements();
        body();
    }

    private void forStatements() {
        openBracket();
        assignExpr();
        condition();
        exprEnd();
        indexChange();
        closeBracket();
    }

    private void indexChange() {
        var();
        assignOp();
        assignValue();
    }

    private void ifStatement() {
        ifKw();
        conditionInBr();
        body();
    }

    private void printSt() {
        printKw();
        operand();
        exprEnd();
    }

    private void structureDeclaration() {
        letKw();
        var();
        isKw();
        structName();
        exprEnd();
    }

    private void getOperation() {
        var();
        get();
        operand();
    }

    private void structureOperation() {
        var();
        operationKw();
        operand();
        exprEnd();
    }

    private void assignExpr() {
        var();
        //Добавление переменной в таблицу переменных
        try {
            table.addVariable(currentToken.getText(), "number", "0");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assignOp();
        assignValue();
        exprEnd();
    }

    private void assignValue() {
        arithmExpr();
    }

    private void inBr() {
        openBracket();
        arithmExpr();
        closeBracket();
    }

    private void arithmExpr() {
        operand();
        while (tokens.peek() != null && tokens.peek().getType().equals("ARITHMETIC_OP")) {
            arOp();
            operand();
        }
    }

    private void operand() {
        Queue<Token> t = new LinkedList<>(tokens);
        if (t.poll().getType().equals("OPEN_BRACKET")) {
            inBr();
        } else {
            if (t.poll().getType().equals("GET")) {
                getOperation();
            } else {
                singleOperand();
            }
        }
    }

    private void singleOperand() {
        Token token = tokens.peek();
        switch (token.getType()) {
            case "VAR":
                var();
                break;
            case "CONST_INT":
                constInt();
                break;
            case "CONST_FLOAT":
                constFloat();
                break;
            default:
                error(token.getType(), "VAR | CONST_INT | CONST_FLOAT");
                tokens.remove();
        }
    }

    private void var() {
        checkToken("VAR");
    }

    private void assignOp() {
        checkToken("ASSIGN_OP");
    }

    private void openBracket() {
        checkToken("OPEN_BRACKET");
    }

    private void closeBracket() {
        checkToken("CLOSE_BRACKET");
    }

    private void arOp() {
        checkToken("ARITHMETIC_OP");
    }

    private void constInt() {
        checkToken("CONST_INT");
    }

    private void constFloat() {
        checkToken("CONST_FLOAT");
    }

    private void compOp() {
        checkToken("COMP_OP");
    }

    private void openBrace() {
        checkToken("OPEN_BRACE");
    }

    private void closeBrace() {
        checkToken("CLOSE_BRACE");
    }

    private void whileKw() {
        checkToken("WHILE_KW");
    }

    private void forKw() {
        checkToken("FOR_KW");
    }

    private void ifKw() {
        checkToken("IF_KW");
    }

    private void letKw() {
        checkToken("LET_KW");
    }

    private void isKw() {
        checkToken("IS_KW");
    }

    private void printKw() {
        checkToken("PRINT_KW");
    }

    private void structName() {
        checkToken("STRUCT_NAME");
    }

    private void operationKw() {
        checkToken("OPERATION_KW");
    }

    private void get() {
        checkToken("GET");
    }

    private void exprEnd() {
        checkToken("EXPR_END");
    }
}
