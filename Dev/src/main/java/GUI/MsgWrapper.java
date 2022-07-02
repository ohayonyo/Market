package GUI;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MsgWrapper {

    private ResourceBundle messages;

    public MsgWrapper(String language) {
        Locale locale;
        if (language != null){
            locale = new Locale(language);
        }
        else{
            //default
            locale = Locale.ENGLISH;
        }
        //this.messages = ResourceBundle.getBundle("localization/messages", locale);
    }

    public String get(String message) {
        return messages.getString(message);
    }

//    public final String get(final String key, final Object... args) {
//        return MessageFormat.format(get(key), args);
//    }


}
