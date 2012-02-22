package de.ecw.zabos.sql.tao;

import de.ecw.zabos.sql.resource.DBResource;

/**
 * Cached TAOs für eine Datenbankverbindung. Auf die einzelnen TAOS kann mittels
 * get<Name>TAO() zugegriffen werden.
 * 
 * @author bsp
 * 
 */
final public class TaoFactory
{

    private DBResource dbresource;

    private BenutzerVerwaltungTAO benutzerVerwaltungTAO;

    private FuenfTonTAO fuenftonTAO;

    private ProbeTerminTAO probeterminTAO;

    private RechteTAO rechteTAO;

    private RolleTAO rolleTAO;

    private RueckmeldungStatusAliasTAO rueckmeldungStatusAliasTAO;

    private SmsOutTAO smsOutTAO;

    private SystemKonfigurationTAO systemKonfigurationTAO;

    public TaoFactory(final DBResource _dbresource)
    {
        dbresource = _dbresource;
    }

    public BenutzerVerwaltungTAO getBenutzerVerwaltungTAO()
    {
        if (benutzerVerwaltungTAO == null)
        {
            benutzerVerwaltungTAO = new BenutzerVerwaltungTAO(dbresource);
        }
        return benutzerVerwaltungTAO;
    }

    public FuenfTonTAO getFuenfTonTAO()
    {
        if (fuenftonTAO == null)
        {
            fuenftonTAO = new FuenfTonTAO(dbresource);
        }
        return fuenftonTAO;
    }

    public ProbeTerminTAO getProbeTerminTAO()
    {
        if (probeterminTAO == null)
        {
            probeterminTAO = new ProbeTerminTAO(dbresource);
        }

        return probeterminTAO;
    }

    public RechteTAO getRechteTAO()
    {
        if (rechteTAO == null)
        {
            rechteTAO = new RechteTAO(dbresource);
        }
        return rechteTAO;
    }

    public RueckmeldungStatusAliasTAO getRueckmeldungStatusAliasTAO()
    {
        if (rueckmeldungStatusAliasTAO == null)
        {
            rueckmeldungStatusAliasTAO = new RueckmeldungStatusAliasTAO(
                            dbresource);
        }
        return rueckmeldungStatusAliasTAO;
    }

    public RolleTAO getRolleTAO()
    {
        if (rolleTAO == null)
        {
            rolleTAO = new RolleTAO(dbresource);
        }
        return rolleTAO;
    }

    public SmsOutTAO getSmsOutTAO()
    {
        if (smsOutTAO == null)
        {
            smsOutTAO = new SmsOutTAO(dbresource);
        }
        return smsOutTAO;
    }

    public SystemKonfigurationTAO getSystemKonfigurationTAO()
    {
        if (systemKonfigurationTAO == null)
        {
            systemKonfigurationTAO = new SystemKonfigurationTAO(dbresource);
        }
        return systemKonfigurationTAO;
    }

}
