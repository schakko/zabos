package de.ecw.interceptors.executor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ecw.zabos.util.ListUtil;

/**
 * Ausführung eines Kommandozeilen-Tools in einer gethreadeten Umgebung
 * 
 * @author ckl
 * 
 */
public class ThreadedCommandLineExecutor extends Thread
{
    private final static Logger log = Logger
                    .getLogger(ThreadedCommandLineExecutor.class);

    private List<String> command;

    private Map<String, String> environmentVariables;

    /**
     * @param _command
     *            Kommandozeilenargumente
     * @param _environmentVariables
     *            Umgebungsvariablen; wenn die Umgebungsvariable bereits
     *            existiert, wird sie überschrieben
     */
    public ThreadedCommandLineExecutor(List<String> _command,
                    Map<String, String> _environmentVariables)
    {
        setCommand(_command);
        setEnvironmentVariables(_environmentVariables);
    }

    public void run()
    {
        if (getCommand() == null || getCommand().size() == 0)
        {
            log
                            .error("Kommando kann nicht ausgefuehrt werden, da keines gesetzt wurde");
            return;
        }

        log.info("Fuehre Shell-Kommando \""
                        + ListUtil.toString(getCommand(), " ") + "\" aus...");

        try
        {
            ProcessBuilder pb = new ProcessBuilder(getCommand());

            Map<String, String> env = pb.environment();

            if (env != null)
            {
                if (getEnvironmentVariables() != null)
                {
                    log.info("Injiziere zusaetzliche Umgebungsvariablen...");
                    Iterator<String> itKeys = getEnvironmentVariables()
                                    .keySet().iterator();

                    while (itKeys.hasNext())
                    {
                        String key = itKeys.next();
                        env.put(key, getEnvironmentVariables().get(key));
                    }
                }

                Iterator<String> itKeys = env.keySet().iterator();
                log.debug("Environment-Variablen: ");

                while (itKeys.hasNext())
                {
                    String key = itKeys.next();
                    log.debug("  \"" + key + "\"=\"" + env.get(key) + "\"");
                }
            }

            Process process = pb.start();
            int r = process.waitFor();

            log
                            .info("Shell-Kommando lieferte  \"" + r
                                            + "\" als Rueckgabewert");
        }
        catch (Exception e)
        {
            log.error("Ausfuehrung des Shell-Kommandos schlug fehl: "
                            + e.getMessage());
        }

    }

    /**
     * Setzt das auszuführende Kommando
     * 
     * @param command
     */
    final public void setCommand(List<String> command)
    {
        this.command = command;
    }

    /**
     * Liefert das auszuführende Kommando
     * 
     * @return
     */
    final public List<String> getCommand()
    {
        return command;
    }

    /**
     * Setzt zusätzliche Umgebungsvariablen
     * 
     * @param environmentVariables
     */
    public void setEnvironmentVariables(Map<String, String> environmentVariables)
    {
        this.environmentVariables = environmentVariables;
    }

    /**
     * Liefert zusätzliche Umgebungsvariablen
     * 
     * @return
     */
    public Map<String, String> getEnvironmentVariables()
    {
        return environmentVariables;
    }
}
