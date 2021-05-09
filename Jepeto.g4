grammar Jepeto;

@header{
    import main.ast.nodes.*;
    import main.ast.nodes.declaration.*;
    import main.ast.nodes.expression.*;
    import main.ast.nodes.expression.operators.*;
    import main.ast.nodes.expression.values.*;
    import main.ast.nodes.expression.values.primitive.*;
    import main.ast.nodes.statement.*;
    import java.util.LinkedHashMap;
    import java.util.HashMap;
    import java.util.Map;
}

jepeto returns[Program jepetoProgram]:
    program {$jepetoProgram = $program._program;}EOF
    ;

program returns[Program _program]:
    {$_program = new Program();}
    (a=functionDeclaration {$_program.addFunction($a._functionDeclaration);})*
    main {$_program.setMain($main._main);}
    (b=functionDeclaration {$_program.addFunction($b._functionDeclaration);})*
    {$_program.setLine(1);}
    ;

functionDeclaration returns[FunctionDeclaration _functionDeclaration] locals[Identifier MethodName]:
    FUNC IDENTIFIER functionArgumentsDeclaration COLON body
    {
        $_functionDeclaration = new FunctionDeclaration();
        $MethodName = new Identifier($IDENTIFIER.text);
        $MethodName.setLine($IDENTIFIER.line);
        $_functionDeclaration.setFunctionName($MethodName);
        $_functionDeclaration.setArgs($functionArgumentsDeclaration._functionArgumentsDeclaration);
        $_functionDeclaration.setBody($body._body);
        $_functionDeclaration.setLine($FUNC.line);
    }
    ;

functionArgumentsDeclaration returns[ArrayList<Identifier> _functionArgumentsDeclaration = new ArrayList<Identifier>(), int _line] locals[Identifier Arg]:
    LPAR {$_line = $LPAR.line;} (a=IDENTIFIER {$Arg = new Identifier($a.text); $Arg.setLine($a.line); $_functionArgumentsDeclaration.add($Arg);}
    (COMMA b=IDENTIFIER {$Arg = new Identifier($b.text); $Arg.setLine($b.line); $_functionArgumentsDeclaration.add($Arg);})*)? RPAR
    ;


body returns[Statement _body]:
    singleStatement {$_body = $singleStatement._singleStatement;} |
    block {$_body = $block._block;}
    ;

main returns[MainDeclaration _main]:
    {$_main = new MainDeclaration();}
    MAIN COLON (functionCallStatement {$_main.setBody($functionCallStatement._functionCallStatement);} |
    printStatement{$_main.setBody($printStatement._printStatement);})
    {$_main.setLine($MAIN.line);}
    ;

functionCall returns[FunctionCall _functionCall] locals[Expression instance]:
    identifier {$instance = $identifier._identifier;}
    (lpar1=LPAR a=functionArguments
    {
        $_functionCall = new FunctionCall($instance, $a._args, $a._argsWithKey);
        $_functionCall.setLine($lpar1.line);
        $instance = $_functionCall;
    } RPAR)*
    (lpar2=LPAR b=functionArguments
    {
        $_functionCall = new FunctionCall($instance, $b._args, $b._argsWithKey);
        $_functionCall.setLine($lpar2.line);
        $instance = $_functionCall;
    } RPAR)
    ;

functionArguments returns[ArrayList<Expression> _args = new ArrayList<Expression>(), Map<Identifier, Expression> _argsWithKey = new HashMap<>()]:
    a=splitedExpressionsWithComma {$_args = $a._splitedExpressionsWithComma;} |
    b=splitedExpressionsWithCommaAndKey {$_argsWithKey = $b._splitedExpressionsWithCommaAndKey;}
    ;

splitedExpressionsWithComma returns[ArrayList<Expression> _splitedExpressionsWithComma = new ArrayList<Expression>()]:
    (a=expression {$_splitedExpressionsWithComma.add($a._expression);}
    (COMMA b=expression {$_splitedExpressionsWithComma.add($b._expression);} )*)?
    ;

