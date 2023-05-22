package ca.parser;

import ca.CaException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileProgramLoader implements ProgramLoader {
    private final String path;
    private final InstructionParser parser;

    public FileProgramLoader(String path, InstructionParser parser) {
        this.path = path;
        this.parser = parser;
    }

    public short[] loadProgram() throws CaException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            short[] instructions = new short[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                instructions[i] = parser.parse(lines.get(i));
            }
            return instructions;
        } catch (IOException e) {
            throw new CaException("Could not read file: " + path, e);
        }
    }
}
