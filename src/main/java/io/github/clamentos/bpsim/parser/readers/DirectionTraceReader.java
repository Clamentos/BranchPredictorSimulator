package io.github.clamentos.bpsim.parser.readers;

///
import io.github.clamentos.bpsim.parser.TraceConfiguration;
import io.github.clamentos.bpsim.simulation.SimulationType;
import io.github.clamentos.bpsim.traces.DirectionTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///..
import java.io.IOException;

///
public final class DirectionTraceReader extends TraceReader {

    ///
    public DirectionTraceReader(final String filePath, final TraceConfiguration traceRadixes, final int bufferSize) throws IOException {

        super(filePath, traceRadixes, bufferSize);
    }

    ///
    @Override
    protected TraceEntry parseTraceEntry(final String[] splits) throws IllegalArgumentException, NumberFormatException {

        if(splits.length < 2) throw new IllegalArgumentException("Trace " + super.getTraceName() + " is not compatible with simulation type: " + SimulationType.DIRECTION);
        return new DirectionTraceEntry(Long.parseLong(splits[0], super.getAddressRadix()), super.isTaken(splits[1]));
    }

    ///
}
