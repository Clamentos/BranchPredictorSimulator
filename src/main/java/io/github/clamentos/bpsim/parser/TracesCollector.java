package io.github.clamentos.bpsim.parser;

///
import io.github.clamentos.bpsim.parser.readers.DirectionAndTargetTraceReader;
import io.github.clamentos.bpsim.parser.readers.DirectionTraceReader;
import io.github.clamentos.bpsim.parser.readers.TargetTraceReader;
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.simulation.SimulationType;

///..
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

///..
import lombok.Getter;

///
@Getter

///
public final class TracesCollector implements AutoCloseable {

    ///
    private final List<TraceReader> traceReaders;

    ///
    public TracesCollector(final SimulationType simulationType, final Path directory, final Map<String, TraceConfiguration> traceRadixMap, final int bufferSize)
    throws IOException {

        traceReaders = new ArrayList<>();

        try(final Stream<Path> tracePaths = Files.list(directory)) {

            final List<Path> tracePathsList = tracePaths.toList();

            for(final Path tracePath : tracePathsList) {

                final String traceName = tracePath.getFileName().toString();

                final TraceReader traceReader = switch(simulationType) {

                    case DIRECTION -> new DirectionTraceReader(tracePath.toString(), traceRadixMap.get(traceName), bufferSize);
                    case TARGET -> new TargetTraceReader(tracePath.toString(), traceRadixMap.get(traceName), bufferSize);
                    case DIRECTION_AND_TARGET -> new DirectionAndTargetTraceReader(tracePath.toString(), traceRadixMap.get(traceName), bufferSize);
                };

                traceReaders.add(traceReader);
            }
        }
    }

    ///
    @Override
    public void close() throws IOException {

        for(final TraceReader traceReader : traceReaders) traceReader.close();
    }

    ///
}
