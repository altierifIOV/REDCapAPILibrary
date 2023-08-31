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

public class RenameRecord
{
  private final List<NameValuePair> params;
  private final HttpPost post;
  private HttpResponse resp;
  private final HttpClient client;
  private int respCode;
  private BufferedReader reader;
  private final StringBuffer result;
  private String line;

  public RenameRecord(final String api_token, final String format, final String url, final String curName, final String newName)
  {
    this(api_token, format, url, curName, newName, null);
  }

  public RenameRecord(final String api_token, final String format, final String url, final String curName, final String newName, final String arm)
  {
    params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("token", api_token));
    params.add(new BasicNameValuePair("action", "rename"));
    params.add(new BasicNameValuePair("content", "record"));
/*    params.add(new BasicNameValuePair("record", "1"));
    params.add(new BasicNameValuePair("new_record_name", "record_1"));
    params.add(new BasicNameValuePair("arm", "1"));
    params.add(new BasicNameValuePair("returnFormat", "json"));*/

    params.add(new BasicNameValuePair("record", curName));
    params.add(new BasicNameValuePair("new_record_name", newName));
    params.add(new BasicNameValuePair("arm", arm));
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
        while ((line = reader.readLine()) != null)
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