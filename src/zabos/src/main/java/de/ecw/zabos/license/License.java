package de.ecw.zabos.license;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.UnavailableException;

import org.springframework.core.io.Resource;

import de.ecw.zabos.types.UnixTime;

/**
 * Helper Klasse um das Licensefile einzulesen bzw. Abzufragen
 * 
 * @author bsp
 * 
 */
final public class License
{
    private static final byte[] xor_key = new byte[]
    { (byte) 123, (byte) 72, (byte) 161, (byte) 246, (byte) 242, (byte) 178, (byte) 232, (byte) 56, (byte) 251, (byte) 198, (byte) 234, (byte) 225, (byte) 173, (byte) 216, (byte) 124, (byte) 140, (byte) 169, (byte) 246, (byte) 49, (byte) 156, (byte) 175, (byte) 196, (byte) 187, (byte) 42, (byte) 242, (byte) 24, (byte) 2, (byte) 154, (byte) 181, (byte) 232, (byte) 175, (byte) 252, (byte) 160, (byte) 33, (byte) 40, (byte) 252, (byte) 33, (byte) 184, (byte) 104, (byte) 64, (byte) 236, (byte) 56, (byte) 211, (byte) 210, (byte) 233, (byte) 239, (byte) 228, (byte) 118, (byte) 22, (byte) 158, (byte) 114, (byte) 16, (byte) 220, (byte) 120, (byte) 134, (byte) 223, (byte) 212, (byte) 140, (byte) 168, (byte) 43, (byte) 34, (byte) 206, (byte) 252, (byte) 3, (byte) 8, (byte) 186, (byte) 112, (byte) 91, (byte) 28, (byte) 54, (byte) 107, (byte) 118, (byte) 159, (byte) 156, (byte) 50, (byte) 183, (byte) 114, (byte) 53, (byte) 171, (byte) 159, (byte) 148, (byte) 66, (byte) 112, (byte) 203, (byte) 45, (byte) 229, (byte) 128, (byte) 93, (byte) 205, (byte) 123, (byte) 142, (byte) 129, (byte) 240, (byte) 127, (byte) 32, (byte) 186, (byte) 102, (byte) 110, (byte) 206, (byte) 111, (byte) 214, (byte) 222, (byte) 204, (byte) 231, (byte) 10, (byte) 134, (byte) 15, (byte) 154, (byte) 248, (byte) 143, (byte) 56, (byte) 159, (byte) 92, (byte) 144, (byte) 152, (byte) 152, (byte) 56, (byte) 118, (byte) 139, (byte) 59, (byte) 147, (byte) 238, (byte) 213, (byte) 171, (byte) 90, (byte) 23, (byte) 201, (byte) 177, (byte) 251, (byte) 154, (byte) 85, (byte) 185, (byte) 213, (byte) 228, (byte) 71, (byte) 229, (byte) 72, (byte) 131, (byte) 98, (byte) 170, (byte) 82, (byte) 200, (byte) 45, (byte) 67, (byte) 97, (byte) 7, (byte) 9, (byte) 154, (byte) 34, (byte) 27, (byte) 222, (byte) 29, (byte) 87, (byte) 64, (byte) 112, (byte) 177, (byte) 114, (byte) 151, (byte) 74, (byte) 34, (byte) 165, (byte) 55, (byte) 252, (byte) 187, (byte) 76, (byte) 27, (byte) 250, (byte) 19, (byte) 46, (byte) 185, (byte) 198, (byte) 226, (byte) 5, (byte) 19, (byte) 167, (byte) 53, (byte) 22, (byte) 67, (byte) 3, (byte) 21, (byte) 49, (byte) 125, (byte) 55, (byte) 57, (byte) 102, (byte) 248, (byte) 63, (byte) 52, (byte) 42, (byte) 169, (byte) 215, (byte) 105, (byte) 170, (byte) 11, (byte) 145, (byte) 19, (byte) 126, (byte) 17, (byte) 88, (byte) 155, (byte) 154, (byte) 25, (byte) 70, (byte) 93, (byte) 171, (byte) 123, (byte) 5, (byte) 164, (byte) 34, (byte) 166, (byte) 77, (byte) 201, (byte) 190, (byte) 237, (byte) 175, (byte) 138, (byte) 193, (byte) 210, (byte) 105, (byte) 85, (byte) 35, (byte) 232, (byte) 121, (byte) 220, (byte) 86, (byte) 81, (byte) 194, (byte) 144, (byte) 165, (byte) 14, (byte) 92, (byte) 141, (byte) 199, (byte) 22, (byte) 165, (byte) 106, (byte) 68, (byte) 207, (byte) 168, (byte) 13, (byte) 105, (byte) 82, (byte) 210, (byte) 204, (byte) 95, (byte) 50, (byte) 67, (byte) 32, (byte) 163, (byte) 180, (byte) 134, (byte) 164, (byte) 67, (byte) 159, (byte) 34, (byte) 106, (byte) 20, (byte) 168, (byte) 62, (byte) 72, (byte) 5, (byte) 153, (byte) 236, (byte) 40, (byte) 155, (byte) 58, (byte) 186, (byte) 135, (byte) 78, (byte) 216, (byte) 49, (byte) 235, (byte) 172, (byte) 129, (byte) 152, (byte) 168, (byte) 23, (byte) 36, (byte) 113, (byte) 135, (byte) 57, (byte) 243, (byte) 176, (byte) 121, (byte) 254, (byte) 94, (byte) 195, (byte) 27, (byte) 68, (byte) 220, (byte) 203, (byte) 33, (byte) 56, (byte) 49, (byte) 25, (byte) 93, (byte) 142, (byte) 35, (byte) 13, (byte) 9, (byte) 3, (byte) 29, (byte) 218, (byte) 151, (byte) 0, (byte) 228, (byte) 165, (byte) 67, (byte) 234, (byte) 162, (byte) 138, (byte) 214, (byte) 193, (byte) 45, (byte) 192, (byte) 224, (byte) 42, (byte) 169, (byte) 205, (byte) 214, (byte) 28, (byte) 170, (byte) 86, (byte) 73, (byte) 178, (byte) 32, (byte) 72, (byte) 22, (byte) 65, (byte) 104, (byte) 203, (byte) 196, (byte) 42, (byte) 24, (byte) 252, (byte) 187, (byte) 194, (byte) 248, (byte) 179, (byte) 103, (byte) 179, (byte) 190, (byte) 32, (byte) 227, (byte) 22, (byte) 181, (byte) 16, (byte) 142, (byte) 186, (byte) 232, (byte) 61, (byte) 220, (byte) 217, (byte) 196, (byte) 118, (byte) 223, (byte) 72, (byte) 166, (byte) 222, (byte) 200, (byte) 212, (byte) 139, (byte) 84, (byte) 87, (byte) 1, (byte) 145, (byte) 147, (byte) 211, (byte) 40, (byte) 108, (byte) 180, (byte) 13, (byte) 29, (byte) 26, (byte) 154, (byte) 31, (byte) 12, (byte) 107, (byte) 161, (byte) 108, (byte) 53, (byte) 62, (byte) 154, (byte) 57, (byte) 57, (byte) 119, (byte) 117, (byte) 145, (byte) 8, (byte) 162, (byte) 160, (byte) 181, (byte) 254, (byte) 151, (byte) 176, (byte) 111, (byte) 112, (byte) 44, (byte) 110, (byte) 192, (byte) 87, (byte) 159, (byte) 198, (byte) 77, (byte) 167, (byte) 61, (byte) 127, (byte) 55, (byte) 106, (byte) 210, (byte) 78, (byte) 35, (byte) 148, (byte) 33, (byte) 108, (byte) 19, (byte) 155, (byte) 2, (byte) 243, (byte) 89, (byte) 248, (byte) 165, (byte) 36, (byte) 157, (byte) 131, (byte) 53, (byte) 86, (byte) 131, (byte) 32, (byte) 98, (byte) 8, (byte) 17, (byte) 17, (byte) 207, (byte) 242, (byte) 182, (byte) 56, (byte) 92, (byte) 226, (byte) 253, (byte) 25, (byte) 129, (byte) 138, (byte) 185, (byte) 88, (byte) 193, (byte) 97, (byte) 9, (byte) 99, (byte) 47, (byte) 67, (byte) 105, (byte) 88, (byte) 184, (byte) 230, (byte) 18, (byte) 253, (byte) 228, (byte) 231, (byte) 141, (byte) 85, (byte) 0, (byte) 75, (byte) 170, (byte) 60, (byte) 235, (byte) 54, (byte) 17, (byte) 13, (byte) 63, (byte) 141, (byte) 53, (byte) 7, (byte) 193, (byte) 126, (byte) 162, (byte) 237, (byte) 228, (byte) 9, (byte) 207, (byte) 251, (byte) 2, (byte) 37, (byte) 90, (byte) 188, (byte) 131, (byte) 251, (byte) 115, (byte) 22, (byte) 213, (byte) 69, (byte) 230, (byte) 91, (byte) 246, (byte) 109, (byte) 158, (byte) 31, (byte) 46, (byte) 99, (byte) 204, (byte) 49, (byte) 30, (byte) 241, (byte) 208, (byte) 172, (byte) 202, (byte) 221, (byte) 180, (byte) 14, (byte) 121, (byte) 94, (byte) 58, (byte) 0 };

