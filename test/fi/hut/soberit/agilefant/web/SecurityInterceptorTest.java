package fi.hut.soberit.agilefant.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    
    public static void main(String[] args) {
        try{
            testSetAdmin();
            testSetTeam();
            testSetAccessRights();
            testGetIteration();
            testChangeIteration();
        } catch(Exception e){
            e.printStackTrace();
        }
    }    
    private static void testSetAdmin() throws ClientProtocolException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        DefaultHttpClient uhttpclient = new DefaultHttpClient();
        
        HttpPost httpost = new HttpPost("http://localhost:8080/agilefant/j_spring_security_check");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", "test"));
        nvps.add(new BasicNameValuePair("j_password", "test"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(httpost);

        System.out.println("Login form status: " + response.getStatusLine());
        CookieStore cookies = httpclient.getCookieStore();
        
        HttpPost uhttpost = new HttpPost("http://localhost:8080/agilefant/ajax/storeUser.action");
        
        List <NameValuePair> unvps = new ArrayList <NameValuePair>();
        unvps.add(new BasicNameValuePair("userId", "50"));
        unvps.add(new BasicNameValuePair("user.admin", "true"));
        
        uhttpost.setEntity(new UrlEncodedFormEntity(unvps, HTTP.UTF_8));
              
        uhttpclient.setCookieStore(cookies);
        HttpResponse response2 = uhttpclient.execute(uhttpost);
        
        System.out.println("Change admin status: " + response2.getStatusLine());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
        String line = null;
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
        
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
        uhttpclient.getConnectionManager().shutdown();   
    }
    
    private static void testSetTeam() throws ClientProtocolException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        DefaultHttpClient uhttpclient = new DefaultHttpClient();
        
        HttpPost httpost = new HttpPost("http://localhost:8080/agilefant/j_spring_security_check");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", "test"));
        nvps.add(new BasicNameValuePair("j_password", "test"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(httpost);

        System.out.println("Login form status: " + response.getStatusLine());
        CookieStore cookies = httpclient.getCookieStore();
        
        HttpPost uhttpost = new HttpPost("http://localhost:8080/agilefant/ajax/storeUser.action");
        
        List <NameValuePair> unvps = new ArrayList <NameValuePair>();
        unvps.add(new BasicNameValuePair("userId", "50"));
        unvps.add(new BasicNameValuePair("teamIds", "3"));
        unvps.add(new BasicNameValuePair("teamsChanged", "true"));
        
        uhttpost.setEntity(new UrlEncodedFormEntity(unvps, HTTP.UTF_8));
              
        uhttpclient.setCookieStore(cookies);
        HttpResponse response2 = uhttpclient.execute(uhttpost);
        
        System.out.println("Change admin status: " + response2.getStatusLine());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
        String line = null;
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
        
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
        uhttpclient.getConnectionManager().shutdown();   
    }
    
    private static void testSetAccessRights() throws ClientProtocolException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        DefaultHttpClient uhttpclient = new DefaultHttpClient();
        
        HttpPost httpost = new HttpPost("http://localhost:8080/agilefant/j_spring_security_check");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", "test"));
        nvps.add(new BasicNameValuePair("j_password", "test"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(httpost);

        System.out.println("Login form status: " + response.getStatusLine());
        CookieStore cookies = httpclient.getCookieStore();
        
        HttpPost uhttpost = new HttpPost("http://localhost:8080/agilefant/ajax/storeProduct.action");
        
        List <NameValuePair> unvps = new ArrayList <NameValuePair>();
        unvps.add(new BasicNameValuePair("productId", "1"));
        unvps.add(new BasicNameValuePair("teamIds", "6"));
        unvps.add(new BasicNameValuePair("teamIds", "3"));
        unvps.add(new BasicNameValuePair("teamsChanged", "true"));
        
        uhttpost.setEntity(new UrlEncodedFormEntity(unvps, HTTP.UTF_8));
              
        uhttpclient.setCookieStore(cookies);
        HttpResponse response2 = uhttpclient.execute(uhttpost);
        
        System.out.println("Change admin status: " + response2.getStatusLine());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
        String line = null;
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
        
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
        uhttpclient.getConnectionManager().shutdown();   
    }
    
    private static void testChangeIteration() throws ClientProtocolException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        DefaultHttpClient uhttpclient = new DefaultHttpClient();
        
        HttpPost httpost = new HttpPost("http://localhost:8080/agilefant/j_spring_security_check");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", "test"));
        nvps.add(new BasicNameValuePair("j_password", "test"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(httpost);

        System.out.println("Login form status: " + response.getStatusLine());
        CookieStore cookies = httpclient.getCookieStore();
        
        HttpPost uhttpost = new HttpPost("http://localhost:8080/agilefant/ajax/storeIteration.action");
        
        List <NameValuePair> unvps = new ArrayList <NameValuePair>();
        unvps.add(new BasicNameValuePair("iterationId", "33"));
        unvps.add(new BasicNameValuePair("iteration.backlogSize", "75"));
        
        uhttpost.setEntity(new UrlEncodedFormEntity(unvps, HTTP.UTF_8));
              
        uhttpclient.setCookieStore(cookies);
        HttpResponse response2 = uhttpclient.execute(uhttpost);
        
        System.out.println("Change iteration size status: " + response2.getStatusLine());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
        String line = null;
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
        
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
        uhttpclient.getConnectionManager().shutdown();   
    }

    private static void testGetIteration() throws ClientProtocolException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        DefaultHttpClient uhttpclient = new DefaultHttpClient();
        
        HttpPost httpost = new HttpPost("http://localhost:8080/agilefant/j_spring_security_check");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", "test"));
        nvps.add(new BasicNameValuePair("j_password", "test"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(httpost);

        System.out.println("Login form status: " + response.getStatusLine());
        CookieStore cookies = httpclient.getCookieStore();
        
        HttpPost uhttpost = new HttpPost("http://localhost:8080/agilefant/ajax/iterationData.action");
        
        List <NameValuePair> unvps = new ArrayList <NameValuePair>();
        unvps.add(new BasicNameValuePair("iterationId", "33"));
        
        uhttpost.setEntity(new UrlEncodedFormEntity(unvps, HTTP.UTF_8));
              
        uhttpclient.setCookieStore(cookies);
        HttpResponse response2 = uhttpclient.execute(uhttpost);
        
        System.out.println("Get iteration status: " + response2.getStatusLine());
        
        BufferedReader in = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
        String line = null;
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
        
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
        uhttpclient.getConnectionManager().shutdown();   
    }
}
