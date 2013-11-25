package ru.ifmo.rain.loboda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private Token token;
    Map<Expression, Expression> references = new HashMap<Expression, Expression>();
    Expression toReturn;

    public ArrayList<Expression> toParse(LogicStreamTokenizer tokenizer) throws IOException, ParserException {
        ArrayList<Expression> list = new ArrayList<Expression>();
        token = Token.PRINT;
        while (true) {
            token = tokenizer.nextToken();
            if (token == Token.PRINT) {
                continue;
            }
            if (token == Token.END) {
                break;
            }
            list.add(expression(tokenizer));

            if (token != Token.PRINT && token != Token.END) {
                throw new ParserException("Syntax error", tokenizer.getLine());
            }
        }
        return list;
    }

    //!, (, ), A
    private Expression primary(LogicStreamTokenizer tokenizer) throws IOException, ParserException {

        if (token == null) {
            token = tokenizer.nextToken();
        }
        switch (token) {
            case VARIABLE:
                char ch = tokenizer.variableName;
                token = tokenizer.nextToken();
                toReturn = new Variable(ch);
                if (references.containsKey(toReturn)) {
                    return references.get(toReturn);
                }
                references.put(toReturn, toReturn);
                return toReturn;
            case LP:
                token = tokenizer.nextToken();
                Expression expression = expression(tokenizer);
                if (token != Token.RP) {
                    if (token == Token.PRINT) {
                        throw new ParserException("Expected ')'", tokenizer.getLine() - 1);
                    } else {
                        if (token == Token.NOT || token == Token.LP) {
                            throw new ParserException("Syntax error", tokenizer.getLine());
                        } else {
                            throw new ParserException("Expected ')'", tokenizer.getLine());
                        }
                    }
                }
                token = tokenizer.nextToken();
                return expression;
            case NOT:
                token = null;
                toReturn = new OperationNot(primary(tokenizer));
                if (references.containsKey(toReturn)) {
                    return references.get(toReturn);
                }
                references.put(toReturn, toReturn);
                return toReturn;
            default:
                if (token == Token.PRINT) {
                    throw new ParserException("Expected primary expression", tokenizer.getLine() - 1);
                } else {
                    throw new ParserException("Expected primary expression", tokenizer.getLine());
                }
        }
    }

    // &
    private Expression andExpression(LogicStreamTokenizer tokenizer) throws IOException, ParserException {
        Expression left = primary(tokenizer);
        while (true) {
            if (token == Token.AND) {
                token = null;
                toReturn = new OperationAnd(left, primary(tokenizer));
                if (references.containsKey(toReturn)) {
                    toReturn = references.get(toReturn);
                } else {
                    references.put(toReturn, toReturn);
                }
                left = toReturn;
            } else {
                return left;
            }
        }
    }

    // |
    private Expression orExpression(LogicStreamTokenizer tokenizer) throws IOException, ParserException {
        Expression left = andExpression(tokenizer);
        while (true) {
            if (token == Token.OR) {
                token = null;
                toReturn = new OperationOr(left, andExpression(tokenizer));
                if (references.containsKey(toReturn)) {
                    toReturn = references.get(toReturn);
                } else {
                    references.put(toReturn, toReturn);
                }
                left = toReturn;
            } else {
                return left;
            }
        }
    }

    // ->
    private Expression expression(LogicStreamTokenizer tokenizer) throws IOException, ParserException {
        Expression left = orExpression(tokenizer);
        if (token == Token.IMPLICATION) {
            token = null;
            toReturn = new Implication(left, expression(tokenizer));
            if (references.containsKey(toReturn)) {
                return references.get(toReturn);
            }
            references.put(toReturn, toReturn);
            return toReturn;
        } else {
            return left;
        }
    }

    public ArrayList<Expression> statement(LogicStreamTokenizer tokenizer) throws IOException, ParserException {
        Expression left = expression(tokenizer);
        ArrayList<Expression> list = new ArrayList<Expression>();
        while (token == Token.COMMA) {
            list.add(left);
            token = null;
            left = expression(tokenizer);
        }
        if (token != Token.PROVABLY) {
            throw new ParserException("Operator |- expected", tokenizer.getLine() - (token == Token.PRINT ? 1 : 0));
        }
        list.add(left);
        token = null;
        Expression right = expression(tokenizer);
        list.add(right);
        return list;
    }
}
