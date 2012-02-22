package de.ecw.zabos.alarm;

import java.util.HashMap;
import java.util.Map;

import de.ecw.zabos.sql.vo.FunktionstraegerVO;

/**
 * Statistiken der Funktionsträger
 * 
 * @author ckl
 */
public class FunktionstraegerStatistik
{
    // Key ist FunktionstraegerVO, Value ist RueckmeldeStatistik
    private Map<FunktionstraegerVO, RueckmeldeStatistik> hmFunktionstraeger = new HashMap<FunktionstraegerVO, RueckmeldeStatistik>();

    /**
     * Findet die Rückmeldestatistik eines Funktionsträgers. Ist keine
     * Rückmeldestatistik vorhanden, wird eine neue Statistik erzeugt.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public RueckmeldeStatistik findRueckmeldeStatistik(
                    FunktionstraegerVO _funktionstraegerVO)
    {
        if (!hmFunktionstraeger.containsKey(_funktionstraegerVO))
        {
            hmFunktionstraeger.put(_funktionstraegerVO,
                            new RueckmeldeStatistik(0));
        }

        return (RueckmeldeStatistik) hmFunktionstraeger
                        .get(_funktionstraegerVO);
    }

    /**
     * Liefert alle Statistiken der Funktionsträger. Key ist FunktionstraegerVO,
     * Value ist RueckmeldeStatistik.
     * 
     * @return
     * @author ckl
     */
    public Map<FunktionstraegerVO, RueckmeldeStatistik> findAll()
    {
        return hmFunktionstraeger;
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public int getNumJa(FunktionstraegerVO _funktionstraegerVO)
    {
        return findRueckmeldeStatistik(_funktionstraegerVO).getNumJa();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public int getNumNein(FunktionstraegerVO _funktionstraegerVO)
    {
        return findRueckmeldeStatistik(_funktionstraegerVO).getNumNein();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public int getNumSpaeter(FunktionstraegerVO _funktionstraegerVO)
    {
        return findRueckmeldeStatistik(_funktionstraegerVO).getNumSpaeter();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public int getNumUnbekannt(FunktionstraegerVO _funktionstraegerVO)
    {
        return findRueckmeldeStatistik(_funktionstraegerVO).getNumUnbekannt();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     * @since 19.06.2007_11:02:12
     */
    public void incJa(FunktionstraegerVO _funktionstraegerVO)
    {
        findRueckmeldeStatistik(_funktionstraegerVO).incJa();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public void incNein(FunktionstraegerVO _funktionstraegerVO)
    {
        findRueckmeldeStatistik(_funktionstraegerVO).incNein();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public void incSpaeter(FunktionstraegerVO _funktionstraegerVO)
    {
        findRueckmeldeStatistik(_funktionstraegerVO).incSpaeter();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public void incTotal(FunktionstraegerVO _funktionstraegerVO)
    {
        findRueckmeldeStatistik(_funktionstraegerVO).incTotal();
    }

    /**
     * Delegiert an Rückmeldestatistik.
     * 
     * @param _funktionstraegerVO
     * @return
     * @author ckl
     */
    public void incUnbekannt(FunktionstraegerVO _funktionstraegerVO)
    {
        findRueckmeldeStatistik(_funktionstraegerVO).incUnbekannt();
    }
}
