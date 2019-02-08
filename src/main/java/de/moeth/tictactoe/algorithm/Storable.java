package de.moeth.tictactoe.algorithm;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface Storable {

    public void read(Reader reader) throws IOException;

    public void write(Writer writer) throws IOException;
}
