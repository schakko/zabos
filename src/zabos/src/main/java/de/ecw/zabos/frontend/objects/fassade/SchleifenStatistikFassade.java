package de.ecw.zabos.frontend.objects.fassade;

import de.ecw.zabos.alarm.RueckmeldeStatistik;
import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Fassadenobjekt für die Schleifenstatistiken
 * 
 * @author ckl
 */
public class SchleifenStatistikFassade
{
    /**
     * Schleife
     */
    private SchleifeVO schleifeVO = null;

    /**
     * Rückmeldestatistik
     */
    private RueckmeldeStatistik rs = null;

    /**
     * Liefert die Schleife
     * 
     * @return SchleifeVO
     */
    public SchleifeVO getSchleife()
    {
        return schleifeVO;
    }

    /**
     * Setzt die Schleife
     * 
     * @param _schleife
     */
    public void setSchleife(SchleifeVO _schleife)
    {
        this.schleifeVO = _schleife;
    }

    /**
     * Liefert die Zwischenstatistik der Schleife
     * 
     * @return RueckmeldeStatistik
     */
    public RueckmeldeStatistik getRueckmeldeStatistik()
    {
        return rs;
    }

    /**
     * Setzt die Rückmeldestatistik der Schleife
     * 
     * @param _rs
     */
    public void setRueckmeldeStatistik(RueckmeldeStatistik _rs)
    {
        this.rs = _rs;
    }
}
