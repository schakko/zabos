package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;

/**
 * Zu Testzwecken
 * 
 * @author bsp
 * 
 */
public class TestTAO extends BaseTAO
{
    private final static Logger log = Logger.getLogger(TestTAO.class);

    public TestTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    public void testOrganisation() throws StdException
    {
        try
        {
            begin();

            OrganisationDAO organisationDAO = daoFactory.getOrganisationDAO();

            OrganisationVO organisationVO = daoFactory.getObjectFactory()
                            .createOrganisation();
            organisationVO.setBeschreibung("beschreibung");
            organisationVO.setName("test");
            organisationVO = organisationDAO.createOrganisation(organisationVO);

            log.debug("organisation angelegt");

            organisationDAO.deleteOrganisation(organisationVO
                            .getOrganisationId());

            log.debug("organisation geloescht");

            organisationVO = daoFactory.getObjectFactory().createOrganisation();
            organisationVO.setBeschreibung("beschreibung2");
            organisationVO.setName("test");

            organisationVO = organisationDAO.createOrganisation(organisationVO);

            log.debug("organisation wieder angelegt");

            organisationVO.setBeschreibung("beschreibung3");

            organisationDAO.updateOrganisation(organisationVO);

            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            throw e;
        }
    }

    public void testTelefon() throws StdException
    {
        try
        {
            begin();

            PersonDAO personDAO = daoFactory.getPersonDAO();
            TelefonDAO telefonDAO = daoFactory.getTelefonDAO();

            PersonVO personVO = daoFactory.getObjectFactory().createPerson();

            personVO.setName("testTelefon");
            personVO.setNachname("testTElefon_nachname");
            personVO.setVorname("testTelefon_vorname");
            personVO = personDAO.createPerson(personVO);

            TelefonVO telefonVO = daoFactory.getObjectFactory().createTelefon();
            telefonVO.setPersonId(personVO.getPersonId());
            telefonVO.setNummer(new TelefonNummer("042123490234"));

            telefonVO = telefonDAO.createTelefon(telefonVO);

            telefonVO.setNummer(new TelefonNummer("239482034234"));
            telefonVO = telefonDAO.updateTelefon(telefonVO);

            telefonDAO.deleteTelefon(telefonVO.getTelefonId());

            personDAO.deletePerson(personVO.getPersonId());

            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            throw e;
        }
    }

    public void testFindSchleifenByPersonAndRechtForJS() throws StdException
    {
        SchleifenDAO schleifenDAO = daoFactory.getSchleifenDAO();
        String s = schleifenDAO.findSchleifenByPersonAndRechtForJS(
                        new PersonId(2616), RechtId.ALARM_AUSLOESEN);
        log.debug("s=\"" + s + "\"");
    }

    public void testCreatePersonLizenz() throws StdException
    {
        try
        {
            begin();

            PersonDAO personDAO = daoFactory.getPersonDAO();

            PersonVO personVO = daoFactory.getObjectFactory().createPerson();
            personVO.setName("test");
            personVO.setVorname("vorname");
            personVO.setNachname("nachname");
            personVO.setBeschreibung("lizenztest");
            personVO = personDAO.createPerson(personVO);

            rollback(); // das hier soll niemals in der datenbank landen
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /**
     * Bereinigt die Datenbank von Test-Eintraegen
     * 
     * @throws StdException
     */
    public void cleanTestData() throws StdException
    {
        log.info("Saeubere Tabellen");
        begin();
        cleanTable(Scheme.PROBE_TERMIN_TABLE);
        cleanTable(Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE);
        cleanTable(Scheme.PERSON_IN_ALARM_TABLE);
        cleanTable(Scheme.SCHLEIFE_IN_SMSOUT_TABLE);
        cleanTable(Scheme.SCHLEIFE_IN_ALARM_TABLE);
        cleanTable(Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE);
        cleanTable(Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE);
        cleanTable(Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE);
        cleanTable(Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE);
        cleanTable(Scheme.SMSOUT_TABLE);
        cleanTable(Scheme.BEREICH_IN_ALARM_TABLE);
        cleanTable(Scheme.ALARM_TABLE);
        cleanTable(Scheme.BEREICH_IN_SCHLEIFE_TABLE);
        cleanTable(Scheme.SCHLEIFE_TABLE);
        cleanTable(Scheme.TELEFON_TABLE);
        cleanTable(Scheme.PERSON_TABLE);
        cleanTable(Scheme.ORGANISATIONSEINHEIT_TABLE);
        cleanTable(Scheme.ORGANISATION_TABLE);
        cleanTable(Scheme.SMSIN_TABLE);
        cleanTable(Scheme.FUENFTON_TABLE);
        cleanTable(Scheme.FUNKTIONSTRAEGER_TABLE, Scheme.COLUMN_ID + " > 12 ");
        cleanTable(Scheme.BEREICH_TABLE);
        cleanTable(Scheme.RECHT_IN_ROLLE_TABLE);
        cleanTable(Scheme.ROLLE_TABLE);
        cleanTable(Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE);

        commit();
    }

    private void cleanTable(String _table) throws StdException
    {
        createStatement("DELETE FROM " + _table + " CASCADE").execute();
    }

    private void cleanTable(String _table, String _where) throws StdException
    {
        createStatement("DELETE FROM " + _table + " CASCADE WHERE " + _where)
                        .execute();
    }

    private PreparedStatement createStatement(String _sql) throws StdException
    {
        // log.debug("Statement zum Ausfuehren: " + _sql);
        return new PreparedStatement(dbresource.getDBConnection(), _sql);
    }
}
