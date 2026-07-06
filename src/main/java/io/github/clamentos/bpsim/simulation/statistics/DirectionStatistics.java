package io.github.clamentos.bpsim.simulation.statistics;

///
import io.github.clamentos.bpsim.traces.DirectionTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class DirectionStatistics extends Statistics {

    ///
    private final StatisticsEntry statisticsEntry;

    ///
    public DirectionStatistics(final String predictorName, final String traceName) {

        super(predictorName, traceName);
        statisticsEntry = new StatisticsEntry();
    }

    ///
    @Override
    public void update(final TraceEntry traceEntry, final boolean isPredictedTaken, final long predictedTarget) {

        final DirectionTraceEntry directionTraceEntry = (DirectionTraceEntry)traceEntry;

        if(directionTraceEntry.isTaken() == isPredictedTaken) statisticsEntry.correct();
        else statisticsEntry.incorrect();
    }

    ///..
    public float calculateAccuracy() {

        return statisticsEntry.calculateAccuracy();
    }

    ///
}
