package de.ecw.zabos.sql.dao;

import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.vo.ObjectFactory;

/**
 * Dient zur Verwaltung von DAO Instanzen für eine Datenbankverbindung. Die DAO
 * Instanzen werden über Lazy-Getter erfragt und für die Dauer einer
 * Datenbankverbindung gecached.
 * 
 * @author bsp
 * 
 */
final public class DAOFactory
{

    private DBConnection dbconnection;

    private ObjectFactory objectFactory;

    private AlarmDAO alarmDAO;

    private AlarmQuelleDAO alarmQuelleDAO;

    private BereichDAO bereichDAO;

    private BereichInSchleifeDAO bereichInSchleifeDAO;

    private FuenfTonDAO fuenfTonDAO;

    private FunktionstraegerDAO funktionstraegerDAO;

    private OrganisationDAO organisationDAO;

    private OrganisationsEinheitDAO organisationsEinheitDAO;

    private PersonDAO personDAO;

    private PersonInAlarmDAO personInAlarmDAO;

    private ProbeTerminDAO probeTerminDAO;

    private RechtDAO rechtDAO;

    private RolleDAO rolleDAO;

    private RueckmeldungStatusDAO rueckmeldungStatusDAO;

    private RueckmeldungStatusAliasDAO rueckmeldungStatusAliasDAO;

    private SchleifenDAO schleifenDAO;

    private SmsInDAO smsInDAO;

    private StatistikDAO statistikDAO;

    private SmsOutDAO smsOutDAO;

    private SystemKonfigurationDAO systemKonfigurationDAO;

    private PersonMitRollenDAO personMitRollenDAO;

    private TelefonDAO telefonDAO;

    public DAOFactory(final DBConnection _dbConnection,
                    ObjectFactory _objectFactory)
    {
        dbconnection = _dbConnection;
        setObjectFactory(_objectFactory);
    }

    public DBConnection getDBConnection()
    {
        return dbconnection;
    }

    public AlarmDAO getAlarmDAO()
    {
        if (alarmDAO == null)
        {
            alarmDAO = new AlarmDAO(getDBConnection(), getObjectFactory());
        }
        return alarmDAO;
    }

    public AlarmQuelleDAO getAlarmQuelleDAO()
    {
        if (alarmQuelleDAO == null)
        {
            alarmQuelleDAO = new AlarmQuelleDAO(getDBConnection(),
                            getObjectFactory());
        }
        return alarmQuelleDAO;
    }

    public FuenfTonDAO getFuenfTonDAO()
    {
        if (fuenfTonDAO == null)
        {
            fuenfTonDAO = new FuenfTonDAO(getDBConnection(), getObjectFactory());
        }
        return fuenfTonDAO;
    }

    public FunktionstraegerDAO getFunktionstraegerDAO()
    {
        if (funktionstraegerDAO == null)
        {
            funktionstraegerDAO = new FunktionstraegerDAO(getDBConnection(),
                            getObjectFactory());
        }
        return funktionstraegerDAO;
    }

    public OrganisationDAO getOrganisationDAO()
    {
        if (organisationDAO == null)
        {
            organisationDAO = new OrganisationDAO(getDBConnection(),
                            getObjectFactory());
        }
        return organisationDAO;
    }

    public OrganisationsEinheitDAO getOrganisationsEinheitDAO()
    {
        if (organisationsEinheitDAO == null)
        {
            organisationsEinheitDAO = new OrganisationsEinheitDAO(
                            getDBConnection(), getObjectFactory());
        }
        return organisationsEinheitDAO;
    }

    public PersonDAO getPersonDAO()
    {
        if (personDAO == null)
        {
            personDAO = new PersonDAO(getDBConnection(), getObjectFactory());
        }
        return personDAO;
    }

    public PersonInAlarmDAO getPersonInAlarmDAO()
    {
        if (personInAlarmDAO == null)
        {
            personInAlarmDAO = new PersonInAlarmDAO(getDBConnection(),
                            getObjectFactory());
        }
        return personInAlarmDAO;
    }

