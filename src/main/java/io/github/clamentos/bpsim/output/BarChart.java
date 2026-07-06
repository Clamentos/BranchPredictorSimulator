package io.github.clamentos.bpsim.output;

///
import java.util.List;

///..
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public final class BarChart {

    ///
    private final List<String> labels;
    private final List<BarChartDataset> datasets;

    ///
}
