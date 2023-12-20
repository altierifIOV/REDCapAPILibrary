package it.ioveneto.redcap.api;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public abstract class APIRequest {

    protected final HttpPost post;
    private final HttpClient client;
    private final StringBuffer result;
    private boolean debugMode;
    private HttpResponse resp;
    private int respCode;
    private BufferedReader reader;
    private String line;

    public APIRequest(final String url, final boolean debugMode) {

        if (url == null)
            throw new IllegalArgumentException("must provide valid URL of REDCap API endpoint");

        post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        this.debugMode = debugMode;
        result = new StringBuffer();
        client = HttpClientBuilder.create().build();
        respCode = -1;
        reader = null;
        line = null;
    }

    public void setParams(List<NameValuePair> params){
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (final Exception e) {
            throw new APICallException("Error in settings params call - "+e.getMessage(), e.getCause());
        }
    }

    /**
     * Performs the Http request and handles results. Prints HTTP response and result on standard output if debug mode
     * is active
     */
    public void doPost() throws APICallException {
        resp = null;

        try {
            resp = client.execute(post);
        } catch (final Exception e) {
            throw new APICallException("Failure in API invocation execution - "+e.getMessage(), e.getCause());
        }

        if (resp != null) {
            respCode = resp.getStatusLine().getStatusCode();
            try {
                reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            } catch (final Exception e) {
                throw new APICallException("Failure in API invocation execution - "+e.getMessage(), e.getCause());
            }
        }

        if (reader != null) {
            try {
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (final Exception e) {
                throw new APICallException("Failure in API invocation execution - "+e.getMessage(), e.getCause());
            }
        }
        if (debugMode) {
            System.out.println("respCode: " + respCode);
            System.out.println("result: " + result.toString());
        }

        if (respCode != 200)
            throw new APICallException("Call failed with HTTP code "+respCode+": "+resp.getStatusLine().getReasonPhrase()+
                    " - result: "+result.toString());

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
