package io.github.clamentos.bpsim;

import io.github.clamentos.bpsim.predictors.Predictor;
import io.github.clamentos.bpsim.predictors.implementations.bimodal.Bimodal;
import io.github.clamentos.bpsim.simulation.SimulationType;
import io.github.clamentos.bpsim.simulation.Simulator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Application {

    public static void main(final String[] args) throws IOException {

        final List<Predictor<?>> predictors = List.of(

            new Bimodal(2, 2, 3),
            new Bimodal(2, 2, 4),
            new Bimodal(2, 2, 5),
            new Bimodal(2, 2, 6),
            new Bimodal(2, 2, 7),
            new Bimodal(2, 2, 8),
            new Bimodal(2, 2, 9),
            new Bimodal(2, 2, 10),
            new Bimodal(2, 2, 11),
            new Bimodal(2, 2, 12),
            new Bimodal(2, 2, 13),
            new Bimodal(2, 2, 14),
            new Bimodal(2, 2, 15),
            new Bimodal(2, 2, 16)
        );

        final Simulator simulator = new Simulator(

            SimulationType.DIRECTION,
            65536,
            1,
            Map.of(),
            predictors
        );

        simulator.simulate();
    }
}
