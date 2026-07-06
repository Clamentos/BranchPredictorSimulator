package io.github.clamentos.bpsim.simulation.handlers;

///
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.simulation.statistics.Statistics;
import io.github.clamentos.bpsim.simulation.statistics.TargetStatistics;

///
public class TargetPredictorHandler<T> extends PredictorHandler<T> {

    ///
    public TargetPredictorHandler(final int delayCycles, final Predictor<T> predictor, final TraceReader traceReader) {

        super(delayCycles, predictor, traceReader);
    }

    ///
    @Override
    protected Statistics generateStatistics(final String predictorDescription, final String traceName) {

        return new TargetStatistics(predictorDescription, traceName);
    }

    ///
}
