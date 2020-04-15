package net.videofactory.new_audi.common;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Utae on 2015-11-06.
 */
public class UserInfo {

    static private HashMap<String, Object> userInfo = new HashMap<>();

//    static public void setNickName(String nickName){
//        userInfo.put("nickName", nickName);
//    }
//
//    static public String getNickName(){
//        if(userInfo.get("nickName") == null){
//            return null;
//        }else{
//            return userInfo.get("nickName").toString();
//        }
//    }
//
//    //Gender
//    //0 : male, 1 : femail
//
//    static public void setGender(int gender){
//        userInfo.put("gender", gender);
//    }
//
//    static public int getGender(){
//        if(userInfo.get("gender") == null){
//            return -1;
//        }else{
//            return (int)userInfo.get("gender");
//        }
//    }
//
//    static public void setUserName(String userName){
//        userInfo.put("userName", userName);
//    }
//
//    static public String getUserName(){
//        if(userInfo.get("userName") == null){
//            return null;
//        }else{
//            return userInfo.get("userName").toString();
//        }
//    }
//
//    static public void setUserEmail(String email){
//        userInfo.put("email", email);
//    }
//
//    static public String getUserEmail(){
//        if(userInfo.get("email") == null){
//            return null;
//        }else{
//            return userInfo.get("email").toString();
//        }
//    }
//
//    static public void setConnectKey(String connectKey){
//        userInfo.put("connectKey", connectKey);
//    }
//
//    static public String getConnectKey(){
//        if(userInfo.get("connectKey") == null){
//            return null;
//        }else{
//            return userInfo.get("connectKey").toString();
//        }
//    }
//
//    static public void setPubKey(PublicKey pubKey){
//        userInfo.put("pubKey", pubKey);
//    }
//
//    static public PublicKey getPubKey(){
//        if(userInfo.get("pubKey") == null){
//            return null;
//        }else{
//            return (PublicKey) userInfo.get("pubKey");
//        }
//    }

    static public String getUserAndroidId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    static public void setUserNum(String userNum){
        userInfo.put("userNum", userNum);
    }

    static public String getUserNum(){
        if(userInfo.get("userNum") == null){
            return null;
        }else{
            return userInfo.get("userNum").toString();
        }
    }

    static public void setSessAuthKey(String sessAuthKey){
        userInfo.put("sessAuthKey", sessAuthKey);
    }

    static public String getSessAuthKey(){
        if(userInfo.get("sessAuthKey") == null){
            return null;
        }else{
            return userInfo.get("sessAuthKey").toString();
        }
    }

    static public boolean isNull(){
        return getSessAuthKey() == null || getUserNum() == null;
    }

    static public void clearUserInfo(){
        userInfo.clear();
    }
}
