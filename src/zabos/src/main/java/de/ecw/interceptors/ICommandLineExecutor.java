package de.ecw.interceptors;

import java.util.List;
import java.util.Map;

import de.ecw.interceptors.executor.ThreadedCommandLineExecutor;

public interface ICommandLineExecutor
{

    /**
     * Liefert die Liste mit den auszuführenden Kommandos
     * 
     * @param commandArguments
     */
    public void setCommandArguments(List<String> commandArguments);

    /**
     * Setzt die Liste mit den auszuführenden Kommandos
     * 
     * @return
     */
    public List<String> getCommandArguments();

    /**
     * Setzt die Umgebungsvariablen
     * 
     * @param environmentVariables
     */
    public void setEnvironmentVariables(Map<String, String> environmentVariables);

    /**
     * Liefert die Umgebungsvariablen
     * 
     * @return
     */
    public Map<String, String> getEnvironmentVariables();

    /**
     * Erstellt einen Executor aus den Argumenten dieser Klasse
     * 
     * @return
     */
    public ThreadedCommandLineExecutor buildExecutor();

    /**
     * Erstellt einen {@link ThreadedCommandLineExecutor} mit den übergebenen
     * Argumenten
     * 
     * @param _commandArguments
     * @param _environmentVariables
     * @return
     */
    public ThreadedCommandLineExecutor buildExecutor(
                    List<String> _commandArguments,
                    Map<String, String> _environmentVariables);

}