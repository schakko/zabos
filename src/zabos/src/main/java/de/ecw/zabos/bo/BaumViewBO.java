package de.ecw.zabos.bo;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.cvo.BaumCVO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.cache.ICacheEventListener;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Erstellt den Baum für die Anzeige aller Schleifen, Organisationen und
 * Organisationseinheiten des Zabos-Systems
 * 
 * @author ckl
 * 
 */
public class BaumViewBO
{
    // Serial
    public final static long serialVersionUID = 1209312249;

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOrganisationseinheit = null;

    protected SchleifenDAO daoSchleife = null;

    protected DBResource db = null;

    private final static Logger log = Logger.getLogger(BaumViewBO.class);

    private boolean isSynchronized = false;

    private BaumCVO cachedBaum = null;

    public BaumViewBO(DBResource _db)
    {
        this.db = _db;

        daoOrganisation = db.getDaoFactory().getOrganisationDAO();
        daoOrganisationseinheit = db.getDaoFactory()
                        .getOrganisationsEinheitDAO();
        daoSchleife = db.getDaoFactory().getSchleifenDAO();

        initCacheListener();
    }

    /**
     * Hilfsklasse, für die Überprüfung, ob Änderungen an den Datensätzen
     * stattgefunden haben
     * 
     * @author ckl
     * 
     */
    public class CacheEventListener implements ICacheEventListener
    {
        public void onEventOccured(EVENT event)
        {
            setSynchronized(false);
        }
    }

    /**
     * Initalisiiert die {@link ICacheEventListener} für {@link SchleifenDAO},
     * {@link OrganisationDAO} und {@link OrganisationsEinheitDAO}
     */
    private void initCacheListener()
    {
        CacheEventListener cel = new CacheEventListener();
        daoOrganisation.CACHE_FIND_ALL.addEventListener(
                        ICacheEventListener.EVENT.AFTER_UPDATE, cel);
        daoSchleife.CACHE_FIND_ALL.addEventListener(
                        ICacheEventListener.EVENT.AFTER_UPDATE, cel);
        daoOrganisationseinheit.CACHE_FIND_ALL.addEventListener(
                        ICacheEventListener.EVENT.AFTER_UPDATE, cel);
    }

    /**
     * Erstellt den Baum. Wenn der Baum bereits im Cache existiert und sich noch
     * keine Änderungen aufgetan haben, wird er aus dem Cache zuü�ckgeliefert.
     */
    public synchronized BaumCVO findTreeView()
    {
        if ((cachedBaum == null) || (!isSynchronized()))
        {
            log.debug("BaumCVO im Cache: " + cachedBaum + " synchronisiert: "
                            + isSynchronized());
            cachedBaum = findRootAsBranch();
            setSynchronized(true);
        }

        return cachedBaum;
    }

    /**
     * Liefert den Root-Branch zurück
     * 
     * @return BaumBO
     */
    protected BaumCVO findRootAsBranch()
    {
        OrganisationVO[] branchOrganisationen = null;
        BaumCVO branch = new BaumCVO();
        branch.setId(0);
        branch.setName("System");

        try
        {
            branchOrganisationen = daoOrganisation.findAll();
        }
        catch (StdException e)
        {
            log.error(e);
        }

        if (branchOrganisationen != null)
        {
            for (int i = 0, m = branchOrganisationen.length; i < m; i++)
            {
                branch
                                .setSubTree(findOrganisationAsBranch(branchOrganisationen[i]));
            }
        }

        return branch;
    }

    /**
     * Liefert den Zweig der Organisation inkl. aller Unterknoten
     * 
     * @param _organisation
     * @return BaumBO
     */
    protected BaumCVO findOrganisationAsBranch(OrganisationVO _organisation)
    {
        OrganisationsEinheitVO[] branchOrganisationseinheiten = null;

        BaumCVO branch = new BaumCVO();
        branch.setId(_organisation.getBaseId().getLongValue());
        branch.setName(_organisation.getName());

        try
        {
            branchOrganisationseinheiten = daoOrganisationseinheit
                            .findOrganisationsEinheitenByOrganisationId(_organisation
                                            .getOrganisationId());
        }
        catch (StdException e)
        {
            log.error(e);
        }

        if (branchOrganisationseinheiten != null)
        {
            for (int i = 0, m = branchOrganisationseinheiten.length; i < m; i++)
            {
                branch
                                .setSubTree(findOrganisationseinheitAsBranch(branchOrganisationseinheiten[i]));
            }
        }

        return branch;
    }

    /**
     * Liefert den Zweig der Organisationseinheit inkl. aller Unterknoten
     * 
     * @param _organisationseinheit
     * @return BaumBO
     */
    protected BaumCVO findOrganisationseinheitAsBranch(
                    OrganisationsEinheitVO _organisationseinheit)
    {
        SchleifeVO[] branchSchleifen = null;

        BaumCVO branch = new BaumCVO();
        branch.setId(_organisationseinheit.getBaseId().getLongValue());
        branch.setName(_organisationseinheit.getName());

        try
        {
            branchSchleifen = daoSchleife
                            .findSchleifenByOrganisationsEinheitId(_organisationseinheit
                                            .getOrganisationsEinheitId());
        }
        catch (StdException e)
        {
            log.error(e);
        }

        for (int i = 0, m = branchSchleifen.length; i < m; i++)
        {
            branch.setSubTree(findSchleifeAsBranch(branchSchleifen[i]));
        }

        return branch;
    }

    /**
     * Liefert die Schleife - ohne Unterknoten, da die Schleife das unterste
     * Element ist
     * 
     * @param _schleife
     * @return BaumBO
     */
    protected BaumCVO findSchleifeAsBranch(SchleifeVO _schleife)
    {
        BaumCVO branch = new BaumCVO();
        branch.setId(_schleife.getBaseId().getLongValue());
        branch.setName(_schleife.getName());

        return branch;
    }

    /**
     * Setzt das Objekt als synchronisiert
     * 
     * @param isSynchronized
     */
    public synchronized void setSynchronized(boolean isSynchronized)
    {
        this.isSynchronized = isSynchronized;
    }

    /**
     * Liefert, ob das Objekt synchronisiert ist
     * 
     * @return
     */
    public synchronized boolean isSynchronized()
    {
        return isSynchronized;
    }
}
