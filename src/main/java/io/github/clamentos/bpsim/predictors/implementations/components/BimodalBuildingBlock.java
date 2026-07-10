package io.github.clamentos.bpsim.predictors.implementations.components;

///
import io.github.clamentos.bpsim.predictors.DirectionPrediction;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.traces.DirectionTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;
import io.github.clamentos.bpsim.utils.Utils;

///..
import java.util.ArrayList;
import java.util.List;

///..
import lombok.Getter;

///
public final class BimodalBuildingBlock implements Predictor<Byte> {

    ///
    private final int counterSize;
    private final int startingBias;
    @Getter private final int tableSizeInBits;
    @Getter private final boolean speculativeTraining;

    ///..
    private final int maxCounterValue;
    private final int takenThreshold;
    @Getter private final long addressMask;

    ///..
    private final byte[] saturatingCounters;

    ///..
    private final List<DirectionPrediction<Byte>> predictionHolders;

    ///
    public BimodalBuildingBlock(final int counterSize, final int startingBias, final int tableSizeInBits, final boolean speculativeTraining, final int delayCycles) {

        this.counterSize = counterSize;
        this.startingBias = startingBias;
        this.tableSizeInBits = tableSizeInBits;
        this.speculativeTraining = speculativeTraining;

        maxCounterValue = Math.unsignedPowExact(2, counterSize) - 1;
        takenThreshold = (maxCounterValue + 1) / 2;
        addressMask = Utils.calculateMask(tableSizeInBits);

        saturatingCounters = new byte[Math.unsignedPowExact(2, tableSizeInBits)];
        for(int i = 0; i < saturatingCounters.length; i++) saturatingCounters[i] = (byte)startingBias;

        predictionHolders = new ArrayList<>(delayCycles);
        for(int i = 0; i < delayCycles; i++) predictionHolders.add(new DirectionPrediction<>());
    }

    ///
    @Override
    public Prediction<Byte> predict(final TraceEntry traceEntry, final int delayCycle) {

        return this.predictInternal(traceEntry.getAddress(), delayCycle);
    }

    ///..
    public Prediction<Byte> predict(final long address, final int delayCycle) {

        return this.predictInternal(address, delayCycle);
    }

    ///..
    @Override
    public void train(final TraceEntry traceEntry, final Prediction<Byte> context, final int delayCycle) {

        final DirectionTraceEntry directionTraceEntry = (DirectionTraceEntry)traceEntry;
        this.train(directionTraceEntry.getAddress(), directionTraceEntry.isTaken(), context.getContext(), delayCycle);
    }

    ///..
    public void train(final long address, final boolean isTaken, final byte originalValue, final int delayCycle) {

        final int index = (int)(address & addressMask);

        if(speculativeTraining) {

            if(isTaken != (originalValue >= takenThreshold)) {

                saturatingCounters[index] = this.saturate(originalValue, isTaken);
            }
        }

        else {

            saturatingCounters[index] = this.saturate(originalValue, isTaken);
        }
    }

    ///..
    @Override
    public String getDescription() {

        return "Bimodal(" + saturatingCounters.length + ", " + counterSize + " BCs, bias: " + startingBias + (speculativeTraining ? ", spec." : "") + ")";
    }

    ///..
    @Override
    public long getStorageCost() {

        return (long)saturatingCounters.length * counterSize;
    }

    ///..
    @Override
    public boolean doesSupportDelay() {

        return true;
    }

    ///.
    private Prediction<Byte> predictInternal(final long address, final int delayCycle) {

        final int index = (int)(address & addressMask);
        final byte counterValue = saturatingCounters[index];
        final boolean isTaken = counterValue >= takenThreshold;
        final DirectionPrediction<Byte> prediction = predictionHolders.get(delayCycle);

        if(speculativeTraining) this.train(address, isTaken, counterValue, delayCycle);

        prediction.setTaken(isTaken);
        prediction.setContext(counterValue);

        return prediction;
    }

    ///..
    private byte saturate(final byte value, final boolean direction) {

        return direction ? this.saturateIncrement(value) : this.saturateDecrement(value);
    }

    ///..
    private byte saturateIncrement(final byte value) {

        if(value < maxCounterValue) return (byte)(value + 1);
        return value;
    }

    ///..
    private byte saturateDecrement(final byte value) {

        if(value > 0) return (byte)(value - 1);
        return value;
    }

    ///
}
