package de.ecw.zabos.frontend.sql.ho;

import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.beans.ACLBean;
import de.ecw.zabos.frontend.beans.UserBean;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * HelperObject<br />
 * Enthält Methoden um die ACLs eines Benutzers zu resetten / zu ändern.
 * 
 * @author ckl
 */
public class AccessControllerHO
{
    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(BaseControllerAdapter.class);

    private DBResource dbResource = null;

    private UserBean userBean = null;

    private RechtDAO daoRecht = null;

    private PersonHO hoPerson = null;

    public final static String OPTION_PERSONEN_SUCHE_AKTIVIEREN = "personenSucheAktivieren";

    /**
     * @param _ub
     * @param _db
     */
    public AccessControllerHO(UserBean _ub, final DBResource _db)
    {
        this.dbResource = _db;
        this.userBean = _ub;

        this.daoRecht = dbResource.getDaoFactory().getRechtDAO();
        this.hoPerson = new PersonHO(_db);
    }

    /**
     * Ändert die ACLs für die übergebene Id/Kontext
     * 
     * @param _baseId
     *            Id vom Typ BaseId
     */
    public void update(BaseId _baseId)
    {
        RechtVO[] voRechte = null;

        // 2006-31-05 CKL: Performance & Globale Rechte werden nicht
        // überschrieben
        if (userBean != null && (userBean.getPerson() != null))
        {
            PersonId personId = userBean.getPerson().getPersonId();

            try
            {

                if (_baseId == null)
                {
                    voRechte = daoRecht.findRechteByPersonInSystem(personId);
                }
                else if (_baseId instanceof OrganisationId)
                {
                    voRechte = daoRecht.findRechteByPersonInOrganisation(
                                    personId, (OrganisationId) _baseId);
                }
                else if (_baseId instanceof OrganisationsEinheitId)
                {
                    voRechte = daoRecht
                                    .findRechteByPersonInOrganisationsEinheit(
                                                    personId,
                                                    (OrganisationsEinheitId) _baseId);
                }
                else if (_baseId instanceof SchleifeId)
                {
                    voRechte = daoRecht.findRechteByPersonInSchleife(personId,
                                    (SchleifeId) _baseId);
                }

                if (voRechte != null)
                {
                    userBean.getAccessControlList().setAktiveRechteImKontext(voRechte);
                }
            }
            catch (StdException e)
            {
                log.error(e);
            }
        }
    }

