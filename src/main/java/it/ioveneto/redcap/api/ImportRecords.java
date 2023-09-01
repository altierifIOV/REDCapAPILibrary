package it.ioveneto.redcap.api;

import org.apache.commons.codec.digest.DigestUtils;
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
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ImportRecords
{
	private final List<NameValuePair> params;
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;
	private final String recordID;
	private final SecureRandom random;

	public ImportRecords(final String api_token, final String url, final JSONArray data)
	{
		random = new SecureRandom();
		recordID = DigestUtils.sha1Hex(new BigInteger(16, random).toString(16)).substring(0, 16);

/*		record = new JSONObject();
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

		data = new JSONArray();
		data.add(record);*/

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", api_token));
		params.add(new BasicNameValuePair("content", "record"));
		params.add(new BasicNameValuePair("format", "json"));
		params.add(new BasicNameValuePair("type", "flat"));
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

	public int getRespCode() {

		return respCode;
	}

	public String getResult() {

		return result.toString();
	}
}
