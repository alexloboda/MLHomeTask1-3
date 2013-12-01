package ru.ifmo.rain.loboda;

import java.util.*;

public class Checker {
    private List<Expression> proof;
    private Map<Expression, Boolean> proved;
    private ListIterator<Expression> iterator;
    private Expression lastPonens;
    private Map<Expression, List<Expression>> rightParts;
    private boolean globalError;
    private Map<Expression, Boolean> hypothesis;

    private void init(List<Expression> proof) {
        this.proof = proof;
        proved = new HashMap<Expression, Boolean>();
        iterator = proof.listIterator();
        rightParts = new HashMap<Expression, List<Expression>>();
        globalError = false;
    }

    public Checker(List<Expression> proof, List<Expression> hypothesis) {
        this.hypothesis = new HashMap<Expression, Boolean>();
        for (Expression e : hypothesis) {
            this.hypothesis.put(e, true);
        }
        init(proof);
    }

    public Checker(List<Expression> proof) {
        init(proof);
    }

    public boolean hasMore() {
        return iterator.hasNext();
    }

    public Expression getPonensParcel() {
        return lastPonens;
    }

    public Type next() throws SubstitutionException {
        Expression current = iterator.next();
        if (globalError) {
            return Type.ERROR;
        }
        Type type = Type.ERROR;
        if (ClassicalAxioms.isAxiom(current) != -1) {
            proved.put(current, true);
            type = Type.AXIOM;
        }
        if (type == Type.ERROR) {
            List<Expression> candidates = rightParts.get(current);
            if (candidates != null) {
                ListIterator<Expression> it = candidates.listIterator();
                while (it.hasNext()) {
                    Expression parcel = it.next();
                    if (proved.get(parcel) != null) {
                        type = Type.PONENS;
                        lastPonens = parcel;
                        break;
                    }
                }
            }
        }

        if (type == Type.ERROR && hypothesis != null) {
            if (hypothesis.get(current) != null) {
                type = Type.HYPOTHESIS;
            }
        }

        if (type == Type.ERROR) {
            globalError = true;
            return type;
        }
        if (current.getClass() == Implication.class) {
            Expression right = ((Implication) current).getRight();
            if (rightParts.get(right) == null) {
                rightParts.put(right, new ArrayList<Expression>());
            }
            rightParts.get(right).add(((Implication) current).getLeft());
        }
        proved.put(current, true);
        return type;
    }

    public enum Type {
        AXIOM, PONENS, ERROR, HYPOTHESIS
    }
}
