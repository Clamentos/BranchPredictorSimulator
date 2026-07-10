package io.github.clamentos.bpsim.simulation.statistics;

///
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.traces.TraceEntry;

///..
import lombok.Getter;

///
@Getter

///
public abstract class Statistics {

    ///
    private final String predictorDescription;
    private final String traceName;

    ///
    protected Statistics(final String predictorDescription, final String traceName) {

        this.predictorDescription = predictorDescription;
        this.traceName = traceName;
    }

    ///
    public abstract void update(final TraceEntry traceEntry, final Prediction<?> prediction);

    ///
}
