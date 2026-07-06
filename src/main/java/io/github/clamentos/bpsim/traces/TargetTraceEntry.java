package io.github.clamentos.bpsim.traces;

///
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public class TargetTraceEntry implements TraceEntry {

    ///
    private final long address;
    private final long target;

    ///
}
