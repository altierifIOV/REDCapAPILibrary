package it.ioveneto.redcap.api;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows to import records inside a project.
 */
public class ImportRecords extends APIRequest{
    private final List<NameValuePair> params;
    private final String recordID;
    private final SecureRandom random;


	/**
	 * Basic constructor with fundamental fields
	 *
	 * @param api_token         the token that defines the REDCap project of interest. Note about export rights: To use
	 *                          this method, you must have API Import/Update privileges in the project.
	 * @param format            "csv", "json", "xml", "odm" ('odm' refers to CDISC ODM XML format, specifically ODM
	 *                          version 1.3.1).
	 * @param data              The formatted data to be imported.
	 *                          <p>
	 *                          TIP: If importing repeating instances for a repeating event or repeating instrument, you
	 *                          may auto-number the instances by providing a value of 'new' for the
	 *                          'redcap_repeat_instance' field in the dataset you are importing. This is useful because
	 *                          it allows you to import such data without the need to determine how many instances
	 *                          already exist for a given repeating event/instance prior to the import. NOTICE: The
	 *                          'new' value option for auto-numbering instances does NOT work for 'eav' type data but
	 *                          only for 'flat' type.
	 *                          <p>
	 *                          NOTE: When importing data in EAV type format, please be aware that checkbox fields must
	 *                          have their field_name listed as variable+'___'+optionCode and its value as either '0' or
	 *                          '1' (unchecked or checked, respectively). For example, for a checkbox field with
	 *                          variable name 'icecream', it would be imported as EAV with the field_name as
	 *                          'icecream___4' having a value of '1' in order to set the option coded with '4' (which
	 *                          might be 'Chocolate') as 'checked'.
	 * @param url               API endpoint (usually "https://myredcapdomain/redcap/api/")
	 */
	public ImportRecords(final String api_token,  final String format, final String data, final String url){
		this(api_token, format, null, null, false, data, null, null, null, format, url, false);
	}

