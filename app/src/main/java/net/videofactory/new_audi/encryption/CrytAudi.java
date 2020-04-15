package net.videofactory.new_audi.encryption;

import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.crypto.Cipher;

/**
 * Created by Utae on 2015-12-21.
 */
public class CrytAudi {


    public static PublicKey setPublicKey(String key){
        byte[] publicByte = null;
        try {
            publicByte = Base64.decode(key, Base64.DEFAULT);
            X509EncodedKeySpec keySpe = new X509EncodedKeySpec(publicByte);
            KeyFactory keyFactor = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactor.generatePublic(keySpe);
            return pubKey;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }


    public static PublicKey getPubKey(String hexPubKey)    {
        StringTokenizer tk = new StringTokenizer(hexPubKey, " ");
        String strPubModulus = tk.nextToken();
        String strPubExponent = tk.nextToken();
        int[] intArrayPubModulus = str2int(strPubModulus, 128);
        int[] intArrayPubExponent = str2int(strPubExponent, 128);
        byte[] byteArrayPubModulus = int2byte(intArrayPubModulus);
        byte[] byteArrayPubExponent = int2byte(intArrayPubExponent);
        BigInteger bigIntPubModulus = new BigInteger(byteArrayPubModulus);
        BigInteger bigIntPubExponent = new BigInteger(byteArrayPubExponent);
        return setPublicKey(bigIntPubModulus, bigIntPubExponent);
    }

    public static PublicKey setPublicKey(BigInteger modulus, BigInteger exponent){
        byte[] publicByte = null;
        PublicKey pubKey = null;
        try {
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory keyFactor = KeyFactory.getInstance("RSA");
            pubKey = keyFactor.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private static int [] str2int(String src, int arraySize)    {
        int[] dst;
        if (arraySize > 0){
            dst = new int[arraySize];
            int noOfZero = arraySize - (src.length() / 2);
            for (int i = 0; i < noOfZero; i++) dst[i] = 0x00;
            for (int i = noOfZero; i < dst.length; i++){
                char first = src.charAt(i * 2);
                char second = src.charAt(i * 2 + 1);
                dst[i] = hex2int(first) * 16 + hex2int(second);
            }
        }
        else{
            dst = new int[src.length() / 2];
            for (int i = 0; i < dst.length; i++){
                char first = src.charAt(i * 2);
                char second = src.charAt(i * 2 + 1);
                dst[i] = hex2int(first) * 16 + hex2int(second);
            }
        }
        return dst;
    }

    private static byte[] int2byte(int[] src){
        byte[] dst = new byte[src.length];
        for (int i = 0; i < src.length; i++)
            dst[i] = (byte)src[i];
        return dst;
    }

    public static String RSADecode_PublicKey(String msg, PublicKey publicKey){
        int[] intArray = StrToInt(msg, -1);
        byte[] msgArray = intTobyte(intArray);

        try {
            Cipher cipher = Cipher.getInstance(AudiKey.ENCRYT_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            Vector arrayList = new Vector();
            int totalSize = 0;
            for (int i = 0; i < msgArray.length; i += AudiKey.ENCRYPTED_MSG_LENGTH)
            {
                byte[] temp = new byte[AudiKey.ENCRYPTED_MSG_LENGTH];
                System.arraycopy(msgArray, i, temp, 0, AudiKey.ENCRYPTED_MSG_LENGTH);
                byte[] decedMsg = cipher.doFinal(temp);
                arrayList.addElement(decedMsg);
                totalSize += decedMsg.length;
            }
            byte[] totalArray = new byte[totalSize];
            int offset = 0;
            for (int i = 0; i < arrayList.size(); i++)
            {
                byte[] tempArray = (byte[])arrayList.elementAt(i);
                System.arraycopy(tempArray, 0, totalArray, offset, tempArray.length);
                offset += tempArray.length;
            }
            return new String(totalArray);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";

    }
    private static byte[] intTobyte(int[] src){
        byte[] dst = new byte[src.length];
        for (int i = 0; i < src.length; i++)
            dst[i] = (byte)src[i];
        return dst;
    }

    private static int[] StrToInt(String src, int arraySize){
        int[] dst;
        if (arraySize > 0){
            dst = new int[arraySize];
            int noOfZero = arraySize - (src.length() / 2);
            for (int i = 0; i < noOfZero; i++) dst[i] = 0x00;
            for (int i = noOfZero; i < dst.length; i++){
                char first = src.charAt(i * 2);
                char second = src.charAt(i * 2 + 1);
                dst[i] = hex2int(first) * 16 + hex2int(second);
            }
        }else{

            dst = new int[src.length() / 2];
            for (int i = 0; i < dst.length; i++){
                char first = src.charAt(i * 2);
                char second = src.charAt(i * 2 + 1);
                dst[i] = hex2int(first) * 16 + hex2int(second);
            }
        }
        return dst;
    }

    private static int hex2int(char src){
        if(src >= '0' && src <= '9') return ((int)src - (int)'0');
        else if(src >= 'a' && src <= 'f') return ((int)src - (int)'a' + 10);
        else if(src >= 'A' && src <= 'F') return ((int)src - (int)'A' + 10);
        else throw new NumberFormatException();
    }

    public static String RSAEncode_Public(String msg, PublicKey key){
        byte[] msgArray = msg.getBytes();
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(AudiKey.ENCRYT_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            Vector arrayList = new Vector();
            int totalSize = 0;
            for (int i = 0; i < msgArray.length; i += AudiKey.MSG_BLOCK_LENGTH){
                int size = Math.min(AudiKey.MSG_BLOCK_LENGTH, msgArray.length - i);
                byte[] temp = new byte[size];
                System.arraycopy(msgArray, i, temp, 0, size);
                byte[] encedMsg = cipher.doFinal(temp);
                arrayList.addElement(encedMsg);
                totalSize += encedMsg.length;
            }
            byte[] totalArray = new byte[totalSize];
            int offset = 0;
            for (int i = 0; i < arrayList.size(); i++){
                byte[] tempArray = (byte[])arrayList.elementAt(i);
                System.arraycopy(tempArray, 0, totalArray, offset, tempArray.length);
                offset += tempArray.length;
            }
            return makeStrKey(totalArray, -1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return "";
    }
    private static String makeStrKey(byte bytes[], int arraySize){
        StringBuffer bf = new StringBuffer();
        if (arraySize > 0){
            int noOfZero = arraySize - bytes.length;
            for (int i = 0; i < noOfZero; i++) bf.append("00");
        }
        for(int i=0; i<bytes.length; i++){
            if((bytes[i] > 15) || (bytes[i] < 0))
                bf.append(java.lang.Integer.toHexString(bytes[i] & 0xff));
            else
                bf.append("0" + java.lang.Integer.toHexString(bytes[i] & 0xff));
        }
        return bf.toString();
    }
}