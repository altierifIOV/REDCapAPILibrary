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

public class ExportProjectXML
{
	private final List<NameValuePair> params;
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;

	public ExportProjectXML(final String api_token, final String format, final String url){
		this(api_token, format, url, false, false, false);
	}

	public ExportProjectXML(final String api_token, final String format, final String url, final boolean metadataOnly, final boolean surveyFields, final boolean dataAccessGroups)
	{
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", api_token));
		params.add(new BasicNameValuePair("content", "project_xml"));

		if (metadataOnly)
			params.add(new BasicNameValuePair("returnMetadataOnly", "true"));
		else
			params.add(new BasicNameValuePair("returnMetadataOnly", "false"));

		if (surveyFields)
			params.add(new BasicNameValuePair("exportSurveyFields", "true"));
		else
			params.add(new BasicNameValuePair("exportSurveyFields", "false"));

		if (dataAccessGroups)
			params.add(new BasicNameValuePair("exportDataAccessGroups", "true"));
		else
			params.add(new BasicNameValuePair("exportDataAccessGroups", "false"));


		//params.add(new BasicNameValuePair("returnFormat", "json"));

		params.add(new BasicNameValuePair("returnFormat", format));

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

		result = new StringBuffer();
		client = HttpClientBuilder.create().build();
		respCode = -1;
		reader = null;
		line = null;
	}

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

		System.out.println("respCode: " + respCode);
		System.out.println("result: " + result.toString());
	}
}
