package ru.ifmo.rain.loboda;

import java.util.ArrayList;

public class OperationOr extends BinaryOperation {
    public OperationOr(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression substitute(ArrayList<Expression> toSub) throws Exception {
        return new OperationOr(getLeft().substitute(toSub), getRight().substitute(toSub));
    }

    @Override
    protected String getName() {
        return "OperationOr";
    }

    @Override
    protected String getSign() {
        return "|";
    }

    @Override
    protected String getShortName() {
        return "or";
    }
}
