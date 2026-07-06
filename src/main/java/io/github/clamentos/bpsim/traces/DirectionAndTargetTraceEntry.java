package io.github.clamentos.bpsim.traces;

///
import lombok.Getter;

///
@Getter

///
public final class DirectionAndTargetTraceEntry extends DirectionTraceEntry {

    ///
    private final long target;

    ///
    public DirectionAndTargetTraceEntry(final long address, final boolean isTaken, final long target) {

        super(address, isTaken);
        this.target = target;
    }

    ///
}