    /**
     * Updatet die eigenen Rechte. Diese Methode braucht eigentlich nur während
     * des Login-Prozesses aufgerufen werden
     * 
     * @since 2006-05-31 CKL: Recht Alarm-Auslösen hinzugefügt
     * @since 2006-05-27 CKL: Rechte ALARMHISTORIE_SEHEN und
     *        ALARMHISTORIE_DETAILS_SEHEN hinzugefügt
     * @since 2006-07-19 CKL: Recht EIGENE_ABWESENHEITEN_AENDERN hinzugefügt
     * @author ckl
     */
    public void setGlobaleRechte()
    {
        if (userBean != null)
        {
            ACLBean acl = userBean.getAccessControlList();
            PersonId personId = userBean.getPerson().getPersonId();

            List<Object> listZugehoerigkeiten = hoPerson
                            .findObjekteMitZugehoerigkeitVonPerson(personId);

            acl.setRechtStatus(RechtId.EIGENE_PERSON_AENDERN, hoPerson
                            .hatPersonRechtInObjektListe(listZugehoerigkeiten,
                                            personId,
                                            RechtId.EIGENE_PERSON_AENDERN), true);

            acl.setRechtStatus(RechtId.EIGENE_TELEFONE_AENDERN, hoPerson
                            .hatPersonRechtInObjektListe(listZugehoerigkeiten,
                                            personId,
                                            RechtId.EIGENE_TELEFONE_AENDERN), true);

            // 2006-07-19 CKL: Recht EIGENE_ABWESENHEITSZEITEN_AENDERN
            // hinzugefuegt
            acl.setRechtStatus(
                            RechtId.EIGENE_ABWESENHEITSZEITEN_AENDERN,
                            hoPerson.hatPersonRechtInObjektListe(
                                            listZugehoerigkeiten,
                                            personId,
                                            RechtId.EIGENE_ABWESENHEITSZEITEN_AENDERN), true);

            acl.setRechtStatus(RechtId.ALARM_AUSLOESEN, hoPerson
                            .hatPersonRechtInObjektListe(listZugehoerigkeiten,
                                            personId, RechtId.ALARM_AUSLOESEN), true);

            // 2006-05-27 CKL: Rechte ALARMHISTORIE_SEHEN und
            // ALARMHISTORIE_DETAILS_SEHEN hinzugefuegt
            acl.setRechtStatus(RechtId.ALARMHISTORIE_SEHEN, hoPerson
                            .hatPersonRechtInObjektListe(listZugehoerigkeiten,
                                            personId,
                                            RechtId.ALARMHISTORIE_SEHEN), true);

            acl.setRechtStatus(
                            RechtId.ALARMHISTORIE_DETAILS_SEHEN,
                            hoPerson.hatPersonRechtInObjektListe(
                                            listZugehoerigkeiten, personId,
                                            RechtId.ALARMHISTORIE_DETAILS_SEHEN), true);

            // 2011-01-22 CKL: Suche nach Personen aktivieren
            String aktivierePersonenSuche = "0";

            if (hoPerson.isRechtInBezugAufAnderePersonVerfuegbar(personId,
                            RechtId.PERSON_AENDERN, null))
            {
                aktivierePersonenSuche = "1";
            }

            userBean.getOptionen().put(OPTION_PERSONEN_SUCHE_AKTIVIEREN,
                            aktivierePersonenSuche);

            // 2011-02-16 CKL: Neue Person anlegen erstellen aktivieren
            acl.setRechtStatus(RechtId.PERSON_ANLEGEN_LOESCHEN, hoPerson
                            .hatPersonRechtInObjektListe(listZugehoerigkeiten,
                                            personId,
                                            RechtId.PERSON_ANLEGEN_LOESCHEN), true);

            // 2011-07-18 CKL: Mail von Fr. Menzel: Link zu Berichten wird nicht
            // angezeigt
            acl.setRechtStatus(RechtId.STATISTIK_ANZEIGEN, hoPerson
                            .hatPersonRechtInObjektListe(listZugehoerigkeiten,
                                            personId,
                                            RechtId.STATISTIK_ANZEIGEN), true);
        }
    }

    /**
     * Setzt die Einstellungen, sobald ein Benutzer einen anderen Benutzer
     * bearbeiten oder einen neuen Benutzer erstellen will. Es werden die Rechte
     * <ul>
     * <li> {@link RechtId#PERSON_AENDERN}</li>
     * <li> {@link RechtId#PERSON_ANLEGEN_LOESCHEN}</li>
     * <li>und {@link RechtId#OE_KOSTENSTELLE_FESTLEGEN}</li>
     * </ul>
     * gesetzt
     * 
     * @param _id
     */
    public void updateRechteFuerPersonenBearbeiten(PersonId _id)
    {
        if (userBean != null)
        {
            userBean.getAccessControlList().setRechtStatus(
                            RechtId.PERSON_AENDERN,
                            hoPerson.isRechtInBezugAufAnderePersonVerfuegbar(
                                            userBean.getPerson().getPersonId(),
                                            RechtId.PERSON_AENDERN, _id), false);

            userBean.getAccessControlList().setRechtStatus(
                            RechtId.PERSON_ANLEGEN_LOESCHEN,
                            hoPerson.isRechtInBezugAufAnderePersonVerfuegbar(
                                            userBean.getPerson().getPersonId(),
                                            RechtId.PERSON_ANLEGEN_LOESCHEN,
                                            _id), false);

            userBean.getAccessControlList().setRechtStatus(
                            RechtId.OE_KOSTENSTELLE_FESTLEGEN,
                            hoPerson.isRechtInBezugAufAnderePersonVerfuegbar(
                                            userBean.getPerson().getPersonId(),
                                            RechtId.OE_KOSTENSTELLE_FESTLEGEN,
                                            _id), false);
        }
    }
}
