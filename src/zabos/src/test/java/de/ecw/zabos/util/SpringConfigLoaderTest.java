package de.ecw.zabos.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.context.support.XmlWebApplicationContext;

import de.ecw.zabos.test.ZabosTestAdapter;

public class SpringConfigLoaderTest extends ZabosTestAdapter
{
    private static boolean isInitialized = false;

    public class SpringConfigLoaderMock extends SpringConfigLoader
    {
        public SpringConfigLoaderMock()
        {
            super(new MockServletConfig(), new XmlWebApplicationContext());
            availablePathes.add(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION);
        }

        public List<String> availablePathes = new ArrayList<String>();

        public boolean isConfigLocationAvailable(String _path)
        {
            return availablePathes.contains(_path);
        }
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
        }
    }

    @Test
    public void appendConfigurationLocations()
    {
        SpringConfigLoader scl = new SpringConfigLoaderMock();
        List<String> locations = new ArrayList<String>();

        scl.appendConfigurationLocations(locations, "localhost");
        scl.appendConfigurationLocations(locations, "127.0.0.1");

        Assert.assertTrue(locations
                        .contains("/WEB-INF/config/localhost/applicationContext.xml"));
        Assert.assertTrue(locations
                        .contains("/WEB-INF/applicationContext-localhost.xml"));
    }

    @Test
    public void getConfigurationLocationCandidates()
    {
        SpringConfigLoader scl = new SpringConfigLoaderMock();
        List<String> locations = scl.getConfigurationLocationCandidates();

        Assert.assertEquals(6, locations.size());
    }

    @Test
    public void getPreferedConfigLocation()
    {
        SpringConfigLoaderMock mock = new SpringConfigLoaderMock();
        String testPath = "";
        List<String> locations = mock.getConfigurationLocationCandidates();
        // Keine Verzeichnisse da, also Default
        Assert.assertEquals(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION,
                        mock.getPreferedConfigLocation(locations));

        // Nur Datei existiert
        testPath = "/WEB-INF/applicationContext-localhost.xml";
        mock.availablePathes.add(testPath);
        mock.appendConfigurationLocations(locations, "localhost");
        Assert.assertEquals(testPath, mock.getPreferedConfigLocation(locations));

        // Verzeichnis localhost existiert, ebenso Datei localhost. Dann muss 
        // das
        // Verzeichnis genommen werden
        testPath = "/WEB-INF/config/localhost/applicationContext.xml";
        mock.availablePathes.add(testPath);
        mock.appendConfigurationLocations(locations, "localhost");
        Assert.assertEquals(testPath, mock.getPreferedConfigLocation(locations));
    }
}
