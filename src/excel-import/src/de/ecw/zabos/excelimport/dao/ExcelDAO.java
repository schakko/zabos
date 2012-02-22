package de.ecw.zabos.excelimport.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import de.ecw.zabos.excelimport.cto.DataCTO;
import de.ecw.zabos.excelimport.cto.KameradCTO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.TelefonNummer;

public class ExcelDAO
{
	static Logger log = Logger.getLogger(ExcelDAO.class);

	private String dateiname = "";

	private String sheetname = "";

	private POIFSFileSystem poiFS = null;

	private HSSFWorkbook workbook = null;

	private HashMap<String, String> lookupTable = new HashMap<String, String>();

	private HSSFSheet sheet = null;

	public static String KEYWORD_SCHLEIFENNUMMER = "Schleifennummer";

	public static String KEYWORD_KAMERADEN_BEGINNT = "Lfd. Nr.";

	public static int KEYWORD_COLUMN = 0;

	public ExcelDAO(String _dateiname, String _sheetname) throws StdException
	{
		dateiname = _dateiname;
		sheetname = _sheetname;

		openSheet(dateiname, sheetname);
	}

	/**
	 * Öffnet ein Sheet zum Lesen
	 * 
	 * @param _dateiname
	 * @param _sheetname
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_10:46:21
	 */
	public void openSheet(String _dateiname, String _sheetname) throws StdException
	{
		try
		{
			log.debug("Öffne '" + _dateiname + "'...");
			poiFS = new POIFSFileSystem(new FileInputStream(_dateiname));
			workbook = new HSSFWorkbook(poiFS);
			sheet = workbook.getSheet(sheetname);
			log.info("Exceldatei '" + _dateiname + "' zum Lesen geöffnet");

		}
		catch (FileNotFoundException e)
		{
			log.error("Konnte " + _dateiname + " nicht finden: " + e.getMessage());
			throw new StdException(e.getMessage());
		}
		catch (IOException e)
		{
			log.error("Konnte nicht auf " + _dateiname + " zugreifen: " + e.getMessage());
			throw new StdException(e.getMessage());
		}
	}

	/**
	 * Findet eine spezifische Zelle innerhalb des geöffneten Worksheets
	 * 
	 * @param _column
	 * @param _name
	 * @return
	 * @author ckl
	 * @since 14.08.2008_10:47:10
	 */
	private int findCell(int _column, String _name) throws StdException
	{
		for (int i = 0, m = sheet.getLastRowNum(); i < m; i++)
		{
			HSSFRow row = sheet.getRow(i);

			HSSFCell cell = row.getCell(_column);

			if (cell == null)
			{
				continue;
			}

			String val = cell.getRichStringCellValue().toString();
			// log.info("Zelle (Zeile, Spalte): " + i + ", " + _column + ": " + val);

			if (val.equals(_name))
			{
				log.debug("Zelle mit Inhalt '" + _name + "' in Zeile '" + i + "' Spalte '" + _column
						+ "' gefunden");
				return i;
			}
		}

		log.error("Zelle mit dem Inhalt '" + _name + "' konnte nicht gefunden werden");
		throw new StdException("Keyword '" + _name + "' konnte nicht gefunden werden");
	}

