package io.github.clamentos.bpsim.predictors.implementations.bimodal;

///
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.predictors.implementations.components.BimodalBuildingBlock;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class Bimodal implements Predictor<Byte> {

///
    private final BimodalBuildingBlock innerBimodal;

    ///
    public Bimodal(final int counterSize, final int startingBias, final int tableSizeInBits, final boolean speculativeTraining, final int delayCycles) {

        innerBimodal = new BimodalBuildingBlock(counterSize, startingBias, tableSizeInBits, speculativeTraining, delayCycles);
    }

    ///
    @Override
    public Prediction<Byte> predict(final TraceEntry traceEntry,final int delayCycle) {

        return innerBimodal.predict(traceEntry, delayCycle);
    }

    ///..
    @Override
    public void train(final TraceEntry traceEntry, final Prediction<Byte> context,final int delayCycle) {

        innerBimodal.train(traceEntry, context, delayCycle);
    }

    ///..
    @Override
    public String getDescription() {

        return innerBimodal.getDescription();
    }

    ///..
    @Override
    public long getStorageCost() {

        return innerBimodal.getStorageCost();
    }

    ///..
    @Override
    public boolean doesSupportDelay() {

        return innerBimodal.isSpeculativeTraining();
    }

    ///
}
