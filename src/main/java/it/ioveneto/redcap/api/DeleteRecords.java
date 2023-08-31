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

public class DeleteRecords
{
  private final List<NameValuePair> params;
  private final HttpPost post;
  private HttpResponse resp;
  private final HttpClient client;
  private int respCode;
  private BufferedReader reader;
  private final StringBuffer result;
  private String line;

  public DeleteRecords(final String api_token, final String url, final String[] recordsID, final String arm, final String instrument, final String event)
  {
    params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("token", api_token));
    params.add(new BasicNameValuePair("action", "delete"));
    params.add(new BasicNameValuePair("content", "record"));

    for (int i = 0; i < recordsID.length; i++) {
      params.add(new BasicNameValuePair("records["+i+"]", recordsID[i]));
    }

    params.add(new BasicNameValuePair("arm", arm));
    params.add(new BasicNameValuePair("instrument", instrument));
    params.add(new BasicNameValuePair("event", event));

/*    ArrayList<String> array = new ArrayList<String>();
    array.add("1");
    params.add(new BasicNameValuePair("records", array.get(0)));
    params.add(new BasicNameValuePair("arm", "1"));
    params.add(new BasicNameValuePair("instrument", "demographics"));
    params.add(new BasicNameValuePair("event", "visit_1_arm_1"));*/

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