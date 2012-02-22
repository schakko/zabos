package de.ecw.zabos.excelimport;

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.ecw.zabos.Globals;
import de.ecw.zabos.excelimport.cto.DataCTO;
import de.ecw.zabos.excelimport.cto.KameradCTO;
import de.ecw.zabos.excelimport.dao.ExcelDAO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.RolleId;

public class ImportBO
{
	static Logger log = Logger.getLogger(ImportBO.class);

	private ExcelDAO dao = null;

	private boolean isDryRun = true;

	private OrganisationsEinheitId oeId = null;

	private RolleId rId = null;

	private DataCTO data = null;

	/**
	 * @return Returns the data.
	 */
	public DataCTO getData()
	{
		return data;
	}

	/**
	 * @param data
	 *          The data to set.
	 */
	public void setData(DataCTO data)
	{
		this.data = data;
	}

	/**
	 * @return Returns the isDryRun.
	 */
	public boolean isDryRun()
	{
		return isDryRun;
	}

	/**
	 * @param isDryRun
	 *          The isDryRun to set.
	 */
	public void setDryRun(boolean isDryRun)
	{
		this.isDryRun = isDryRun;
	}

	public ImportBO(String _dateiname, String _sheet, String _oeId, String _rolleId) throws Exception
	{
		dao = new ExcelDAO(_dateiname, _sheet);
		oeId = new OrganisationsEinheitId(new Long(_oeId).longValue());
		rId = new RolleId(new Long(_rolleId).longValue());
	}

	/**
	 * Führt den Importvorgang durch
	 * 
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_12:00:33
	 */
	public void run() throws StdException
	{
		setData(dao.parse());
		importSchleifen();
		importKameraden();
		importZugehoerigkeiten();
	}

	/**
	 * Importiert die Schleifen in ZABOS
	 * 
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_12:00:44
	 */
	private void importSchleifen() throws StdException
	{
		log.info("Importiere Schleifen...");

		OrganisationsEinheitDAO daoOE = ZabosImporter.getDBResource().getDaoFactory()
			.getOrganisationsEinheitDAO();
		BenutzerVerwaltungTAO taoBV = ZabosImporter.getDBResource().getTaoFactory()
			.getBenutzerVerwaltungTAO();

		if (daoOE.findOrganisationsEinheitById(oeId) == null)
		{
			throw new StdException("Die Organisationseinheit mit der Id " + oeId.toString()
					+ " konnte nicht gefunden werden");
		}

		SchleifenDAO daoSchleife = ZabosImporter.getDBResource().getDaoFactory().getSchleifenDAO();

		SchleifeVO[] schleifen = data.getSchleifen();

		for (int i = 0, m = schleifen.length; i < m; i++)
		{
			SchleifeVO schleifeExcel = schleifen[i];
			schleifeExcel.setOrganisationsEinheitId(oeId);

			log.info("Suche nach Schleife mit Fuenfton '" + schleifeExcel.getFuenfton() + "'");
			SchleifeVO schleifeDb = daoSchleife.findSchleifeByFuenfton(schleifeExcel.getFuenfton());

			if (schleifeDb == null)
			{
				log.info("Die Schleife existiert noch nicht in der Datenbank, erstelle neue Schleife");

				if (!isDryRun())
				{
					schleifen[i] = taoBV.createSchleife(schleifeExcel);
				}
			}
			else
			{
				schleifen[i] = schleifeDb;
				log.info("Die Schleife mit dem angegebenen Fuenfton existiert bereits in der Datenbank");
			}
		}
	}

