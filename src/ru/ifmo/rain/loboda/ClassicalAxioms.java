package ru.ifmo.rain.loboda;

import java.util.ArrayList;

public class ClassicalAxioms {
    private static ArrayList<Expression> axioms = new ArrayList<Expression>();
    private static boolean init = false;

    public static ArrayList<Expression> aFollowA(Expression alpha) throws SubstitutionException {
        ArrayList<Expression> tmp = new ArrayList<Expression>();
        tmp.add(alpha);
        return Substitution.substitute("AFollowA", tmp);
    }

    public static ArrayList<Expression> getAxioms() throws SubstitutionException {
        if (!init) {
            ArrayList<Expression> tmp = new ArrayList<Expression>();
            tmp.add(new Variable('A'));
            tmp.add(new Variable('B'));
            tmp.add(new Variable('C'));
            axioms = Substitution.substitute("ClassicalAxioms", tmp);
            init = true;
        }
        return axioms;
    }

    public static int isAxiom(Expression e) throws SubstitutionException {
        getAxioms();
        for (int i = 0; i < axioms.size(); i++) {
            if (e.isIsomorphic(axioms.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<Expression> deductionStep(ArrayList<Expression> hyps, ArrayList<Expression> proof) throws Exception {
        ArrayList<Expression> newProof = new ArrayList<Expression>();
        if (hyps.size() == 0) {
            return null;
        }
        Checker checker = new Checker(proof, hyps);
        Expression parcel = hyps.get(hyps.size() - 1);
        for (int i = 0; i < proof.size(); ++i) {
            Expression cur = proof.get(i);
            if (parcel.equals(cur)) {
                //A->A
                checker.next();
                newProof.addAll(ClassicalAxioms.aFollowA(parcel));
                continue;
            }
            Checker.Type type = checker.next();
            if (type == Checker.Type.ERROR) {
                throw new Exception("Доказательство некорректно");
            }
            if (type == Checker.Type.HYPOTHESIS) {
                // parcel, parcel->cur->parcel, cur->parcel
                newProof.add(cur);
                newProof.add(new Implication(cur, new Implication(parcel, cur)));
                newProof.add(new Implication(parcel, cur));
                continue;
            }
            if (type == Checker.Type.AXIOM) {
                // Axiom
                newProof.add(cur);
                newProof.add(new Implication(cur, new Implication(parcel, cur)));
                newProof.add(new Implication(parcel, cur));
                continue;
            }
            // Modus Ponens
            Expression from = checker.getPonensParcel();
            newProof.add(new Implication(new Implication(parcel, from), new Implication(new Implication(parcel, new Implication(from, cur)), new Implication(parcel, cur))));
            newProof.add(new Implication(new Implication(parcel, new Implication(from, cur)), new Implication(parcel, cur)));
            newProof.add(new Implication(parcel, cur));
        }
        return newProof;
    }
}
