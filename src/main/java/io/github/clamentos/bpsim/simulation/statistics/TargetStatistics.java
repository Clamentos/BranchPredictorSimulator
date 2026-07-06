package io.github.clamentos.bpsim.simulation.statistics;

///
import io.github.clamentos.bpsim.traces.TargetTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class TargetStatistics extends Statistics {

    ///
    private final StatisticsEntry statisticsEntry;

    ///
    public TargetStatistics(final String predictorName, final String traceName) {

        super(predictorName, traceName);
        statisticsEntry = new StatisticsEntry();
    }

    ///
    @Override
    public void update(final TraceEntry traceEntry, final boolean isPredictedTaken, final long predictedTarget) {

        final TargetTraceEntry targetTraceEntry = (TargetTraceEntry)traceEntry;

        if(targetTraceEntry.getTarget() == predictedTarget) statisticsEntry.correct();
        else statisticsEntry.incorrect();
    }

    ///..
    public float calculateAccuracy() {

        return statisticsEntry.calculateAccuracy();
    }

    ///
}
