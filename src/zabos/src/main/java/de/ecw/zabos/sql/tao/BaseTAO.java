package de.ecw.zabos.sql.tao;

import java.sql.Savepoint;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.DAOFactory;
import de.ecw.zabos.sql.resource.DBResource;

/**
 * 
 * Basisklasse für alle "Transaction Objects".
 * 
 * Jede Transaktionsmethode kapselt ein oder mehrere schreibende DAO Aufrufe in
 * einen try/catch Block.
 * 
 * Am Anfang des try Blocks wird ein Transaction Savepoint gesetzt. Wird der try
 * Block ohne Fehler abgearbeitet; wird die Transaktion mit commit()
 * abgeschlossen. Im Fehlerfall wird die Datenbank mittels rollback() auf den
 * Savepoint zurückgesetzt.
 * 
 * Für die Savepoints wird ein Stack verwendet, so dass sich TAO Aufrufe
 * schachteln lassen.
 * 
 * TAO Methoden werfen i.d.R. keine Exceptions; Der Erfolg einer Methode wird
 * über den R+ckgabewert mitgeteilt.
 * 
 * Um Fehler, wie z.B. das doppelte Anlegen von Datensaetzen, zu vermeiden
 * sollte vor Aufruf der TAO Methode geprüft werden ob der Datensatz bereits
 * existiert.
 * 
 * Plausibilitätsprüfungen sind ebenso dem Aufrufer überlassen; dazu gehört z.B.
 * das Anlegen von Schleifen ohne Verantwortliche bzw. Einsatzkräfte oder auch
 * das Anlegen von Personen ohne Zuweisung eines Telefon.
 * 
 * @author bsp
 * 
 */
public class BaseTAO
{

    private final static Logger log = Logger.getLogger(BaseTAO.class);

    protected DBResource dbresource;

    protected DAOFactory daoFactory;

    protected TaoFactory taoFactory;

    public BaseTAO(DBResource _dbresource)
    {
        dbresource = _dbresource;
        daoFactory = dbresource.getDaoFactory();
        taoFactory = dbresource.getTaoFactory();
    }

    /**
     * Savepoint Stack fuer Rollback
     */
    private Vector<Savepoint> savepoints = new Vector<Savepoint>();

    /**
     * Legt einen neuen Savepoint und markiert damit den Beginn einer
     * Transaktion
     * 
     */
    synchronized public void begin()
    {
        try
        {
            Savepoint savepoint = dbresource.getDBConnection().setSavepoint();
            savepoints.insertElementAt(savepoint, 0);
        }
        catch (StdException e)
        {
            log.error("unable to set savepoint", e);
        }
    }

    /**
     * Beendet eine Transaktion.
     * 
     * Die Aenderungen, die waehrend der Transaktion durchgefuehrt sind danach
     * fuer andere Transaktionen sichtbar.
     * 
     * @throws StdException
     */
    synchronized public void commit() throws StdException
    {
        if (savepoints.size() == 0)
        {
            throw new StdException("commit() ohne begin()");
        }
        dbresource.getDBConnection().commit();
        savepoints.remove(0);
    }

    /**
     * Verwirft eine Transaktion und kehrt zu dem in commit() gesetzten
     * Savepoint zurueck.
     * 
     */
    synchronized public void rollback()
    {
        try
        {
            if (savepoints.size() == 0)
            {
                throw new StdException("rollback() ohne begin()");
            }
            Savepoint savepoint = (Savepoint) savepoints.get(0);
            savepoints.remove(0);
            dbresource.getDBConnection().rollback(savepoint);
        }
        catch (StdException e)
        {
            log.error("error while rolling back transaction", e);
        }
    }

    public DBResource getDBResource()
    {
        return dbresource;
    }

}
