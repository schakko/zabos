package de.ecw.interceptors.executor;

import java.util.List;
import java.util.Map;

import de.ecw.interceptors.ICommandLineExecutor;

/**
 * Adapter für Klassen, die Kommandozeilen-Optionen und -Umgebungsvariablen
 * verarbeiten.
 * 
 * @author ckl
 * 
 */
public class CommandLineExecutorAdapter implements
                ICommandLineExecutor
{
    /**
     * Kommandozeilenargumente
     */
    List<String> commandArguments;

    /**
     * Umgebungsvariablen
     */
    Map<String, String> environmentVariables;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.interceptors.IThreadedCommandLineExecutor#setCommandArguments(
     * java.util.List)
     */
    public final void setCommandArguments(List<String> commandArguments)
    {
        this.commandArguments = commandArguments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.interceptors.IThreadedCommandLineExecutor#getCommandArguments()
     */
    public final List<String> getCommandArguments()
    {
        return commandArguments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.interceptors.IThreadedCommandLineExecutor#setEnvironmentVariables
     * (java.util.Map)
     */
    public void setEnvironmentVariables(Map<String, String> environmentVariables)
    {
        this.environmentVariables = environmentVariables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.interceptors.IThreadedCommandLineExecutor#getEnvironmentVariables
     * ()
     */
    public final Map<String, String> getEnvironmentVariables()
    {
        return environmentVariables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.interceptors.IThreadedCommandLineExecutor#buildExecutor()
     */
    public ThreadedCommandLineExecutor buildExecutor()
    {
        return buildExecutor(getCommandArguments(), getEnvironmentVariables());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.interceptors.IThreadedCommandLineExecutor#buildExecutor(java.util
     * .List, java.util.Map)
     */
    public ThreadedCommandLineExecutor buildExecutor(
                    List<String> _commandArguments,
                    Map<String, String> _environmentVariables)
    {
        return new ThreadedCommandLineExecutor(_commandArguments,
                        _environmentVariables);
    }

}
