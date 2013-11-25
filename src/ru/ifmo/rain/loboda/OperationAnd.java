package ru.ifmo.rain.loboda;

import java.util.ArrayList;

public class OperationAnd extends BinaryOperation {
    public OperationAnd(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression substitute(ArrayList<Expression> toSub) throws Exception {
        return new OperationAnd(getLeft().substitute(toSub), getRight().substitute(toSub));
    }

    @Override
    protected String getName() {
        return "OperationAnd";
    }

    @Override
    protected String getSign() {
        return "&";
    }

    @Override
    protected String getShortName() {
        return "and";
    }
}
