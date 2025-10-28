package com.example.bankcards.constant;

/**
 * Класс для хранения констант путей (endpoints) для контроллеров.
 * Централизует все пути API для удобства управления и избежания "магических строк".
 */
public class PathConstants {
    public static final String AUTHORIZE_CONTROLLER_PATH = "/authorize";
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String CARD_CONTROLLER_PATH = "/cards";
    public static final String ADMIN_CONTROLLER_PATH = "/admin";
    public static final String BLOCK_REQUEST_CONTROLLER_PATH = "/admin/cards/block-requests";
    public static final String CARD_ADMIN_CONTROLLER_PATH = "/admin/cards";
    public static final String USER_ADMIN_CONTROLLER_PATH = "/admin/users";

    private PathConstants() {
    }
}
