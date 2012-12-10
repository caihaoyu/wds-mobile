/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.satech.wds.web.util;

import com.liferay.client.soap.portal.model.UserSoap;
import com.liferay.client.soap.portal.service.http.Portal_UserServiceSoapBindingStub;
import com.liferay.client.soap.portal.service.http.UserServiceSoap;
import com.liferay.client.soap.portal.service.http.UserServiceSoapServiceLocator;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author cai
 */
public class UserUtil {

    public static UserSoap getUser(long userId) {
        UserSoap user = null;
        Properties prop = new Properties();
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext eContext = context.getExternalContext();
            InputStream in = eContext.getResourceAsStream("/WEB-INF/WebService.properties");
            prop.load(in);
            String url = prop.getProperty("RootSiteUrl");
            UserServiceSoapServiceLocator locatorUser =
                    new UserServiceSoapServiceLocator();
            UserServiceSoap serviceUser = locatorUser.getPortal_UserService(
                    getURL("Portal_UserService", url));
            ((Portal_UserServiceSoapBindingStub) serviceUser).setUsername(prop.getProperty("adminEmailAddress"));
            ((Portal_UserServiceSoapBindingStub) serviceUser).setPassword(prop.getProperty("adminPassword"));
            user = serviceUser.getUserById(userId);
            System.out.println(user.getEmailAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    private static URL getURL(String serviceName, String rootSiteUrl) throws MalformedURLException {
        String url = rootSiteUrl;
        int pos = url.indexOf("://");
        String protocol = url.substring(0, pos + 3);
        String host = url.substring(pos + 3, url.length());

        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append(host);
        sb.append("/api/secure/axis/");
        sb.append(serviceName);

        return new URL(sb.toString());
    }
}
