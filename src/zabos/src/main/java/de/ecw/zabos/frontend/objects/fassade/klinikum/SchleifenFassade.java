package de.ecw.zabos.frontend.objects.fassade.klinikum;

import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Diese Fassade hält die Schleife und die Bereiche innerhalb der Schleife
 * inklusive der zugehörigen Personen vor.
 * 
 * @author ckl
 * 
 */
public class SchleifenFassade
{
    private BereichInSchleifeMitPersonenFassade[] bereichInSchleifeMitPersonenFassade;

    private BereichInSchleifeFassade[] bereichInSchleifeFassade;

    private SchleifeVO schleifeVO;

    public SchleifenFassade(
                    SchleifeVO _schleifeVO,
                    BereichInSchleifeFassade[] _bereichInSchleifeFassade,
                    BereichInSchleifeMitPersonenFassade[] _bereichInSchleifeMitPersonenFassade)
    {
        setSchleifeVO(_schleifeVO);
        setBereichInSchleifeFassade(_bereichInSchleifeFassade);
        setBereichInSchleifeMitPersonenFassade(_bereichInSchleifeMitPersonenFassade);
    }

    public void setBereichInSchleifeMitPersonenFassade(
                    BereichInSchleifeMitPersonenFassade[] bereichInSchleifeMitPersonenFassade)
    {
        this.bereichInSchleifeMitPersonenFassade = bereichInSchleifeMitPersonenFassade;
    }

    public BereichInSchleifeMitPersonenFassade[] getBereichInSchleifeMitPersonenFassade()
    {
        return bereichInSchleifeMitPersonenFassade;
    }

    public void setSchleifeVO(SchleifeVO schleifeVO)
    {
        this.schleifeVO = schleifeVO;
    }

    public SchleifeVO getSchleifeVO()
    {
        return schleifeVO;
    }

    public void setBereichInSchleifeFassade(
                    BereichInSchleifeFassade[] bereichInSchleifeFassade)
    {
        this.bereichInSchleifeFassade = bereichInSchleifeFassade;
    }

    public BereichInSchleifeFassade[] getBereichInSchleifeFassade()
    {
        return bereichInSchleifeFassade;
    }
}
