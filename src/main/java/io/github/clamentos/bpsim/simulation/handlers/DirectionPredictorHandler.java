package io.github.clamentos.bpsim.simulation.handlers;

///
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.simulation.statistics.DirectionStatistics;
import io.github.clamentos.bpsim.simulation.statistics.Statistics;

///
public final class DirectionPredictorHandler<T> extends PredictorHandler<T> {

    ///
    public DirectionPredictorHandler(final int delayCycles, final Predictor<T> predictor, final TraceReader traceReader) {

        super(delayCycles, predictor, traceReader);
    }

    ///
    @Override
    protected Statistics generateStatistics(final String predictorDescription, final String traceName) {

        return new DirectionStatistics(predictorDescription, traceName);
    }

    ///
}
