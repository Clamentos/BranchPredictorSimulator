package io.github.clamentos.bpsim.utils;

///
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

///..
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

    ///..
    public static Path getResourcePath(final String pathStr) throws URISyntaxException, IOException {

        final URI uri = Thread.currentThread().getContextClassLoader().getResource(pathStr).toURI();

        if("jar".equals(uri.getScheme())) {

            try(final FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of())) {

                return fileSystem.getPath(File.separator + pathStr);
            }
        }

        else {

            return Paths.get(uri);
        }
    }

    ///..
    /*public static String[] fastSplit(final String input, final char separator) {

        final StringBuilder[] splits = new StringBuilder[4];
        final int length = input.length();

        int idx = 0;
        int splitSize = 0;

        for(int i = 0; i < splits.length; i++) splits[i] = new StringBuilder();

        for(int i = 0; i < length; i++) {

            final char currentChar = input.charAt(i);

            if(currentChar != separator) splits[idx].append(currentChar);
            else idx++;
        }

        for(int i = 0; i < splits.length; i++) {

            if(!splits[i].isEmpty()) splitSize++;
        }

        final String[] splitsStr = new String[splitSize];

        for(int i = 0; i < splitsStr.length; i++) {

            splitsStr[i] = splits[i].toString();
        }

        return splitsStr;
    }*/

    ///
}
