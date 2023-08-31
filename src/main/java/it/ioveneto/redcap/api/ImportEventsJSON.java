package it.ioveneto.redcap.api;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImportEventsJSON
{
	private final List<NameValuePair> params;
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;

	public ImportEventsJSON(final String api_token, final String url, final JSONArray data)
	{
/*
		record = new JSONObject();
		record.put("event_name", "Event 1");
		record.put("arm_num", "1");
		record.put("day_offset", "0");
		record.put("offset_min", "0");
		record.put("offset_max", "0");
		record.put("unique_event_name", "event_1_arm_1");

		data = new JSONArray();
		data.add(record);
*/

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", api_token));
		params.add(new BasicNameValuePair("content", "event"));
		params.add(new BasicNameValuePair("action", "import"));
		params.add(new BasicNameValuePair("format", "json"));
		params.add(new BasicNameValuePair("override", "0"));
		params.add(new BasicNameValuePair("data", data.toJSONString()));

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
