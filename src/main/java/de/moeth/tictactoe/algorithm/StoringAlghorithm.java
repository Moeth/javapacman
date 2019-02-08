package de.moeth.tictactoe.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class StoringAlghorithm implements KIAlgorithm, Storable {

    private static final Logger log = LoggerFactory.getLogger(StoringAlghorithm.class);

    private final String filePath;

    public StoringAlghorithm(final String filePath) throws IOException {
        this.filePath = filePath;
        read(new File(filePath));
    }

    @Override
    public final void storeData() throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            write(writer);
            writer.flush();
            log.info(String.format("saved %d to %s", toString(), filePath));
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
