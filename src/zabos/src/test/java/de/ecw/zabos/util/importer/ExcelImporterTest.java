package de.ecw.zabos.util.importer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.util.importer.ExcelImporter.MitarbeiterExcelModel;

public class ExcelImporterTest extends ZabosTestAdapter
{
    private static final String CSV_BENUTZER = "c:/temp/benutzer.csv";

    private static final String CSV_SOLLIST = "c:/temp/sollist.csv";

    private static final String XLS_SCHLEIFEN = "c:/temp/schleifen.xls";

    private static final String XLS_MITARBEITER = "c:/temp/mitarbeiter.xls";

    private static boolean isInitialized = false;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            isInitialized = true;
        }
    }

    @Test
    public void testInjectTelefonNummer()
    {
        ExcelImporter kbi = new ExcelImporter(dbResource);

        MitarbeiterExcelModel model = kbi.new MitarbeiterExcelModel();
        model.setName("Test");
        ExcelImporter.injectTelefonNummer("0385541234", model);
        ExcelImporter.injectTelefonNummer("0", model);

        Assert.assertNull(model.getHandyNummer());
        Assert.assertEquals("0049385541234", model.getFestnetzNummer()
                        .getNummer());
    }

    // @Test
    public void testReadMitarbeiter()
    {
        ExcelImporter importer = new ExcelImporter(dbResource);
        importer.setRolleDerMitarbeiter("Rolle");

        try
        {
            HSSFWorkbook wb = importer.readExcelFile(XLS_MITARBEITER);
            importer.readMitarbeiter(wb);
        }
        catch (StdException e)
        {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void testReadOrganisation()
    {
        ExcelImporter kbi = new ExcelImporter(dbResource);

        try
        {
            HSSFWorkbook wb = kbi.readExcelFile(XLS_SCHLEIFEN);
            kbi.readOrganisationStruktur(wb);
        }
        catch (StdException e)
        {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void testFullImport()
    {
        ExcelImporter kbi = new ExcelImporter(dbResource);

        kbi.setRolleDerMitarbeiter("Mitarbeiter");
        kbi.setSimulateImport(false);
        kbi.setOrganisationName("KBS");
        kbi.setPathOrgastrukturSchleifenXls(XLS_SCHLEIFEN);
        kbi.setPathSchleifenMitarbeiterXls(XLS_MITARBEITER);
        kbi.setPathTargetBenutzerAccountCsv(CSV_BENUTZER);
        kbi.setPathTargetSollIstStaerkeCsv(CSV_SOLLIST);

        try
        {
            kbi.init();
        }
        catch (StdException e)
        {
            logger.error("Import fehlgeschlagen: " + e.getMessage());
        }
    }
}