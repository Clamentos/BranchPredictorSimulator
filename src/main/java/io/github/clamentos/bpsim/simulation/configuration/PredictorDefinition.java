package io.github.clamentos.bpsim.simulation.configuration;

///
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

///..
import io.github.clamentos.bpsim.utils.Constants;

///..
import java.util.Map;

///..
import lombok.Getter;

///
@Getter

///
public final class PredictorDefinition {

    ///
    final String predictorClass;
    final Map<String, Object> arguments;

    ///
    @JsonCreator
    public PredictorDefinition(@JsonProperty(Constants.PREDICTOR_CLASS) final String predictorClass, @JsonProperty("arguments") final Map<String, Object> arguments)
    throws IllegalArgumentException {

        if(predictorClass == null || predictorClass.isBlank()) {

            throw new IllegalArgumentException("Field \"" + Constants.PREDICTOR_CLASS + "\" of object \"predictorDefinition\" cannot be null or blank");
        }

        this.predictorClass = predictorClass;
        this.arguments = arguments;
    }

    ///
}
