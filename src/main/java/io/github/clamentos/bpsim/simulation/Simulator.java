package io.github.clamentos.bpsim.simulation;

///
import io.github.clamentos.bpsim.output.BarChart;
import io.github.clamentos.bpsim.output.BarChartDataset;
import io.github.clamentos.bpsim.parser.TracesCollector;
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.predictors.PredictorFactory;
import io.github.clamentos.bpsim.simulation.configuration.Configuration;
import io.github.clamentos.bpsim.simulation.configuration.PredictorDefinition;
import io.github.clamentos.bpsim.simulation.configuration.SimulationType;
import io.github.clamentos.bpsim.simulation.handlers.PredictorHandler;
import io.github.clamentos.bpsim.simulation.statistics.DirectionAndTargetStatistics;
import io.github.clamentos.bpsim.simulation.statistics.DirectionStatistics;
import io.github.clamentos.bpsim.simulation.statistics.Statistics;
import io.github.clamentos.bpsim.simulation.statistics.TargetStatistics;
import io.github.clamentos.bpsim.utils.Constants;
import io.github.clamentos.bpsim.utils.Utils;

///..
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

///..
import lombok.extern.slf4j.Slf4j;

///..
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

///
@Slf4j

///
public final class Simulator {

    ///
    private final ObjectMapper objectMapper;

    ///..
    private final SimulationType simulationType;

    ///..
    private final TracesCollector tracesCollector;
    private final List<PredictorHandler<?>> predictorHandlers;

    ///
    public Simulator(final String setupFilePath) throws IOException, IllegalArgumentException {

        objectMapper = JsonMapper

            .builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build()
        ;

        final Configuration configuration = objectMapper.readValue(Files.readAllBytes(Path.of(setupFilePath)), new TypeReference<Configuration>(){});
        simulationType = configuration.getSimulationType();

        tracesCollector = new TracesCollector(

            simulationType,
            configuration.getTraceArguments().getTraceGenerationMode(),
            Path.of(Constants.TRACE_PATH),
            configuration.getTraceArguments().getTraceNames(),
            configuration.getTraceArguments().getTraceConfigurations(),
            configuration.getTraceArguments().getCustomTracePatterns(),
            configuration.getReaderBufferSize()
        );

        predictorHandlers = new ArrayList<>(configuration.getPredictorDefinitions().size() * tracesCollector.getTraceReaders().size());

        for(final PredictorDefinition predictorDefinition : configuration.getPredictorDefinitions()) {

            for(final TraceReader traceReader : tracesCollector.getTraceReaders()) {

                final int delayCycles = configuration.getDelayCycles();
                final Predictor<?> predictor = PredictorFactory.createPredictor(predictorDefinition, delayCycles);

                if(!predictor.doesSupportDelay() && delayCycles > 1) {

                    log.warn("Predictor {} does not support speculative training or it's explicitly disabled", predictor.getDescription());
                }

                predictorHandlers.add(new PredictorHandler<>(simulationType, delayCycles, predictor, traceReader));
            }
        }

        predictorHandlers.sort((a, b) -> a.getTraceName().compareTo(b.getTraceName()));
        predictorHandlers.sort((a, b) -> a.getPredictorDescription().compareTo(b.getPredictorDescription()));
        predictorHandlers.sort((a, b) -> (int)(a.getPredictorStorageCost() - b.getPredictorStorageCost()));
    }

    ///
    public void simulate() {

        final long startTime = System.currentTimeMillis();
        final List<CompletableFuture<Statistics>> futureList = new ArrayList<>(predictorHandlers.size());
        final List<Statistics> statisticsList = new ArrayList<>(predictorHandlers.size());
        final int poolSize = 1; //Runtime.getRuntime().availableProcessors() / 2;

        try(
            tracesCollector;
            final ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 8, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ) {

            for(final PredictorHandler<?> predictorHandler : predictorHandlers) {

                futureList.add(predictorHandler.simulate(executor));
            }

            for(final CompletableFuture<Statistics> future : futureList) {

                statisticsList.add(future.join());
            }

            final String placeholderValue = objectMapper.writeValueAsString(this.generateCharts(statisticsList));

            Files.write(

                Path.of(Constants.RESULT_REPORT_PATH),

                Files.readString(Utils.getResourcePath(Constants.RESULT_TEMPLATE_REPORT_PATH))
                    .replace(Constants.RESULT_TEMPLATE_REPORT_PLACEHOLDER, placeholderValue)
                    .getBytes()
            );

            log.info("Simulation complete in {}ms", (System.currentTimeMillis() - startTime));
            log.info("Results written in {}", Constants.RESULT_REPORT_PATH);
        }

        catch(final Exception exc) {

            log.error("Could not simulate", exc);
        }
    }

    ///.
    private Map<String, BarChart> generateCharts(final List<Statistics> statisticsList) {

        // trace -> predictor -> statistics
        final Map<String, Map<String, Statistics>> dataMap = new LinkedHashMap<>();

        for(final Statistics statistics : statisticsList) {

            dataMap.computeIfAbsent(statistics.getTraceName(), _ -> new LinkedHashMap<>()).put(statistics.getPredictorDescription(), statistics);
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
        for(final Statistics statistics : statisticsList) predictorNames.add(statistics.getPredictorDescription());

        return predictorNames.stream().toList();
    }

    ///
}
