package io.github.clamentos.bpsim.predictors;

///
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public interface Predictor<T> {

    ///
    Prediction<T> predict(final TraceEntry traceEntry, final int delayCycle);
    void train(final TraceEntry traceEntry, final Prediction<T> context, final int delayCycle);
    String getDescription();
    long getStorageCost();
    boolean doesSupportDelay();

    ///
}
