package ru.ifmo.rain.loboda;

import java.util.ArrayList;
import java.util.Map;

public class OperationNot extends Expression {
    private Expression expression;

    public OperationNot(Expression expression) {
        this.expression = expression;
    }

    Expression getExpression() {
        return expression;
    }

    protected boolean isIsomorphic(Expression expression, Expression[] vars) {
        if (expression instanceof OperationNot) {
            return this.expression.isIsomorphic(((OperationNot) expression).getExpression(), vars);
        }
        if (expression instanceof Variable) {
            char ch = ((Variable) expression).getName();
            if (vars[ch - 'A'] == null) {
                vars[ch - 'A'] = this;
                return true;
            } else {
                return equals(vars[ch - 'A']);
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj.getClass() == OperationNot.class) {
            return expression.equals(((OperationNot) obj).getExpression());
        } else {
            return false;
        }
    }

    @Override
    public Expression substitute(ArrayList<Expression> toSub) throws Exception {
        return new OperationNot(expression.substitute(toSub));
    }

    @Override
    public boolean getMeasure(Map<Character, Boolean> hypothesis, ArrayList<Expression> proof, Map<String, Boolean> exists) throws SubstitutionException {
        if (exists.containsKey(this.toString())) {
            return true;
        }
        if (exists.containsKey((new OperationNot(this).toString()))) {
            return false;
        }
        boolean inner = expression.getMeasure(hypothesis, proof, exists);
        ArrayList<Expression> toReplace = new ArrayList<Expression>();
        if (inner) {
            toReplace.add(expression);
            exists.put((new OperationNot(this)).toString(), true);
            proof.addAll(Substitution.substitute("AnotnotA", toReplace));
            return false;
        } else {
            exists.put(this.toString(), true);
            return true;
        }
    }

    public String toString() {
        if (expression.getClass() == Variable.class) {
            return "!" + expression.toString();
        }
        return "!(" + expression.toString() + ")";
    }

    public String toCode() {
        return "new OperatorNot(" + expression.toCode() + ")";
    }
}