    public static final int OFF_AUSSTELLUNGSDATUM = 0;

    public static final int OFF_ABLAUFDATUM = 8;

    public static final int OFF_KUNDENNUMMER = 16;

    public static final int OFF_MAJOR_VERSION = 20;

    public static final int OFF_MINOR_VERSION = 21;

    public static final int OFF_SCHLEIFEN = 22;

    public static final int OFF_PERSONEN = 24;

    public static final int OFF_USER = 26;

    public static final int OFF_PASSWD = 45;

    private UnixTime ausstellungsdatum;

    private UnixTime ablaufdatum;

    private int kundennummer;

    private byte majorVersion;

    private byte minorVersion;

    private short schleifen;

    private short personen;

    private String user;

    private String passwd;

    private Resource licensePath;

    public License(Resource _licensePath) throws UnavailableException
    {
        setLicensePath(_licensePath);

        if (!readLicense())
        {
            throw new UnavailableException(
                            "Die Lizenz-Datei konnte nicht geladen werden");
        }
    }

    /**
     * Liefert das Ausstellungsdatum der Lizenz
     * 
     * @return
     */
    public UnixTime getAusstellungsDatum()
    {
        return ausstellungsdatum;
    }

    /**
     * Liefert das Ablaufdatum der Lizenz
     * 
     * @return
     */
    public UnixTime getAblaufDatum()
    {
        return ablaufdatum;
    }

