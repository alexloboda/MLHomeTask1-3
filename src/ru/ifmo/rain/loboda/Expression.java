package ru.ifmo.rain.loboda;

import java.util.ArrayList;
import java.util.Map;

public abstract class Expression {
    private int hashCache;
    private boolean isHashComputed;

    public boolean isIsomorphic(Expression expression) {
        return isIsomorphic(expression, new Expression[26]);
    }

    protected abstract boolean isIsomorphic(Expression expression, Expression[] vars);

    public abstract String toString();

    public abstract String toCode();

    public abstract boolean equals(Object obj);

    public abstract Expression substitute(ArrayList<Expression> toSub) throws Exception;

    public abstract boolean getMeasure(Map<Character, Boolean> hypothesis, ArrayList<Expression> proof, Map<String, Boolean> exists) throws SubstitutionException;

    @Override
    public int hashCode() {
        if (isHashComputed) {
            return hashCache;
        }
        hashCache = toString().hashCode();
        isHashComputed = true;
        return hashCache;
    }
}

