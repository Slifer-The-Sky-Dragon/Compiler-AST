package main.visitor;

import main.ast.nodes.Program;
import main.ast.nodes.declaration.FunctionDeclaration;
import main.ast.nodes.declaration.MainDeclaration;
import main.ast.nodes.expression.*;
import main.ast.nodes.expression.values.ListValue;
import main.ast.nodes.expression.values.VoidValue;
import main.ast.nodes.expression.values.primitive.BoolValue;
import main.ast.nodes.expression.values.primitive.IntValue;
import main.ast.nodes.expression.values.primitive.StringValue;
import main.ast.nodes.statement.*;
import main.compileErrors.nameErrors.*;
import main.symbolTable.SymbolTable;
import main.symbolTable.items.*;
import main.symbolTable.utils.Stack;
import main.symbolTable.exceptions.*;
import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NameAnalyser extends Visitor<Void> {

    @Override
    public Void visit(Program program) {
        compiler_error_exists = false;
        SymbolTable.root = new SymbolTable();

        ArrayList<FunctionDeclaration> functions = program.getFunctions();

        for(int i = 0 ; i < functions.size(); i++){
            int cnt = 0;
            for(int j = 0 ; j < i ; j++){
                if(functions.get(i).getFunctionName().getName().equals(functions.get(j).getFunctionName().getName())){
                    cnt += 1;
                }
            }
            if(cnt == 0){
                FunctionSymbolTableItem new_func_item = new FunctionSymbolTableItem(functions.get(i));
                try {
                    SymbolTable.root.put(new_func_item);
                }catch(ItemAlreadyExistsException e){}
            }
            else{ //TODO CE:Duplicate Fucntion
                compiler_error_exists = true;
                DuplicateFunction cerr = new DuplicateFunction(functions.get(i).getLine(), functions.get(i).getFunctionName().getName());
                System.out.println(cerr.getMessage());
                FunctionSymbolTableItem new_func_item = new FunctionSymbolTableItem(functions.get(i));
                new_func_item.setName("@" + new_func_item.getName() + cnt);
                try {
                    SymbolTable.root.put(new_func_item);
                }catch(ItemAlreadyExistsException e){}
            }
        }

        SymbolTable main_symbol_table = new SymbolTable();
        SymbolTable.push(main_symbol_table);
        program.getMain().accept(this);
        SymbolTable.pop();
        for (FunctionDeclaration cur_func : functions) {
            SymbolTable cur_func_symbol_table = new SymbolTable();
            ArrayList<Identifier> cur_func_args = cur_func.getArgs();

            for(int i = 0 ; i < cur_func_args.size(); i++){
                try{ //TODO CE: Argument Conflict
                    SymbolTable.root.getItem("Function_" + cur_func_args.get(i).getName());
                    NameConflict cerr = new NameConflict(cur_func_args.get(i).getLine(), cur_func_args.get(i).getName());
                    compiler_error_exists = true;
                    System.out.println(cerr.getMessage());
                }catch(ItemNotFoundException e){}

                int cnt = 0;
                for(int j = 0 ; j < i ; j++){
                    if(cur_func_args.get(i).getName().equals(cur_func_args.get(j).getName())){
                        cnt += 1;
                    }
                }
                if(cnt == 0){
                    VariableSymbolTableItem new_var_item = new VariableSymbolTableItem(cur_func_args.get(i));
                    try {
                        cur_func_symbol_table.put(new_var_item);
                    }catch(ItemAlreadyExistsException e){}
                }
                else{//TODO CE:Duplicate Argument
                    compiler_error_exists = true;
                    DuplicateArgument cerr = new DuplicateArgument(cur_func_args.get(i).getLine(), cur_func_args.get(i).getName());
                    System.out.println(cerr.getMessage());
                    VariableSymbolTableItem new_var_item = new VariableSymbolTableItem(cur_func_args.get(i));
                    new_var_item.setName("@" + new_var_item.getName() + String.valueOf(cnt));
                    try {
                        cur_func_symbol_table.put(new_var_item);
                    }catch(ItemAlreadyExistsException e){}
                }
            }
            SymbolTable.push(cur_func_symbol_table);
            cur_func.accept(this);
            SymbolTable.pop();
        }
        return null;
    }

    @Override
    public Void visit(FunctionDeclaration funcDeclaration) {
        funcDeclaration.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(MainDeclaration mainDeclaration) {
        mainDeclaration.getBody().accept(this);
        return null;
    }


    @Override
    public Void visit(BlockStmt blockStmt) {
        ArrayList<Statement> block_statements = blockStmt.getStatements();
        for(Statement cur_statement : block_statements){
            cur_statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ConditionalStmt conditionalStmt) {
        conditionalStmt.getCondition().accept(this);
        conditionalStmt.getThenBody().accept(this);
        Statement else_body = conditionalStmt.getElseBody();
        if(else_body != null)
            else_body.accept(this);
        return null;
    }

    @Override
    public Void visit(FunctionCallStmt funcCallStmt) {
        funcCallStmt.getFunctionCall().accept(this);
        return null;
    }

    @Override
    public Void visit(PrintStmt print) {
        print.getArg().accept(this);
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        returnStmt.getReturnedExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        binaryExpression.getFirstOperand().accept(this);
        binaryExpression.getSecondOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(UnaryExpression unaryExpression) {
        unaryExpression.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(AnonymousFunction anonymousFunction) {
        ArrayList<Identifier> cur_func_args = anonymousFunction.getArgs();
        SymbolTable cur_func_symbol_table = new SymbolTable();
        for(int i = 0 ; i < cur_func_args.size(); i++){
            try{ //TODO CE: Argument Conflict
                SymbolTable.root.getItem("Function_" + cur_func_args.get(i).getName());
                NameConflict cerr = new NameConflict(cur_func_args.get(i).getLine(), cur_func_args.get(i).getName());
                compiler_error_exists = true;
                System.out.println(cerr.getMessage());
            }catch(ItemNotFoundException e){}

            int cnt = 0;
            for(int j = 0 ; j < i ; j++){
                if(cur_func_args.get(i).getName().equals(cur_func_args.get(j).getName())){
                    cnt += 1;
                }
            }
            if(cnt == 0){
                VariableSymbolTableItem new_var_item = new VariableSymbolTableItem(cur_func_args.get(i));
                try {
                    cur_func_symbol_table.put(new_var_item);
                }catch(ItemAlreadyExistsException e){}
            }
            else{//TODO CE:Duplicate Argument
                compiler_error_exists = true;
                DuplicateArgument cerr = new DuplicateArgument(cur_func_args.get(i).getLine(), cur_func_args.get(i).getName());
                System.out.println(cerr.getMessage());
                VariableSymbolTableItem new_var_item = new VariableSymbolTableItem(cur_func_args.get(i));
                new_var_item.setName("@" + new_var_item.getName() + String.valueOf(cnt));
                try {
                    cur_func_symbol_table.put(new_var_item);
                }catch(ItemAlreadyExistsException e){}
            }
        }
        SymbolTable.push(cur_func_symbol_table);
        anonymousFunction.getBody().accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        try {
            SymbolTable.top.getItem("Var_" + identifier.getName());
        }
        catch(ItemNotFoundException e){
            try{
                SymbolTable.root.getItem("Function_" + identifier.getName());
            }
            catch (ItemNotFoundException e2) { //TODO CE:Variable Not Declared
                compiler_error_exists = true;
                VariableNotDeclared cerr = new VariableNotDeclared(identifier.getLine(), identifier.getName());
                System.out.println(cerr.getMessage());
            }
        }
        return null;
    }

    @Override
    public Void visit(ListAccessByIndex listAccessByIndex) {
        listAccessByIndex.getInstance().accept(this);
        listAccessByIndex.getIndex().accept(this);
        return null;
    }

    @Override
    public Void visit(ListSize listSize) {
        listSize.getInstance().accept(this);
        return null;
    }

    @Override
    public Void visit(FunctionCall funcCall) {
        Expression cur_expr = funcCall.getInstance();
        Identifier cur_func_id = null;

        try {
            cur_func_id = (Identifier) cur_expr;
        }
        catch(ClassCastException e){}

        FunctionSymbolTableItem cur_func = null;
        boolean is_func_available = true;
        if(cur_func_id != null) {
            try {
                cur_func = (FunctionSymbolTableItem) SymbolTable.root.getItem("Function_" + cur_func_id.getName());
            }catch(ItemNotFoundException e){ // TODO CE:Function not Declared
                compiler_error_exists = true;
                FunctionNotDeclared new_func_not_declared = new FunctionNotDeclared(cur_func_id.getLine(), cur_func_id.getName());
                System.out.println(new_func_not_declared.getMessage());
                is_func_available = false;
            }
        }

        ArrayList<Expression> function_args = funcCall.getArgs();
        Map<Identifier, Expression> function_args_with_keys = funcCall.getArgsWithKey();

        if(!function_args.isEmpty()) {
            for (Expression cur_arg : function_args) {
                cur_arg.accept(this);
            }
        }
        else if(!function_args_with_keys.isEmpty() && cur_func_id != null){
            for (Identifier cur_arg_id : function_args_with_keys.keySet()){
                Expression cur_arg_expr = function_args_with_keys.get(cur_arg_id);

//                try { // TODO CE: Argument Conflict
//                    compiler_error_exists = true;
//                    SymbolTableItem tmp = SymbolTable.root.getItem("Function_" + cur_arg_id.getName());
//                    NameConflict same_name = new NameConflict(cur_arg_id.getLine(), cur_arg_id.getName());
//                    System.out.println(same_name.getMessage());
//                }catch(ItemNotFoundException e){}

                if(is_func_available){ //TODO CE: Argument not declared
                    ArrayList<Identifier> cur_func_args = cur_func.getFuncDeclaration().getArgs();
                    boolean flag = false;
                    for(Identifier cur_func_argument : cur_func_args){
                        if(cur_func_argument.getName().equals(cur_arg_id.getName()))
                            flag = true;
                    }
                    if(!flag){
                        compiler_error_exists = true;
                        ArgumentNotDeclared cerr = new ArgumentNotDeclared(cur_arg_id.getLine(), cur_arg_id.getName(), cur_func_id.getName());
                        System.out.println(cerr.getMessage());
                    }
                }

                cur_arg_expr.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(ListValue listValue) {
        ArrayList<Expression> list_exprs = listValue.getElements();
        if(!list_exprs.isEmpty()){
            for(Expression cur_arg : list_exprs){
                cur_arg.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(IntValue intValue) {
        return null;
    }

    @Override
    public Void visit(BoolValue boolValue) {
        return null;
    }

    @Override
    public Void visit(StringValue stringValue) {
        return null;
    }

    @Override
    public Void visit(VoidValue voidValue) {
        return null;
    }

}