    /**
     * Testen ob die Lizenz abgelaufen ist
     * 
     * @return
     */
    public boolean isStillValid()
    {
        return (ablaufdatum.isLaterThan(UnixTime.now()));
    }

    /**
     * Liefert die Kundennummer
     * 
     * @return
     */
    public int getKundennummer()
    {
        return kundennummer;
    }

    /**
     * Liefert die Major Versionsnummer
     * 
     * @return
     */
    public byte getMajorVersion()
    {
        return majorVersion;
    }

    /**
     * Liefert die Minor Versionsnummer
     * 
     * @return
     */
    public byte getMinorVersion()
    {
        return minorVersion;
    }

    /**
     * Liefert einen Versionnstring in der Form <major>.<minor>
     * 
     * @return
     */
    public String getVersionString()
    {
        return "" + majorVersion + "." + minorVersion;
    }

    /**
     * Liefert die Anzahl der Maximal anlegbaren Schleifen
     * 
     * @return
     */
    public short getSchleifen()
    {
        return schleifen;
    }

    /**
     * Liefert die Anzahl der maximal anlegbaren Personen
     * 
     * @return
     */
    public short getPersonen()
    {
        return personen;
    }

    /**
     * Liefert den Usernamen fuer das SMS Gateway
     * 
     * @return
     */
    public String getGatewayUser()
    {
        return user;
    }

    /**
     * Liefert das Passwort fuer den SMS Gateway Benutzer
     * 
     * @return
     */
    public String getGatewayPasswd()
    {
        return passwd;
    }

    /**
     * Liest das default-License-File
     * 
     * @return
     */
    final public boolean readLicense()
    {
        return readLicense(licensePath);
    }

