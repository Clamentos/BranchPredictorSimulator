package io.github.clamentos.bpsim.predictors;

///
import lombok.Getter;
import lombok.Setter;

///
@Getter
@Setter

///
public final class DirectionAndTargetPrediction<T> extends Prediction<T> {

    ///
    private boolean isTaken;
    private long target;

    ///
}