	/**
     * Complete constructor with all possible parameters
     *
     * @param api_token         the token that defines the REDCap project of interest. Note about export rights: To use
     *                          this method, you must have API Import/Update privileges in the project.
     * @param format            "csv", "json", "xml", "odm" ('odm' refers to CDISC ODM XML format, specifically ODM
     *                          version 1.3.1).
     * @param type              "flat" - inputs as one record per row
	 *                          "eav" - inputs as one data point per row
     *                                Non-longitudinal projects: Will have the fields record_ID_for_the_project,
	 *                                field_name, value
	 *                                Longitudinal projects: Will have the fields - record_ID_for_the_project,
	 *                                field_name, value, redcap_event_name.
     * @param overwriteBehavior "normal" - blank/empty values will be ignored
	 *                          "overwrite" - blank/empty values are valid and will overwrite data
     * @param forceAutoNumber   If record auto-numbering has been enabled in the project, it may be desirable to import
     *                          records where each record's record name is automatically determined by REDCap (just as
     *                          it does in the user interface). If this parameter is set to 'true', the record names
     *                          provided in the request will not be used (although they are still required in order to
     *                          associate multiple rows of data to an individual record in the request), but instead
     *                          those records in the request will receive new record names during the import process.
     *                          NOTE: To see how the provided record names get translated into new auto record names,
     *                          the returnContent parameter should be set to 'auto_ids', which will return a record list
     *                          similar to 'ids' value, but it will have the new record name followed by the provided
     *                          record name in the request, in which the two are comma-delimited. For example, if false
     *                          the record names provided in the request will be used, while if true new record names
	 *                          will be automatically determined.
     * @param data              The formatted data to be imported.
     *                          <p>
     *                          TIP: If importing repeating instances for a repeating event or repeating instrument, you
     *                          may auto-number the instances by providing a value of 'new' for the
     *                          'redcap_repeat_instance' field in the dataset you are importing. This is useful because
     *                          it allows you to import such data without the need to determine how many instances
     *                          already exist for a given repeating event/instance prior to the import. NOTICE: The
     *                          'new' value option for auto-numbering instances does NOT work for 'eav' type data but
     *                          only for 'flat' type.
     *                          <p>
     *                          NOTE: When importing data in EAV type format, please be aware that checkbox fields must
     *                          have their field_name listed as variable+'___'+optionCode and its value as either '0' or
     *                          '1' (unchecked or checked, respectively). For example, for a checkbox field with
     *                          variable name 'icecream', it would be imported as EAV with the field_name as
     *                          'icecream___4' having a value of '1' in order to set the option coded with '4' (which
     *                          might be 'Chocolate') as 'checked'.
     * @param dateFormat        "MDY", "DMY", "YMD" - the format of values being imported for dates or datetime
     *                          fields (understood with M representing 'month', D as 'day', and Y as 'year') - NOTE: The
     *                          default format is Y-M-D (with dashes), while MDY and DMY values should always be
     *                          formatted as M/D/Y or D/M/Y (with slashes), respectively.
     * @param csvDelimiter      Set the delimiter used to separate values in the CSV data file (for CSV format only).
     *                          Options include: comma ',', 'tab', semi-colon ';', pipe '|', or caret '^'. Simply
     *                          provide the value in quotes for this parameter.
     * @param returnContent     "count" - the number of records imported
	 *                          "ids" - a list of all record IDs that were imported
	 *                          "auto_ids" = (used only when forceAutoNumber=true) a list of pairs of all record IDs
	 *                          that were imported, includes the new ID created and the ID value that was sent in
	 *                          the API request (e.g., 323,10).
     * @param errorFormat       "csv", "json", "xml" - specifies the format of error messages.
     * @param url               API endpoint (usually "https://myredcapdomain/redcap/api/")
     * @param debugMode         if set to "true", doPost() invocation will print out http response and output on the
     *                          standard output.
     */
    public ImportRecords(final String api_token, final String format, final String type, final String overwriteBehavior, final boolean forceAutoNumber, final String data, final String dateFormat, final String csvDelimiter, final String returnContent, final String errorFormat, final String url, final boolean debugMode) {

		super(url, debugMode);

        random = new SecureRandom();
        recordID = DigestUtils.sha1Hex(new BigInteger(16, random).toString(16)).substring(0, 16);

/*		code snippet left here commented as JSON example
        record = new JSONObject();
		record.put("record_id", recordID);
		record.put("first_name", "First");
		record.put("last_name", "Last");
		record.put("address", "123 Cherry Lane\nNashville, TN 37015");
		record.put("telephone", "(615) 255-4000");
		record.put("email", "first.last@gmail.com");
		record.put("dob", "1972-08-10");
		record.put("age", "43");
		record.put("ethnicity", "1");
		record.put("race", "4");
		record.put("sex", "1");
		record.put("height", "180");
		record.put("weight", "105");
		record.put("bmi", "31.4");
		record.put("comments", "comments go here");
		record.put("redcap_event_name", "events_2_arm_1");
		record.put("basic_demography_form_complete", "2");

		dataJSON = new JSONArray();
		dataJSON.add(record);
		data = dataJSON.toString()*/

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

		if (overwriteBehavior == null)
			params.add(new BasicNameValuePair("overwriteBehavior", "normal"));
		else {
			if (StringUtils.indexOfAny(overwriteBehavior, new String[]{"normal", "overwrite"}) == -1)
				throw new IllegalArgumentException("Allowed overwrite instructions: \"normal\"  (default), \"overwrite\"");
			else
				params.add(new BasicNameValuePair("overwriteBehavior", overwriteBehavior));
		}

		if (forceAutoNumber)
			params.add(new BasicNameValuePair("forceAutoNumber", "true"));
		else
			params.add(new BasicNameValuePair("forceAutoNumber", "false"));

        if (data == null)
			throw new IllegalArgumentException("Must provide data to inset as String");
		else
			params.add(new BasicNameValuePair("data", data));

		if (dateFormat == null)
			params.add(new BasicNameValuePair("dateFormat", "YMD"));
		else {
			if (StringUtils.indexOfAny(dateFormat, new String[]{"csv", "json", "xml"}) == -1)
				throw new IllegalArgumentException("Allowed data formats: \"MDY\", \"DMY\", \"YMD\" (default)");
			else
				params.add(new BasicNameValuePair("dateFormat", dateFormat));
		}

		if (csvDelimiter == null)
			params.add(new BasicNameValuePair("csvDelimiter", ","));
		else {
			if (StringUtils.indexOfAny(csvDelimiter, new String[]{",", "tab", ";", "|", "^"}) == -1)
				throw new IllegalArgumentException("Allowed CSV delimiters: \",\" (default), \"tab\", \";\", \"|\", \"^\"");
			else
				params.add(new BasicNameValuePair("csvDelimiter", csvDelimiter));
		}

		if (returnContent == null)
			params.add(new BasicNameValuePair("returnContent", "count"));
		else {
			if (StringUtils.indexOfAny(returnContent, new String[]{"count", "ids", "auto-ids"}) == -1)
				throw new IllegalArgumentException("Allowed return contents: \"count\" (default), \"ids\", \"auto-ids\"");
			else
				params.add(new BasicNameValuePair("returnContent", returnContent));
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
