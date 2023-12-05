package it.ioveneto.redcap.api;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Export Project Information. This class allows you to export some basic attributes of a given REDCap project,
 * such as the project's title, if it is longitudinal, if surveys are enabled, the time the project was created and
 * moved to production, etc.
 */
public class ExportProject extends APIRequest{
    private final List<NameValuePair> params;
    /**
     * Complete constructor with all parameters
     *
     * @param api_token   The API token specific to your REDCap project and username (each token is unique to each user
     *                    for each project). See the section on the left-hand menu for obtaining a token for a given
     *                    project. To use this call, you must have API Export privileges in the project.
     * @param format      "csv", "json", "xml", "odm" ('odm' refers to CDISC ODM XML format, specifically ODM version
     *                    1.3.1).
     * @param errorFormat "csv", "json", "xml" - specifies the format of error messages.
     * @param url         API endpoint (usually "https://myredcapdomain/redcap/api/")
     * @param debugMode   if set to "true", doPost() invocation will print out http response and output on the standard
     *                    output.
     */
    public ExportProject(final String api_token, final String format, final String errorFormat, final String url, final boolean debugMode) throws IllegalArgumentException {
        super(url, debugMode);

        params = new ArrayList<NameValuePair>();

        if (api_token == null)
            throw new IllegalArgumentException("Must provide a valid API token");
        else
            params.add(new BasicNameValuePair("token", api_token));

        params.add(new BasicNameValuePair("content", "project"));

        if (format == null)
            params.add(new BasicNameValuePair("format", "xml"));
        else {
            if (StringUtils.indexOfAny(format, new String[]{"csv", "json", "xml", "odm"}) == -1)
                throw new IllegalArgumentException("Allowed record formats: \"csv\", \"json\", \"xml\" (default), \"odm\"");
            else
                params.add(new BasicNameValuePair("format", format));
        }

        if (errorFormat == null)
            params.add(new BasicNameValuePair("returnFormat", "xml"));
        else {
            if (StringUtils.indexOfAny(errorFormat, new String[]{"csv", "json", "xml"}) == -1)
                throw new IllegalArgumentException("Allowed error formats: \"csv\", \"json\", \"xml\" (default)");
            else
                params.add(new BasicNameValuePair("returnFormat", errorFormat));
        }

        super.setParams(params);

    }

}
