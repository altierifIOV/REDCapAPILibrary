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

public class ImportUserRolesJSON
{
	private final List<NameValuePair> params;
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;


	public ImportUserRolesJSON(final String api_token, final String url, final JSONArray data)
	{
/*		record = new JSONObject();
		record.put("unique_role_name", "U-527D39JXAC");
		record.put("role_label", "Project Manager");
		record.put("data_access_group", "1");
		record.put("data_export_tool", "1");
		record.put("mobile_app", "1");
		record.put("mobile_app_download_data", "1");
		record.put("lock_records_all_forms", "1");
		record.put("lock_records", "1");
		record.put("lock_records_customization", "1");
		record.put("record_delete", "1");
		record.put("record_rename", "1");
		record.put("record_create", "1");
		record.put("api_import", "1");
		record.put("api_export", "1");
		record.put("data_quality_execute", "1");
		record.put("data_quality_create", "1");
		record.put("file_repository", "1");
		record.put("logging", "1");
		record.put("data_comparison_tool", "1");
		record.put("data_import_tool", "1");
		record.put("calendar", "1");
		record.put("stats_and_charts", "1");
		record.put("reports", "1");
		record.put("user_rights", "1");
		record.put("design", "1");

		data = new JSONArray();
		data.add(record);*/

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", api_token));
		params.add(new BasicNameValuePair("content", "userRole"));
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
