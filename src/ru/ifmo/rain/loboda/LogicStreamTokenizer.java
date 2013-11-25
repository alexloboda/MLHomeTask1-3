package ru.ifmo.rain.loboda;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class LogicStreamTokenizer {
    public char variableName;
    private PushbackInputStream stream;
    private int line = 1;

    public LogicStreamTokenizer(InputStream stream) {
        this.stream = new PushbackInputStream(new BufferedInputStream(stream));
    }

    public int getLine() {
        return line;
    }

    public Token nextToken() throws IOException, ParserException {
        int ch = stream.read();
        while (ch == ' ') {
            ch = stream.read();
        }
        if (ch < 0) {
            return Token.END;
        }
        switch (ch) {
            case '&':
                return Token.AND;
            case '|':
                ch = stream.read();
                if (ch == '-') {
                    return Token.PROVABLY;
                }
                if (ch != -1) {
                    stream.unread(ch);
                }
                return Token.OR;
            case '!':
                return Token.NOT;
            case '\n':
                ++line;
                return Token.PRINT;
            case ',':
                return Token.COMMA;
            case '-':
                ch = stream.read();
                if (ch == '>') {
                    return Token.IMPLICATION;
                } else {
                    throw new ParserException("Expected ->", line);
                }
            case '(':
                return Token.LP;
            case ')':
                return Token.RP;
            default:
                if (ch >= 'A' && ch <= 'Z') {
                    variableName = (char) ch;
                    return Token.VARIABLE;
                } else {
                    throw new ParserException("Unrecognized token", line);
                }
        }
    }
}