package expression.parser;

public interface CharSource {
    boolean hasNext();
    char getNext();
    char next();
    IllegalArgumentException error(String message);
}
