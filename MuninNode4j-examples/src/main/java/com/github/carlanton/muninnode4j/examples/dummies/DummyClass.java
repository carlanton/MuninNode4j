package com.github.carlanton.muninnode4j.examples.dummies;

import com.github.carlanton.muninnode4j.MuninMonitor;

import java.util.Random;

class DummyClass {
    @MuninMonitor(title = "graph_title", args = "args", category = "cat",
        info = "info", order = "order", scale = "scale", period = "period",
        vlabel = "vlabel", labels = {"dice1","dice2","dice3"})
    public static Integer[] demoGraphDice() {
        Random generator = new Random(System.currentTimeMillis());

        int dice1 = generator.nextInt(6) + 1;
        int dice2 = generator.nextInt(6) + 1;
        int dice3 = generator.nextInt(6) + 1;

        return new Integer[] {dice1, dice2, dice3};
    }


    @MuninMonitor
    public static int demoRandomGenerator() {
        return 4;
    }

    @MuninMonitor(title = "demo-graph-mixed", labels = {"value1", "value2"})
    public static Number[] demoGraphMixed() {
        Random generator = new Random(System.currentTimeMillis());

        int value1 = generator.nextInt(10);
        double value2 = generator.nextDouble();

        return new Number[] {value1, value2};
    }
}