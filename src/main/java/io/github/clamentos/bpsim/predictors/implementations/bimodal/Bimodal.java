package io.github.clamentos.bpsim.predictors.implementations.bimodal;

///
import io.github.clamentos.bpsim.predictors.CollisionTracker;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.traces.DirectionTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;
import io.github.clamentos.bpsim.utils.Utils;

///
public final class Bimodal implements Predictor<Byte> {

    ///
    private final int counterSize;
    private final int startingBias;

    ///..
    private final int maxCounterValue;
    private final int takenThreshold;
    private final long addressMask;

    ///..
    private final byte[] saturatingCounters;
    private final CollisionTracker collisionTracker;

    ///..
    private final Prediction<Byte> predictionHolder;

    ///
    public Bimodal(final int counterSize, final int startingBias, final int tableSizeInBits) {

        this.counterSize = counterSize;
        this.startingBias = startingBias;

        maxCounterValue = Math.unsignedPowExact(2, counterSize) - 1;
        takenThreshold = (maxCounterValue + 1) / 2;
        addressMask = Utils.calculateMask(tableSizeInBits);

        saturatingCounters = new byte[Math.unsignedPowExact(2, tableSizeInBits)];
        for(int i = 0; i < saturatingCounters.length; i++) saturatingCounters[i] = (byte)startingBias;
        collisionTracker = new CollisionTracker(saturatingCounters.length);

        predictionHolder = new Prediction<>(false, Integer.MIN_VALUE, false, (byte)0);
    }

    ///
    @Override
    public Prediction<Byte> predict(final TraceEntry traceEntry) {

        final int index = (int)(traceEntry.getAddress() & addressMask);
        final byte counterValue = saturatingCounters[index];
        final long collisionCounter = collisionTracker.update(index, counterValue);

        predictionHolder.setTaken(counterValue >= takenThreshold);
        predictionHolder.setCollided(collisionCounter > 1);
        predictionHolder.setContext(counterValue);

        return predictionHolder;
    }

    ///..
    @Override
    public void train(final TraceEntry traceEntry, final Prediction<Byte> context) {

        final DirectionTraceEntry directionTraceEntry = (DirectionTraceEntry)traceEntry;
        final int index = (int)(traceEntry.getAddress() & addressMask);
        final byte originalValue = context.getContext();

        saturatingCounters[index] = this.saturate(originalValue, directionTraceEntry.isTaken());
    }

    ///..
    @Override
    public String getDescription() {

        return "Bimodal(" + saturatingCounters.length + ", " + counterSize + " BCs, bias: " + startingBias + ")";
    }

    ///..
    @Override
    public int getStorageCost() {

        return saturatingCounters.length * counterSize;
    }

    ///.
    private byte saturate(final byte value, final boolean direction) {

        if(direction && value < maxCounterValue) return (byte)(value + 1);
        else if(value > 0) return (byte)(value - 1);

        return value;
    }

    ///
}
