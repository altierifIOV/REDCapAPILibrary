package it.ioveneto.redcap.api;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to export a set of records for a project.
 *
 **/
public class ExportRecords extends APIRequest{
    private final List<NameValuePair> params;

    /**
     * Essential constructor with just fundamental parameters that cannot be assumed elsewhere, the invocation of the
     * request simply tries to extract all the records (or record-events, if a longitudinal project) from the project
     * in xml format.
     * @param api_token                    the token that defines the REDCap project of interest.
     *                                     Note about export rights: Please be aware that Data Export user rights
     *                                     will be applied to this API request. For  example, if you have 'No Access'
     *                                     data export rights in the project, then the API data export will fail and
     *                                     return an error. And if you have 'De-Identified' or 'Remove All
     *                                     Identifier Fields' data export rights, then some data fields might
     *                                     be removed and filtered out of the data set returned from the API. To make
     *                                     sure that no data is unnecessarily filtered out of your API request, you
     *                                     should have 'Full Data Set' export rights in the project
     * @param url                          API endpoint (usually "https://myredcapdomain/redcap/api/")
     */
    public ExportRecords(final String api_token, final String url){
        this(api_token,"xml", "flat", null, null, null, null, "raw", "raw", false, "xml", false, false, null, null, ",", ".", false, url, false);
    }

    /**
     * Basic invocation with record and field filtering
     * @param api_token                    the token that defines the REDCap project of interest.
     *                                     Note about export rights: Please be aware that Data Export user rights
     *                                     will be applied to this API request. For  example, if you have 'No Access'
     *                                     data export rights in the project, then the API data export will fail and
     *                                     return an error. And if you have 'De-Identified' or 'Remove All
     *                                     Identifier Fields' data export rights, then some data fields might
     *                                     be removed and filtered out of the data set returned from the API. To make
     *                                     sure that no data is unnecessarily filtered out of your API request, you
     *                                     should have 'Full Data Set' export rights in the project
     * @param format                       "csv", "json", "xml", "odm" ('odm' refers to CDISC ODM XML format,
     *                                     specifically ODM version 1.3.1)
     * @param filter                       String of logic text (e.g., "[age] > 30") for filtering the data to be
     *                                     returned by this API method, in which the API will only return the records
     *                                     (or record-events, if a longitudinal project) where the logic evaluates as
     *                                     TRUE. Please note that if the filter logic contains any incorrect syntax,
     *                                     the API will respond with an error message.
     * @param fieldsList                   an array of field names specifying specific fields you wish to pull or,
     *                                     alternatively, as a string (comma-separated list)
     * @param exportDAGs                   specifies whether or not to export the 'redcap_data_access_group' field when
     *                                     data access groups are utilized in the project. NOTE: This flag is only
     *                                     viable if the user whose token is being used to make the API request is
     *                                     *not* in a data access group. If the user is in a group, then this flag will
     *                                     revert to its default value.
     * @param url                          API endpoint (usually "https://myredcapdomain/redcap/api/")
     */
    public ExportRecords(final String api_token, final String format, final String filter, final String fieldsList, final boolean exportDAGs, final String url){
        this(api_token, format, "flat", filter, null, fieldsList, null, "raw", "raw", false, format, false, exportDAGs, null, null, ",", ".", false, url, false);
    }

