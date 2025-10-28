package com.example.bankcards.constant;

/**
 * Класс для хранения констант, связанных с IP-адресами и геолокацией.
 * Содержит URL для API ipstack и параметры для запросов.
 */
public class IpAddressesConstant {

    public static final String API_IPSTACK_URL = "http://api.ipstack.com/";

    public static final String ACCESS_KEY_GET_PARAMETER = "?access_key=";

    private IpAddressesConstant() {
        // Утилитарный класс, не предназначен для инстанцирования
    }
}
