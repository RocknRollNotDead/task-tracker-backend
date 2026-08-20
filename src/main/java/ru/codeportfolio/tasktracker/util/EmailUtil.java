package ru.codeportfolio.tasktracker.util;

public final class EmailUtil {

    public final static String HEADER = "Приветственное письмо от Task Ledger";

    private final static String TEXT_WELCOME_MAIL_MUST_BE_FORMATTED = """
            
            Приветствуем, %s!
            
            Вы зарегистрировались в нашем сервисе Task Ledger на сайте %s!
            Приятного пользования!
            """;
    private final static String DOMAIN = "codeportfolio.ru";


    private EmailUtil() {
    }



    public static String getWelcomeText(String username){
        return TEXT_WELCOME_MAIL_MUST_BE_FORMATTED.formatted(username, DOMAIN);
    }
}