splitedExpressionsWithCommaAndKey returns[Map<Identifier, Expression> _splitedExpressionsWithCommaAndKey = new LinkedHashMap<>()]:
    (a=identifier ASSIGN b=expression {$_splitedExpressionsWithCommaAndKey.put($a._identifier, $b._expression);}
    (COMMA  c=identifier ASSIGN d=expression {$_splitedExpressionsWithCommaAndKey.put($c._identifier, $d._expression);} )*)?
    ;

functionCallStatement returns[FunctionCallStmt _functionCallStatement]:
    functionCall SEMICOLLON
    {
        $_functionCallStatement = new FunctionCallStmt($functionCall._functionCall);
        $_functionCallStatement.setLine($functionCall._functionCall.getLine());
    }
    ;

returnStatement returns[ReturnStmt _returnStatement]:
    RETURN (expression {$_returnStatement = new ReturnStmt($expression._expression);} |
    voidValue {$_returnStatement = new ReturnStmt($voidValue._voidValue);}) SEMICOLLON
    {$_returnStatement.setLine($RETURN.line);}
    ;

ifStatement returns[ConditionalStmt _ifStatement]:
    IF expression COLON a=conditionBody {$_ifStatement = new ConditionalStmt($expression._expression, $a._conditionBody);}
    (ELSE COLON b=conditionBody {$_ifStatement.setElseBody($b._conditionBody);})?
    {$_ifStatement.setLine($IF.line);}
    ;

ifStatementWithReturn returns[ConditionalStmt _ifStatementWithReturn]:
    IF expression COLON a=body {$_ifStatementWithReturn = new ConditionalStmt($expression._expression, $a._body);}
    ELSE COLON b=body {$_ifStatementWithReturn.setElseBody($b._body);}
    {$_ifStatementWithReturn.setLine($IF.line);}
    ;

printStatement returns[PrintStmt _printStatement]:
    PRINT LPAR expression RPAR SEMICOLLON
    {
        $_printStatement = new PrintStmt($expression._expression);
        $_printStatement.setLine($PRINT.line);
    }
    ;

statement returns[Statement _statement]:
    ifStatement {$_statement = $ifStatement._ifStatement;} |
    printStatement {$_statement = $printStatement._printStatement;} |
    functionCallStatement {$_statement = $functionCallStatement._functionCallStatement;} |
    returnStatement {$_statement = $returnStatement._returnStatement;}
    ;

singleStatement returns[Statement _singleStatement]:
    returnStatement {$_singleStatement = $returnStatement._returnStatement;} |
    ifStatementWithReturn {$_singleStatement = $ifStatementWithReturn._ifStatementWithReturn;}
    ;

block returns[BlockStmt _block]:
    {$_block = new BlockStmt();}
    LBRACE ((a=statement {$_block.addStatement($a._statement);} )*
    (returnStatement {$_block.addStatement($returnStatement._returnStatement);} |
    ifStatementWithReturn {$_block.addStatement($ifStatementWithReturn._ifStatementWithReturn);} )
    (b=statement {$_block.addStatement($b._statement);} )*) RBRACE
    {$_block.setLine($LBRACE.line);}
    ;

conditionBody returns[Statement _conditionBody] locals[BlockStmt Block]:
    LBRACE {$Block = new BlockStmt();} (a=statement {$Block.addStatement($a._statement);} )*
    {$_conditionBody = $Block; $_conditionBody.setLine($LBRACE.line);} RBRACE |
    b=statement {$_conditionBody = $b._statement;}
    ;

expression returns[Expression _expression]:
    a=andExpression {$_expression = $a._andExpression;}
    (OR b=andExpression
    {
        $_expression = new BinaryExpression($_expression, $b._andExpression, BinaryOperator.or);
        $_expression.setLine($OR.line);
    }
    )*;

