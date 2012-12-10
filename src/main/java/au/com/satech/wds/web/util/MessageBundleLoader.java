package au.com.satech.wds.web.util;

import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;

public class MessageBundleLoader {

    public static final String MESSAGE_PATH = "messages";

    private static HashMap messageBundles = new HashMap();

    /**
     * Gets a string for the given key from this resource bundle or one of its
     * parents.
     *
     * @param key the key for the desired string
     * @return the string for the given key.  If the key string value is not
     *         found the key itself is returned.
     */
    public static String getMessage(String key) {
        if (key == null) {
            return null;
        }
        try {
            Locale locale =
                FacesContext.getCurrentInstance().getViewRoot().getLocale();
            if (locale == null) {
                locale = Locale.ENGLISH;
            }
            ResourceBundle messages = (ResourceBundle)
                messageBundles.get(locale.toString());
            if (messages == null) {
                messages = ResourceBundle.getBundle(MESSAGE_PATH, locale);
                messageBundles.put(locale.toString(), messages);
            }
            return messages.getString(key);
        }
        // on any failure we just return the key, which should aid in debugging.
        catch (Exception e) {
            return key;
        }
    }
}
