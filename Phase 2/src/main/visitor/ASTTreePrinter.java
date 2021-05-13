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

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ASTTreePrinter extends Visitor<Void> {

    public void messagePrinter(int line, String message){
        System.out.println("Line " + line + ": " + message);
    }

    @Override
    public Void visit(Program program) {
        messagePrinter(program.getLine(), program.toString());
        program.getMain().accept(this);
        ArrayList<FunctionDeclaration> program_funcs = program.getFunctions();
        if(!program_funcs.isEmpty()){
            for(FunctionDeclaration cur_func : program_funcs){
                cur_func.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(FunctionDeclaration funcDeclaration) {
        messagePrinter(funcDeclaration.getLine(), funcDeclaration.toString());
        funcDeclaration.getFunctionName().accept(this);
        ArrayList<Identifier> function_args = funcDeclaration.getArgs();
        for (Expression cur_arg : function_args) {
            cur_arg.accept(this);
        }
        funcDeclaration.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(MainDeclaration mainDeclaration) {
        messagePrinter(mainDeclaration.getLine() , mainDeclaration.toString());
        mainDeclaration.getBody().accept(this);
        return null;
    }


    @Override
    public Void visit(BlockStmt blockStmt) {
        messagePrinter(blockStmt.getLine(), blockStmt.toString());
        ArrayList<Statement> block_statements = blockStmt.getStatements();
        for(Statement cur_statement : block_statements){
            cur_statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ConditionalStmt conditionalStmt) {
        messagePrinter(conditionalStmt.getLine(), conditionalStmt.toString());
        conditionalStmt.getCondition().accept(this);
        conditionalStmt.getThenBody().accept(this);
        Statement else_body = conditionalStmt.getElseBody();
        if(else_body != null)
            else_body.accept(this);
        return null;
    }

    @Override
    public Void visit(FunctionCallStmt funcCallStmt) {
        messagePrinter(funcCallStmt.getLine() , funcCallStmt.toString());
        funcCallStmt.getFunctionCall().accept(this);
        return null;
    }

    @Override
    public Void visit(PrintStmt print) {
        messagePrinter(print.getLine(), print.toString());
        print.getArg().accept(this);
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        messagePrinter(returnStmt.getLine(), returnStmt.toString());
        returnStmt.getReturnedExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        messagePrinter(binaryExpression.getLine() , binaryExpression.toString());
        binaryExpression.getFirstOperand().accept(this);
        binaryExpression.getSecondOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(UnaryExpression unaryExpression) {
        messagePrinter(unaryExpression.getLine(), unaryExpression.toString());
        unaryExpression.getOperand().accept(this);
        return null;
    }

    @Override
    public Void visit(AnonymousFunction anonymousFunction) {
        messagePrinter(anonymousFunction.getLine(), anonymousFunction.toString());
        ArrayList<Identifier> func_args = anonymousFunction.getArgs();
        if(!func_args.isEmpty()){
            for(Identifier cur_arg : func_args){
                cur_arg.accept(this);
            }
        }
        anonymousFunction.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        messagePrinter(identifier.getLine(), identifier.toString());
        return null;
    }

    @Override
    public Void visit(ListAccessByIndex listAccessByIndex) {
        messagePrinter(listAccessByIndex.getLine(), listAccessByIndex.toString());
        listAccessByIndex.getInstance().accept(this);
        listAccessByIndex.getIndex().accept(this);
        return null;
    }

    @Override
    public Void visit(ListSize listSize) {
        messagePrinter(listSize.getLine(), listSize.toString());
        listSize.getInstance().accept(this);
        return null;
    }

    @Override
    public Void visit(FunctionCall funcCall) {
        messagePrinter(funcCall.getLine(), funcCall.toString());
        Expression cur_expr = funcCall.getInstance();
        if(cur_expr != null)
            cur_expr.accept(this);
        ArrayList<Expression> function_args = funcCall.getArgs();
        Map<Identifier, Expression> function_args_with_keys = funcCall.getArgsWithKey();

        if(!function_args.isEmpty()) {
            for (Expression cur_arg : function_args) {
                cur_arg.accept(this);
            }
        }
        else if(!function_args_with_keys.isEmpty()){
            for (Identifier cur_arg_id : function_args_with_keys.keySet()){
                Expression cur_arg_expr = function_args_with_keys.get(cur_arg_id);
                cur_arg_id.accept(this);
                cur_arg_expr.accept(this);
            }
        }

        return null;
    }

    @Override
    public Void visit(ListValue listValue) {
        messagePrinter(listValue.getLine(), listValue.toString());
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
        messagePrinter(intValue.getLine(), intValue.toString());
        return null;
    }

    @Override
    public Void visit(BoolValue boolValue) {
        messagePrinter(boolValue.getLine(), boolValue.toString());
        return null;
    }

    @Override
    public Void visit(StringValue stringValue) { //TODO Check ""
        messagePrinter(stringValue.getLine(), stringValue.toString());
        return null;
    }

    @Override
    public Void visit(VoidValue voidValue) {
        messagePrinter(voidValue.getLine(), voidValue.toString());
        return null;
    }

}
