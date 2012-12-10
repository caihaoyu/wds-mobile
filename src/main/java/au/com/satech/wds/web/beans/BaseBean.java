/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.satech.wds.web.beans;

import au.com.satech.wds.service.AnswerManager;
import au.com.satech.wds.service.BranchItemManager;
import au.com.satech.wds.service.BranchItemRuleManager;
import au.com.satech.wds.service.ColumnchoiceManager;
import au.com.satech.wds.service.ConditionManager;
import au.com.satech.wds.service.PageBranchManager;
import au.com.satech.wds.service.PageManager;
import au.com.satech.wds.service.QuestionManager;
import au.com.satech.wds.service.ResultManager;
import au.com.satech.wds.service.ScenarioManager;
import au.com.satech.wds.service.ScenarioccManager;
import au.com.satech.wds.service.SubquestionManager;
import au.com.satech.wds.service.SurveyManager;
import au.com.satech.wds.web.util.MessageBundleLoader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sam
 */
public abstract class BaseBean implements Serializable {
    //Use liferay bridged log factory instead of the default one
//    protected transient final Logger log = LoggerFactory.getLogger(getClass());
//    protected transient final Logger log = LoggerFactory.getLogger(getClass());
//    protected final Log log = LogFactory.getLog(getClass());
    
    protected Map<String, String> destinations = new HashMap<String, String>();
    
    /**
     * 初始化目标地址的PortletURL链接，并存放到MAP
     *
     * @param dest 需要跳转的目标地址如："/admin/editQuestion.xhtml"
     */
    protected void initDestination(String dest){
//        RenderResponse actionResponse = (RenderResponse) getFacesContext().getExternalContext().getResponse();
//        PortletURL destinationURL = actionResponse.createActionURL();
//        destinationURL.setParameter("_facesViewIdRender", dest);
//        String destination = destinationURL.toString();
//        StringBuilder sb = new StringBuilder(destination);
//        sb.append("&");
//        sb.append("previousView");
//        sb.append("=");
//        sb.append(getFacesContext().getViewRoot().getViewId());
//        destinations.put(dest, sb.toString());
    }
    
    
    /**
     * 设置Map中相应ActionURL的参数。
     *
     * @param dest 需要跳转的目标地址如："/admin/editQuestion.xhtml"
     * @param key   需要设置的参数的变量名称。
     * @param value 参数的值String
     */
    protected void setParameter(String dest, String key, String value){
        String destination = destinations.get(dest);
        StringBuilder sb = new StringBuilder(destination);
        sb.append("&");
        sb.append(key);
        sb.append("=");
        sb.append(value);
        destinations.put(dest, sb.toString());
    }
    
    // Evaluate Spring service beans from context 
    protected QuestionManager evaluateQuestionManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.QUESTION_MANAGER, QuestionManager.class);
    }
    
    protected AnswerManager evaluateAnswerManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.ANSWER_MANAGER, AnswerManager.class);
    }
    
    protected SubquestionManager evaluateSubquestionManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.SUBQUESTION_MANAGER, SubquestionManager.class);
    }
    
    protected ColumnchoiceManager evaluateColumnchoiceManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.COLUMNCHOICE_MANAGER, ColumnchoiceManager.class);
    }
    
    protected SurveyManager evaluateSurveyManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.SURVEY_MANAGER, SurveyManager.class);
    }
    
    protected PageManager evaluatePageManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.PAGE_MANAGER, PageManager.class);
    }
    
    protected PageBranchManager evaluatePageBranchManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.PAGEBRANCH_MANAGER, PageBranchManager.class);
    }
    
    protected BranchItemManager evaluateBranchItemManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.BRANCHITEM_MANAGER, BranchItemManager.class);
    }
    
    protected BranchItemRuleManager evaluateBranchItemRuleManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.BRANCHITEMRULE_MANAGER, BranchItemRuleManager.class);
    }
    
    protected ConditionManager evaluateConditionManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.CONAIDITON_MANAGER, ConditionManager.class);
    }
    
    protected ResultManager evaluateResultManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.RESULT_MANAGER, ResultManager.class);
    }
    
    protected ScenarioManager evaluateScenarioManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.SCENARIO_MANAGER, ScenarioManager.class);
    }
    protected ScenarioccManager evaluateScenarioccManager(){
        return getFacesContext().getApplication().evaluateExpressionGet(getFacesContext(), ServiceBeanNameConstant.SCENARIOCC_MANAGER, ScenarioccManager.class);
    }
     /**
     * Utility method for building SelectItem[] from a series of
     * localised entries in a MessageBundle. The SelectItem value
     * is the MessageBundle key, and the label is the localised
     * MessageBundle value.
     *
     * @param prefix Beginning part of the MessageBundle key
     * @param suffix Ending part of the MessageBundle key
     * @param first First number of the series
     * @param last Lat number of the series
     */
    protected static SelectItem[] buildSelectItemArray(
            String prefix, String suffix, int first, int last) {
        int num = last - first + 1;
        SelectItem[] ret = new SelectItem[num];
        for(int i = 0; i < num; i++) {
            String key = prefix + Integer.toString(first+i) + suffix;
            ret[i] = buildSelectItem(key);
        }
        return ret;
    }
    
    protected static FacesContext getFacesContext(){
        return FacesContext.getCurrentInstance();
    }
    
    protected static UIViewRoot getViewRoot(){
        return getFacesContext().getViewRoot();
    }
    
    protected static ExternalContext getExternalContext(){
        return getFacesContext().getExternalContext();
    }
    
    protected static HttpServletRequest getHttpServletRequest(){
//        PortletRequest portletRequest = (PortletRequest) getExternalContext().getRequest();
        return (HttpServletRequest)getFacesContext().getExternalContext().getRequest();
    }
    
    protected static SelectItem buildSelectItem(String key) {
        return new SelectItem(
                key, MessageBundleLoader.getMessage(key));
    }
    
    public UIComponent findComponent(UIComponent c, String id) {
        if (id.equals(c.getId())) {
          return c;
        }
        Iterator<UIComponent> kids = c.getFacetsAndChildren();
        while (kids.hasNext()) {
          UIComponent found = findComponent(kids.next(), id);
          if (found != null) {
            return found;
          }
        }
        return null;
      }
}