andExpression returns[Expression _andExpression]:
    a=equalityExpression {$_andExpression = $a._equalityExpression;}
    (AND b=equalityExpression
    {
        $_andExpression = new BinaryExpression($_andExpression, $b._equalityExpression, BinaryOperator.and);
        $_andExpression.setLine($AND.line);
    }
    )*;

equalityExpression returns[Expression _equalityExpression]:
    a=relationalExpression {$_equalityExpression = $a._relationalExpression;}
    ((EQUAL | flag=NOT_EQUAL) b=relationalExpression
    {
        $_equalityExpression = new BinaryExpression($_equalityExpression, $b._relationalExpression, BinaryOperator.eq);
        if ($flag!=null)
        {
            ((BinaryExpression)$_equalityExpression).setBinaryOperator(BinaryOperator.neq);
            $_equalityExpression.setLine($flag.line);
        }
        else
        {
            $_equalityExpression.setLine($EQUAL.line);
        }
    }
    )*;

relationalExpression returns[Expression _relationalExpression]:
    a=additiveExpression {$_relationalExpression = $a._additiveExpression;}
    ((GREATER_THAN | flag=LESS_THAN) b=additiveExpression
    {
        $_relationalExpression = new BinaryExpression($_relationalExpression, $b._additiveExpression, BinaryOperator.gt);
        if ($flag!=null)
        {
            ((BinaryExpression)$_relationalExpression).setBinaryOperator(BinaryOperator.lt);
            $_relationalExpression.setLine($flag.line);
        }
        else
        {
            $_relationalExpression.setLine($GREATER_THAN.line);
        }
    }
    )*;

additiveExpression returns[Expression _additiveExpression]:
    a=multiplicativeExpression {$_additiveExpression = $a._multiplicativeExpression;}
    ((PLUS | flag=MINUS) b=multiplicativeExpression
    {
        $_additiveExpression = new BinaryExpression($_additiveExpression, $b._multiplicativeExpression, BinaryOperator.add);
        if ($flag!=null)
        {
            ((BinaryExpression)$_additiveExpression).setBinaryOperator(BinaryOperator.sub);
            $_additiveExpression.setLine($flag.line);
        }
        else
        {
            $_additiveExpression.setLine($PLUS.line);
        }
    }
    )*;

multiplicativeExpression returns[Expression _multiplicativeExpression]:
    a=preUnaryExpression {$_multiplicativeExpression = $a._preUnaryExpression;}
    ((MULT |  flag=DIVIDE ) b=preUnaryExpression
    {
        $_multiplicativeExpression = new BinaryExpression($_multiplicativeExpression, $b._preUnaryExpression, BinaryOperator.mult);
        if ($flag!=null)
        {
            ((BinaryExpression)$_multiplicativeExpression).setBinaryOperator(BinaryOperator.div);
            $_multiplicativeExpression.setLine($flag.line);
        }
        else
        {
            $_multiplicativeExpression.setLine($MULT.line);
        }
    }
    )*;

preUnaryExpression returns[Expression _preUnaryExpression]:
    ((NOT | flag=MINUS) a=preUnaryExpression
    {
        $_preUnaryExpression = new UnaryExpression($a._preUnaryExpression, UnaryOperator.not);
        if ($flag!=null)
        {
            ((UnaryExpression)$_preUnaryExpression).setOperator(UnaryOperator.minus);
            $_preUnaryExpression.setLine($flag.line);
        }
        else
        {
             $_preUnaryExpression.setLine($NOT.line);
        }
    }
    ) |
    b=appendExpression {$_preUnaryExpression = $b._appendExpression;}
    ;

appendExpression returns [Expression _appendExpression]:
    a=accessExpression {$_appendExpression = $a._accessExpression;}
    (APPEND b=accessExpression
    {
        $_appendExpression = new BinaryExpression($_appendExpression, $b._accessExpression, BinaryOperator.append);
        $_appendExpression.setLine($APPEND.line);
    }
    )*;

