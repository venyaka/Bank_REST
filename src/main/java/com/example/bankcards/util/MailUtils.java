package com.example.bankcards.util;

public class MailUtils {

    public static final String ACCOUNT_VERIFY_HEADER = "Верификация аккаунта BankCards";

    public static final String ACCOUNT_CHANGE_PASSWORD_HEADER = "Изменение пароля аккаунта Shop";

    public static final String ACCOUNT_VERIFY_TEMPLATE = " <div>\n" +
            "            <h3>Здраствуйте!</h3>\n" +
            "            <p style=\"font-size: 20px;\">Ссылка для подтверждения аккаунта</p>\n" +
            "                    \n" +
            "            <p style=\"font-size: 20px;\">@LINK@</p>\n" +
            "                     \n" +
            "            </div>";
    public static final String CHANGE_PASSWORD_TEMPLATE = "     <div>\n" +
            "            <h3>Здраствуйте!</h3>\n" +
            "            <p style=\"font-size: 20px;\">Ссылка для смены пароля:</p>\n" +
            "                    \n" +
            "            <p style=\"font-size: 20px;\">@LINK@</p>\n" +
            "                     \n" +
            "            </div>";
    public static final String LINK = "@LINK@";


}
