package com.example.bankcards.util;

public class MailUtils {
/**
 * Утилитарный класс, содержащий константы для отправки электронных писем.
 * Включает заголовки, HTML-шаблоны и плейсхолдеры.
 */

    /**
     * Приватный конструктор для предотвращения инстанцирования.
     */
    private MailUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Заголовок письма для верификации аккаунта.
     */
    public static final String ACCOUNT_VERIFY_HEADER = "Верификация аккаунта BankCards";

    /**
     * Заголовок письма для смены пароля.
     */
    public static final String ACCOUNT_CHANGE_PASSWORD_HEADER = "Изменение пароля аккаунта Shop";

    /**
     * HTML-шаблон письма для верификации аккаунта.
     * Содержит плейсхолдер {@link #LINK} для вставки ссылки.
     */
    public static final String ACCOUNT_VERIFY_TEMPLATE = " <div>\n" +
            "            <h3>Здраствуйте!</h3>\n" +
            "            <p style=\"font-size: 20px;\">Ссылка для подтверждения аккаунта</p>\n" +
            "                    \n" +
            "            <p style=\"font-size: 20px;\">@LINK@</p>\n" +
            "                     \n" +
            "            </div>";

    /**
     * HTML-шаблон письма для смены пароля.
     * Содержит плейсхолдер {@link #LINK} для вставки ссылки.
     */
    public static final String CHANGE_PASSWORD_TEMPLATE = "     <div>\n" +
            "            <h3>Здраствуйте!</h3>\n" +
            "            <p style=\"font-size: 20px;\">Ссылка для смены пароля:</p>\n" +
            "                    \n" +
            "            <p style=\"font-size: 20px;\">@LINK@</p>\n" +
            "                     \n" +
            "            </div>";

    /**
     * Плейсхолдер для вставки URL-ссылки в шаблоны писем.
     */
    public static final String LINK = "@LINK@";


}
