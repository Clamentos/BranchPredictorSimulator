package io.github.clamentos.bpsim.utils;

///
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

///
@NoArgsConstructor(access = AccessLevel.PRIVATE)

///
public final class Constants {

    ///
    public static final String PREDICTOR_CLASS = "CLASS";
    public static final String TRACE_PATH = "./traces";
    public static final int DEFAULT_TRACE_ADDRESS_RADIX = 16;
    public static final int DEFAULT_TRACE_TARGET_RADIX = 16;
    public static final String DEFAULT_TRACE_SEPARATOR = ",";
    public static final int DEFAULT_BUFFERED_READER_SIZE = 65536;
    public static final String RESULT_REPORT_PATH = "./Results.html";
    public static final String RESULT_TEMPLATE_REPORT_PATH = "ResultTemplate.html";
    public static final String RESULT_TEMPLATE_REPORT_PLACEHOLDER = "__CHART_DATA__";
    public static final String DEFAULT_SIMULATION_SETUP_PATH = "./SimulationSetup.jsonc";
    public static final int RANDOM_PATTERN_INSTANCES = 8;
    public static final String GENERATED_TRACE_PATH = "./traces/generated";

    ///
}
