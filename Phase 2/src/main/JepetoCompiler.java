package main;

import main.ast.nodes.Program;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import parsers.*;
import main.visitor.*;

public class JepetoCompiler {
    public void compile(CharStream textStream) {
        JepetoLexer jepetoLexer = new JepetoLexer(textStream);
        CommonTokenStream tokenStream = new CommonTokenStream(jepetoLexer);
        JepetoParser jepetoParser = new JepetoParser(tokenStream);
        Program program = jepetoParser.jepeto().jepetoProgram;

        //ToDo

        NameAnalyser treeAnalayzer = new NameAnalyser();
        treeAnalayzer.visit(program);
        if(treeAnalayzer.compiler_error_exists == false) {
            ASTTreePrinter printerTree = new ASTTreePrinter();
            printerTree.visit(program);
        }

    }

}
