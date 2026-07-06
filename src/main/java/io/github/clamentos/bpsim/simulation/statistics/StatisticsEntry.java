package io.github.clamentos.bpsim.simulation.statistics;

///
public final class StatisticsEntry {

    ///
    private int correct;
    private int incorrect;

    ///
    public void correct() {

        correct++;
    }

    ///..
    public void incorrect() {

        incorrect++;
    }

    ///..
    public float calculateAccuracy() {

        return (correct / (float)(correct + incorrect)) * 100;
    }

    ///
}
