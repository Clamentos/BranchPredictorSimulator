package io.github.clamentos.bpsim.parser.readers;

///
import io.github.clamentos.bpsim.parser.TraceConfiguration;
import io.github.clamentos.bpsim.traces.TraceEntry;

///..
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

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
    private BufferedReader reader;

    ///
    protected TraceReader(final String filePath, final TraceConfiguration traceRadixes, final int bufferSize) throws IOException {

        this.filePath = filePath;
        this.bufferSize = bufferSize;

        separator = traceRadixes != null ? traceRadixes.getSeparator() : ",";

        addressRadix = traceRadixes != null ? traceRadixes.getAddressRadix() : 16;
        targetRadix = traceRadixes != null ? traceRadixes.getTargetRadix() : 16;
        traceName = Path.of(filePath).getFileName().toString();

        reader = new BufferedReader(new FileReader(filePath), bufferSize);
    }

    ///
    @Override
    public void close() throws IOException {

        reader.close();
    }

    ///..
    public TraceEntry readTraceEntry() throws IOException, IllegalArgumentException, NumberFormatException {

        final String line = reader.readLine();

        if(line == null) return null;
        return this.parseTraceEntry(this.split(line));
    }

    ///..
    public void reset() throws IOException {

        reader.close();
        reader = new BufferedReader(new FileReader(filePath), bufferSize);
    }

    ///.
    protected abstract TraceEntry parseTraceEntry(final String[] splits) throws IllegalArgumentException, NumberFormatException;

    ///..
    protected boolean isTaken(final String value) {

        return isTakenValues.contains(value);
    }

    ///.
    private String[] split(final String line) {

        return line.split(separator);
    }

    ///
}
