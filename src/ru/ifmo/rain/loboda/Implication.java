package ru.ifmo.rain.loboda;

import java.util.ArrayList;

public class Implication extends BinaryOperation {


    public Implication(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression substitute(ArrayList<Expression> toSub) throws Exception {
        return new Implication(getLeft().substitute(toSub), getRight().substitute(toSub));
    }

    @Override
    protected String getName() {
        return "Implication";
    }

    @Override
    protected String getSign() {
        return "->";
    }

    @Override
    protected String getShortName() {
        return "follow";
    }
}
