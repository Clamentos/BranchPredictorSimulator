package io.github.clamentos.bpsim.simulation.statistics;

///
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.TargetPrediction;
import io.github.clamentos.bpsim.traces.TargetTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class TargetStatistics extends Statistics {

    ///
    private final StatisticsEntry statisticsEntry;

    ///
    public TargetStatistics(final String predictorDescription, final String traceName) {

        super(predictorDescription, traceName);
        statisticsEntry = new StatisticsEntry();
    }

    ///
    @Override
    public void update(final TraceEntry traceEntry, final Prediction<?> prediction) {

        final TargetTraceEntry targetTraceEntry = (TargetTraceEntry)traceEntry;
        final TargetPrediction<?> targetPrediction = (TargetPrediction<?>)prediction;

        if(targetTraceEntry.getTarget() == targetPrediction.getTarget()) statisticsEntry.correct();
        else statisticsEntry.incorrect();
    }

    ///..
    public float calculateAccuracy() {

        return statisticsEntry.calculateAccuracy();
    }

    ///
}
