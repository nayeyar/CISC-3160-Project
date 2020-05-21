import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Scanner;

public class ExpEvaluator {

    private String s;
    private int currIndex;
    private char inputToken;
    private HashMap<String, Integer> hmap = new HashMap<String, Integer>();
    //  HashMap <key,value>

    void expEvaluator(String s) {
        this.s = s.replaceAll("\\s", "");     // removing whitespace from s String
        currIndex = 0;
        nextToken();

    }

    void nextToken() {                                                  // get the next element of the String
        char c;
        if (!s.endsWith(";"))                                           // check the string if it ends with ;
            throw new RuntimeException("Missing ';' token exptected");  // and throw RuntimeException if ; is missing
        c = s.charAt(currIndex++);                                      // increment the index for next Character

        inputToken = c;
    }

    void match(char token) {                                      // checking for Parenthesis
        if (inputToken == token) {
            nextToken();
        } else {
            throw new RuntimeException("Missing Parenthesis");    // throw a RuntimeException if the closing Parenthesis is missing
        }
    }

    int eval() {                      // evaluating the expression
        int x = exp();
        if (inputToken == ';') {      // determines the assignment if it ends with ;
            return x;
        } else {
            throw new RuntimeException("Missing ';' token expected ");
        }
    }

    //  funtion call from main whcih read the file line by line using the loop
    public void run(Scanner fs) {
        while (fs.hasNextLine()) {
            expEvaluator(fs.nextLine());
            assignment();
        }
    }

    //  this function will do the assignment for the variable initialization
    //  and the value will be assigned to it
    void assignment() {

        String var = identifier();
        int operand = eval();
        hmap.put(var, operand);
        System.out.println(var + " = " + operand);

    }

    //  this method is applied if the operator is + or -
    int exp() {
        int x = term();
        while (inputToken == '+' || inputToken == '-') {
            char op = inputToken;
            nextToken();
            int y = term();
            x = apply(op, x, y);
        }
        return x;
    }

    //  this method is applied if the operator is * or /
    int term() {
        int x = factor();
        while (inputToken == '*' || inputToken == '/') {
            char op = inputToken;
            nextToken();
            int y = factor();
            x = apply(op, x, y);
        }
        return x;
    }

    //    thsi method will take care of more than one assignment declaration
    //    and store the operaters to get executed in orderly manner
    //    also store duplicate operators as token and checking Parenthesis
    int factor() {
        int x = 0;
        String temp = String.valueOf(inputToken);   // get the value of input using temporary string

        if (hmap.containsKey(temp)) {
            x = hmap.get(temp).intValue();
            nextToken();
            return x;
        } else if (inputToken == '(') {
            nextToken();
            x = exp();
            match(')');
            return x;
        } else if (inputToken == '-') {
            nextToken();
            x = factor();
            return -x;
        } else if (inputToken == '+') {
            nextToken();
            x = factor();
            return x;
        } else if (inputToken == '0') {
            nextToken();
            if (Character.isDigit(inputToken))
                throw new RuntimeException("ERROR !! Invalid value");
            return 0;
        }
        temp = "";

        while (Character.isDigit(inputToken)) {
            temp += inputToken;
            nextToken();
        }

        return Integer.parseInt(temp);

    }

    // this function is construction of string that identify the creation of variable and the assignment
    // to distinct between the variable name and the value assigning to it
    String identifier() {
        StringBuilder sb = new StringBuilder();

        if (Character.isLetter(inputToken))
            sb.append(inputToken);
        else
            throw new RuntimeException("Invalid variable name");
        nextToken();

        //  make sure string append is a character
        //  also allowing the underscore and digit

        while (Character.isLetter(inputToken) || inputToken == '_' || Character.isDigit(inputToken)) {
            sb.append(inputToken);
            nextToken();
        }
        if (inputToken != '=')      // and make sure it is follow by the = operator to assign
            throw new RuntimeException("Not an assignment statement");
        nextToken();
        return sb.toString();
    }

    //  Operation to compute depending on the signs of the operators
    static int apply(char op, int x, int y) {
        int z = 0;
        switch (op) {
            case '+':
                z = x + y;
                break;
            case '-':
                z = x - y;
                break;
            case '*':
                z = x * y;
                break;
            case '/':
                z = x / y;
                break;
        }
        return z;
    }

    public static void main(String[] args) {

        try {
            Scanner ObjFile = new Scanner(new FileInputStream(args[0]));
            ExpEvaluator expEval = new ExpEvaluator();
            expEval.run(ObjFile);
                                    // if the file isn't passed as parameter by the user, or file doesn't exist
        } catch (Exception ecpt) {
            System.out.println("ERROR: " + ecpt);
        }
    }
}
