package pl.signalbus;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class TodoTest {

    protected void todo(String message) {
        System.out.println(message);
        Assumptions.abort("TODO: " + message);
    }

    @Test
    public void _20261013205759() {
        todo("Utwórz generowanie UUID dla konta");
    }

    @Test
    public void _20261013205810() {
        todo("Ograniczenie ilości UUID dla konta w jednostce czasu");
    }

    @Test
    public void _20261013205752() {
        todo("Utwórz generowanie UUID dla transakcji");
    }

    @Test
    public void _20261013205815() {
        todo("Ograniczenie ilości UUID dla transakcji w jednostce czasu");
    }

    @Test
    public void _20261013205804() {
        todo("Ograniczenie ilości UUID dla konta w jednostce czasu");
    }

}
