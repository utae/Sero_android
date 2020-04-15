package net.videofactory.new_audi.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.videofactory.new_audi.login.Splash;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by silentstorm on 15. 10. 15..
 */
public abstract class Network extends AsyncTask<String, String, String> {

    private Context context;
    private String mode;
    private String timeStamp;
    private ArrayList list;
    private SharedPreferences prefs;
    private AudiProgressDialog audiProgressDialog;

    public Network(Context context, String mode) {
        this.context = context;
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        audiProgressDialog = new AudiProgressDialog(context);
//        audiProgressDialog.show();
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setList(ArrayList list) {
        this.list = list;
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder builder = new StringBuilder();
        try {
            ObjectMapper mapperObj = new ObjectMapper();

            String json = mapperObj.writeValueAsString(list);

            String cpy = null;

//            if(UserInfo.getPubKey() != null){
//                cpy = CrytAudi.RSAEncode_Public(json, UserInfo.getPubKey());
//            }

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);

            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

            ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("mode", mode));
            nameValuePairs.add(new BasicNameValuePair("TIMESTAMP", timeStamp));

            nameValuePairs.add(new BasicNameValuePair("DATA", json));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

            HttpResponse response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jsonFactory = mapper.getFactory();
            JsonParser jsonParser = jsonFactory.createParser(result);
            JsonNode actualObj = mapper.readTree(jsonParser);
            if(actualObj.get("RTN_VAL") != null){
                if("Y".equals(actualObj.get("RTN_VAL").asText())){
                    processFinish(actualObj);
                }else if("N".equals(actualObj.get("RTN_VAL").asText())){
                    onResultN(actualObj);
                }else{
                    Toast.makeText(context, "Session is expired.", Toast.LENGTH_LONG).show();
                    prefs = context.getSharedPreferences("loginInfo", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear().apply();
                    UserInfo.clearUserInfo();
                    Intent intent = new Intent(context, Splash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Utilities.logD("Network", "Network Exception : " + e.toString());
            connectFail();
        }
        audiProgressDialog.dismiss();
    }

    protected void onResultN(JsonNode result){
        if(result.get("MSG") != null && !"".equals(result.get("MSG").asText())){
            Toast.makeText(context, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
        }
    }

    protected void connectFail(){}

    protected abstract void processFinish(JsonNode result);
}
