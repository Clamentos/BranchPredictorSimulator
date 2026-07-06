package io.github.clamentos.bpsim.simulation;

///
import io.github.clamentos.bpsim.output.BarChart;
import io.github.clamentos.bpsim.output.BarChartDataset;
import io.github.clamentos.bpsim.parser.TraceConfiguration;
import io.github.clamentos.bpsim.parser.TracesCollector;
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.simulation.handlers.DirectionAndTargetPredictorHandler;
import io.github.clamentos.bpsim.simulation.handlers.DirectionPredictorHandler;
import io.github.clamentos.bpsim.simulation.handlers.PredictorHandler;
import io.github.clamentos.bpsim.simulation.handlers.TargetPredictorHandler;
import io.github.clamentos.bpsim.simulation.statistics.DirectionAndTargetStatistics;
import io.github.clamentos.bpsim.simulation.statistics.DirectionStatistics;
import io.github.clamentos.bpsim.simulation.statistics.Statistics;
import io.github.clamentos.bpsim.simulation.statistics.TargetStatistics;

///..
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import tools.jackson.databind.ObjectMapper;

///
public final class Simulator {

    ///
    private final SimulationType simulationType;

    private final TracesCollector tracesCollector;
    private final List<PredictorHandler<?>> predictorHandlers;

    ///
    @SuppressWarnings("squid:S2093")
    public Simulator(
        
        final SimulationType simulationType,
        final int bufferSize,
        final int delayCycles,
        final Map<String, TraceConfiguration> traceRadixMap,
        final List<Predictor<?>> predictors

    ) throws IOException {

        if(delayCycles < 1) throw new IllegalArgumentException("delayCycles must be at least 1");

        this.simulationType = simulationType;
        FileSystem filesystem = null;

        try {

            final URI uri = Thread.currentThread().getContextClassLoader().getResource("traces").toURI();
            Path path;

            if("jar".equals(uri.getScheme())) {

                filesystem = FileSystems.newFileSystem(uri, Map.of());
                path = filesystem.getPath("/traces");
            }

            else {

                path = Paths.get(uri);
            }

            tracesCollector = new TracesCollector(simulationType, path, traceRadixMap, bufferSize);
            predictorHandlers = new ArrayList<>(predictors.size() * tracesCollector.getTraceReaders().size());

            for(final Predictor<?> predictor : predictors) {

                for(final TraceReader traceReader : tracesCollector.getTraceReaders()) {

                    final PredictorHandler<?> predictorHandler = switch(simulationType) {

                        case DIRECTION -> new DirectionPredictorHandler<>(delayCycles, predictor, traceReader);
                        case TARGET -> new TargetPredictorHandler<>(delayCycles, predictor, traceReader);
                        case DIRECTION_AND_TARGET -> new DirectionAndTargetPredictorHandler<>(delayCycles, predictor, traceReader);
                    };

                    predictorHandlers.add(predictorHandler);
                }
            }

            predictorHandlers.sort((a, b) -> a.getTraceName().compareTo(b.getTraceName()));
            predictorHandlers.sort((a, b) -> a.getPredictorDescription().compareTo(b.getPredictorDescription()));
            predictorHandlers.sort((a, b) -> a.getSortOrder() - b.getSortOrder());
        }

        catch(final URISyntaxException exc) {

            throw new IOException(exc);
        }

        finally {

            if(filesystem != null) filesystem.close();
        }
    }

    ///
    public void simulate() throws IOException, IllegalArgumentException, NumberFormatException {

        final List<Statistics> statisticsList = new ArrayList<>(predictorHandlers.size());

        try(tracesCollector) {

            for(final PredictorHandler<?> predictorHandler : predictorHandlers) {

                statisticsList.add(predictorHandler.simulate());
            }
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        final String placeholderValue = objectMapper.writeValueAsString(this.generateCharts(statisticsList));

        Files.write( // FIXME: make it jar proof

            Path.of("./Results.html"),
            Files.readString(Path.of("./src/main/resources/ResultTemplate.html")).replace("__CHART_DATA__", placeholderValue).getBytes()
        );

        System.out.println("Results written in ./Results.html");
    }

    ///.
    private Map<String, BarChart> generateCharts(final List<Statistics> statisticsList) { // TODO: overall metric for target & direction predictors

        // trace -> predictor -> statistics
        final Map<String, Map<String, Statistics>> dataMap = new LinkedHashMap<>();

        for(final Statistics statistics : statisticsList) {

            dataMap.computeIfAbsent(statistics.getTraceName(), _ -> new LinkedHashMap<>()).put(statistics.getPredictorName(), statistics);
        }

        final Map<String, BarChart> charts = new LinkedHashMap<>();
        final List<String> predictorNames = this.extractPredictorNames(statisticsList);

        for(final Map.Entry<String, Map<String, Statistics>> dataMapEntry : dataMap.entrySet()) {

            final float[] directionAccuracies = new float[predictorNames.size()];
            final float[] targetAccuracies = new float[predictorNames.size()];
            final float[] overallAccuracies = new float[predictorNames.size()];

            for(int i = 0; i < predictorNames.size(); i++) {

                final Statistics statistics = dataMapEntry.getValue().get(predictorNames.get(i));

                switch(simulationType) {

                    case DIRECTION: directionAccuracies[i] = ((DirectionStatistics)statistics).calculateAccuracy(); break;
                    case TARGET: targetAccuracies[i] = ((TargetStatistics)statistics).calculateAccuracy(); break;

                    case DIRECTION_AND_TARGET:

                        final DirectionAndTargetStatistics castedStatistics = (DirectionAndTargetStatistics)statistics;

                        directionAccuracies[i] = castedStatistics.calculateDirectionAccuracy();
                        targetAccuracies[i] = castedStatistics.calculateTargetAccuracy();
                        overallAccuracies[i] = castedStatistics.calculateOverallAccuracy();

                    break;
                }
            }

            final List<BarChartDataset> datasets = new ArrayList<>();

            if(simulationType == SimulationType.DIRECTION) datasets.add(new BarChartDataset("Direction accuracy", directionAccuracies));
            if(simulationType == SimulationType.TARGET) datasets.add(new BarChartDataset("Target accuracy", targetAccuracies));

            if(simulationType == SimulationType.DIRECTION_AND_TARGET) {

                datasets.add(new BarChartDataset("Direction accuracy", directionAccuracies));
                datasets.add(new BarChartDataset("Target accuracy", targetAccuracies));
                datasets.add(new BarChartDataset("Overall accuracy", null));
            }

            final BarChart chart = new BarChart(predictorNames, datasets);
            charts.put(dataMapEntry.getKey(), chart);
        }

        return charts;
    }

    ///..
    private List<String> extractPredictorNames(final List<Statistics> statisticsList) {

        final Set<String> predictorNames = new LinkedHashSet<>();
        for(final Statistics statistics : statisticsList) predictorNames.add(statistics.getPredictorName());

        return predictorNames.stream().toList();
    }

    ///
}
