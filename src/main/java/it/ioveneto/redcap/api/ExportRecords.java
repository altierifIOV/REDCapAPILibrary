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
 * Allows to export a set of records for a project.
 * <p>
 * Note about export rights: Please be aware that Data Export user rights will be applied to this API request. For
 * example, if you have 'No Access' data export rights in the project, then the API data export will fail and return an
 * error. And if you have 'De-Identified' or 'Remove All Identifier Fields' data export rights, then some data fields
 * *might* be removed and filtered out of the data set returned from the API. To make sure that no data is unnecessarily
 * filtered out of your API request, you should have 'Full Data Set' export rights in the project.
 **/
public class ExportRecords {
    private final List<NameValuePair> params;
    private final HttpPost post;
    private HttpResponse resp;
    private final HttpClient client;
    private int respCode;
    private BufferedReader reader;
    private final StringBuffer result;
    private String line;


    public ExportRecords(final String api_token, final String format, final String url, final String type) {
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("token", api_token));
        params.add(new BasicNameValuePair("content", "record"));
        params.add(new BasicNameValuePair("format", format));
        params.add(new BasicNameValuePair("type", type));

		/*
		params.add(new BasicNameValuePair("type", "flat"));
		params.add(new BasicNameValuePair("format", "json"));
		params.add(new BasicNameValuePair("type", "flat"));
		*/

        post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        result = new StringBuffer();
        client = HttpClientBuilder.create().build();
        respCode = -1;
        reader = null;
        line = null;
    }

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

        System.out.println("respCode: " + respCode);
        System.out.println("result: " + result.toString());
    }

    public int getRespCode() {
        return respCode;
    }

    public String getResult() {
        return result.toString();
    }
}
