package fi.hut.soberit.agilefant.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class SecurityInterceptorTest{

    private static DefaultHttpClient httpclient = new DefaultHttpClient();
    private static DefaultHttpClient uhttpclient = new DefaultHttpClient();
    
    public static void main(String[] args) {
        try{
            testSetAdmin();
        } catch(Exception e){
            e.printStackTrace();
            
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
            uhttpclient.getConnectionManager().shutdown();            
        }
    }
    
    private static void testSetAdmin() throws ClientProtocolException, IOException {
        
        HttpPost httpost = new HttpPost("http://localhost:8080/agilefant/j_spring_security_check");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", "yourlogin"));
        nvps.add(new BasicNameValuePair("j_password", "yourpassword"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(httpost);

        System.out.println("Login form status: " + response.getStatusLine());
        CookieStore cookies = httpclient.getCookieStore();
        
        HttpPost uhttpost = new HttpPost("http://localhost:8080/agilefant/ajax/storeUser.action");
        
        List <NameValuePair> unvps = new ArrayList <NameValuePair>();
        unvps.add(new BasicNameValuePair("userId", "36"));
        unvps.add(new BasicNameValuePair("user.admin", "true"));
        
        uhttpost.setEntity(new UrlEncodedFormEntity(unvps, HTTP.UTF_8));
              
        uhttpclient.setCookieStore(cookies);
        HttpResponse response2 = uhttpclient.execute(uhttpost);
        
        System.out.println("Change admin status: " + response2.getStatusLine());
    }
    
    
}