    public ProbeTerminDAO getProbeTerminDAO()
    {
        if (probeTerminDAO == null)
        {
            probeTerminDAO = new ProbeTerminDAO(getDBConnection(),
                            getObjectFactory());
        }
        return probeTerminDAO;
    }

    public RechtDAO getRechtDAO()
    {
        if (rechtDAO == null)
        {
            rechtDAO = new RechtDAO(getDBConnection(), getObjectFactory());
        }
        return rechtDAO;
    }

    public RolleDAO getRolleDAO()
    {
        if (rolleDAO == null)
        {
            rolleDAO = new RolleDAO(getDBConnection(), getObjectFactory());
        }
        return rolleDAO;
    }

    public RueckmeldungStatusDAO getRueckmeldungStatusDAO()
    {
        if (rueckmeldungStatusDAO == null)
        {
            rueckmeldungStatusDAO = new RueckmeldungStatusDAO(
                            getDBConnection(), getObjectFactory());
        }
        return rueckmeldungStatusDAO;
    }

    public RueckmeldungStatusAliasDAO getRueckmeldungStatusAliasDAO()
    {
        if (rueckmeldungStatusAliasDAO == null)
        {
            rueckmeldungStatusAliasDAO = new RueckmeldungStatusAliasDAO(
                            getDBConnection(), getObjectFactory());
        }
        return rueckmeldungStatusAliasDAO;
    }

    public SchleifenDAO getSchleifenDAO()
    {
        if (schleifenDAO == null)
        {
            schleifenDAO = new SchleifenDAO(getDBConnection(),
                            getObjectFactory());
        }
        return schleifenDAO;
    }

    public SmsInDAO getSmsInDAO()
    {
        if (smsInDAO == null)
        {
            smsInDAO = new SmsInDAO(getDBConnection(), getObjectFactory());
        }
        return smsInDAO;
    }

    public SmsOutDAO getSmsOutDAO()
    {
        if (smsOutDAO == null)
        {
            smsOutDAO = new SmsOutDAO(getDBConnection(), getObjectFactory());
        }
        return smsOutDAO;
    }

    public TelefonDAO getTelefonDAO()
    {
        if (telefonDAO == null)
        {
            telefonDAO = new TelefonDAO(getDBConnection(), getObjectFactory());
        }
        return telefonDAO;
    }

    public SystemKonfigurationDAO getSystemKonfigurationDAO()
    {
        if (systemKonfigurationDAO == null)
        {
            systemKonfigurationDAO = new SystemKonfigurationDAO(
                            getDBConnection(), getObjectFactory());
        }
        return systemKonfigurationDAO;
    }

    public PersonMitRollenDAO getPersonMitRollenDAO()
    {
        if (personMitRollenDAO == null)
        {
            personMitRollenDAO = new PersonMitRollenDAO(getDBConnection(),
                            getObjectFactory(), getPersonDAO());

        }

        return personMitRollenDAO;
    }

    public BereichDAO getBereichDAO()
    {
        if (bereichDAO == null)
        {
            bereichDAO = new BereichDAO(getDBConnection(), getObjectFactory());

        }

        return bereichDAO;
    }

    public BereichInSchleifeDAO getBereichInSchleifeDAO()
    {
        if (bereichInSchleifeDAO == null)
        {
            bereichInSchleifeDAO = new BereichInSchleifeDAO(getDBConnection(),
                            getObjectFactory());

        }

        return bereichInSchleifeDAO;
    }

    public StatistikDAO getStatistikDao()
    {
        if (statistikDAO == null)
        {
            statistikDAO = new StatistikDAO(getDBConnection(),
                            getObjectFactory());
        }

        return statistikDAO;
    }

    public void setObjectFactory(ObjectFactory objectFactory)
    {
        this.objectFactory = objectFactory;
    }

    public ObjectFactory getObjectFactory()
    {
        return objectFactory;
    }
}
