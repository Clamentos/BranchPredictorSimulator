package io.github.clamentos.bpsim.predictors;

///
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public interface Predictor<T> {

    ///
    Prediction<T> predict(final TraceEntry traceEntry);
    void train(final TraceEntry traceEntry, final Prediction<T> context);
    String getDescription();
    int getStorageCost();

    ///
}
