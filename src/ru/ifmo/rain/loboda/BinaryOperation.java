package ru.ifmo.rain.loboda;

import java.util.ArrayList;
import java.util.Map;

public abstract class BinaryOperation extends Expression {
    private Expression left, right;
    private String stringCache;

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public BinaryOperation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    protected boolean isIsomorphic(Expression expression, Expression[] vars) {
        if (expression.getClass() == Variable.class) {
            char ch = ((Variable) expression).getName();
            if (vars[ch - 'A'] == null) {
                vars[ch - 'A'] = this;
                return true;
            } else {
                return equals(vars[ch - 'A']);
            }
        }
        if (this.getClass() != expression.getClass()) {
            return false;
        }
        return left.isIsomorphic(((BinaryOperation) expression).getLeft(), vars) && right.isIsomorphic(((BinaryOperation) expression).getRight(), vars);
    }

    public boolean getMeasure(Map<Character, Boolean> hypothesis, ArrayList<Expression> proof, Map<String, Boolean> exists) throws SubstitutionException {
        if (exists.containsKey(this.toString())) {
            return true;
        }
        if (exists.containsKey((new OperationNot(this).toString()))) {
            return false;
        }
        boolean left = getLeft().getMeasure(hypothesis, proof, exists);
        ArrayList<Expression> toReplace = new ArrayList<Expression>();
        toReplace.add(getLeft());

        boolean right = getRight().getMeasure(hypothesis, proof, exists);
        String resourceName = "";
        if (!left) {
            resourceName += "not";
        }
        resourceName += "A";
        if (!right) {
            resourceName += "not";
        }
        resourceName += "B";
        resourceName += getShortName();
        toReplace.add(getRight());
        proof.addAll(Substitution.substitute(resourceName, toReplace));
        Expression last = proof.get(proof.size() - 1);
        exists.put(last.toString(), true);
        return last.equals(this);
    }

    public String toString() {
        if (stringCache != null) {
            return stringCache;
        }
        String leftS;
        String rightS;
        if (left.getClass() == Variable.class) {
            leftS = left.toString();
        } else {
            leftS = "(" + left.toString() + ")";
        }
        if (right.getClass() == Variable.class) {
            rightS = right.toString();
        } else {
            rightS = "(" + right.toString() + ")";
        }
        //return leftS + getSign() + rightS;
        stringCache = leftS + getSign() + rightS;
        return stringCache;
    }

    public String toCode() {
        return "new " + getName() + "(" + left.toCode() + ", " + right.toCode() + ")";
    }

    public boolean equals(Object obj) {
        if (obj instanceof BinaryOperation) {
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            return left.equals(((BinaryOperation) obj).getLeft()) && right.equals(((BinaryOperation) obj).getRight());
        } else {
            return false;
        }
    }

    public abstract Expression substitute(ArrayList<Expression> toSub) throws Exception;

    protected abstract String getName();

    protected abstract String getSign();

    protected abstract String getShortName();
}
