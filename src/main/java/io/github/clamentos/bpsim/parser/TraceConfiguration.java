package io.github.clamentos.bpsim.parser;

///
import lombok.AllArgsConstructor;
import lombok.Getter;

///..
@AllArgsConstructor
@Getter

///
public final class TraceConfiguration {

    ///
    private final int addressRadix;
    private final int targetRadix;
    private final String separator;

    ///
}
