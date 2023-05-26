package ca.parser;

import ca.CaException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileProgramLoader implements ProgramLoader {
    private final String path;

    public FileProgramLoader(String path) {
        this.path = path;
    }

    public List<String> loadProgram() throws CaException {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            throw new CaException("Could not read file: " + path, e);
        }
    }
}
