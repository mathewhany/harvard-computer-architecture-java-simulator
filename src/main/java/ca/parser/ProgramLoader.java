package ca.parser;

import ca.CaException;

import java.util.List;

public interface ProgramLoader {
    List<String> loadProgram() throws CaException;
}
