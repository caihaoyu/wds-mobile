/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.satech.wds.web.portlet.beans.test;


import au.com.satech.wds.model.Survey;
import au.com.satech.wds.service.SurveyManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author cai
 */
@ManagedBean(name="testViewBean")
@ViewScoped
public class TestViewBean {
    @ManagedProperty(value="#{surveyManager}")
    private SurveyManager surveyManager;
    
    private List<Survey> surveys=new ArrayList<Survey>();
    @PostConstruct
    public void setup(){
        surveys=surveyManager.getAll();
    }

    public SurveyManager getSurveyManager() {
        return surveyManager;
    }

    public void setSurveyManager(SurveyManager surveyManager) {
        this.surveyManager = surveyManager;
    }

    public List<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }
    
}
