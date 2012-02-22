package de.ecw.report.types;

import java.util.ArrayList;

/**
 * Report format types
 * 
 * @author ckl
 */
final public class ReportFormat
{
    /**
     * Formats: CSV, PDF
     * 
     * @author ckl
     * 
     */
    public enum FORMAT
    {
        CSV, PDF
    };

    /**
     * mime type
     */
    private String mimeType = "text/unknown";

    /**
     * extension
     */
    private String extension = "txt";

    /**
     * Format
     */
    private FORMAT format = FORMAT.CSV;

    /**
     * Format: CSV
     */
    public static final ReportFormat CSV = new ReportFormat(FORMAT.CSV,
                    "text/csv", "csv");

    /**
     * Format: PDF
     */
    public static final ReportFormat PDF = new ReportFormat(FORMAT.PDF,
                    "application/pdf", "pdf");

    /**
     * default constructor
     */
    public ReportFormat()
    {
    }

    /**
     * @param _format
     * @param _mimeType
     * @param _extension
     */
    public ReportFormat(FORMAT _format, String _mimeType, String _extension)
    {
        setFormat(_format);
        setMimeType(_mimeType);
        setExtension(_extension);
    }

    /**
     * 
     * @param _typ
     * @param _mimeType
     * @param _extension
     */
    public ReportFormat(ReportFormat _typ, String _mimeType, String _extension)
    {
        setMimeType(_mimeType);
        setExtension(_extension);
    }

    /**
     * Retrieve file extension without "."
     * 
     * @return
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * Retrieve MIME type
     * 
     * @return
     */
    public String getMimeType()
    {
        return mimeType;
    }

    public String toString()
    {
        return getExtension();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof ReportFormat)
        {
            ReportFormat other = (ReportFormat) obj;

            return other.getFormat().equals(this.getFormat());
        }

        return false;
    }

    /**
     * Set format of report
     * 
     * @param format
     */
    public void setFormat(FORMAT format)
    {
        this.format = format;
    }

    /**
     * get format
     * 
     * @return
     */
    public FORMAT getFormat()
    {
        return format;
    }

    /**
     * Returns an array with all defined report formats
     * 
     * @return
     */
    public static ReportFormat[] findAll()
    {
        ReportFormat[] r;

        ArrayList<ReportFormat> alReportFormat = new ArrayList<ReportFormat>();
        alReportFormat.add(CSV);
        alReportFormat.add(PDF);

        r = new ReportFormat[alReportFormat.size()];
        alReportFormat.toArray(r);

        return r;
    }

    /**
     * Retrieves a report format by ID
     * 
     * @param _id
     * @return
     */
    public static ReportFormat findById(FORMAT _id)
    {
        ReportFormat[] all = findAll();

        for (int i = 0, m = all.length; i < m; i++)
        {
            if (all[i].getFormat().equals(_id))
            {
                return all[i];
            }
        }

        return PDF;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }
}