	/**
	 * Import die Kameraden in die Datenbank
	 * 
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_12:13:51
	 */
	private void importKameraden() throws StdException
	{
		log.info("Importiere Kameraden...");

		PersonDAO daoPerson = ZabosImporter.getDBResource().getDaoFactory().getPersonDAO();
		BenutzerVerwaltungTAO taoBV = ZabosImporter.getDBResource().getTaoFactory()
			.getBenutzerVerwaltungTAO();
		TelefonDAO daoTelefon = ZabosImporter.getDBResource().getDaoFactory().getTelefonDAO();
		KameradCTO[] kameraden = data.getKameraden();

		for (int i = 0, m = kameraden.length; i < m; i++)
		{
			PersonVO personExcel = kameraden[i].getPerson();
			TelefonVO telefonExcel = kameraden[i].getTelefon();

			log.info("Suche nach Person '" + personExcel.getDisplayName() + "'");
			PersonVO[] personenDb = daoPerson.findPersonenByPattern("%", personExcel.getVorname(),
					personExcel.getNachname(), "%");
			PersonVO personDb = null;

			boolean bPersonExistiert = false;

			if (personenDb == null)
			{
				log.info("Person existiert nicht in der Datenbank und kann hinzugefügt werden");
				bPersonExistiert = false;
			}
			else
			{
				for (int j = 0, n = personenDb.length; j < n; j++)
				{
					personDb = personenDb[j];

					// Person existiert
					if (personDb.getVorname().toLowerCase().equals(personExcel.getVorname().toLowerCase())
							&& personDb.getNachname().toLowerCase().equals(
									personExcel.getNachname().toLowerCase()))
					{
						bPersonExistiert = true;
						log.info("Person wurde in der Datenbank gefunden, überprüfe Telefonnummer");

						// Telefon überprüfen
						TelefonVO telefonDb = daoTelefon.findTelefonByNummer(telefonExcel.getNummer());

						if (telefonDb != null)
						{
							log.info("Person '" + personExcel.getDisplayName()
									+ " existiert bereits mit der Telefonnummer '" + telefonDb.getNummer().toString()
									+ "' in der Datenbank");

							// Benutzer updaten, so dass die BenutzerId verüfgbar ist
							kameraden[i].setPerson(personDb);
						}
						else
						{
							log.error("KONFLIKT: Person '" + personExcel.getDisplayName()
									+ " existiert in der Datenbank, die Telefonnummer '"
									+ telefonExcel.getNummer().toString() + " hingegen noch nicht");
						}
					}
				}
			}

			if (!bPersonExistiert)
			{
				// Benutzername generieren
				updateFreeBenutzername(personExcel);

				log.info("Erstelle neuen Benutzer " + personExcel.getDisplayName()
						+ " mit der Telefonnummer " + telefonExcel.getNummer());

				if (!isDryRun())
				{
					kameraden[i].setPerson(taoBV.createPerson(personExcel));
					telefonExcel.setPersonId(kameraden[i].getPerson().getPersonId());
					
					if (telefonExcel.getNummer().toString().length() != 0)
					{
						kameraden[i].setTelefon(taoBV.createTelefon(telefonExcel));
					}
				}
			}
		}
	}

	/**
	 * Erstellt einen freien Benutzernamen
	 * 
	 * @param _person
	 * @author ckl
	 * @since 14.08.2008_12:31:08
	 */
	private void updateFreeBenutzername(PersonVO _person) throws StdException
	{
		PersonDAO daoPerson = ZabosImporter.getDBResource().getDaoFactory().getPersonDAO();

		String vorname = _person.getVorname().toLowerCase();
		String nachname = _person.getNachname().toLowerCase();
		String name;

		int idxVorname = 1;
		int idxNachname = 2;
		int idxNumber = 1;

		do
		{
			name = vorname.substring(0, idxVorname);
			name += nachname.substring(0, idxNachname);

			if (idxNachname >= nachname.length())
			{
				if (idxVorname >= vorname.length())
				{
					name += idxNumber;
					idxNumber++;
				}
				else
				{
					idxVorname++;
				}
			}
			else
			{
				idxNachname++;
			}

		} while (daoPerson.findPersonByName(name) != null);

		_person.setName(name);
	}

	/**
	 * Importiert die Zugehörigkeit eines Benutzers zu Schleifen
	 * 
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_12:40:44
	 */
	private void importZugehoerigkeiten() throws StdException
	{
		log.info("Importiere Zugehörigkeiten von Personen zu Schleifen...");

		SchleifenDAO daoSchleife = ZabosImporter.getDBResource().getDaoFactory().getSchleifenDAO();
		RechteTAO taoRecht = ZabosImporter.getDBResource().getTaoFactory().getRechteTAO();
		PersonDAO daoPerson = ZabosImporter.getDBResource().getDaoFactory().getPersonDAO();
		HashMap<String, String> hmLookup = getData().getHmLookupSchleifeFuenfton();
		KameradCTO[] kameraden = getData().getKameraden();

		for (int i = 0, m = kameraden.length; i < m; i++)
		{
			PersonVO person = kameraden[i].getPerson();
			String[] zugehoerigkeit = kameraden[i].getZugehoerigeSchleifen();

			if (person.getPersonId() == null)
			{
				log.info("Überspringe Person " + person.getDisplayName()
						+ ". Der Kamerad wurde nicht in die Datenbank eingetragen");

				continue;
			}

			for (int j = 0, n = zugehoerigkeit.length; j < n; j++)
			{
				String fuenfton = hmLookup.get(zugehoerigkeit[j]);

				if (fuenfton == null || fuenfton.length() == 0)
				{
					continue;
				}

				SchleifeVO schleife = daoSchleife.findSchleifeByFuenfton(fuenfton);

				if (schleife == null)
				{
					log.error("Die Schleife mit dem Fuenfton '" + fuenfton
							+ "' existiert nicht in der Datenbank");
					continue;
				}

				if (daoPerson.hatPersonRolleInSchleifeNichtVererbt(person.getPersonId(), rId, schleife
					.getSchleifeId()))
				{
					log.error("Person " + person.getDisplayName() + " ist bereits in der Schleife "
							+ schleife.getDisplayName() + " mit der Rolle " + rId.toString() + " eingetragen");

					continue;
				}

				log.info("Füge Person " + person.getDisplayName() + " in der Rolle " + rId.toString()
						+ " der Schleife " + schleife.getDisplayName() + " hinzu");

				if (!isDryRun())
				{
					taoRecht.addPersonInRolleToSchleife(person.getPersonId(), rId, schleife.getSchleifeId());
				}

			}
		}
	}
}
