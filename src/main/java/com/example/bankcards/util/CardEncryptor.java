package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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
            throw new RuntimeException("Ключ шифрования не получен из Vault");
        }
        int len = key.length();
        if (len != 16 && len != 24 && len != 32) {
            throw new RuntimeException("Неверная длина ключа для AES: " + len + ". Ожидается 16, 24 или 32 символа.");
        }
        return key.getBytes();
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
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(pan.getBytes());
            String result = Base64.getEncoder().encodeToString(encrypted);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования номера карты", e);
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