    /**
     * Complete constructor with all possible parameters
     *
     * @param api_token                    the token that defines the REDCap project of interest.
     *                                     Note about export rights: Please be aware that Data Export user rights
     *                                     will be applied to this API request. For  example, if you have 'No Access'
     *                                     data export rights in the project, then the API data export will fail and
     *                                     return an error. And if you have 'De-Identified' or 'Remove All
     *                                     Identifier Fields' data export rights, then some data fields might
     *                                     be removed and filtered out of the data set returned from the API. To make
     *                                     sure that no data is unnecessarily filtered out of your API request, you
     *                                     should have 'Full Data Set' export rights in the project
     * @param format                       "csv", "json", "xml", "odm" ('odm' refers to CDISC ODM XML format,
     *                                     specifically ODM version 1.3.1).
     * @param type                         "flat" - output as one record per row
     *                                     "eav" - output as one data point per row
     *                                     Non-longitudinal projects: Will have the fields record_ID_for_the_project,
     *                                     field_name, value
     *                                     Longitudinal projects: Will have the fields - record_ID_for_the_project,
     *                                     field_name, value, redcap_event_name. Set to "flat" if unspecified.
     * @param filterLogic                  String of logic text (e.g., "[age] > 30") for filtering the data to be
     *                                     returned by this API method, in which the API will only return the records
     *                                     (or record-events, if a longitudinal project) where the logic evaluates as
     *                                     TRUE. Please note that if the filter logic contains any incorrect syntax,
     *                                     the API will respond with an error message.
     * @param recordNames                  an array of record names specifying specific records you wish to pull
     * @param fieldsList                   an array of field names specifying specific fields you wish to pull or,
     *                                     alternatively, as a string (comma-separated list)
     * @param eventsList                   an array of unique event names that you wish to pull records for - only for
     *                                     longitudinal projects
     * @param rawOrLabel                   "raw" - export the raw coded values
     *                                     "label" - export the labels for the options of multiple choice fields
     * @param rawOrLabelHeaders            (for 'csv' format 'flat' type only) for the CSV headers:
     *                                     "raw" - export the variable/field names
     *                                     "label" - export the field labels (label)
     * @param exportCheckboxLabel          specifies the format of checkbox field values specifically
     *                                     when exporting the data as labels (i.e., when rawOrLabel=label) in flat
     *                                     format (i.e., when type=flat). When exporting labels, by default
     *                                     (exportCheckboxLabel=false), all checkboxes will either have a value
     *                                     'Checked' if they are checked or 'Unchecked' if not checked.
     *                                     But if exportCheckboxLabel is set to true, it will instead export the
     *                                     checkbox value as the checkbox option's label (e.g., 'Choice 1') if checked
     *                                     or it will be blank/empty (no value) if not checked.
     *                                     If rawOrLabel=false or if type=eav, then the exportCheckboxLabel flag is
     *                                     ignored. (The exportCheckboxLabel parameter is ignored for type=eav because
     *                                     'eav' type always exports checkboxes differently anyway, in which checkboxes
     *                                     are exported with their true variable name (whereas the 'flat' type exports
     *                                     them as variable___code format), and another difference is that 'eav'
     *                                     type *always* exports checkbox values as the choice label for labels export,
     *                                     or as 0 or 1 (if unchecked or checked, respectively) for raw export.)
     * @param errorFormat                  "csv", "json", "xml" - specifies the format of error messages.
     * @param exportSurveyFields           specifies whether or not to export the survey identifier field (e.g.,
     *                                     'redcap_survey_identifier') or survey timestamp fields (e.g.,
     *                                     instrument+'_timestamp') when surveys are utilized in the project.
     *                                     If set to 'true', it will return the redcap_survey_identifier
     *                                     field and also the survey timestamp field for a particular survey when at
     *                                     least one field from that survey is being exported.
     *                                     NOTE: If the survey identifier field or survey timestamp fields are imported
     *                                     via API data import, they will simply be ignored since they are not real
     *                                     fields in the project but rather are pseudo-fields.
     * @param exportDAGs                   specifies whether or not to export the 'redcap_data_access_group' field when
     *                                     data access groups are utilized in the project. NOTE: This flag is only
     *                                     viable if the user whose token is being used to make the API request is
     *                                     *not* in a data access group. If the user is in a group, then this flag will
     *                                     revert to its default value.
     * @param dateTimeBegin                To return only records that have been <u>created or modified</u> *after* a
     *                                     given date/time, provide a timestamp in the format YYYY-MM-DD HH:MM:SS (e.g.,
     *                                     '2017-01-01 00:00:00' for January 1, 2017 at midnight server time).
     * @param dateTimeEnd                  To return only records that have been <u>created or modified</u> *before* a
     *                                     given date/time, provide a timestamp in the format YYYY-MM-DD HH:MM:SS (e.g.,
     *                                     '2017-01-01 00:00:00' for January 1, 2017 at midnight server time).
     * @param csvDelimiter                 Set the delimiter used to separate values in the CSV data file (for CSV
     *                                     format only). Options include: comma ',', 'tab', semi-colon ';',
     *                                     pipe '|', or caret '^'. Simply provide the value in quotes for this
     *                                     parameter.
     * @param decimalSeparator             If specified, force all numbers into same decimal format. You may choose to
     *                                     force all data values containing a decimal to have the same decimal
     *                                     character, which will be applied to all calc fields and number-validated text
     *                                     fields. Options include comma ',' or dot/full stop '.'.
     *                                     Simply provide the value of either ',' or '.' for this parameter.
     * @param exportBlankForGrayFormStatus specifies whether or not to export blank values for instrument complete
     *                                     status fields that have a gray status icon. All instrument complete status
     *                                     fields having a gray icon can be exported either as a blank value or as "0"
     *                                     (Incomplete). Blank values are recommended in a data export if the data will
     *                                     be re-imported into a REDCap project.
     * @param url                          API endpoint (usually "https://myredcapdomain/redcap/api/")
     * @param debugMode                    if set to "true", doPost() invocation will print out http response and output
     *                                     on the standard output.
     */
    public ExportRecords(final String api_token, final String format, final String type, final String filterLogic,
                         String recordNames, String fieldsList, String eventsList, String rawOrLabel,
                         String rawOrLabelHeaders, boolean exportCheckboxLabel, String errorFormat, boolean exportSurveyFields, boolean exportDAGs, String dateTimeBegin,
                         String dateTimeEnd, String csvDelimiter, String decimalSeparator, boolean exportBlankForGrayFormStatus, final String url, final boolean debugMode) {

        super(url, debugMode);

        params = new ArrayList<NameValuePair>();

        if (api_token == null)
            throw new IllegalArgumentException("Must provide a valid API token");
        else
            params.add(new BasicNameValuePair("token", api_token));

        params.add(new BasicNameValuePair("content", "record"));

        if (format == null)
            params.add(new BasicNameValuePair("format", "xml"));
        else {
            if (StringUtils.indexOfAny(format, new String[]{"csv", "json", "xml", "odm"}) == -1)
                throw new IllegalArgumentException("Allowed record formats: \"csv\", \"json\", \"xml\" (default), \"odm\"");
            else
                params.add(new BasicNameValuePair("format", format));
        }

        if (type == null)
            params.add(new BasicNameValuePair("type", "flat"));
        else {
            if (StringUtils.indexOfAny(type, new String[]{"flat", "eav"}) == -1)
                throw new IllegalArgumentException("Allowed row by row data output alignments: \"flat\" (default), \"eav\"");
            else
                params.add(new BasicNameValuePair("type", type));
        }

        if (filterLogic != null)
            params.add(new BasicNameValuePair("filterLogic", filterLogic));

        if (recordNames != null)
            params.add(new BasicNameValuePair("records", recordNames));

        if (fieldsList != null)
            params.add(new BasicNameValuePair("fields", fieldsList));

        if (eventsList != null)
            params.add(new BasicNameValuePair("events", eventsList));

        if (rawOrLabel == null)
            params.add(new BasicNameValuePair("rawOrLabel", "raw"));
        else {
            if (StringUtils.indexOfAny(rawOrLabel, new String[]{"raw", "label"}) == -1)
                throw new IllegalArgumentException("Allowed multiple choice fields export formats: \"raw\" (default), \"label\"");
            else
                params.add(new BasicNameValuePair("rawOrLabel", rawOrLabel));
        }

        if (rawOrLabelHeaders == null)
            params.add(new BasicNameValuePair("rawOrLabelHeaders", "raw"));
        else {
            if (StringUtils.indexOfAny(rawOrLabelHeaders, new String[]{"raw", "label"}) == -1)
                throw new IllegalArgumentException("Allowed multiple choice fields CSV headers export formats: \"raw\" (default), \"label\"");
            else
                params.add(new BasicNameValuePair("rawOrLabelHeaders", rawOrLabelHeaders));
        }

        if (exportCheckboxLabel)
            params.add(new BasicNameValuePair("exportCheckboxLabel", "true"));
        else
            params.add(new BasicNameValuePair("exportCheckboxLabel", "false"));


        if (errorFormat == null)
            params.add(new BasicNameValuePair("returnFormat", "xml"));
        else {
            if (StringUtils.indexOfAny(errorFormat, new String[]{"csv", "json", "xml"}) == -1)
                throw new IllegalArgumentException("Allowed error formats: \"csv\", \"json\", \"xml\" (default)");
            else
                params.add(new BasicNameValuePair("returnFormat", errorFormat));
        }

        if (exportSurveyFields)
            params.add(new BasicNameValuePair("exportSurveyFields", "true"));
        else
            params.add(new BasicNameValuePair("exportSurveyFields", "false"));

        if (exportDAGs)
            params.add(new BasicNameValuePair("exportDataAccessGroups", "true"));
        else
            params.add(new BasicNameValuePair("exportDataAccessGroups", "false"));

        if (dateTimeBegin != null){
            if (Utilities.isValidDate(dateTimeBegin))
                params.add(new BasicNameValuePair("dateRangeBegin", dateTimeBegin));
            else
                throw new IllegalArgumentException("Dates must be in this format \"YYYY-MM-DD HH:MM:SS\"");
        }

        if (dateTimeEnd != null){
            if (Utilities.isValidDate(dateTimeEnd))
                params.add(new BasicNameValuePair("dateTimeEnd", dateTimeEnd));
            else
                throw new IllegalArgumentException("Dates must be in this format \"YYYY-MM-DD HH:MM:SS\"");
        }

        if (csvDelimiter == null)
            params.add(new BasicNameValuePair("csvDelimiter", ","));
        else {
            if (StringUtils.indexOfAny(csvDelimiter, new String[]{",", "tab", ";", "|", "^"}) == -1)
                throw new IllegalArgumentException("Allowed CSV delimiters: \",\" (default), \"tab\", \";\", \"|\", \"^\"");
            else
                params.add(new BasicNameValuePair("csvDelimiter", csvDelimiter));
        }

        if (decimalSeparator == null)
            params.add(new BasicNameValuePair("decimalCharacter", ","));
        else {
            if (StringUtils.indexOfAny(decimalSeparator, new String[]{",", ".",}) == -1)
                throw new IllegalArgumentException("Allowed decimals separators: \".\" (default), \",\"");
            else
                params.add(new BasicNameValuePair("decimalCharacter", decimalSeparator));
        }

        if (exportBlankForGrayFormStatus)
            params.add(new BasicNameValuePair("exportBlankForGrayFormStatus", "true"));
        else
            params.add(new BasicNameValuePair("exportBlankForGrayFormStatus", "false"));

        super.setParams(params);

    }

}
