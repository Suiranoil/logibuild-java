package io.github.lionarius;

public class Main {
    public static void main(String[] args) {
        var engine = new Logibuild(args);
        engine.run();
        engine.close();
    }
}