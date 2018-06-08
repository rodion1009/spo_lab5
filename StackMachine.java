import java.util.ArrayList;
import java.util.Stack;

public class StackMachine {
    VariableTable table;
    ArrayList<Token> tokens;
    Stack<Token> stack = new Stack<>();

    public StackMachine(VariableTable table, ArrayList<Token> tokens) {
        this.table = table;
        this.tokens = new ArrayList<>(tokens);
    }

    private Token calcArithmOp(String operation) throws Exception {
        Token tokenOp2 = stack.pop();
        Token tokenOp1 = stack.pop();

        if (tokenOp1.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp1.getText()).equals("list") || table.getVariableType(tokenOp1.getText()).equals("set")) {
                throw new Exception("Ошибка! Несовместимость типов в операции " + operation);
            }
        }

        if (tokenOp2.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp2.getText()).equals("list") || table.getVariableType(tokenOp2.getText()).equals("set")) {
                throw new Exception("Ошибка! Несовместимость типов в операции " + operation);
            }
        }

        double numOp1 = tokenOp1.getType().equals("VAR") ? (double) table.getVariableValue(tokenOp1.getText()) :
                Double.valueOf(tokenOp1.getText());
        double numOp2 = tokenOp2.getType().equals("VAR") ? (double) table.getVariableValue(tokenOp2.getText()) :
                Double.valueOf(tokenOp2.getText());
        double result = 0.0;

        switch (operation) {
            case "+":
                result = numOp1 + numOp2;
                break;
            case "-":
                result = numOp1 - numOp2;
                break;
            case "*":
                result = numOp1 * numOp2;
                break;
            case "/":
                result = numOp1 / numOp2;
                break;
        }

        //Если дробная часть равна 0, то отбросить её
        return (result - (int)result) == 0 ? new Token(String.valueOf((int)result), "CONST_INT") :
                new Token(String.valueOf(result), "CONST_FLOAT");
    }

    private Token calcCompOp(String operation) throws Exception {
        Token tokenOp2 = stack.pop();
        Token tokenOp1 = stack.pop();

        if (tokenOp1.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp1.getText()).equals("list") || table.getVariableType(tokenOp1.getText()).equals("set")) {
                throw new Exception("Ошибка! Несовместимость типов в операции " + operation);
            }
        }

        if (tokenOp2.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp2.getText()).equals("list") || table.getVariableType(tokenOp2.getText()).equals("set")) {
                throw new Exception("Ошибка! Несовместимость типов в операции " + operation);
            }
        }

        double numOp1 = tokenOp1.getType().equals("VAR") ? (double) table.getVariableValue(tokenOp1.getText()) :
                Double.valueOf(tokenOp1.getText());
        double numOp2 = tokenOp2.getType().equals("VAR") ? (double) table.getVariableValue(tokenOp2.getText()) :
                Double.valueOf(tokenOp2.getText());

        String result = "";
        switch (operation) {
            case ">":
                result = String.valueOf(numOp1 > numOp2);
                break;
            case "<":
                result = String.valueOf(numOp1 < numOp2);
                break;
            case "==":
                result = String.valueOf(numOp1 == numOp2);
                break;
            case ">=":
                result = String.valueOf(numOp1 >= numOp2);
                break;
            case "<=":
                result = String.valueOf(numOp1 <= numOp2);
                break;
            case "!=":
                result = String.valueOf(numOp1 != numOp2);
                break;
        }

        return new Token(result, "");
    }

    private void assign() throws Exception {
        Double value = Double.valueOf(stack.pop().getText());
        String name = stack.pop().getText();
        table.addVariable(name, "number", value);
    }

    public void calculate() throws Exception {
        Token currentToken;
        for (int i = 0; i < tokens.size(); i++) {
            currentToken = tokens.get(i);
            switch (currentToken.getType()) {
                case "VAR":
                case "CONST_INT":
                case "CONST_FLOAT":
                case "LABEL_START":
                case "LABEL_END":
                case "STRUCT_NAME":
                    stack.push(currentToken);
                    break;
                case "ARITHMETIC_OP":
                    stack.push(calcArithmOp(currentToken.getText()));
                    break;
                case "COMP_OP":
                    stack.push(calcCompOp(currentToken.getText()));
                    break;
                case "ASSIGN_OP":
                    assign();
                    break;
                case "GOTO":
                    Token it = stack.pop();
                    i = it.getType().equals("LABEL_START") || it.getType().equals("LABEL_END") ?
                            Integer.valueOf(it.getText()) - 1 : -1;
                    break;
                case "GOTO_BY_FALSE":
                    it = stack.pop();
                    if (stack.pop().getText().equals("false")) {
                        i = Integer.valueOf(it.getText()) - 1;
                    }
                    break;
                case "PRINT_KW":
                    Token token = stack.pop();
                    String arg = token.getType().equals("VAR") ?
                            table.getVariableValue(token.getText()).toString() : token.getText();
                    System.out.println(arg);
                    break;
                case "IS_KW":
                    String type = stack.pop().getText();
                    String name = stack.pop().getText();
                    switch (type) {
                        case "list":
                            table.addVariable(name, type, new MyLinkedList());
                            break;
                        case "set":
                            table.addVariable(name, type, new MyHashSet());
                            break;
                    }
                    break;
                case "OPERATION_KW":
                    Token tokenOperand = stack.pop();
                    Object operand;
                    switch (tokenOperand.getType()) {
                        case "CONST_INT":
                            operand = Integer.valueOf(tokenOperand.getText());
                            break;
                        case "CONST_FLOAT":
                            operand = Double.valueOf(tokenOperand.getText());
                            break;
                        default:
                            operand = table.getVariableValue(tokenOperand.getText());
                            break;
                    }

                    String structName = stack.pop().getText();
                    MyStructure struct = (MyStructure) table.getVariableValue(structName);
                    switch (currentToken.getText()) {
                        case "add":
                            struct.add(operand);
                            break;
                        case "has":
                            if (struct.contains(operand)) {
                                System.out.println(structName + " has " + tokenOperand.getText());
                            } else {
                                System.out.println(structName + " has no " + tokenOperand.getText());
                            }
                            break;
                        case "remove":
                            try {
                                struct.remove(Integer.valueOf(operand.toString()));
                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("Элемента с индексом " + operand + " не существует в списке "
                                        + structName);
                                return;
                            }
                            break;
                    }
                    break;
                case "GET":
                    Integer index = Integer.valueOf(stack.pop().getText());
                    MyLinkedList list = null;
                    list = (MyLinkedList) table.getVariableValue(stack.pop().getText());
                    try {
                        stack.push(new Token(String.valueOf(list.get(index)), "CONST_FLOAT"));
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Индекс " + index + " отсутствует в списке");
                         return;
                    }
                    break;
            }
        }
    }
}
