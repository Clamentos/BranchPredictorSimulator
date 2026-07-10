package io.github.clamentos.bpsim.simulation.configuration;

///
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

///..
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import lombok.Getter;

///
@Getter

///
public final class TraceArguments {

    ///
    private final TraceGenerationMode traceGenerationMode;
    private final Set<String> traceNames;
    private final Map<String, TraceConfiguration> traceConfigurations;
    private final List<CustomTracePattern> customTracePatterns;

    ///
    @JsonCreator
    public TraceArguments(

        @JsonProperty("traceGenerationMode") final TraceGenerationMode traceGenerationMode,
        @JsonProperty("traceNames") final Set<String> traceNames,
        @JsonProperty("traceConfigurations") final Map<String, TraceConfiguration> traceConfigurations,
        @JsonProperty("customTracePatterns") final List<CustomTracePattern> customTracePatterns

    ) throws IllegalArgumentException {

        if(traceGenerationMode == null) throw new IllegalArgumentException("Field \"traceGenerationMode\" cannot be null");

        final boolean isTraceNamesOk = this.isWellDefined(traceNames);
        final boolean isCustomTracePatternsOk = this.isWellDefined(customTracePatterns);

        if(!isTraceNamesOk && !isCustomTracePatternsOk) throw new IllegalArgumentException("Must define one of: \"traceNames\" or \"customTracePatterns\"");
        if(isTraceNamesOk && isCustomTracePatternsOk) throw new IllegalArgumentException("Only one between: \"traceNames\" or \"customTracePatterns\" is allowed");

        if(traceGenerationMode == TraceGenerationMode.FILE_DRIVEN) {

            if(!isTraceNamesOk) throw new IllegalArgumentException("Field \"traceNames\" must exist in " + TraceGenerationMode.FILE_DRIVEN + " mode");
            if(traceNames.contains("*") && traceNames.size() > 1) throw new IllegalArgumentException("When using wildcard, only it can be present in the \"traceNames\" field");

            if(traceConfigurations != null && !traceConfigurations.isEmpty()) {

                for(final Map.Entry<String, TraceConfiguration> traceConfigurationEntry : traceConfigurations.entrySet()) {

                    if(traceConfigurationEntry.getKey() == null || traceConfigurationEntry.getKey().isBlank()) {

                        throw new IllegalArgumentException("Keys of \"traceConfigurations\" cannot be null or blank");
                    }

                    if(traceConfigurationEntry.getValue() == null) throw new IllegalArgumentException("Elements of \"traceConfigurations\" cannot be null");
                }
            }
        }

        this.traceGenerationMode = traceGenerationMode;
        this.traceNames = traceNames;
        this.traceConfigurations = traceConfigurations;
        this.customTracePatterns = customTracePatterns;
    }

    ///..
    private boolean isWellDefined(final Collection<?> collection) {

        return collection != null && !collection.isEmpty();
    }

    ///
}
