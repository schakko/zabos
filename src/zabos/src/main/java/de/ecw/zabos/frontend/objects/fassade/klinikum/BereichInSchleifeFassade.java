package de.ecw.zabos.frontend.objects.fassade.klinikum;

import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;

/**
 * Fassade für die Zuweisung der einzelnen Bereiche zur Schleife
 * 
 * @author ckl
 * 
 */
public class BereichInSchleifeFassade
{
    private BereichInSchleifeVO bereichInSchleife;

    private FunktionstraegerVO funktionstraeger;

    private BereichVO bereich;

    /**
     * Konstruktor
     * 
     * @param _bereichInSchleifeVO
     * @param _funktionstraegerVO
     * @param _bereichVO
     */
    public BereichInSchleifeFassade(BereichInSchleifeVO _bereichInSchleifeVO,
                    FunktionstraegerVO _funktionstraegerVO, BereichVO _bereichVO)
    {
        setFunktionstraeger(_funktionstraegerVO);
        setBereich(_bereichVO);
        setBereichInSchleife(_bereichInSchleifeVO);
    }

    final public void setBereich(BereichVO bereich)
    {
        this.bereich = bereich;
    }

    public BereichVO getBereich()
    {
        return bereich;
    }

    final public void setFunktionstraeger(FunktionstraegerVO funktionstraegerVO)
    {
        this.funktionstraeger = funktionstraegerVO;
    }

    public FunktionstraegerVO getFunktionstraeger()
    {
        return funktionstraeger;
    }

    final public void setBereichInSchleife(BereichInSchleifeVO bereichInSchleife)
    {
        this.bereichInSchleife = bereichInSchleife;
    }

    public BereichInSchleifeVO getBereichInSchleife()
    {
        return bereichInSchleife;
    }

}
