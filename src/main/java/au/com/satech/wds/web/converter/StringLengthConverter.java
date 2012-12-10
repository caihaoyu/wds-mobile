/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.satech.wds.web.converter;

import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author sam
 */
@FacesConverter(value = "stringLengthConverter")
public class StringLengthConverter implements Converter {
    private final static Logger logger = Logger.getLogger("au.com.satech.wds");

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        String v = value.toString();
        int length = 40;
        if(component.getAttributes().get("length") != null){
            length = Integer.parseInt(component.getAttributes().get("length").toString());
        } else {
            logger.info("stringLengthConverter : Cannot find length attribute, using default length = 40 instead.");
        }
        if(v.length()>length){
            StringBuilder result = new StringBuilder();
            result.append(v.substring(0, length));
            result.append("...");
            return result.toString();
        } else {
            return v;
        }
    }

}
