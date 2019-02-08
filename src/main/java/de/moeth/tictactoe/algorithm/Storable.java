package de.moeth.tictactoe.algorithm;

import java.io.*;

public interface Storable {

    public default void read(final File file) throws IOException {
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                read(br);
//                log.info("load " + filePath);
            }
        }
    }

    public void read(Reader reader) throws IOException;

    public void write(Writer writer) throws IOException;
}
