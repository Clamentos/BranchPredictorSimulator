package io.github.clamentos.bpsim.simulation.handlers;

///
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.predictors.Prediction;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.simulation.statistics.Statistics;
import io.github.clamentos.bpsim.traces.TraceEntry;

///..
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

///..
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

///
@AllArgsConstructor(access = AccessLevel.PROTECTED)

///
public abstract class PredictorHandler<T> {

    ///
    private final int delayCycles;
    private final Predictor<T> predictor;
    private final TraceReader traceReader;

    ///
    public Statistics simulate() throws IOException, IllegalArgumentException, NumberFormatException {

        System.out.println("Simulating " + predictor.getDescription() + " on trace: " + traceReader.getTraceName());

        final Statistics statistics = this.generateStatistics(predictor.getDescription(), traceReader.getTraceName());
        final List<Prediction<T>> predictions = new ArrayList<>(delayCycles);

        for(int i = 0; i < delayCycles; i++) predictions.add(null);
        TraceEntry traceEntry;

        while((traceEntry = traceReader.readTraceEntry()) != null) {

            for(int i = 0; i < delayCycles; i++) predictions.set(i, predictor.predict(traceEntry));

            for(final Prediction<T> prediction : predictions) {

                statistics.update(traceEntry, prediction);
                predictor.train(traceEntry, prediction);
            }
        }

        traceReader.reset();
        return statistics;
    }

    ///..
    public int getSortOrder() {

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
    protected abstract Statistics generateStatistics(final String predictorDescription, final String traceName);

    ///
}
