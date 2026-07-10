package io.github.clamentos.bpsim.predictors;

///
import io.github.clamentos.bpsim.predictors.implementations.Static;
import io.github.clamentos.bpsim.predictors.implementations.bimodal.Bimodal;
import io.github.clamentos.bpsim.predictors.implementations.gshare.GShare;
import io.github.clamentos.bpsim.simulation.configuration.PredictorDefinition;
import io.github.clamentos.bpsim.utils.Constants;

///..
import java.util.Map;

///..
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

///
@NoArgsConstructor(access = AccessLevel.PRIVATE)

///
public final class PredictorFactory {

    ///
    @SuppressWarnings("squid:S1452")
    public static Predictor<?> createPredictor(final PredictorDefinition predictorDefinition, final int delayCycles) throws IllegalArgumentException {

        final String predictorClass = predictorDefinition.getPredictorClass();
        final Map<String, Object> arguments = predictorDefinition.getArguments();

        switch(predictorClass) {

            case "Static": return new Static((boolean)arguments.get("predictTaken"));

            case "Bimodal": return new Bimodal(

                (int)arguments.get("counterSize"),
                (int)arguments.get("startingBias"),
                (int)arguments.get("tableSizeInBits"),
                (boolean)arguments.get("speculativeTraining"),
                delayCycles
            );

            case "GShare": return new GShare((int)arguments.get("counterSize"), (int)arguments.get("startingBias"), (int)arguments.get("tableSizeInBits"), delayCycles);

            default: throw new IllegalArgumentException("Unknown predictor " + Constants.PREDICTOR_CLASS + ": " + predictorClass);
        }
    }

    ///
}
