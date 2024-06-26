import java.util.LinkedHashMap;
import java.util.LinkedList;

public class IRGenerator {
    static LinkedHashMap<String, String> globalSymbolTable;
    static LinkedList<String> code = new LinkedList<>();

    public static void generate (Node root){
        code.add(";IR Code");

        // Convert to 3AC
        convertStatements(root, code, 1);
        code.add(";RET");


    }// end generate fn

    public static void convertStatements(Node root, LinkedList<String> code, int temp){
        Node current = root;

        if(current.getName() != null && current.getName().equals("main")){
            code.add(";LABEL main");
            code.add(";LINK");
        }
        else if(current.getType() != null && (current.getType().equals("INT") || current.getType().equals("FLOAT"))){
            code.add(";VARDCL " + current.getName());
        }
        else if(current.getType() != null && current.getType().equals("STRING")){
            code.add(";STRDCL "+ current.getName() + " "+ current.getValue());
        }

        else if(current.getType() != null && current.getType().equals("READ")){
            String[] variables = current.getName().split(",");
            int i = 0;
            for(String var : variables){
                if(globalSymbolTable.get("name "+ var).contains("type INT")){
                    code.add(";READI "+ var);
                }
                else if(globalSymbolTable.get("name "+ var).contains("type FLOAT")){
                    code.add(";READF "+ var);
                }
                i++;
            }
        }

        else if(current.getType() != null && current.getType().equals("WRITE")){
            String[] variables = current.getValue().split(",");
            int i = 0;
            for (String var : variables){
                if(globalSymbolTable.get("name "+ var).contains("type INT")){
                    code.add(";WRITEI "+ var);
                }
                else if(globalSymbolTable.get("name "+ var).contains("type FLOAT")){
                    code.add(";WRITEF "+var);
                }
                else if(globalSymbolTable.get("name "+ var).contains("type STRING")){
                    code.add(";WRITES "+var);
                }
                i++;
            }
        }

        else if(current.getType() != null && current.getType().contains("ASGN-I")){
            code.add(";STOREI "+current.getValue()+ " $T"+ temp);
            code.add(";STOREI $T"+ (temp++) + " "+ current.getName());
        }

        else if(current.getType() != null && current.getType().contains("ASGN-F")){
            code.add(";STOREF "+ current.getValue()+ " $T"+ temp);
            code.add(";STOREF $T"+ (temp++)+ " "+ current.getName());

        }

        else if (current.getType() != null && current.getType().contains("EXPR")) {
            char symbol = ' ';
            String curValue = current.getValue();
            String[] ops = new String[2];
            boolean activeFloat = false;

            curValue = curValue.replace("(", "");
            curValue = curValue.replace(")", "");
            curValue = curValue.replace(" ", "");

            // Regular expression to match arithmetic operators and split the expression
            String[] operators = curValue.split("(?=[-+*/])");

            // Extract operands and operator
            if (operators.length == 1) {

                ops[0] = operators[0];
            } else if (operators.length == 2) {

                ops[0] = operators[0];
                ops[1] = operators[1];
                symbol = curValue.charAt(ops[0].length());
            }

            ops[1] = ops[1].replaceAll("[-+*/]", "");
            for (String var : ops) {
                if (var != null && var.contains(".")) {
                    activeFloat = true;
                    break;
                }
            }

            // Integer expressions
            if (!activeFloat) {
                if (symbol == '+') {
                    code.add(";ADDI " + ops[0] + " " + ops[1] + " $T" + temp);
                } else if (symbol == '-') {
                    code.add(";SUBI " + ops[0] + " " + ops[1] + " $T" + temp);
                } else if (symbol == '*') {
                    code.add(";MULTI " + ops[0] + " " + ops[1] + " $T" + temp);
                } else if (symbol == '/') {
                    code.add(";DIVI " + ops[0] + " " + ops[1] + " $T" + temp);
                }
                code.add(";STOREI $T" + (temp++) + " " + current.getName());
            }
            // Float expressions
            else {
                if (symbol == '+') {
                    code.add(";ADDF " + ops[0] + " " + ops[1] + " $T" + temp);
                } else if (symbol == '-') {
                    code.add(";SUBF " + ops[0] + " " + ops[1] + " $T" + temp);
                } else if (symbol == '*') {
                    code.add(";MULTF " + ops[0] + " " + ops[1] + " $T" + temp);
                } else if (symbol == '/') {
                    code.add(";DIVF " + ops[0] + " " + ops[1] + " $T" + temp);
                }
                code.add(";STOREF $T" + (temp++) + " " + current.getName());
            }
        }


        for(int i =0; i < current.size(); i++){
            convertStatements(current.getChild(i), code, temp);
        }
    }// end convertStatments()
}// end IRGenerator class