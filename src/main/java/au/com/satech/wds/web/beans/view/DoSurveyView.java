
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.satech.wds.web.beans.view;

import au.com.satech.wds.model.Survey;
import au.com.satech.wds.web.beans.BaseBean;
import au.com.satech.wds.web.util.UserUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.Flash;
import org.primefaces.component.datalist.DataList;

/**
 *
 * @author sam
 */
@ManagedBean(name = "doSurveyView")
@ViewScoped
public class DoSurveyView extends BaseBean implements Serializable {

    @PostConstruct
    public void setup() {
        List<Survey> allSurvey = evaluateSurveyManager().getAll();
        for(Survey survey:allSurvey){
            if(survey.getActive()){
                surveyList.add(survey);
            }
        }
        UserUtil.getUser(10195);
    }

    @PreDestroy
    public void destroy() {
    }

    private List<Survey> surveyList = new ArrayList<Survey>();
    private Integer sid;

    public void preRenderView() 
    {
//        LiferayFacesContext context = LiferayFacesContext.getInstance();
//        if (context.getRequestAttribute("ERROR") != null) 
//        {
//            String errorMsg = context.getRequestAttribute("ERROR").toString();
//            FacesMessage fm = new FacesMessage("ERROR: ", errorMsg);
//            fm.setSeverity(FacesMessage.SEVERITY_ERROR);
//            context.addMessage("ERROR", fm);
//        }
    }
    public String doSurvey(){
        Survey survey= (Survey)getSurveyDataList().getRowData();
        Integer sid=survey.getSid();
        Flash flash= getFacesContext().getExternalContext().getFlash();
        flash.put("sid", sid);
        return "/wds/doSurvey?faces-redirect=true";
    }

    
    
    /**
     * @return the surveyListTable
     */
    public DataList getSurveyDataList() {
        return (DataList) findComponent(getViewRoot(), "surveyList");
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    /**
     * @return the surveyList
     */
    public List<Survey> getSurveyList() {
        return surveyList;
    }

    /**
     * @param surveyList the surveyList to set
     */
    public void setSurveyList(List<Survey> surveyList) {
        this.surveyList = surveyList;
    }
    
}      
