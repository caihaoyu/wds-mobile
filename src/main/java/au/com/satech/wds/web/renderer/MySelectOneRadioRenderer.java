/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.satech.wds.web.renderer;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.mobile.renderkit.SelectOneRadioRenderer;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.util.HTML;

/**
 *
 * @author cai
 */
public class MySelectOneRadioRenderer extends SelectOneRadioRenderer{
    @Override
    protected void encodeMarkup(FacesContext context, SelectOneRadio radio) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = radio.getClientId(context);
        String style = radio.getStyle();
        String styleClass = radio.getStyleClass();
        Integer index=(Integer)radio.getAttributes().get("index");
        if(index%2==0 ){
            styleClass="twoClass";
        }else{
            styleClass="oneClass";
        }
      
        List<SelectItem> selectItems = getSelectItems(context, radio);
        
      
            writer.startElement("tr", radio);
            writer.writeAttribute("id", clientId, "id");
            writer.writeAttribute("class", styleClass, "styleClass");
            if(style != null) {
                writer.writeAttribute("style", style, "style");
            }
            writer.writeAttribute("data-role", "controlgroup", null);
            encodeOptionTd(context, radio);
            encodeSelectItems(context, radio, selectItems);

            writer.endElement("tr");
        
    }
     @Override
    protected void encodeSelectItems(FacesContext context, SelectOneRadio radio, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = radio.getConverter();
        String name = radio.getClientId(context);
        Object value = radio.getSubmittedValue();
        if(value == null) {
            value = radio.getValue();
        }
        Class type = value == null ? String.class : value.getClass();
        
        int idx = -1;
        for(SelectItem selectItem : selectItems) {
            idx++;
            boolean disabled = selectItem.isDisabled() || radio.isDisabled();
            String id = name + UINamingContainer.getSeparatorChar(context) + idx;
            Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
            boolean selected = (coercedItemValue != null) && coercedItemValue.equals(value);
            encodeOption(context, radio, selectItem, id, name, converter, selected, disabled);

           
        }
         
    }
      @Override
     protected void encodeOption(FacesContext context, SelectOneRadio radio, SelectItem option, String id, String name, Converter converter, boolean selected, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, radio, converter, option.getValue());
       
        writer.startElement("td", null);
        writer.writeAttribute("align", "center", null);
        writer.writeAttribute("class", "MatrixRadioClass", "class");

       
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", itemValueAsString, null);

        if(MobileUtils.isMini(context)) writer.writeAttribute("data-mini", "true", null);
        if(selected) writer.writeAttribute("checked", "checked", null);
        if(disabled) writer.writeAttribute("disabled", "disabled", null);
        if(radio.getOnchange() != null) writer.writeAttribute("onchange", radio.getOnchange(), null);
         writer.endElement("input");
        writer.startElement("label", null);
        
        writer.writeAttribute("for", id, null);
        writer.endElement("label");
       
        writer.endElement("td");
    }
      protected void encodeOptionTd(FacesContext context,SelectOneRadio radio) throws IOException{
           ResponseWriter writer = context.getResponseWriter();
            writer.startElement("td", null);
            writer.writeAttribute("class", "subquestionTitle", "styleClass");
            writer.writeText(radio.getAttributes().get("label").toString(), null);
            writer.endElement("td");
     }
}
