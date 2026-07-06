package io.github.clamentos.bpsim.predictors.implementations;

///
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class Static implements Predictor<Void> {

    ///
    final boolean predictTaken;

    ///
    public Static(final boolean predictTaken) {

        this.predictTaken = predictTaken;
    }

    ///
    @Override
    public Prediction<Void> predict(final TraceEntry traceEntry) {

        return new Prediction<>(predictTaken, Integer.MIN_VALUE, false, null);
    }

    ///..
    @Override
    public void train(final TraceEntry traceEntry, final Prediction<Void> context) {

        // noop
    }

    ///..
    @Override
    public String getDescription() {

        return "Static predictor (always " + (predictTaken ? "taken" : "not taken") + ")";
    }

    ///..
    @Override
    public int getStorageCost() {

        return 0;
    }

    ///
}
