package io.github.clamentos.bpsim.parser.readers;

///
import io.github.clamentos.bpsim.parser.TraceConfiguration;
import io.github.clamentos.bpsim.simulation.SimulationType;
import io.github.clamentos.bpsim.traces.DirectionAndTargetTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///..
import java.io.IOException;

///
public final class DirectionAndTargetTraceReader extends TraceReader {

    ///
    public DirectionAndTargetTraceReader(final String filePath, final TraceConfiguration traceRadixes, final int bufferSize) throws IOException {

        super(filePath, traceRadixes, bufferSize);
    }

    ///
    @Override
    protected TraceEntry parseTraceEntry(final String[] splits) throws IllegalArgumentException, NumberFormatException {

        if(splits.length < 3) {

            throw new IllegalArgumentException("Trace " + super.getTraceName() + " is not compatible with simulation type: " + SimulationType.DIRECTION_AND_TARGET);
        }

        return new DirectionAndTargetTraceEntry(

            Long.parseLong(splits[0], super.getAddressRadix()),
            super.isTaken(splits[1]),
            Long.parseLong(splits[2], super.getTargetRadix())
        );
    }

    ///
}
