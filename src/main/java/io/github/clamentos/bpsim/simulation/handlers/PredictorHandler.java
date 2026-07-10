package io.github.clamentos.bpsim.simulation.handlers;

///
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.simulation.configuration.SimulationType;
import io.github.clamentos.bpsim.simulation.statistics.DirectionAndTargetStatistics;
import io.github.clamentos.bpsim.simulation.statistics.DirectionStatistics;
import io.github.clamentos.bpsim.simulation.statistics.Statistics;
import io.github.clamentos.bpsim.simulation.statistics.TargetStatistics;
import io.github.clamentos.bpsim.traces.TraceEntry;

///..
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

///..
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

///
@AllArgsConstructor
@Slf4j

///
public final class PredictorHandler<T> {

    ///
    private final SimulationType simulationType;
    private final int delayCycles;
    private final Predictor<T> predictor;
    private final TraceReader traceReader;

    ///
    public CompletableFuture<Statistics> simulate(final Executor executor) {

        final CompletableFuture<Statistics> result = new CompletableFuture<>();

        executor.execute(() -> {

            log.info("START Simulating {} on trace {} ", predictor.getDescription(), traceReader.getTraceName());

            final Thread current = Thread.currentThread();
            final Statistics statistics = this.generateStatistics(predictor.getDescription(), traceReader.getTraceName());
            final List<Prediction<T>> predictions = new ArrayList<>(delayCycles);

            try {

                traceReader.reserveStream();

                for(int i = 0; i < delayCycles; i++) predictions.add(null);
                TraceEntry traceEntry;

                while((traceEntry = traceReader.readTraceEntry(current)) != null) {

                    for(int i = 0; i < delayCycles; i++) predictions.set(i, predictor.predict(traceEntry, i));

                    for(int i = 0; i < delayCycles; i++) {

                        statistics.update(traceEntry, predictions.get(i));
                        predictor.train(traceEntry, predictions.get(i), i);
                    }
                }

                log.info("END Simulating {} on trace {} ", predictor.getDescription(), traceReader.getTraceName());
                result.complete(statistics);
            }

            catch(final Exception exc) {

                log.error("Could not simulate", exc);
                result.cancel(true);
            }
        });

        return result;
    }

    ///..
    public long getPredictorStorageCost() {

        return predictor.getStorageCost();
    }

    ///..
    public String getPredictorDescription() {

        return predictor.getDescription();
    }

    ///..
    public String getTraceName() {

        return traceReader.getTraceName();
    }

    ///.
    private Statistics generateStatistics(final String predictorDescription, final String traceName) {

        switch(simulationType) {

            case DIRECTION: return new DirectionStatistics(predictorDescription, traceName);
            case TARGET: return new TargetStatistics(predictorDescription, traceName);
            case DIRECTION_AND_TARGET: return new DirectionAndTargetStatistics(predictorDescription, traceName);

            default: return null;
        }
    }

    ///
}
