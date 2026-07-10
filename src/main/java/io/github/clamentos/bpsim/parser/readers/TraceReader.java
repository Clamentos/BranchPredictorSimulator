package io.github.clamentos.bpsim.parser.readers;

///
import io.github.clamentos.bpsim.simulation.configuration.TraceConfiguration;
import io.github.clamentos.bpsim.traces.TraceEntry;
import io.github.clamentos.bpsim.utils.Constants;

///..
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

///..
import lombok.Getter;

///
public abstract class TraceReader implements AutoCloseable {

    ///
    private static final Set<String> isTakenValues = Set.of("true", "TRUE", "taken", "TAKEN", "1", "t", "T", "y", "Y", "yes", "YES");

    ///..
    private final String filePath;
    private final int bufferSize;
    private final String separator;

    ///..
    @Getter private final int addressRadix;
    @Getter private final int targetRadix;
    @Getter private final String traceName;

    ///..
    private final Map<Thread, BufferedReader> readers;

    ///
    protected TraceReader(final String filePath, final TraceConfiguration traceConfiguration, final int readerBufferSize) {

        this.filePath = filePath;
        this.bufferSize = readerBufferSize;

        separator = traceConfiguration != null ? traceConfiguration.getSeparator() : Constants.DEFAULT_TRACE_SEPARATOR;

        addressRadix = traceConfiguration != null ? traceConfiguration.getAddressRadix() : Constants.DEFAULT_TRACE_ADDRESS_RADIX;
        targetRadix = traceConfiguration != null ? traceConfiguration.getTargetRadix() : Constants.DEFAULT_TRACE_TARGET_RADIX;
        traceName = Path.of(filePath).getFileName().toString();

        readers = new ConcurrentHashMap<>();
    }

    ///
    @Override
    public void close() throws IOException {

        for(final BufferedReader reader : readers.values()) reader.close();
    }

    ///..
    public TraceEntry readTraceEntry(final Thread thread) throws IOException, IllegalArgumentException, NumberFormatException {

        final String line = readers.get(thread).readLine();

        if(line == null) return null;
        return this.parseTraceEntry(line.split(separator));
    }

    ///..
    public void reserveStream() throws IOException {

        readers.put(Thread.currentThread(), new BufferedReader(new FileReader(filePath), bufferSize));
    }

    ///.
    protected abstract TraceEntry parseTraceEntry(final String[] splits) throws IllegalArgumentException, NumberFormatException;

    ///..
    protected boolean isTaken(final String value) {

        return isTakenValues.contains(value);
    }

    ///
}
