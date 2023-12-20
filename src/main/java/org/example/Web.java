package org.example;

public class Web {
    public static void main(String[] args) throws Exception {
        new MainVerticle(25565, "indexes").start();
    }
}
