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
    private final String predictorName;
    private final String traceName;

    ///..
    private int conflicts;

    ///
    protected Statistics(final String predictorName, final String traceName) {

        this.predictorName = predictorName;
        this.traceName = traceName;
    }

    ///
    public void update(final TraceEntry traceEntry, final Prediction<?> prediction) {

        if(prediction.isCollided()) conflicts++;
        this.update(traceEntry, prediction.isTaken(), prediction.getTarget());
    }

    ///.
    protected abstract void update(final TraceEntry traceEntry, final boolean isPredictedTaken, final long predictedTarget);

    ///
}
