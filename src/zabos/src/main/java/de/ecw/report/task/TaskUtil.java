package de.ecw.report.task;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

/**
 * Utility class
 * 
 * @author ckl
 * 
 */
public class TaskUtil
{
    public final static String END_ID = "_ID";

    /**
     * Sets correct values for a report Every key/value-pair of parameter _hm
     * will be injected into the {@link IRunAndRenderTask} object. <br />
     * If key ends with {@value #END_ID}, the value will be converted to an
     * integer, otherwise value will be a String.
     * 
     * @param _task
     * @param _hm
     */
    public static void setParameterValues(final IRunAndRenderTask _task,
                    Map<String, String> _hm)
    {
        Iterator<String> itKeySet = _hm.keySet().iterator();

        while (itKeySet.hasNext())
        {
            String key = (String) itKeySet.next();
            String val = _hm.get(key);

            if (key.endsWith(END_ID))
            {
                int id = 0;

                try
                {
                    id = Integer.valueOf(val);
                }
                catch (NumberFormatException e)
                {
                    id = 0;
                }

                _task.setParameterValue(key, id);
            }
            else
            {
                _task.setParameterValue(key, val);
            }

        }
    }
}
