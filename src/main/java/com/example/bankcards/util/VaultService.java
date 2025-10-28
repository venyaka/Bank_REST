package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Сервис для взаимодействия с HashiCorp Vault для безопасного получения секретов.
 * В данном случае используется для получения ключа шифрования.
 */
@Service
public class VaultService {
    @Value("${VAULT_ADDR}")
    private String vaultAddr;

    @Value("${VAULT_TOKEN}")
    private String vaultToken;

    @Value("${VAULT_SECRET_PATH}")
    private String secretPath;

    private String cachedKey;

    /**
     * Получает ключ шифрования из Vault.
     * Результат кешируется после первого успешного получения для уменьшения количества запросов.
     *
     * @return Ключ шифрования в виде строки.
     * @throws IllegalStateException если не удалось получить ключ из Vault.
     */
    public String getEncryptionKey() {
        if (cachedKey != null) return cachedKey;
        String url = vaultAddr + "/v1/" + secretPath;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Vault-Token", vaultToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<VaultResponse> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, VaultResponse.class);

        String key = response.getBody().getData().getData().getKey();
        cachedKey = key;
        return key;
    }

    /**
     * DTO для корневого объекта ответа от Vault.
     */
    public static class VaultResponse {
        private VaultDataWrapper data;
        public VaultDataWrapper getData() { return data; }
        public void setData(VaultDataWrapper data) { this.data = data; }
    }

    /**
     * DTO для вложенного объекта 'data', содержащего фактические секреты.
     */
    public static class VaultDataWrapper {
        private VaultData data;
        public VaultData getData() { return data; }
        public void setData(VaultData data) { this.data = data; }
    }

    /**
     * DTO, представляющий собой конечные данные секрета.
     * Содержит поле 'key'.
     */
    public static class VaultData {
        private String key;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
    }
}
