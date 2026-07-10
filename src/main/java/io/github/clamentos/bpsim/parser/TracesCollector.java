package io.github.clamentos.bpsim.parser;

///
import io.github.clamentos.bpsim.parser.readers.DirectionAndTargetTraceReader;
import io.github.clamentos.bpsim.parser.readers.DirectionTraceReader;
import io.github.clamentos.bpsim.parser.readers.TargetTraceReader;
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.simulation.configuration.CustomTraceGenerationMode;
import io.github.clamentos.bpsim.simulation.configuration.CustomTracePattern;
import io.github.clamentos.bpsim.simulation.configuration.SimulationType;
import io.github.clamentos.bpsim.simulation.configuration.TraceConfiguration;
import io.github.clamentos.bpsim.simulation.configuration.TraceGenerationMode;
import io.github.clamentos.bpsim.utils.Constants;

///..
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
    public TracesCollector(

        final SimulationType simulationType,
        final TraceGenerationMode traceGenerationMode,
        final Path directory,
        final Set<String> traceNames,
        final Map<String, TraceConfiguration> traceConfigurations,
        final List<CustomTracePattern> customTracePatterns,
        final Integer readerBufferSize

    ) throws IOException {

        final long now = System.currentTimeMillis();
        traceReaders = new ArrayList<>();

        if(traceGenerationMode == TraceGenerationMode.CUSTOM) {

            final Random rng = new SecureRandom();
            int idx = 0;

            for(final CustomTracePattern customTracePattern : customTracePatterns) {

                final StringBuilder sb = new StringBuilder();

                if(customTracePattern.getType() == CustomTraceGenerationMode.RANDOM) {

                    final boolean[] pattern = new boolean[customTracePattern.getLength()];
                    for(int i = 0; i < pattern.length; i++) pattern[i] = rng.nextBoolean();

                    for(int i = 0; i < Constants.RANDOM_PATTERN_INSTANCES; i++) {

                        for(int j = 0; j < pattern.length; j++) {

                            sb.append("0,").append(pattern[j]).append("\n");
                        }
                    }

                    Files.write(Path.of(Constants.GENERATED_TRACE_PATH + "/generated_random_" + idx + "_" + now + ".txt"), sb.toString().getBytes());
                }

                else {

                    final String explicitPattern = customTracePattern.getExplicitPattern();

                    for(int i = 0; i < Constants.RANDOM_PATTERN_INSTANCES; i++) {

                        for(int j = 0; j < explicitPattern.length(); j++) {

                            sb.append("0,").append(explicitPattern.charAt(j)).append("\n");
                        }
                    }

                    Files.write(Path.of(Constants.GENERATED_TRACE_PATH + "/generated_explicit_" + idx + "_" + now + ".txt"), sb.toString().getBytes());
                }
            }
        }

        final Path actualDirectory;
        final Set<String> actualTraceNames;

        if(traceGenerationMode == TraceGenerationMode.CUSTOM) {

            actualDirectory = Path.of("./traces/generated");
            actualTraceNames = Set.of("*");
        }

        else {

            actualDirectory = directory;
            actualTraceNames = traceNames;
        }

        try(final Stream<Path> tracePaths = Files.list(actualDirectory).filter(Files::isRegularFile)) {

            final int bufferSize = readerBufferSize != null ? readerBufferSize : Constants.DEFAULT_BUFFERED_READER_SIZE;
            final Map<String, TraceConfiguration> traceConfigurationsMap = traceConfigurations != null ? traceConfigurations : Map.of();
            final List<Path> tracePathsList;

            if(actualTraceNames.contains("*")) {

                tracePathsList = tracePaths.toList();
            }

            else {

                final Set<Pattern> patterns = actualTraceNames.stream().map(Pattern::compile).collect(Collectors.toSet());
                tracePathsList = tracePaths.filter(path -> this.matchesAny(path, patterns)).toList();
            }

            for(final Path tracePath : tracePathsList) {

                final String traceName = tracePath.getFileName().toString();

                final TraceReader traceReader = switch(simulationType) {

                    case DIRECTION -> new DirectionTraceReader(tracePath.toString(), traceConfigurationsMap.get(traceName), bufferSize);
                    case TARGET -> new TargetTraceReader(tracePath.toString(), traceConfigurationsMap.get(traceName), bufferSize);
                    case DIRECTION_AND_TARGET -> new DirectionAndTargetTraceReader(tracePath.toString(), traceConfigurationsMap.get(traceName), bufferSize);
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

    ///.
    private boolean matchesAny(final Path path, final Set<Pattern> patterns) {

        final String fileName = path.getFileName().toString();

        for(final Pattern pattern : patterns) {

            if(pattern.matcher(fileName).matches()) return true;
        }

        return false;
    }

    ///
}
