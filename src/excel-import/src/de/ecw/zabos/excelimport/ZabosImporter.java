package de.ecw.zabos.excelimport;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import de.ecw.zabos.Globals;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.sql.vo.RolleVO;

public class ZabosImporter
{
	static Logger log = Logger.getLogger(ZabosImporter.class);

	private static DBResource dbResource;

	/**
	 * @param args
	 * @author ckl
	 * @since 14.08.2008_09:41:26
	 */
	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		
		if (args.length == 0)
		{
			usage();
		}

		try
		{
			dbResource = Globals.newDatabaseResource();

			if (args[0].equals("import"))
			{
				if (args.length != 6)
				{
					usage();
				}

				String oeId = args[1];
				String rId = args[2];
				String datei = args[3];
				String sheet = args[4];
				String dryrun = args[5];

				ImportBO bo = new ImportBO(datei, sheet, oeId, rId);

				if (dryrun.equals("false"))
				{
					bo.setDryRun(false);
				}

				bo.run();
			}
			else if (args[0].equals("list-env"))
			{
				listEnvironment();
			}
			else if (args[0].equals("list-priv"))
			{
				listPrivileges();
			}
			else
			{
				usage();
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

		dbResource.free();
	}

	public static DBResource getDBResource()
	{
		return dbResource;
	}

	private static void usage()
	{
		if (dbResource != null)
		{
			dbResource.free();
		}

		out("Aufruf: zabos-excel-importer.jar [list-env|list-priv|import]");
		out("        list-env");
		out("            Listet alle Organisationen und Organisationseinheiten mit ihren IDs auf");
		out("        list-priv");
		out("            Listet alle Rolle und Rechte mit ihren IDs auf");
		out("        import");
		out("            [organisationseinheit-id] [rolle-id] [excel-datei] [excel-sheet] [dry-run]");
		out("            Importiert die Benutzer aus der angegebenen Excel-Datei in das ZABOS-System. Alle fünf Parameter müssen angegeben werden");
		System.exit(1);
	}

	private static void listPrivileges() throws StdException
	{
		RolleDAO daoRolle = ZabosImporter.getDBResource().getDaoFactory().getRolleDAO();
		RechtDAO daoRecht = ZabosImporter.getDBResource().getDaoFactory().getRechtDAO();

		RolleVO[] rollen = daoRolle.findAll();

		for (int i = 0, m = rollen.length; i < m; i++)
		{
			RolleVO r = rollen[i];
			out("Rolle " + r.getName() + " (ID " + r.getBaseId().getLongValue() + ")");

			RechtVO[] rechte = daoRecht.findRechteByRolleId(r.getRolleId());

			for (int j = 0, n = rechte.length; j < n; j++)
			{
				out("  Recht " + rechte[j].getName() + " (ID " + rechte[j].getBaseId().getLongValue() + ")");

			}
		}
	}

	private static void listEnvironment() throws StdException
	{
		OrganisationDAO daoO = ZabosImporter.getDBResource().getDaoFactory().getOrganisationDAO();
		OrganisationsEinheitDAO daoOE = ZabosImporter.getDBResource().getDaoFactory()
			.getOrganisationsEinheitDAO();

		OrganisationVO[] organisationen = daoO.findAll();

		for (int i = 0, m = organisationen.length; i < m; i++)
		{
			OrganisationVO o = organisationen[i];
			out("Organisation " + o.getName() + " (ID " + o.getBaseId().getLongValue() + ")");

			OrganisationsEinheitVO[] oes = daoOE.findOrganisationsEinheitenByOrganisationId(o
				.getOrganisationId());

			for (int j = 0, n = oes.length; j < n; j++)
			{
				out("  OE " + oes[j].getName() + " (ID " + oes[j].getBaseId().getLongValue() + ")");
			}
		}
	}

	public static void out(String _msg)
	{
		System.out.println(_msg);
	}
}
