package io.github.clamentos.bpsim.predictors.implementations.gshare;

///
import io.github.clamentos.bpsim.predictors.DirectionPrediction;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.predictors.implementations.components.BimodalBuildingBlock;
import io.github.clamentos.bpsim.traces.DirectionTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///..
import java.util.ArrayList;
import java.util.List;

///
public final class GShare implements Predictor<GShareContext> {

    ///
    private final BimodalBuildingBlock innerBimodal;
    private long history;

    ///..
    private final List<DirectionPrediction<GShareContext>> predictionHolders;

    ///
    public GShare(final int counterSize, final int startingBias, final int tableSizeInBits, final int delayCycles) {

        innerBimodal = new BimodalBuildingBlock(counterSize, startingBias, tableSizeInBits, false, delayCycles);

        predictionHolders = new ArrayList<>(delayCycles);

        for(int i = 0; i < delayCycles; i++) {

            final DirectionPrediction<GShareContext> predictionHolder = new DirectionPrediction<>();
            predictionHolder.setContext(new GShareContext());
            predictionHolders.add(predictionHolder);
        }
    }

    ///
    @Override
    public Prediction<GShareContext> predict(final TraceEntry traceEntry, final int delayCycle) {

        final int index = (int)((traceEntry.getAddress() ^ history) & innerBimodal.getAddressMask());
        final DirectionPrediction<Byte> bimodalPrediction = (DirectionPrediction<Byte>)innerBimodal.predict(index, delayCycle);
        final DirectionPrediction<GShareContext> predictionHolder = predictionHolders.get(delayCycle);

        predictionHolder.setTaken(bimodalPrediction.isTaken());
        predictionHolder.getContext().setCounterValue(bimodalPrediction.getContext());
        predictionHolder.getContext().setHistory(history);

        return predictionHolder;
    }

    ///..
    @Override
    public void train(final TraceEntry traceEntry, final Prediction<GShareContext> context, final int delayCycle) {

        final int index = (int)((traceEntry.getAddress() ^ history) & innerBimodal.getAddressMask());
        final boolean isTaken = ((DirectionTraceEntry)traceEntry).isTaken();

        innerBimodal.train(index, isTaken, context.getContext().getCounterValue(), delayCycle);
        history = (history << 1) | (isTaken ? 1 : 0);
    }

    ///..
    @Override
    public String getDescription() {

        final String bimodalDescription = innerBimodal.getDescription();
        return "GShare(history: " + innerBimodal.getTableSizeInBits() + ", " + bimodalDescription + ")";
    }

    ///..
    @Override
    public long getStorageCost() {

        return innerBimodal.getStorageCost() + innerBimodal.getTableSizeInBits();
    }

    ///..
    @Override
    public boolean doesSupportDelay() {

        return innerBimodal.isSpeculativeTraining();
    }

    ///
}
