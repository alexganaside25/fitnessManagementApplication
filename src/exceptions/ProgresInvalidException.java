package exceptions;

public class ProgresInvalidException extends Throwable {
    public ProgresInvalidException() {
        super("Valoarea progresului este invalida!");
    }
}
