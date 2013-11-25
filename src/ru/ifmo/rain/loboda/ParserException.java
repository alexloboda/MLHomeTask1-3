package ru.ifmo.rain.loboda;

public class ParserException extends Exception {
    private int line;

    public ParserException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}