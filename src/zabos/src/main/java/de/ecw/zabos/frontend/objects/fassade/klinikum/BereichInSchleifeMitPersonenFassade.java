package de.ecw.zabos.frontend.objects.fassade.klinikum;

import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.PersonVO;

/**
 * Fassade für die Zuweisung von Bereichen / Funktionsträgern innerhalb einer
 * Schleife mit den Personen, die der Kombination angehören
 * 
 * @author ckl
 * 
 */
public class BereichInSchleifeMitPersonenFassade extends
                BereichInSchleifeFassade
{
    private PersonVO[] personen;

    private PersonVO[] personenMitAktivemHandy;

    public BereichInSchleifeMitPersonenFassade(BereichInSchleifeFassade _bisf,
                    PersonVO[] _personen, PersonVO[] _personenMitAktivemHandy)
    {
        super(_bisf.getBereichInSchleife(), _bisf.getFunktionstraeger(), _bisf
                        .getBereich());
        setPersonen(_personen);
        setPersonenMitAktivemHandy(_personenMitAktivemHandy);
    }

    public BereichInSchleifeMitPersonenFassade(
                    BereichInSchleifeVO _bereichInSchleifeVO,
                    FunktionstraegerVO _funktionstraegerVO,
                    BereichVO _bereichVO, PersonVO[] _personen,
                    PersonVO[] _personenMitAktivemHandy)
    {
        super(_bereichInSchleifeVO, _funktionstraegerVO, _bereichVO);
        setPersonen(_personen);
        setPersonenMitAktivemHandy(_personenMitAktivemHandy);
    }

    final public void setPersonen(PersonVO[] personen)
    {
        this.personen = personen;
    }

    final public PersonVO[] getPersonen()
    {
        return personen;
    }

    public void setPersonenMitAktivemHandy(PersonVO[] personenMitAktivemHandy)
    {
        this.personenMitAktivemHandy = personenMitAktivemHandy;
    }

    public PersonVO[] getPersonenMitAktivemHandy()
    {
        return personenMitAktivemHandy;
    }
}
