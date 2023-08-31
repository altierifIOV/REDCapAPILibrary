package it.ioveneto.redcap.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ImportFile
{
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;
	private final HttpEntity httpEntity;
	private final MultipartEntityBuilder multipartEntityBuilder;

	public ImportFile(final String api_token, final String url, final File file, final String recordID, final String eventName)
	{
		//file = new File("/tmp/example.png");

		multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.addBinaryBody("file", file, ContentType.create("application/octet-stream"), file.getName());
		multipartEntityBuilder.addTextBody("token", api_token);
		multipartEntityBuilder.addTextBody("content", "file");
		multipartEntityBuilder.addTextBody("action", "import");

		/*
		params.add(new BasicNameValuePair("event", "event_1_arm_1"));
		params.add(new BasicNameValuePair("record", "f21a3ffd37fc0b3c"));*/

		multipartEntityBuilder.addTextBody("record", recordID);
		multipartEntityBuilder.addTextBody("field", "file_upload");
		multipartEntityBuilder.addTextBody("event", eventName);

		httpEntity = multipartEntityBuilder.build();

		post = new HttpPost(url);

		try
		{
			post.setEntity(httpEntity);
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
