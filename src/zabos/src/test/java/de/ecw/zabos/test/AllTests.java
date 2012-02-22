package de.ecw.zabos.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.ecw.zabos.report.AllReportTests;
import de.ecw.zabos.service.alarm.ext.interceptors.AllInterceptorTests;
import de.ecw.zabos.service.alarm.klinikum.test.AllServiceAlarmKlinikumTests;
import de.ecw.zabos.sql.ao.test.AllAOTests;

@RunWith(value = Suite.class)
@SuiteClasses(
{ AllReportTests.class, AllInterceptorTests.class, AllServiceAlarmKlinikumTests.class, AllAOTests.class })
public class AllTests
{
    /**
     * Diese Klasse wird in der build.xml als Haupt-Testsuite herangezogen. Alle
     * Klassen, die mit {@link SuiteClasses} annotiert sind, beinhalten wiederum
     * Verweise auf die einzelnen Test-Klassen.
     * 
     * Die Annotation {@link RunWith} sorgt dafür, dass der korrekte
     * JUnit-Runner benutzt wird. Es wird JUnit 4.8.1 benötigt.
     * 
     * Wichtig: Statische Methoden dürfen in den All*.class-Dateien nicht
     * vorhanden sein, da sonst die Tests in der build.xml nicht ausgeführt
     * werden.
     */

}
