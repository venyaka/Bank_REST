package com.example.bankcards.util;

import com.example.bankcards.exception.EncryptionException;
import com.example.bankcards.exception.errors.EncryptionError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * Утилитарный класс для шифрования и дешифрования номеров банковских карт (PAN).
 * Использует симметричный алгоритм шифрования AES. Ключ для шифрования
 * получается из {@link VaultService}.
 */
@Component
public class CardEncryptor {

    /**
     * Алгоритм шифрования, режим и дополнение.
     * ECB не рекомендуется для больших объемов данных, но подходит для отдельных
     * блоков фиксированного размера, таких как номер карты.
     */
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM_NAME = "AES";

    private final VaultService vaultService;

    /**
     * Конструктор для внедрения зависимости {@link VaultService}.
     *
     * @param vaultService Сервис для получения ключа шифрования из Vault.
     */
    @Autowired
    public CardEncryptor(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    /**
     * Получает и валидирует ключ шифрования из Vault.
     *
     * @return Ключ в виде массива байт.
     * @throws EncryptionException если ключ не получен или имеет неверную длину.
     */
    private byte[] getKey() {
        String key = vaultService.getEncryptionKey();
        if (key == null) {
            throw new EncryptionException(EncryptionError.ENCRYPTION_KEY_NOT_FOUND);
        }
        int len = key.length();
        if (len != 16 && len != 24 && len != 32) {
            throw new EncryptionException(EncryptionError.INVALID_KEY_LENGTH);
        }
        return key.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Шифрует номер карты (PAN).
     *
     * @param pan Номер карты в виде строки.
     * @return Зашифрованный номер карты в формате Base64.
     * @throws EncryptionException если в процессе шифрования возникает ошибка.
     */
    public String encrypt(String pan) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), ALGORITHM_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(pan.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException(EncryptionError.ENCRYPTION_FAILED);
        }
    }

    /**
     * Дешифрует зашифрованный номер карты.
     *
     * @param encryptedPan Зашифрованный номер карты в формате Base64.
     * @return Исходный номер карты.
     * @throws EncryptionException если в процессе дешифрования возникает ошибка.
     */
    public String decrypt(String encryptedPan) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), ALGORITHM_NAME);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedPan);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new EncryptionException(EncryptionError.DECRYPTION_FAILED);
        }
    }
}
