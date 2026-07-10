package io.github.clamentos.bpsim.simulation.configuration;

///
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

///..
import lombok.Getter;

///
@Getter

///
public final class CustomTracePattern {

    ///
    private final CustomTraceGenerationMode type;
    private final Integer length;
    private final String explicitPattern;

    ///
    @JsonCreator
    public CustomTracePattern(

        @JsonProperty("type") final CustomTraceGenerationMode type,
        @JsonProperty("explicitPattern") final String explicitPattern,
        @JsonProperty("length") final Integer length

    ) throws IllegalArgumentException {

        if(type == null) throw new IllegalArgumentException("Pattern type must be specified");
        final boolean isPatternOk = explicitPattern != null && !explicitPattern.isBlank();

        if(type == CustomTraceGenerationMode.EXPLICIT && !isPatternOk) {

            throw new IllegalArgumentException("Pattern must be explicitly defined when in " + CustomTraceGenerationMode.EXPLICIT + " mode");
        }

        if(type == CustomTraceGenerationMode.RANDOM && isPatternOk) {

            throw new IllegalArgumentException("Field \"explicitPattern\" must be null or blank when in " + CustomTraceGenerationMode.RANDOM + " mode");
        }

        if(type == CustomTraceGenerationMode.RANDOM && (length == null || length <= 0)) {

            throw new IllegalArgumentException("Pattern length must be defined and > 0 when in " + CustomTraceGenerationMode.RANDOM + " mode");
        }

        this.type = type;
        this.explicitPattern = explicitPattern;
        this.length = length;
    }

    ///
}
