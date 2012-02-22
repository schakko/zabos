package de.ecw.zabos.alarm;

import java.util.HashMap;
import java.util.Map;

import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Rückmelde-Statistik für einen Alarm
 * 
 * @author bsp
 * 
 */
public class ZwischenStatistik
{

    public ZwischenStatistik(int _numPersonen)
    {
        gesamt = new RueckmeldeStatistik(_numPersonen);
        mapSchleifenStats = new HashMap<SchleifeVO, RueckmeldeStatistik>();
        mapFunktionstraegerStats = new HashMap<SchleifeVO, FunktionstraegerStatistik>();
    }

    /**
     * Statistik über alle Schleifen eines Alarms
     * 
     */
    public RueckmeldeStatistik gesamt;

    /**
     * Statistik nach Schleifen aufgeteilt Key ist SchleifeVO, Value ist
     * RueckmeldeStatistik
     * 
     */
    public Map<SchleifeVO, RueckmeldeStatistik> mapSchleifenStats;

    /**
     * Statistik der einzelnen Funktionstr�ger innerhalb einer Schleife. Key ist
     * SchleifeVO, Value ist FunktionstraegerStatistik
     */
    public Map<SchleifeVO, FunktionstraegerStatistik> mapFunktionstraegerStats;
}
