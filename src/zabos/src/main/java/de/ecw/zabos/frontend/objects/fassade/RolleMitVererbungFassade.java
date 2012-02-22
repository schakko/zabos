package de.ecw.zabos.frontend.objects.fassade;

import de.ecw.zabos.sql.vo.RolleVO;

/**
 * Hilfsobjekt, speichert ob eine Rolle vererbt ist
 * 
 * @author ckl
 */
public class RolleMitVererbungFassade
{
    private RolleVO rolleVO = null;

    private boolean bIstVererbt = false;

    /**
     * Konstruktor
     * 
     * @param _voRolle
     *            Rolle
     * @param _bIstVererbt
     *            Ist vererbt oder nicht
     */
    public RolleMitVererbungFassade(RolleVO _voRolle, boolean _bIstVererbt)
    {
        this.rolleVO = _voRolle;
        this.bIstVererbt = _bIstVererbt;
    }

    /**
     * Liefert zurück, ob die Rolle vererbt ist oder nicht
     * 
     * @return true|false
     */
    public boolean isIstVererbt()
    {
        return bIstVererbt;
    }

    /**
     * Liefert die Rolle
     * 
     * @return RolleVO
     */
    public RolleVO getRolle()
    {
        return rolleVO;
    }
}