package it.ioveneto.redcap.api;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Delete Records. This method allows you to delete one or more records from a project in a single API request, and also
 * optionally allows you to delete parts of a record, such as a single instrument's data for one or more records or a
 * single event's data for one or more records.
 */
public class DeleteRecords {
    private final List<NameValuePair> params;
    private final HttpPost post;
    private final HttpClient client;
    private final StringBuffer result;
    private boolean debugMode;
    private HttpResponse resp;
    private int respCode;
    private BufferedReader reader;
    private String line;

    /**
     * Essential constructor with fundamental parameters
     * @param api_token      the token that defines the REDCap project of interest. To use this method, you must have
     *                       'Delete Record' user privileges in the project.
     * @param recordsID      an array of record IDs specifying records you wish to delete
     * @param url            API endpoint (usually "https://myredcapdomain/redcap/api/")
     */
    public DeleteRecords(final String api_token, final String[] recordsID, final String url){
        this(api_token, recordsID, null, null, null, null, url, false);
    }

    /**
     * Complete constructor with all possible parameters
     *
     * @param api_token      the token that defines the REDCap project of interest. To use this method, you must have
     *                       'Delete Record' user privileges in the project.
     * @param recordsID      an array of record IDs specifying records you wish to delete
     * @param arm            the arm number of the arm in which the record(s) should be deleted. (This can only be used
     *                       if the project is longitudinal with more than one arm.) NOTE: If the arm parameter is not
     *                       provided, the specified records will be deleted from all arms in which they exist. Whereas,
     *                       if arm is provided, they will only be deleted from the specified arm.
     * @param instrument     the unique instrument name (column B in the Data Dictionary) of an instrument (as a string)
     *                       if you wish to delete the data for all fields on the specified instrument for the records
     *                       specified.
     * @param event          the unique event name - only for longitudinal projects. NOTE: If instrument is provided for
     *                       a longitudinal project, the event parameter is mandatory.
     * @param repeatInstance the repeating instance number for a repeating instrument or repeating event. NOTE: If
     *                       project has repeating instruments/events, it will remove only the data for that repeating
     *                       instance.
     * @param url            API endpoint (usually "https://myredcapdomain/redcap/api/")
     * @param debugMode      if set to "true", doPost() invocation will print out http response and output on the
     *                       standard output.
     */
    public DeleteRecords(final String api_token, final String[] recordsID, final String arm, final String instrument, final String event, final String repeatInstance, final String url, final boolean debugMode) {
        params = new ArrayList<NameValuePair>();

        if (api_token == null)
            throw new IllegalArgumentException("Must provide a valid API token");
        else
            params.add(new BasicNameValuePair("token", api_token));

        params.add(new BasicNameValuePair("action", "delete"));
        params.add(new BasicNameValuePair("content", "record"));

        if (recordsID == null)
            throw new IllegalArgumentException("must provide a list of records ID to delete");
        for (int i = 0; i < recordsID.length; i++) {
            if (recordsID[i] == null)
                throw new IllegalArgumentException("record ID [" + i + "] is null");
            else if (recordsID[i].isEmpty()) {
                throw new IllegalArgumentException("record ID [" + i + "] is empty");
            } else
                params.add(new BasicNameValuePair("records[" + i + "]", recordsID[i]));
        }

        if (arm != null) {
            if (Utilities.checkNumberPositive(arm))
                params.add(new BasicNameValuePair("arm", arm));
            else
                throw new IllegalArgumentException("must provide a positive arm number");
        }

        if (instrument != null)
            params.add(new BasicNameValuePair("instrument", instrument));

        if (event != null)
            params.add(new BasicNameValuePair("event", event));

        if (repeatInstance != null) {
            if (Utilities.checkNumberPositive(repeatInstance))
                params.add(new BasicNameValuePair("repeat_instance", repeatInstance));
            else
                throw new IllegalArgumentException("must provide a positive instance number for a repeating instrument or repeating event");
        }


/*  example of parameters
    ArrayList<String> array = new ArrayList<String>();
    array.add("1");
    params.add(new BasicNameValuePair("records", array.get(0)));
    params.add(new BasicNameValuePair("arm", "1"));
    params.add(new BasicNameValuePair("instrument", "demographics"));
    params.add(new BasicNameValuePair("event", "visit_1_arm_1"));*/


        if (url == null)
            throw new IllegalArgumentException("must provide valid URL of REDCap API endpoint");

        post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        this.debugMode = debugMode;
        result = new StringBuffer();
        client = HttpClientBuilder.create().build();
        respCode = -1;
        reader = null;
        line = null;
    }


    /**
     * Performs the Http request and handles results. Prints HTTP response and result on standard output if debug mode
     * is active
     */
    public void doPost() {
        resp = null;

        try {
            resp = client.execute(post);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (resp != null) {
            respCode = resp.getStatusLine().getStatusCode();

            try {
                reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        if (reader != null) {
            try {
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        if (debugMode) {
            System.out.println("respCode: " + respCode);
            System.out.println("result: " + result.toString());
        }
    }

    /**
     * Returns the HTTP response of the request
     *
     * @return the response as int, -1 if not executed
     */
    public int getRespCode() {
        return respCode;
    }

    /**
     * Returns the result of HTTP request
     *
     * @return the result as string, null if any (or not yet executed)
     */
    public String getResult() {

        return result.toString();
    }

    /**
     * Checks if debut mode is activated or not
     *
     * @return true if active
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Turns on debug mode
     */
    public void setDebugMode() {
        this.debugMode = true;
    }

    /**
     * Turns off debug mode
     */
    public void unsetDebugMode() {
        this.debugMode = false;
    }
}