package de.ecw.zabos.alarm.consumer.fms.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import de.ecw.zabos.alarm.consumer.jms.IMonitordQueueListener;

/**
 * JMS-Listener für Monitord/FMS
 * 
 * @author ckl
 * 
 */
public class FmsQueueListener implements IMonitordQueueListener,
                MessageListener

{
    public final static String FMS_KEY_KENNUNG = "fahrzeugKennung";

    public final static String FMS_KEY_STATUS = "status";

    public final static String FMS_KEY_BAUSTUFE = "baustufe";

    public final static String FMS_KEY_RICHTUNG = "richtung";

    public final static String FMS_KEY_TKI = "tiki";

    public final static String FMS_KEY_BOS_DEZIMAL = "bosdezimal";

    public final static String FMS_KEY_LAND_DEZIMAL = "landdezimal";

    public final static String FMS_KEY_STATUS_DEZIMAL = "statusdezimal";

    public final static String FMS_KEY_BOS = "bos";

    public final static String FMS_KEY_LAND = "land";

    public final static String FMS_KEY_ORT = "ort";

    public final static String FMS_KEY_KFZ = "kfz";

    public final static String FMS_KEY_TEXTUEBERTRAGUNG = "textuebertragung";

    public void onMessage(Message arg0)
    {
        TextMessage textMessage = (TextMessage) arg0;
        // TODO FMS-Nachricht verarbeiten
    }

    public String getListenerTyp()
    {
        return TYP_FMS;
    }
}
