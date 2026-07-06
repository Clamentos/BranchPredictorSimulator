package io.github.clamentos.bpsim.simulation.statistics;

///
import io.github.clamentos.bpsim.traces.DirectionAndTargetTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class DirectionAndTargetStatistics extends Statistics {

    ///
    private final StatisticsEntry directionEntry;
    private final StatisticsEntry targetEntry;
    private final StatisticsEntry overallEntry;

    ///
    public DirectionAndTargetStatistics(final String predictorName, final String traceName) {

        super(predictorName, traceName);

        directionEntry = new StatisticsEntry();
        targetEntry = new StatisticsEntry();
        overallEntry = new StatisticsEntry();
    }

    ///
    @Override
    public void update(final TraceEntry traceEntry, final boolean isPredictedTaken, final long predictedTarget) {

        final DirectionAndTargetTraceEntry directionAndTargetTraceEntry = (DirectionAndTargetTraceEntry)traceEntry;

        if(directionAndTargetTraceEntry.isTaken() == isPredictedTaken) directionEntry.correct();
        else directionEntry.incorrect();

        if(directionAndTargetTraceEntry.getTarget() == predictedTarget) targetEntry.correct();
        else targetEntry.incorrect();

        if(directionAndTargetTraceEntry.isTaken() == isPredictedTaken && directionAndTargetTraceEntry.getTarget() == predictedTarget) overallEntry.correct();
        else overallEntry.incorrect();
    }

    ///..
    public float calculateDirectionAccuracy() {

        return directionEntry.calculateAccuracy();
    }

    ///..
    public float calculateTargetAccuracy() {

        return targetEntry.calculateAccuracy();
    }

    ///..
    public float calculateOverallAccuracy() {

        return overallEntry.calculateAccuracy();
    }

    ///
}
