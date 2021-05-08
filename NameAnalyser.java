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
import main.symbolTable.SymbolTable;

import java.util.ArrayList;


public class NameAnalyser extends Visitor<Void> {

    @Override
    public Void visit(Program program) {
        //ToDo
        SymbolTable root = new SymbolTable();
        ArrayList<FunctionDeclaration>functions = program.getFunctions();
        for (int i = 0; i < functions.size(); i++) {
            //FunctionSymbolTableItem func = new FunctionSymbolTableItem(functions.get(i));
            //add func to root
        }

        program.getMain().accept(this);
        for (int i = 0; i < functions.size(); i++) {
            functions.get(i).accept(this);
        }
        return null;
    }

    @Override
    public Void visit(FunctionDeclaration funcDeclaration) {
        //ToDo
        ArrayList<Identifier> args = funcDeclaration.getArgs();
        for (int i = 0; i < args.size(); i++) {
            args.get(i).accept(this);
        }
        funcDeclaration.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(MainDeclaration mainDeclaration) {
        //ToDo
        mainDeclaration.getBody().accept(this);
        return null;
    }


    @Override
    public Void visit(BlockStmt blockStmt) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(ConditionalStmt conditionalStmt) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(FunctionCallStmt funcCallStmt) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(PrintStmt print) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(UnaryExpression unaryExpression) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(AnonymousFunction anonymousFunction) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(ListAccessByIndex listAccessByIndex) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(ListSize listSize) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(FunctionCall funcCall) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(ListValue listValue) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(IntValue intValue) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(BoolValue boolValue) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(StringValue stringValue) {
        //ToDo
        return null;
    }

    @Override
    public Void visit(VoidValue voidValue) {
        //ToDo
        return null;
    }

}
