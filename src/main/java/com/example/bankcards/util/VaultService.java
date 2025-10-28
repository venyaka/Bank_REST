package com.example.bankcards.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Получает ключ шифрования из Vault.
     * Результат кешируется после первого успешного получения для уменьшения количества запросов.
     *
     * @return Ключ шифрования в виде строки.
     * @throws IllegalStateException если не удалось получить ключ из Vault.
     */
    public String getEncryptionKey() {
        if (cachedKey != null) {
            return cachedKey;
        }

        String url = vaultAddr + "/v1/" + secretPath;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Vault-Token", vaultToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<VaultResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, VaultResponse.class);

            if (response.getBody() != null &&
                    response.getBody().getData() != null &&
                    response.getBody().getData().getData() != null &&
                    response.getBody().getData().getData().getKey() != null) {
                String key = response.getBody().getData().getData().getKey();
                this.cachedKey = key;
                return key;
            } else {
                throw new IllegalStateException("Ответ от Vault не содержит ключ шифрования.");
            }
        } catch (RestClientException e) {
            throw new IllegalStateException("Ошибка при обращении к Vault для получения ключа шифрования.", e);
        }
    }

    /**
     * DTO для корневого объекта ответа от Vault.
     */
    @Data
    public static class VaultResponse {
        private VaultDataWrapper data;
    }

    /**
     * DTO для вложенного объекта 'data', содержащего фактические секреты.
     */
    @Data
    public static class VaultDataWrapper {
        private VaultData data;
    }

    /**
     * DTO, представляющий собой конечные данные секрета.
     * Содержит поле 'key'.
     */
    @Data
    public static class VaultData {
        private String key;
    }
}
