package it.ioveneto.redcap.api;

import org.apache.commons.lang.StringUtils;
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
 * Export Project Information. This class allows you to export some of the basic attributes of a given REDCap project, such as the project's title, if it is longitudinal, if surveys are enabled, the time the project was created and moved to production, etc.
 */
public class ExportProject
{
	private final List<NameValuePair> params;
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;
	private boolean debugMode;

	/**
	 * Complete constructor with all parameters
	 * @param api_token The API token specific to your REDCap project and username (each token is unique to each user for each project). See the section on the left-hand menu for obtaining a token for a given project. To use this call, you must have API Export privileges in the project.
	 * @param format "csv", "json", "xml", "odm" ('odm' refers to CDISC ODM XML format, specifically ODM version 1.3.1).
	 * @param errorFormat "csv", "json", "xml" - specifies the format of error messages.
	 * @param url API endpoint (usually "https://myredcapdomain/redcap/api/")
	 * @param debugMode if set to "true", doPost() invocation will print out http response and output on the standard output.
	 */
	public ExportProject(final String api_token, final String format, final String errorFormat, final String url, final boolean debugMode) throws IllegalArgumentException
	{
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

		if (url == null)
			throw new IllegalArgumentException("must provide valid URL of REDCap API endpoint");

		post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		try
		{
			post.setEntity(new UrlEncodedFormEntity(params));
		}
		catch (final Exception e)
		{
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

	public void doPost()
	{
		resp = null;

		try
		{
			resp = client.execute(post);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		if(resp != null)
		{
			respCode = resp.getStatusLine().getStatusCode();

			try
			{
				reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}

		if(reader != null)
		{
			try
			{
				while((line = reader.readLine()) != null)
				{
					result.append(line);
				}
			}
			catch (final Exception e)
			{
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
	 * @return the response as int, -1 if not executed
	 */
	public int getRespCode() {
		return respCode;
	}

	/**
	 * Returns the result of HTTP request
	 * @return the result as string, null if any (or not yet executed)
	 */
	public String getResult() {

		return result.toString();
	}

	/**
	 * Checks if debut mode is activated or not
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
