package io.github.clamentos.bpsim.simulation.configuration;

///
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

///..
import java.util.List;

///..
import lombok.Getter;

///
@Getter

///
public final class Configuration {

    ///
    private final SimulationType simulationType;
    private final Integer delayCycles;
    private final Integer readerBufferSize;

    ///..
    private final TraceArguments traceArguments;
    private final List<PredictorDefinition> predictorDefinitions;

    ///
    @JsonCreator
    public Configuration(

        @JsonProperty("simulationType") final SimulationType simulationType,
        @JsonProperty("delayCycles") final Integer delayCycles,
        @JsonProperty("readerBufferSize") final Integer readerBufferSize,
        @JsonProperty("traceArguments") final TraceArguments traceArguments,
        @JsonProperty("predictorDefinitions") final List<PredictorDefinition> predictorDefinitions

    ) throws IllegalArgumentException {

        if(simulationType == null) throw new IllegalArgumentException("Field \"simulationType\" cannot be null");
        if(delayCycles == null || delayCycles < 1) throw new IllegalArgumentException("Field \"delayCycles\" cannot be null or less than 1");
        if(readerBufferSize != null && readerBufferSize < 0) throw new IllegalArgumentException("Field \"readerBufferSize\" field cannot be negative");
        if(traceArguments == null) throw new IllegalArgumentException("Field \"traceArguments\" cannot be null");
        if(predictorDefinitions == null || predictorDefinitions.isEmpty()) throw new IllegalArgumentException("Field \"predictorDefinitions\" cannot be null or empty");

        this.simulationType = simulationType;
        this.delayCycles = delayCycles;
        this.readerBufferSize = readerBufferSize;

        this.traceArguments = traceArguments;
        this.predictorDefinitions = predictorDefinitions;
    }

    ///
}
