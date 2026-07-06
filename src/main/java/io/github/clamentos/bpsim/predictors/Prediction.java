package io.github.clamentos.bpsim.predictors;

///
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

///
@AllArgsConstructor
@Getter
@Setter

///
public class Prediction<T> {

    ///
    boolean isTaken;
    long target;
    boolean isCollided;
    T context;

    ///
}
