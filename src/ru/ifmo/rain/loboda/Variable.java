package ru.ifmo.rain.loboda;

import java.util.ArrayList;
import java.util.Map;

public class Variable extends Expression {
    char ch;

    public Variable(char ch) {
        this.ch = ch;
    }

    char getName() {
        return ch;
    }

    public boolean equals(Object obj) {
        if (obj.getClass() == Variable.class) {
            return ch == ((Variable) obj).getName();
        } else {
            return false;
        }
    }

    @Override
    public Expression substitute(ArrayList<Expression> toSub) throws Exception {
        Expression toReplace = toSub.get(ch - 'A');
        if (toReplace != null) {
            return toReplace;
        } else {
            throw new Exception("Crashed");
        }
    }

    @Override
    public boolean getMeasure(Map<Character, Boolean> hypothesis, ArrayList<Expression> proof, Map<String, Boolean> exists) throws SubstitutionException {
        if (exists.containsKey(this.toString())) {
            return true;
        }
        if (exists.containsKey((new OperationNot(this).toString()))) {
            return false;
        }
        Boolean truth = hypothesis.get(ch);
        if (truth == null) {
            throw new SubstitutionException("Crashed during searching info about variable");
        }
        if (truth) {
            exists.put(this.toString(), true);
            proof.add(this);
            return true;
        } else {
            exists.put((new OperationNot(this)).toString(), true);
            proof.add(new OperationNot(this));
            return false;
        }
    }

    protected boolean isIsomorphic(Expression expression, Expression[] vars) {
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

    public String toString() {
        return (new Character(ch)).toString();
    }

    public String toCode() {
        return "new Variable('" + ch + "')";
    }
}
