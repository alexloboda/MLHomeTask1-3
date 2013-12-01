package ru.ifmo.rain.loboda;

import java.io.*;
import java.util.ArrayList;

public class DeductionTheorem {
    public static void main(String[] args) {
        try {
            if(args.length != 2){
                System.err.println("Программа принимает ровно 2 аргумента");
                System.exit(1);
            }
            LogicStreamTokenizer tokenizer = new LogicStreamTokenizer(new FileInputStream(new File(args[0])));
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(args[1])));
            try {
                Parser parser = new Parser();
                ArrayList<Expression> hyps = parser.statement(tokenizer);
                Expression toProve = hyps.get(hyps.size() - 1);
                hyps.remove(hyps.size() - 1);
                ArrayList<Expression> proof = parser.toParse(tokenizer);
                if (proof.size() == 0) {
                    printWriter.println("Нет доказательства");
                    printWriter.close();
                    System.exit(0);
                }
                if (!toProve.equals(proof.get(proof.size() - 1))) {
                    printWriter.println("Последнее высказывание не совпадает с тем, которое необходимо доказать");
                    printWriter.close();
                    System.exit(0);
                }
                ArrayList<Expression> converted = ClassicalAxioms.deductionStep(hyps, proof);
                for (Expression e : converted) {
                    printWriter.println(e);
                }
            } catch (ParserException e) {
                printWriter.print("Синтаксическая ошибка(" + e.getMessage() + ") в строке " + e.getLine());
            } catch (Exception e) {
                printWriter.print(e.getMessage());
            } finally {
                printWriter.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Не могу открыть файл для чтения/записи");
        }
    }

}