accessExpression returns[Expression _accessExpression]: ///////////
    otherExpression {$_accessExpression = $otherExpression._otherExpression;}
    (LPAR functionArguments RPAR)*
    (LBRACK expression RBRACK
    {
        $_accessExpression = new ListAccessByIndex($_accessExpression, $expression._expression);
        $_accessExpression.setLine($LBRACK.line);
    }
    )*
    (sizeExpression {$_accessExpression = new ListSize($_accessExpression); $_accessExpression.setLine($sizeExpression._line);} )*
    ;

otherExpression returns[Expression _otherExpression]:
    values {$_otherExpression = $values._values;} |
    identifier {$_otherExpression = $identifier._identifier;} |
    anonymousFunction {$_otherExpression = $anonymousFunction._anonymousFunction;} |
    LPAR expression RPAR {$_otherExpression = $expression._expression;}
    ;

anonymousFunction returns[AnonymousFunction _anonymousFunction]:
    a=functionArgumentsDeclaration {$_anonymousFunction = new AnonymousFunction($a._functionArgumentsDeclaration);}
    ARROW block {$_anonymousFunction.setBody($block._block); $_anonymousFunction.setLine($a._line);}
    ;

sizeExpression returns[int _line]:
    DOT SIZE {$_line = $DOT.line;}
    ;

values returns [Value _values] locals[IntValue IntVal, StringValue StrVal]:
    boolValue {$_values = $boolValue._boolValue;} |
    STRING_VALUE {$StrVal = new StringValue($STRING_VALUE.text); $StrVal.setLine($STRING_VALUE.line); $_values = $StrVal;}|
    INT_VALUE {$IntVal = new IntValue($INT_VALUE.int); $IntVal.setLine($INT_VALUE.line); $_values = $IntVal;} |
    listValue {$_values = $listValue._listValue;}
    ;

listValue returns[ListValue _listValue]:
    LBRACK splitedExpressionsWithComma RBRACK
    {
        $_listValue = new ListValue($splitedExpressionsWithComma._splitedExpressionsWithComma);
        $_listValue.setLine($LBRACK.line);
    }
    ;

boolValue returns[BoolValue _boolValue]:
    (bool=TRUE {$_boolValue = new BoolValue(true);} |
    bool=FALSE {$_boolValue = new BoolValue(false);})
    {$_boolValue.setLine($bool.line);}
    ;

voidValue returns [VoidValue _voidValue]:
    VOID
    {
        $_voidValue = new VoidValue();
        $_voidValue.setLine($VOID.line);
    }
    ;

identifier returns[Identifier _identifier]:
    IDENTIFIER
    {
        $_identifier = new Identifier($IDENTIFIER.text);
        $_identifier.setLine($IDENTIFIER.line);
    }
    ;


FUNC: 'func';
MAIN: 'main';
SIZE: 'size';

PRINT: 'print';
RETURN: 'return';
VOID: 'void';

IF: 'if';
ELSE: 'else';

PLUS: '+';
MINUS: '-';
MULT: '*';
DIVIDE: '/';

EQUAL: 'is';
NOT_EQUAL: 'not';
GREATER_THAN: '>';
LESS_THAN: '<';

AND: 'and';
OR: 'or';
NOT: '~';

APPEND: '::';

TRUE: 'true';
FALSE: 'false';

ARROW: '->';

ASSIGN: '=';

LPAR: '(';
RPAR: ')';
LBRACK: '[';
RBRACK: ']';
LBRACE: '{';
RBRACE: '}';

COMMA: ',';
DOT: '.';
COLON: ':';
SEMICOLLON: ';';

INT_VALUE: '0' | [1-9][0-9]*;
IDENTIFIER: [a-zA-Z_][A-Za-z0-9_]*;
STRING_VALUE: '"'~["]*'"';
COMMENT: ('#' ~( '\r' | '\n')*) -> skip;
WS: ([ \t\n\r]) -> skip;
