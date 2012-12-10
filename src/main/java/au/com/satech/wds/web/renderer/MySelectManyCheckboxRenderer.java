/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.satech.wds.web.renderer;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.mobile.renderkit.SelectManyCheckboxRenderer;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.util.HTML;

/**
 *
 * @author cai
 */
public class MySelectManyCheckboxRenderer extends SelectManyCheckboxRenderer {
     @Override
    protected void encodeOption(FacesContext context, UIInput component, Object values, Object submittedValues, Converter converter, SelectItem option, int idx) throws IOException {
          ResponseWriter writer = context.getResponseWriter();
        SelectManyCheckbox checkbox = (SelectManyCheckbox) component;
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        String name = checkbox.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || checkbox.isDisabled();
        Object valuesArray;
        Object itemValue;
        if(submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        } else {
            valuesArray = values;
            itemValue = option.getValue();
        }
        
        boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
        if(option.isNoSelectionOption() && values != null && !selected) {
            return;
        }
        
        writer.startElement("td", null);
        writer.startElement("input", null);
        writer.writeAttribute("id", id, "id");
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", itemValueAsString, null);

        if(MobileUtils.isMini(context)) writer.writeAttribute("data-mini", "true", null);
        if(selected) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(checkbox.getOnchange() != null) writer.writeAttribute("onchange", checkbox.getOnchange(), null);

        writer.endElement("input");
        
        //label
        writer.startElement("label", null);
        writer.writeAttribute("for", id, null);
        
        if(option.isEscape())
            writer.writeText(option.getLabel(),null);
        else
            writer.write(option.getLabel());
        
        writer.endElement("label");
        writer.endElement("td");

       
     }
     @Override
     protected void encodeMarkup(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
      
        writer.startElement("tr", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("data-role", "controlgroup", null);
        if(style != null) writer.writeAttribute("style", style, "style");
        if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");
        encodeOptionTd(context, checkbox);
        encodeSelectItems(context, checkbox);

        writer.endElement("tr");
    }
     protected void encodeOptionTd(FacesContext context,SelectManyCheckbox checkbox) throws IOException{
           ResponseWriter writer = context.getResponseWriter();
            writer.startElement("td", null);
            writer.writeAttribute("style", "background-color: gainsboro", "style");
            writer.writeText(checkbox.getAttributes().get("label").toString(), null);
            writer.endElement("td");
     }
     @Override
      protected void encodeSelectItems(FacesContext context, SelectManyCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, checkbox);
        Converter converter = checkbox.getConverter();
        Object values = getValues(checkbox);
        Object submittedValues = getSubmittedValues(checkbox);
        String layout = checkbox.getLayout();
        boolean horizontal = layout != null && layout.equals("lineDirection");
       
        int idx = -1;
        for(SelectItem selectItem : selectItems) {
            idx++;

            encodeOption(context, checkbox, values, submittedValues, converter, selectItem, idx);
        }
        
    }
  
}
