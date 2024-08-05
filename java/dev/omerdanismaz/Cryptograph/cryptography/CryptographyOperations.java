package dev.omerdanismaz.Cryptograph.cryptography;

import dev.omerdanismaz.Cryptograph.enums.ECryptographyError;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptographyOperations
{
    /*
        Plain Data (32)     + Encryption Key (64) = Encrypted Data (110)    -> 150
        Plain Data (64)     + Encryption Key (64) = Encrypted Data (154)    -> 200
        Plain Data (128)    + Encryption Key (64) = Encrypted Data (238)    -> 250
        Plain Data (256)    + Encryption Key (64) = Encrypted Data (410)    -> 450
        Plain Data (512)    + Encryption Key (64) = Encrypted Data (750)    -> 800
        Plain Data (1024)   + Encryption Key (64) = Encrypted Data (1434)   -> 1500
    */

    private static final String PROVIDER = "BC";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS7Padding";
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String REGEX = ":::::";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 100000;
    private static final int SALT_SIZE = 8;
    private static final int IV_SIZE = 16;

    public static String encryptData(String plainData, String encryptionKey)
    {
        try
        {
            Security.addProvider(new BouncyCastleProvider());
            byte[] salt = generateRandomBytes(SALT_SIZE);
            byte[] iv = generateRandomBytes(IV_SIZE);
            SecretKeySpec secretKeySpec = generateAESKey(salt, encryptionKey);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(plainData.getBytes(StandardCharsets.UTF_8));
            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            String encodedIV = Base64.getEncoder().encodeToString(iv);
            String encodedEncryptedBytes = Base64.getEncoder().encodeToString(encryptedBytes);
            return encodedSalt + REGEX + encodedIV + REGEX + encodedEncryptedBytes;
        }
        catch(Exception exception)
        {
            return ECryptographyError.CRYPTOGRAPHY_ERROR.name();
        }
    }

    public static String decryptData(String encryptedData, String encryptionKey)
    {
        try
        {
            Security.addProvider(new BouncyCastleProvider());
            String[] encryptedDataParts = encryptedData.split(REGEX);
            byte[] salt = Base64.getDecoder().decode(encryptedDataParts[0]);
            byte[] iv = Base64.getDecoder().decode(encryptedDataParts[1]);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedDataParts[2]);
            SecretKeySpec secretKeySpec = generateAESKey(salt, encryptionKey);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        }
        catch(Exception exception)
        {
            return ECryptographyError.CRYPTOGRAPHY_ERROR.name();
        }
    }

    private static SecretKeySpec generateAESKey(byte[] salt, String encryptionKey)
    {
        try
        {
            char[] passwordAsCharArray = encryptionKey.toCharArray();
            KeySpec keySpec = new PBEKeySpec(passwordAsCharArray, salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            byte[] keyBytes = secretKeyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, ALGORITHM);
        }
        catch(Exception exception)
        {
            throw new RuntimeException();
        }
    }

    private static byte[] generateRandomBytes(int size)
    {
        byte[] bytes = new byte[size];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
