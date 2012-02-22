package de.ecw.zabos.service.alarm.ext.interceptors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses( { AfterReportCreatedListenerTest.class, ReportPrintingInterceptorTest.class })
public class AllInterceptorTests
{
}