	/**
	 * Extrahiert alle Kameraden aus der bereits geöffneten Excel-Datei
	 * 
	 * @return
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_10:48:38
	 */
	public KameradCTO[] extractKameraden() throws StdException
	{
		log.info("Extrahiere Kameraden...");

		ArrayList<KameradCTO> alKameraden = new ArrayList<KameradCTO>();

		int startCell = findCell(KEYWORD_COLUMN, KEYWORD_KAMERADEN_BEGINNT);
		int i = startCell;
		i++;

		for (int m = sheet.getLastRowNum(); i < m; i++)
		{
			HSSFRow row = sheet.getRow(i);

			if (row.getCell(1) == null || row.getCell(2) == null)
			{
				continue;
			}

			String lfdNummer = row.getCell(0).toString();
			String nachname = row.getCell(1).toString();
			String vorname = row.getCell(2).toString();
			String telefonnummer = row.getCell(3).toString();
			String[] personBelongsToSchleife = new String[9];

			if (lfdNummer == null || lfdNummer.length() == 0)
			{
				log.info("Kameradenliste endet in Zeile " + i);
				break;
			}

			if (nachname != null && vorname != null && nachname.length() != 0 && vorname.length() != 0)
			{
				int k = 0;

				PersonVO person = new PersonVO();
				person.setNachname(nachname);
				person.setVorname(vorname);
				TelefonVO telefon = new TelefonVO();
				telefon.setAktiv(true);
				telefon.setNummer(new TelefonNummer(telefonnummer));

				KameradCTO kamerad = new KameradCTO();
				kamerad.setPerson(person);
				kamerad.setTelefon(telefon);

				log.info("Kamerad wurde extrahiert (Nachname, Vorname, Telefon) : '" + nachname + ", '"
						+ vorname + "', '" + telefon.getNummer() + "'");

				for (int j = 4, n = 14; j <= n; j++)
				{
					HSSFCell cell = row.getCell(j);

					if (cell != null)
					{
						if (cell.getRichStringCellValue().toString().length() > 0)
						{
							personBelongsToSchleife[k] = sheet.getRow(startCell).getCell(j)
								.getRichStringCellValue().toString();
							log.info("  Kamerad '" + nachname + ", " + vorname + " gehört der Schleife '"
									+ personBelongsToSchleife[k] + "' an");
							k++;
						}
					}
				}

				kamerad.setZugehoerigeSchleifen(personBelongsToSchleife);
				alKameraden.add(kamerad);
			}
			else
			{
				log.debug("Überspringe Zeile " + i + " - Name/Vorname ist nicht gegeben (" + nachname
						+ ", " + vorname + ")");
			}
		}

		KameradCTO[] r = new KameradCTO[alKameraden.size()];
		log.info("Insgesamt '" + r.length + "' Kameraden gefunden");

		return alKameraden.toArray(r);
	}

	/**
	 * Extrahiert alle Schleifen aus dem geöffneten Dokument
	 * 
	 * @return
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_10:57:43
	 */
	public SchleifeVO[] extractSchleifen() throws StdException
	{
		log.info("Extrahiere Schleifen");

		ArrayList<SchleifeVO> alSchleifen = new ArrayList<SchleifeVO>();

		int i = findCell(KEYWORD_COLUMN, KEYWORD_SCHLEIFENNUMMER);
		i++;

		for (int m = sheet.getLastRowNum(); i < m; i++)
		{
			HSSFRow row = sheet.getRow(i);
			String schleifenNummer = row.getCell(0).getRichStringCellValue().toString();
			String benennung = row.getCell(1).getRichStringCellValue().toString();
			String kuerzel = row.getCell(2).toString();
			String fuenftonFolge = row.getCell(3).toString();

			if (!schleifenNummer.startsWith("Schleife"))
			{
				log.debug("Ende der Schleifen in Zeile " + i + " gefunden");
				break;
			}

			if (kuerzel == null || fuenftonFolge == null || kuerzel.length() == 0
					|| fuenftonFolge.length() == 0)
			{
				log.error("Schleife konnte nicht extrahiert werden, da 5Ton-Folge oder Kürzel fehlt ('"
						+ fuenftonFolge + "', '" + kuerzel + "')");
				continue;
			}

			fuenftonFolge = fuenftonFolge.substring(0, fuenftonFolge.length() - 2);

			HSSFCell cellBenennung = row.getCell(2);
			HSSFCell cellKuerzel = row.getCell(3);
			HSSFCell cellFuenfton = row.getCell(4);

			SchleifeVO schleife = new SchleifeVO();
			schleife.setAbrechenbar(true);
			schleife.setName(benennung + " " + kuerzel);

			schleife.setKuerzel(fuenftonFolge);
			schleife.setFuenfton(fuenftonFolge);
			alSchleifen.add(schleife);

			lookupTable.put(schleifenNummer, fuenftonFolge);

			log.info("Schleife extrahiert (Kürzel, 5Ton, Name): '" + kuerzel + "', '" + fuenftonFolge
					+ "', '" + benennung + "'");
		}

		SchleifeVO[] r = new SchleifeVO[alSchleifen.size()];

		log.info("Insgesamt " + r.length + " Schleifen extrahiert");
		return alSchleifen.toArray(r);
	}

	/**
	 * Parst die Excel-Datei und liefert die Daten zurück
	 * 
	 * @return
	 * @throws StdException
	 * @author ckl
	 * @since 14.08.2008_11:03:11
	 */
	public DataCTO parse() throws StdException
	{
		DataCTO r = new DataCTO();
		r.setSchleifen(extractSchleifen());
		r.setHmLookupSchleifeFuenfton(lookupTable);
		r.setKameraden(extractKameraden());

		return r;
	}
}
