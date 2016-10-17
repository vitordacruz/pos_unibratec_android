package br.com.aprendendo.vitor.marvel.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by vitor on 18/09/2016.
 */
public class MD5 {

    public static final String PUBLIC_KEY =  "269bd1df839d8bf57acf0b160240e909";

    private static final String PRIVATE_KEY = "18773eb384fca47affddb502299bc58762e683cb";


    public static String gerarMD5(String texto) {

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.update(texto.getBytes(), 0, texto.length());

            String md5 = new BigInteger(1, messageDigest.digest()).toString(16);

            return md5;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String md5Marvel(long timestamp) {

        String texto = String.valueOf(timestamp) + PRIVATE_KEY + PUBLIC_KEY;

        return gerarMD5(texto);
    }

    public static String md5Marvel(Date timestamp) {

        long ts = timestamp.getTime();

        return md5Marvel(ts);

    }


}
