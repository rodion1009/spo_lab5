import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws Exception {
        String ex1 =
                "a = 0;" +
                "b = 2;" +
                "while (a < b) {" +
                    "while (2 < 1) {" +
                        "b = b - 1;" +
                    "}" +
                    "while (1 == 2) {" +
                        "b = 0;" +
                    "}" +
                    "a = a + 1;" +
                "}" +
                "print a;";
        String ex2 = "a = 0;" + "for (i = 0; i < 10; i = i + 2) {" + "a = a + 1;" + "}" + "print a;";
        String ex3 =
                "a = 6;" +
                    "if (a > (2 + 3)) {" +
                    "a = a + 2; print (2 + 3);" +
                "}" + "";
        String ex4 = "let a is list; let b is set; print a; print b; print (1+2);";
        String ex5 = "let a is list; a add 1; a add 2; a add 3; print a; a has 3; a remove 2; a has 3; print a; print (a get 10);";
        String ex6 = "let a is set; a add 1; a add 1; a add 1; print a; a add 2; print a; a has 1; a has 2; a has 3;" +
                "a remove 1; print a; a has 2; a has 1;";
        String ex7 = "for (i = 0; i < 5; i = i + 1) {print i;}";
        String ex8 = "c = 1; b = 2; let a is list; a add 5; a add 6; print (b + c);";
        String ex9 = "let l is list; l add 5; l add 6; l add 7; print l get 2;";
        String ex10 = "let a is list; b = 0; c = a + b;";
        String fibonacci = "let a is list; a add 0; a add 1; n = 10; for (i = 2; i < (n + 1); i = i + 1)" +
                "{a add ((a get (i - 1)) + (a get (i - 2)));} print a; let b is list; b add 100; print b;";

        Queue<Token> tokens = Lexer.getTokenList(fibonacci);

        Parser parser = new Parser();
        if (!parser.parse(tokens)) {
            return;
        }

        Poliz poliz = new Poliz();
        ArrayList<Token> p = poliz.toPoliz((LinkedList<Token>) tokens);

        StackMachine sm = new StackMachine(parser.getTable(), p);
        sm.calculate();
    }
}
