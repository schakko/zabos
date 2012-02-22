package de.ecw.zabos.sql.ao.test;

import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

public class PersonInAlarmAOTest extends ZabosTestAdapter
{
	private static PersonId PERSON_ID;
	
	private static AlarmId ALARM_ID;
	
	private static RueckmeldungStatusId RUECKMELDUNG_STATUS_ID;
	
	private final static String KOMMENTAR = "kommentar";
	
	private final static String KOMMENTAR_LEITUNG = "kommentar-leitung";
	
	private final boolean IS_ENTWARNT = false;
}
