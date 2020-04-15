package net.videofactory.new_audi.push;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import net.videofactory.new_audi.common.Utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Utae on 2016-10-19.
 */

public class AudiFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "AudiFirebaseIIDService";


    // 어플이 처음 실행될 때 자동적으로 실행되는 메소드, 토큰을 등록하는데 사용한다.
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Utilities.logD(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    // Server에 생성된 토큰을 등록하기 위해 보낼 때 사용하는 메소드
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Utilities.logD(TAG, "new token: " + token);

        // HttpURLConnection 을 사용하여 보내는 방법
        HttpURLConnection connection;

        try {
            URL url = new URL("보낼 서버의 주소 (.php)");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true); //서버에 데이터 보낼 때, Post의 경우 꼭 사용
            connection.setDoInput(true); //서버에서 데이터 가져올 때
            connection.setRequestMethod("POST"); // POST방식을

            StringBuffer buffer = new StringBuffer();
            buffer = buffer.append("Token").append("=").append(token);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(buffer.toString());
            wr.flush(); // 서버에 작성
            wr.close(); // 객체를 닫음

            // 서버에서 값을 받아오지 않더라도 작성해야함
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            connection.disconnect();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
