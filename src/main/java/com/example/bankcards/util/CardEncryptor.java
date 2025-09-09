package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CardEncryptor {

    private static final String ALGORITHM = "AES";

    private final VaultService vaultService;

    @Autowired
    public CardEncryptor(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    private byte[] getKey() {
        String key = vaultService.getEncryptionKey();
        if (key == null) {
            throw new RuntimeException("Ключ шифрования не получен из Vault");
        }
        int len = key.length();
        if (len != 16 && len != 24 && len != 32) {
            throw new RuntimeException("Неверная длина ключа для AES: " + len + ". Ожидается 16, 24 или 32 символа.");
        }
        return key.getBytes();
    }

    public String encrypt(String pan) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(pan.getBytes());
            String result = Base64.getEncoder().encodeToString(encrypted);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования номера карты", e);
        }
    }

    public String decrypt(String encryptedPan) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedPan);
            String result = new String(cipher.doFinal(decoded));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка дешифрования номера карты", e);
        }
    }
}
