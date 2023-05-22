package ca.parser;

import ca.CaException;

public interface ProgramLoader {
    short[] loadProgram() throws CaException;
}
