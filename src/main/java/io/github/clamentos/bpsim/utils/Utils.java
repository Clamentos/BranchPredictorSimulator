package io.github.clamentos.bpsim.utils;

///
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

///
@NoArgsConstructor(access = AccessLevel.PRIVATE)

///
public final class Utils {

    ///
    public static long calculateMask(final int tableSizeInBits) {

        return Math.unsignedPowExact(2L, tableSizeInBits) - 1;
    }

    ///
}
