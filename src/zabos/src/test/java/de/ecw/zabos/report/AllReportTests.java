package de.ecw.zabos.report;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.Test;
import junit.framework.TestSuite;

@RunWith(value = Suite.class)
@SuiteClasses( { ReportModelTest.class, ReportCreationServiceTest.class })
public class AllReportTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for de.ecw.zabos.report");
        // $JUnit-BEGIN$

        // $JUnit-END$
        return suite;
    }

}
