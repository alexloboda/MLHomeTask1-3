package ru.ifmo.rain.loboda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Substitution {
    public static ArrayList<Expression> substitute(String resource, ArrayList<Expression> toSubstitute) throws SubstitutionException {
        InputStream stream = Substitution.class.getResourceAsStream("Resources/" + resource);
        LogicStreamTokenizer tokenizer = new LogicStreamTokenizer(stream);
        Parser parser = new Parser();
        try {
            ArrayList<Expression> proof = parser.toParse(tokenizer);
            ArrayList<Expression> newProof = new ArrayList<Expression>();
            for (Expression e : proof) {
                newProof.add(e.substitute(toSubstitute));
            }
            return newProof;
        } catch (IOException e) {
            throw new SubstitutionException("Resource(" + resource + ") not found");
        } catch (ParserException e) {
            throw new SubstitutionException("Parsing resource error");
        } catch (Exception e) {
            throw new SubstitutionException("Substitution fail");
        }

    }
}
