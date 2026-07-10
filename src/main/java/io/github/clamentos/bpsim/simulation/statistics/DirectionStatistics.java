package io.github.clamentos.bpsim.simulation.statistics;

///
import io.github.clamentos.bpsim.predictors.DirectionPrediction;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.traces.DirectionTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class DirectionStatistics extends Statistics {

    ///
    private final StatisticsEntry statisticsEntry;

    ///
    public DirectionStatistics(final String predictorDescription, final String traceName) {

        super(predictorDescription, traceName);
        statisticsEntry = new StatisticsEntry();
    }

    ///
    @Override
    public void update(final TraceEntry traceEntry, final Prediction<?> prediction) {

        final DirectionTraceEntry directionTraceEntry = (DirectionTraceEntry)traceEntry;
        final DirectionPrediction<?> directionPrediction = (DirectionPrediction<?>)prediction;

        if(directionTraceEntry.isTaken() == directionPrediction.isTaken()) statisticsEntry.correct();
        else statisticsEntry.incorrect();
    }

    ///..
    public float calculateAccuracy() {

        return statisticsEntry.calculateAccuracy();
    }

    ///
}
