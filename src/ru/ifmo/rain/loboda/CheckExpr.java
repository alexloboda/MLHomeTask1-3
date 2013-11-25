package ru.ifmo.rain.loboda;

import java.io.*;
import java.util.ArrayList;

public class CheckExpr {

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
                ArrayList<Expression> proof = parser.toParse(tokenizer);
                Checker checker = new Checker(proof);
                int i = 0;
                while (checker.hasMore()) {
                    ++i;
                    if (checker.next() == Checker.Type.ERROR) {
                        printWriter.print("Доказательство некорректно начиная с " + (new Integer(i)).toString() + " высказывания");
                        printWriter.close();
                        System.exit(0);
                    }
                }
                printWriter.print("Доказательство корректно.");
            } catch (ParserException e) {
                printWriter.print("Синтаксическая ошибка(" + e.getMessage() + ") в строке " + e.getLine());
            } catch (SubstitutionException e) {
                printWriter.print("Непредвиденная ошибка");
            }
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("Не могу открыть файл для чтения/записи");
        } catch (IOException e) {
            System.err.println("Error occurred while reading file");
        }
    }
}