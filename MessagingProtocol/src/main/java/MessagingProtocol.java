public class MessagingProtocol {
    private static final char RS = 0x1E;
    private final char GS = 0x1D;


    MessagingProtocol() {

    }

    public char[] T_REGISTER_constructor(String name, String password) { //Конструируем сообщением регистрации об пользователя для сервера
        String constructor;
        constructor = "T_REGISTER" + GS + name + GS + password + RS;
        return constructor.toCharArray();
    }

    public char[] T_MESSAGE_constructor(String message, String username) { //Конструируем сообщение пользователя для сервера
        String constructor;
        constructor = "T_MESSAGE" + GS + username + GS + message + RS;
        return constructor.toCharArray();
    }

    public String lineBreak(String message) {
        message = message.replace(RS, '\n');
        return message;
    }

    public String[] parser(String message) {

        var symbolArray = message.split(String.valueOf(GS)); //Разделяем ключевые слова
        return symbolArray;
    }

}
