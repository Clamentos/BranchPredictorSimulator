package io.github.clamentos.bpsim;

///
import io.github.clamentos.bpsim.simulation.Simulator;
import io.github.clamentos.bpsim.utils.Constants;

///
public class Application {

    ///
    public static void main(final String[] args) throws Exception {

        if(args.length > 1) throw new IllegalArgumentException("Number of arguments must be 0 or 1 (setup file path)");

        final String setupFilePath = args.length == 1 ? args[0] : null;
        final Simulator simulator = new Simulator(setupFilePath != null ? setupFilePath : Constants.DEFAULT_SIMULATION_SETUP_PATH);

        simulator.simulate();
    }

    ///
}
