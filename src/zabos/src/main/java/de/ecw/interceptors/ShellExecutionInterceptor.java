package de.ecw.interceptors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.interceptors.executor.CommandLineExecutorAdapter;

/**
 * Interceptor, der zum Ausführen von Shell-Kommandos benutzt werden kann. Die
 * einzelnen Befehle werden dann in einem neuen Thread ausgeführt.
 * 
 * @author ckl
 */
public class ShellExecutionInterceptor extends
                CommandLineExecutorAdapter implements IInterceptor
{
    /**
     * Logger-Instanz
     */
    private final static Logger log = Logger
                    .getLogger(ShellExecutionInterceptor.class);

    /**
     * Generischer Handler zum Ausführen von Shell-Kommando
     * 
     * @see de.ecw.zabos.service.alarm.ext.IAlarmInterceptor#intercept(de.ecw.zabos.service.alarm.ext.AlarmInterceptorActionType,
     *      de.ecw.zabos.sql.vo.AlarmVO)
     */
    public void intercept(Object _o)
    {
        Field[] fields = _o.getClass().getDeclaredFields();
        List<String> commandArguments = getCommandArguments();
        List<String> commands = new ArrayList<String>();

        for (int i = 0, m = commandArguments.size(); i < m; i++)
        {
            String command = commandArguments.get(i);

            for (int j = 0, n = fields.length; j < n; j++)
            {
                Field f = fields[j];

                try
                {
                    boolean originalAccessState = f.isAccessible();

                    f.setAccessible(true);

                    command = command.replace("%" + f.getName() + "%", f
                                    .get(_o).toString());

                    f.setAccessible(originalAccessState);
                }
                catch (Exception e)
                {
                    log.error("Konnte Feld \"" + f.getName()
                                    + "\" nicht auslesen");
                }
            }

            commands.add(command);

        }

        buildExecutor(commands, getEnvironmentVariables()).start();
    }
}
