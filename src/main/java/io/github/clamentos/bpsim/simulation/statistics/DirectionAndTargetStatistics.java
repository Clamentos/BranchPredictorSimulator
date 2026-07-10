package io.github.clamentos.bpsim.simulation.statistics;

///
import io.github.clamentos.bpsim.predictors.DirectionAndTargetPrediction;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.traces.DirectionAndTargetTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class DirectionAndTargetStatistics extends Statistics {

    ///
    private final StatisticsEntry directionEntry;
    private final StatisticsEntry targetEntry;
    private final StatisticsEntry overallEntry;

    ///
    public DirectionAndTargetStatistics(final String predictorDescription, final String traceName) {

        super(predictorDescription, traceName);

        directionEntry = new StatisticsEntry();
        targetEntry = new StatisticsEntry();
        overallEntry = new StatisticsEntry();
    }

    ///
    @Override
    public void update(final TraceEntry traceEntry, final Prediction<?> prediction) {

        final DirectionAndTargetTraceEntry directionAndTargetTraceEntry = (DirectionAndTargetTraceEntry)traceEntry;
        final DirectionAndTargetPrediction<?> directionAndTargetPrediction = (DirectionAndTargetPrediction<?>)prediction;
        final boolean isDirectionCorrect = directionAndTargetTraceEntry.isTaken() == directionAndTargetPrediction.isTaken();
        final boolean isTargetCorrect = directionAndTargetTraceEntry.getTarget() == directionAndTargetPrediction.getTarget();

        if(isDirectionCorrect) directionEntry.correct();
        else directionEntry.incorrect();

        if(isTargetCorrect) targetEntry.correct();
        else targetEntry.incorrect();

        if(isDirectionCorrect && isTargetCorrect) overallEntry.correct();
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
