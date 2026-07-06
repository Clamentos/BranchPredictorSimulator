package io.github.clamentos.bpsim.predictors;

///
public final class CollisionTracker {

    ///
    final long[] collisionCounters;
    final long[] addresses;

    ///
    public CollisionTracker(final int size) {

        collisionCounters = new long[size];
        addresses = new long[size];
    }

    ///
    public long update(final int index, final long address) {

        long counter = collisionCounters[index];

        if(counter == 0) {

            addresses[index] = address;
            counter++;
        }

        else {

            if(addresses[index] == address) {

                counter++;
            }
        }

        collisionCounters[index] = counter;
        return counter;
    }

    ///
}
