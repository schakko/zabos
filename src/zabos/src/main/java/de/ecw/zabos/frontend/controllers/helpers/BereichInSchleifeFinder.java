package de.ecw.zabos.frontend.controllers.helpers;

import org.apache.log4j.Logger;

import de.ecw.zabos.bo.SchleifeBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.objects.fassade.klinikum.BereichInSchleifeFassade;
import de.ecw.zabos.frontend.objects.fassade.klinikum.BereichInSchleifeMitPersonenFassade;
import de.ecw.zabos.frontend.objects.fassade.klinikum.SchleifenFassade;
import de.ecw.zabos.frontend.utils.ObjectFinder;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;

public class BereichInSchleifeFinder
{
    private DBResource dbRessource;

    private FunktionstraegerDAO daoFunktionstraeger;

    private BereichDAO daoBereich;

    private BereichInSchleifeDAO daoBereichInSchleife;

    private TelefonDAO daoTelefon;

    private SchleifeBO boSchleife;

    private final static Logger log = Logger
                    .getLogger(BereichInSchleifeFinder.class);

    public BereichInSchleifeFinder(DBResource _dbResource)
    {
        setDbRessource(_dbResource);

        daoBereich = _dbResource.getDaoFactory().getBereichDAO();
        daoFunktionstraeger = _dbResource.getDaoFactory()
                        .getFunktionstraegerDAO();
        daoBereichInSchleife = _dbResource.getDaoFactory()
                        .getBereichInSchleifeDAO();
        daoTelefon = _dbResource.getDaoFactory().getTelefonDAO();
        boSchleife = _dbResource.getBoFactory().getSchleifeBO();
    }

    public void setDbRessource(DBResource dbRessource)
    {
        this.dbRessource = dbRessource;
    }

    public DBResource getDbRessource()
    {
        return dbRessource;
    }

    public SchleifenFassade createSchleifenFassade(SchleifeVO _schleifeVO,
                    boolean _bLadePersonenZuordnung)
    {
        SchleifenFassade r;
        BereichInSchleifeMitPersonenFassade[] bisfmpContainer = null;
        BereichInSchleifeFassade[] bisfContainer = null;

        try
        {
            FunktionstraegerVO[] funktionstraegerVOs = daoFunktionstraeger
                            .findAll();
            BereichVO[] bereichVOs = daoBereich.findAll();

            ObjectFinder ofBereich = new ObjectFinder(bereichVOs);
            ObjectFinder ofFunktionstraeger = new ObjectFinder(
                            funktionstraegerVOs);

            BereichInSchleifeVO[] bereichInSchleifeVOs = daoBereichInSchleife
                            .findBereicheInSchleifeBySchleifeId(_schleifeVO
                                            .getSchleifeId());

            bisfContainer = new BereichInSchleifeFassade[bereichInSchleifeVOs.length];

            if (_bLadePersonenZuordnung)
            {
                bisfmpContainer = new BereichInSchleifeMitPersonenFassade[bereichInSchleifeVOs.length];;
            }

            for (int i = 0, m = bereichInSchleifeVOs.length; i < m; i++)
            {
                BereichInSchleifeVO bis = bereichInSchleifeVOs[i];
                BereichInSchleifeFassade bisf = new BereichInSchleifeFassade(
                                bis,
                                (FunktionstraegerVO) ofFunktionstraeger
                                                .findElement(bis
                                                                .getFunktionstraegerId()),
                                (BereichVO) ofBereich.findElement(bis
                                                .getBereichId()));

                bisfContainer[i] = bisf;

                if (_bLadePersonenZuordnung)
                {
                    PersonVO[] personen = boSchleife
                                    .findPersonenMitEmpfangsberechtigungBySchleifeAndBereichAndFunktionstraeger(
                                                    _schleifeVO.getSchleifeId(),
                                                    bis.getBereichId(),
                                                    bis.getFunktionstraegerId());

                    PersonVO[] personenMitAktivemHandy = daoTelefon
                                    .filterPersonenMitAktiverHandyNummer(personen);

                    bisfmpContainer[i] = new BereichInSchleifeMitPersonenFassade(
                                    bisf, personen, personenMitAktivemHandy);
                }
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        r = new SchleifenFassade(_schleifeVO, bisfContainer, bisfmpContainer);

        return r;
    }
}
