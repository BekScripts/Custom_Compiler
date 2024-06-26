import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener{
    private Node root;
    private Node traversal;
    private List<LinkedHashMap<String, String>> symbolTables;
    private Stack<String> scopeStack;
    private int blockCount = 0;

    public SimpleTableBuilder() throws Exception{
        symbolTables = new ArrayList<>();
        scopeStack = new Stack<>();
    }

    @Override
    public void enterProgram(LittleParser.ProgramContext ctx){
        LinkedHashMap<String, String> globalSymbolTable = new LinkedHashMap<>();
        globalSymbolTable.put("SCOPE_NAME", "GLOBAL");
        symbolTables.add(globalSymbolTable);
        scopeStack.push("GLOBAL");
        root = new Node(null, null, null);
        traversal= root;
    }

    @Override
    public void exitProgram(LittleParser.ProgramContext ctx) {
        IRGenerator.globalSymbolTable = symbolTables.get(0);
        IRGenerator.generate(root);

        TinyGenerator.generate(IRGenerator.code);
    }

    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        LinkedHashMap<String, String> functionSymbolTable = new LinkedHashMap<>();
        functionSymbolTable.put("SCOPE_NAME", ctx.any_type().getText().equals("VOID")? ctx.id().getText() : ctx.id().getText());
        symbolTables.add(functionSymbolTable);
        scopeStack.push(ctx.id().getText());
        traversal.addChilredn(new Node(ctx.any_type().getText(), ctx.id().getText(), null));
        traversal = traversal.getChild(0);
    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        scopeStack.pop();
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type = ctx.var_type().getText();
        String allVars[] = ctx.id_list().getText().split(",");

        addSymbolToCurrentScope(ctx.id_list().id().getText(), type, "");
        extractIdFromTail(ctx.id_list().id_tail(),type);
        for(String var : allVars){
            traversal.addChilredn(new Node(ctx.var_type().getText(), var, null));
            traversal = traversal.getChild(0);
        }
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();
        addSymbolToCurrentScope(name, type, value);
        traversal.addChilredn(new Node(type, name, value));
        traversal = traversal.getChild(0);
    }

    @Override
    public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
        String ops = ctx.expr().getText();

        try{
            Integer.parseInt(ops);
            traversal.addChilredn(new Node("ASGN-I", ctx.id().getText(), ops));
            traversal = traversal.getChild(0);
        }catch (Exception ex1){
            try{
                Double.parseDouble(ops);
                traversal.addChilredn(new Node("ASGN-F", ctx.id().getText(), ops));
                traversal = traversal.getChild(0);
            }catch (Exception ex2){
                traversal.addChilredn(new Node("EXPR", ctx.id().getText(),ops));
                traversal = traversal.getChild(0);
            }
        }
    }

    @Override
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        traversal.addChilredn(new Node("WRITE", null, ctx.id_list().getText()));
        traversal = traversal.getChild(0);
    }

    @Override
    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
        traversal.addChilredn(new Node("READ", ctx.id_list().getText(), null));
        traversal = traversal.getChild(0);
    }

    @Override
    public void enterParam_decl(LittleParser.Param_declContext ctx) {
        String name = ctx.id().getText();
        String type = ctx.var_type().getText();
        addSymbolToCurrentScope(name, type, "");
    }

    @Override
    public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        blockCount++;
        LinkedHashMap<String, String> blockSymbolTable = new LinkedHashMap<>();
        blockSymbolTable.put("SCOPE_NAME", "BLOCK "+ blockCount);
        symbolTables.add(blockSymbolTable);
        scopeStack.push("BLOCK "+ blockCount);
    }

    @Override
    public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
        scopeStack.pop();
    }

    @Override
    public void enterElse_part(LittleParser.Else_partContext ctx) {
        if(ctx.stmt_list() != null){
            blockCount++;
            LinkedHashMap<String, String> blockSymbolTable = new LinkedHashMap<>();
            blockSymbolTable.put("SCOPE_NAME", "BLOCK "+ blockCount);
            symbolTables.add(blockSymbolTable);
            scopeStack.push("BLOCK "+ blockCount);
        }
    }

    @Override
    public void exitElse_part(LittleParser.Else_partContext ctx) {
        if(ctx.stmt_list() != null){
            scopeStack.pop();
        }
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        blockCount++;
        LinkedHashMap<String, String> blockSymbolTable = new LinkedHashMap<>();
        blockSymbolTable.put("SCOPE_NAME", "BLOCK "+blockCount);
        symbolTables.add(blockSymbolTable);
        scopeStack.push("BLOCK "+ blockCount);
    }

    @Override
    public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        scopeStack.pop();
    }




    private void addSymbolToCurrentScope(String name, String type, String value){
        LinkedHashMap<String, String> currentScope = symbolTables.get(symbolTables.size()-1);
        if(currentScope.containsKey(name)){
            System.out.println("DECLARATION ERROR "+ name);
            System.exit(1);
        }
        else{
            currentScope.put("name "+name, "type "+ type + (value.isEmpty()? "" : " value "+ value));
        }
    }


    private void extractIdFromTail(LittleParser.Id_tailContext idTail, String type){
        while(idTail.id() != null){
            addSymbolToCurrentScope(idTail.id().getText(), type, "");
            idTail = idTail.id_tail();
        }
    }

    public void prettyPrint(){
        String currentScope = null;
        for(LinkedHashMap<String, String> symbolTable : symbolTables){
            String scopeName = symbolTable.get("SCOPE_NAME");
            if(!scopeName.equals(currentScope)){
                if(currentScope != null){
                    System.out.println();
                }
                System.out.println("Symbol table  "+scopeName);
                currentScope = scopeName;
            }
            symbolTable.forEach((name, value) ->{
                if(!name.equals("SCOPE_NAME")){
                    System.out.println(name + " "+ value);
                }
            });
        }
    }

}// end Class



