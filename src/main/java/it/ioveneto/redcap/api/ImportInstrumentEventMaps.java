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

public class ImportInstrumentEventMaps
{
	private final List<NameValuePair> params;
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;

	public ImportInstrumentEventMaps(final String api_token, final String url, final JSONArray data)
	{
/*		final JSONArray form_1 = new JSONArray();
		form_1.add("instr_1");
		form_1.add("instr_2");

		final JSONArray form_2 = new JSONArray();
		form_2.add("instr_1");

		final JSONObject event_1 = new JSONObject();
		event_1.put("unique_event_name", "event_1_arm_1");
		event_1.put("form", form_1);

		final JSONObject event_2 = new JSONObject();
		event_2.put("unique_event_name", "event_2_arm_1");
		event_2.put("form", form_2);

		final JSONArray event = new JSONArray();
		event.add(event_1);
		event.add(event_2);

		final JSONObject arm = new JSONObject();
		arm.put("number", "1");
		arm.put("event", event);

		data = new JSONObject();
		data.put("arm", arm);*/

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", api_token));
		params.add(new BasicNameValuePair("content", "arm"));
		params.add(new BasicNameValuePair("action", "import"));
		params.add(new BasicNameValuePair("format", "json"));
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
