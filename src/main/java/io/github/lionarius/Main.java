package io.github.lionarius;

public class Main {
    public static void main(String[] args) {
        var game = new Logibuild(args);
        game.run();
        game.close();
    }
}