package ru.ifmo.rain.loboda;

import java.io.*;
import java.util.*;

public class Prover {
    public static void main(String[] args) throws InterruptedException {
        try {
            if(args.length != 2){
                System.err.println("Программа принимает ровно 2 аргумента");
                System.exit(1);
            }
            LogicStreamTokenizer tokenizer = new LogicStreamTokenizer(new FileInputStream(new File(args[0])));
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(args[1])));
            try {
                Parser parser = new Parser();
                ArrayList<Expression> list = parser.toParse(tokenizer);
                if (list.size() != 1) {
                    printWriter.println("Входной файл должен содержать ровно одну строку-высказывание");
                    printWriter.close();
                }
                Expression expression = list.get(0);
                try {
                    ArrayList<Expression> proof = prove(expression);
                    for (Expression e : proof) {
                        printWriter.println(e);
                    }
                } catch (Exception e) {
                    printWriter.println(e.getMessage());
                } catch (ProverException e) {
                    printWriter.println(e.getMessage());
                } finally {
                    printWriter.close();
                }
            } catch (ParserException e) {
                printWriter.print("Синтаксическая ошибка(" + e.getMessage() + ") в строке " + e.getLine());
                printWriter.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Не могу открыть файл для чтения/записи");
        } catch (IOException e) {
            System.err.println("IO error");
        }
    }

    public static ArrayList<Expression> prove(Expression expression) throws Exception, ProverException {
        String s = expression.toString();
        ArrayList<Character> propVariables = new ArrayList<Character>();
        Map<Character, Boolean> hypothesis = new HashMap<Character, Boolean>();
        for (int i = 0; i < s.length(); ++i) {
            char curCh = s.charAt(i);
            if (curCh >= 'A' && curCh <= 'Z') {
                if (!propVariables.contains(curCh)) {
                    propVariables.add(curCh);
                }
            }
        }
        return recursive(expression, 0, propVariables, hypothesis, new ArrayList<Expression>());
    }

    private static ArrayList<Expression> recursive(Expression expression, int numRec, ArrayList<Character> propVariables, Map<Character, Boolean> hypothethis, ArrayList<Expression> hyps) throws Exception, ProverException {
        if (numRec == propVariables.size()) {
            ArrayList<Expression> proof = new ArrayList<Expression>();
            boolean good = expression.getMeasure(hypothethis, proof, new HashMap<String, Boolean>());
            if (!good) {
                String error = "Высказывание ложно при ";
                Set<Character> vars = hypothethis.keySet();
                Iterator<Character> it = vars.iterator();
                while(it.hasNext()){
                    char var = it.next();
                    if(hypothethis.get(var)){
                        error += var;
                        error += "=И, ";
                    } else {
                        error += var;
                        error += "=Л, ";
                    }
                }
                error = error.substring(0, error.length() - 2);
                throw new ProverException(error);
            }
            return proof;
        }
        char curCh = propVariables.get(numRec);
        ArrayList<Expression> proof = new ArrayList<Expression>();
        hyps.add(new Variable(curCh));
        hypothethis.put(curCh, true);
        proof.addAll(ClassicalAxioms.deductionStep(hyps, recursive(expression, numRec + 1, propVariables, hypothethis, hyps)));
        hypothethis.put(curCh, false);
        hyps.remove(hyps.get(hyps.size() - 1));
        hyps.add(new OperationNot(new Variable(curCh)));
        ArrayList<Expression> alpha = recursive(expression, numRec + 1, propVariables, hypothethis, hyps);
        proof.addAll(ClassicalAxioms.deductionStep(hyps, alpha));
        hyps.remove(hyps.get(hyps.size() - 1));
        ArrayList<Expression> toReplace = new ArrayList<Expression>();
        toReplace.add(new Variable(curCh));
        toReplace.add(alpha.get(alpha.size() - 1));
        proof.addAll(Substitution.substitute("discard", toReplace));
        return proof;
    }
}
