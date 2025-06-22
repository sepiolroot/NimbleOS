import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;

public class FileEncryptor {

    public static void main(String[] args) throws Exception {
        String filePath = "dados_sistema.txt";
        String encryptedFilePath = "dados_sistema_encrypted.dat";
        String decryptedFilePath = "dados_sistema_decrypted.txt";
        String password = "senha-forte";

        // Gerar chave a partir da senha
        SecretKey secretKey = generateKeyFromPassword(password);

        // Criptografar
        encryptFile(secretKey, filePath, encryptedFilePath);

        // Descriptografar (para teste)
        decryptFile(secretKey, encryptedFilePath, decryptedFilePath);
    }

    private static SecretKey generateKeyFromPassword(String password) throws Exception {
        byte[] key = password.getBytes("UTF-8");
        // Ajusta o tamanho da chave para 16 bytes (128 bits AES)
        byte[] keyBytes = new byte[16];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, 16));
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static void encryptFile(SecretKey key, String inputFile, String outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        processFile(cipher, inputFile, outputFile);
    }

    private static void decryptFile(SecretKey key, String inputFile, String outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        processFile(cipher, inputFile, outputFile);
    }

    private static void processFile(Cipher cipher, String inputFile, String outputFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
             
            byte[] inputBytes = new byte[64];
            int bytesRead;

            while ((bytesRead = fis.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                if (outputBytes != null) fos.write(outputBytes);
            }

            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) fos.write(outputBytes);
        }
    }
}
