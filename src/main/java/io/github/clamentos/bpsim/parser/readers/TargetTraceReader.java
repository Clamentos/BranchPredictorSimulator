package io.github.clamentos.bpsim.parser.readers;

///
import io.github.clamentos.bpsim.simulation.configuration.SimulationType;
import io.github.clamentos.bpsim.simulation.configuration.TraceConfiguration;
import io.github.clamentos.bpsim.traces.TargetTraceEntry;
import io.github.clamentos.bpsim.traces.TraceEntry;

///
public final class TargetTraceReader extends TraceReader {

    ///
    public TargetTraceReader(final String filePath, final TraceConfiguration traceConfiguration, final int readerBufferSize) {

        super(filePath, traceConfiguration, readerBufferSize);
    }

    ///
    @Override
    protected TraceEntry parseTraceEntry(final String[] splits) throws IllegalArgumentException, NumberFormatException {

        if(splits.length < 2) throw new IllegalArgumentException("Trace " + super.getTraceName() + " is not compatible with simulation type: " + SimulationType.TARGET);
        return new TargetTraceEntry(Long.parseLong(splits[0], super.getAddressRadix()), Long.parseLong(splits[0], super.getTargetRadix()));
    }

    ///
}
