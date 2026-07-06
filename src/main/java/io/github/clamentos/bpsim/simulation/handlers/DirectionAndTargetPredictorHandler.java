package io.github.clamentos.bpsim.simulation.handlers;

///
import io.github.clamentos.bpsim.parser.readers.TraceReader;
import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.simulation.statistics.DirectionAndTargetStatistics;
import io.github.clamentos.bpsim.simulation.statistics.Statistics;

///
public class DirectionAndTargetPredictorHandler<T> extends PredictorHandler<T> {

    ///
    public DirectionAndTargetPredictorHandler(final int delayCycles, final Predictor<T> predictor, final TraceReader traceReader) {

        super(delayCycles, predictor, traceReader);
    }

    ///
    @Override
    protected Statistics generateStatistics(final String predictorDescription, final String traceName) {

        return new DirectionAndTargetStatistics(predictorDescription, traceName);
    }

    ///
}
