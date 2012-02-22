package de.ecw.zabos.frontend.controllers;

import org.apache.log4j.Logger;

import de.ecw.zabos.bo.BaumViewBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.beans.DataBean;
import de.ecw.zabos.frontend.controllers.helpers.BereichInSchleifeFinder;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.objects.fassade.klinikum.SchleifenFassade;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.sql.ho.AccessControllerHO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Controller für die Statistiken/Reporte im Klinikum
 * 
 * @author ckl
 */
public class StatistikController extends BaseControllerAdapter
{

    // Serial
    final static long serialVersionUID = 1509312049;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(StatistikController.class);

    protected PersonDAO daoPerson = null;

    protected SchleifenDAO daoSchleife = null;

    private BereichInSchleifeFinder bereichInSchleifeFinder = null;

    public StatistikController(final DBResource dbResource,
                    BereichInSchleifeFinder _bereichInSchleifenFinder)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_STATISTIK);

        setBereichInSchleifeFinder(_bereichInSchleifenFinder);

        // TAO/DAO-Factory initalisieren
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#processACL(de.ecw
     * .zabos.frontend.ressources.RequestResources)
     */
    @Override
    public boolean processACL(final RequestResources req)
    {
        if (!super.processACL(req))
        {
            return false;
        }

        // Zugriffe auf die einzelnen Ressourcen setzen
        AccessControllerHO accessController = req.buildAccessController();
        accessController.update(req.getBaseId(null, null));

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#setViewData(de.ecw
     * .zabos.frontend.ressources.RequestResources)
     */
    @Override
    public void setViewData(final RequestResources req)
    {
        DataBean beanPersonenOhneRollen = new DataBean();
        DataBean beanSchleifenFassaden = new DataBean();
        DataBean beanPersonenOhneHandyNummer = new DataBean();

        BaumViewBO treeView = req.getDbResource().getBoFactory()
                        .getBaumViewBO();

        req.getServletRequest().setAttribute(Parameters.NAVIGATION_TREE,
                        treeView.findTreeView());

        try
        {
            beanPersonenOhneRollen.setData(daoPerson
                            .findPersonenOhneRolleInSystem());

            beanPersonenOhneHandyNummer.setData(daoPerson
                            .findPersonenOhneHandyNummer());

            SchleifeVO[] alleSchleifen = daoSchleife.findAll();
            SchleifenFassade[] schleifenFassaden = new SchleifenFassade[alleSchleifen.length];

            for (int i = 0, m = alleSchleifen.length; i < m; i++)
            {
                schleifenFassaden[i] = bereichInSchleifeFinder
                                .createSchleifenFassade(alleSchleifen[i], true);
            }

            beanSchleifenFassaden.setData(schleifenFassaden);
        }
        catch (StdException e)
        {
            log.error(e);
        }

        // Kontext zuruecksetzen
        req.getUserBean().setCtxO(null);

        // Attribute setzen
        req
                        .setData(Parameters.ARR_PERSONEN_OHNE_ROLLEN,
                                        beanPersonenOhneRollen);

        req.setData(Parameters.ARR_PERSONEN_OHNE_HANDYNUMMER,
                        beanPersonenOhneHandyNummer);

        req.setData(Parameters.ARR_SCHLEIFEN_FASSADE, beanSchleifenFassaden);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#run(de.ecw.zabos.
     * frontend.ressources.RequestResources)
     */
    @Override
    public void run(final RequestResources req)
    {
        if (req.isValidSubmit())
        {
        }
    }

    public void setBereichInSchleifeFinder(
                    BereichInSchleifeFinder bereichInSchleifeFinder)
    {
        this.bereichInSchleifeFinder = bereichInSchleifeFinder;
    }

    public BereichInSchleifeFinder getBereichInSchleifeFinder()
    {
        return bereichInSchleifeFinder;
    }
}
