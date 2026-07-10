package io.github.clamentos.bpsim.predictors.implementations;

///
import io.github.clamentos.bpsim.predictors.DirectionPrediction;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class Static implements Predictor<Void> {

    ///
    final DirectionPrediction<Void> prediction;

    ///
    public Static(final boolean predictTaken) {

        prediction = new DirectionPrediction<>();
        prediction.setTaken(predictTaken);
    }

    ///
    @Override
    public Prediction<Void> predict(final TraceEntry traceEntry, final int delayCycle) {

        return prediction;
    }

    ///..
    @Override
    public void train(final TraceEntry traceEntry, final Prediction<Void> context, final int delayCycle) {

        // noop
    }

    ///..
    @Override
    public String getDescription() {

        return "Static predictor (always " + (prediction.isTaken() ? "taken" : "not taken") + ")";
    }

    ///..
    @Override
    public long getStorageCost() {

        return 0L;
    }

    ///..
    @Override
    public boolean doesSupportDelay() {

        return true;
    }

    ///
}