    /**
     * License-File einlesen und dekodieren
     * 
     * @param _licensePath
     * @return
     */
    public boolean readLicense(Resource _licensePath)
    {
        try
        {
            if (!_licensePath.exists())
            {
                throw new Exception("Lizenz-Datei [" + _licensePath.toString()
                                + "]  nicht gefunden");
            }

            File f = _licensePath.getFile();

            FileInputStream ifs = new FileInputStream(f);

            byte[] senc = new byte[512];
            if (f.length() != 512)
            {
                // Filegroesse stimmt nicht
                return false;
            }
            int i;
            for (i = 0; i < 512; i++)
            {
                senc[i] = (byte) ((ifs.read() ^ xor_key[i]) & 0xFF);
            }
            // Bits extrahieren
            byte[] uenc = new byte[64];
            int off = 0;
            for (i = 0; i < 64; i++)
            {
                uenc[i] = (byte) (((senc[off + 0] & 0x01))
                                | (((senc[off + 1]) & 0x02))
                                | (((senc[off + 2]) & 0x04))
                                | (((senc[off + 3]) & 0x08))
                                | (((senc[off + 4]) & 0x10))
                                | (((senc[off + 5]) & 0x20))
                                | (((senc[off + 6]) & 0x40)) | (((senc[off + 7]) & 0x80)));
                off += 8;
            }
            // Felder extrahieren
            long t;
            long aus;
            aus = (uenc[OFF_AUSSTELLUNGSDATUM + 0] & 0xFF);
            t = (uenc[OFF_AUSSTELLUNGSDATUM + 1] & 0xFF);
            aus |= t << 8;
            t = (uenc[OFF_AUSSTELLUNGSDATUM + 2] & 0xFF);
            aus |= t << 16;
            t = (uenc[OFF_AUSSTELLUNGSDATUM + 3] & 0xFF);
            aus |= t << 24;
            t = (uenc[OFF_AUSSTELLUNGSDATUM + 4] & 0xFF);
            aus |= t << 32;
            t = (uenc[OFF_AUSSTELLUNGSDATUM + 5] & 0xFF);
            aus |= t << 40;
            t = (uenc[OFF_AUSSTELLUNGSDATUM + 6] & 0xFF);
            aus |= t << 48;
            t = (uenc[OFF_AUSSTELLUNGSDATUM + 7] & 0xFF);
            aus |= t << 56;
            ausstellungsdatum = new UnixTime(aus);

            long ab;
            ab = (uenc[OFF_ABLAUFDATUM + 0] & 0xFF);
            t = (uenc[OFF_ABLAUFDATUM + 1] & 0xFF);
            ab |= t << 8;

            t = (uenc[OFF_ABLAUFDATUM + 2] & 0xFF);
            ab |= t << 16;
            t = (uenc[OFF_ABLAUFDATUM + 3] & 0xFF);
            ab |= t << 24;
            t = (uenc[OFF_ABLAUFDATUM + 4] & 0xFF);
            ab |= t << 32;
            t = (uenc[OFF_ABLAUFDATUM + 5] & 0xFF);
            ab |= t << 40;
            t = (uenc[OFF_ABLAUFDATUM + 6] & 0xFF);
            ab |= t << 48;
            t = (uenc[OFF_ABLAUFDATUM + 7] & 0xFF);
            ab |= t << 56;
            ablaufdatum = new UnixTime(ab);

            kundennummer = (uenc[OFF_KUNDENNUMMER + 0] & 0xFF)
                            | ((uenc[OFF_KUNDENNUMMER + 1] & 0xFF) << 8)
                            | ((uenc[OFF_KUNDENNUMMER + 2] & 0xFF) << 16)
                            | ((uenc[OFF_KUNDENNUMMER + 3] & 0xFF) << 24);
            majorVersion = uenc[OFF_MAJOR_VERSION];
            minorVersion = uenc[OFF_MINOR_VERSION];
            schleifen = (short) ((uenc[OFF_SCHLEIFEN + 0] & 0xFF) | ((uenc[OFF_SCHLEIFEN + 1] & 0xFF) << 8));
            personen = (short) ((uenc[OFF_PERSONEN + 0] & 0xFF) | ((uenc[OFF_PERSONEN + 1] & 0xFF) << 8));
            off = OFF_USER;
            i = 0;
            StringBuffer sb = new StringBuffer();
            while ((i++ < 18) && (uenc[off] != 0))
            {
                sb.append((char) (uenc[off++] & 0xFF));
            }
            user = sb.toString();

            off = OFF_PASSWD;
            i = 0;
            sb = new StringBuffer();
            while ((i++ < 18) && (uenc[off] != 0))
            {
                sb.append((char) (uenc[off++] & 0xFF));
            }
            passwd = sb.toString();

            // Debug
            System.err.println("====================================");
            System.err.println(" ZABOS LIZENZ INFORMATIONEN:");
            System.err.println("====================================");
            System.err.println("Ausstellungsdatum: " + ausstellungsdatum);
            System.err.println("Ablaufdatum: " + ablaufdatum);
            System.err.println("Kundennummer: " + kundennummer);
            System.err.println("Version: " + majorVersion + "." + minorVersion);
            System.err.println("Schleifen: " + schleifen);
            System.err.println("Personen: " + personen);
            System.err.println("Gateway-User: " + user);
            System.err.println("Gateway-Passwd: xxx");
            return true;

        }
        catch (Exception e)
        {
            System.err.println("Die Lizenz-Datei " + _licensePath
                            + " konnte nicht geoeffnet werden (cwd=\""
                            + System.getProperty("user.dir") + "\")");
            e.printStackTrace();
        }

        return false;
    }

    public void setLicensePath(Resource licensePath)
    {
        this.licensePath = licensePath;
    }

    public Resource getLicensePath()
    {
        return licensePath;
    }
}
