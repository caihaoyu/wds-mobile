/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.satech.wds.web.beans.view;

import au.com.satech.wds.model.BranchItem;
import au.com.satech.wds.model.PageBranch;
import au.com.satech.wds.model.PageResponse;
import au.com.satech.wds.model.SurveyResponse;
import au.com.satech.wds.model.jsf.SurveyJSFModel;
import au.com.satech.wds.model.jsf.ViolationRuleJSFModel;
import au.com.satech.wds.service.SurveyManager;
import au.com.satech.wds.web.beans.BaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 *
 * @author sam
 */
@ManagedBean(name ="surveyingView")
@ViewScoped
public class SurveyingView extends BaseBean implements Serializable {
    
    private Integer selectedSurveyId;

    private SurveyJSFModel surveyJSFModel;
    
    private SurveyResponse surveyResponse;
    
    private boolean hasDefaultBranch;
    
    private boolean allItemsHidden = true;
    
    private ViolationRuleJSFModel selectedViolationRule;
    
    private String confirmMessage;
    
    private boolean continueBut;
    
    private boolean finishBut;
    
    private Integer selectedItemIndex;
    
    private String descriptionString;
    private String projectName;
    
    private long startTime;
    
    private BranchItem currItem;
    private Integer selectedItemId;
    private Map<Integer,BranchItem> notHiddenItemMap=new HashMap<Integer, BranchItem>();
    private List<BranchItem> notHiddenItemList=new ArrayList<BranchItem>();
    
    
    @PostConstruct
    public void setup(){
      FacesContext context=getFacesContext();
        selectedSurveyId = (Integer)context.getExternalContext().getFlash().get("sid");
        if(selectedSurveyId != null)
        {
            generateSurveyTree();
            // check violation rules
            initCurrPage();
            setRatio();
            
            setSurveyResponse(new SurveyResponse(surveyJSFModel.getSurvey()));
            getSurveyResponse().setStarttime(Calendar.getInstance().getTime());
            
            
                getSurveyResponse().setUid(Long.valueOf("0"));
          
            
            if(surveyJSFModel.getSurvey().getPageList().size() > 0)
            {
                addPageResponse();
            }
            startTime = System.currentTimeMillis();
        }
        HttpServletRequest request=(HttpServletRequest)context.getExternalContext().getRequest();
        projectName= request.getContextPath();
        initDestination("/wds/do-survey.xhtml");
    }
    public void ajax(){
        
        currItem=notHiddenItemMap.get(selectedItemId);
       
    }
    
    /**
     * 
     */
    public void preRenderView()
    {
//        if (!getFacesContext().getPartialViewContext().isAjaxRequest())
//        {
//            if (selectedSurveyId == null)
//            {
//                LiferayFacesContext context = LiferayFacesContext.getInstance();
//                try {
//                    RenderResponse actionResponse = (RenderResponse) context.getExternalContext().getResponse();
//                    PortletURL actionURL = actionResponse.createActionURL();
//                    actionURL.setParameter("_facesViewIdRender", "/wds/do-survey.xhtml");
//                    context.getExternalContext().redirect(actionURL.toString());
//                    context.setRequestAttribute("ERROR", "SurveyId is null");
//                    context.responseComplete();
//                } catch (Exception ex) {
//                    log.error(ex);
//                }
//            }
//        }
    }
    
    /**
     * 
     */
    private void generateSurveyTree()
    {
        surveyJSFModel = new SurveyJSFModel();
        surveyJSFModel.setSurvey(evaluateSurveyManager().generateSurveyTree(selectedSurveyId));
    }
    
    /**
     * Initialize current page.
     */
    private void initCurrPage()
    {
        if(surveyJSFModel.getSurvey().getPageList() != null && surveyJSFModel.getSurvey().getPageList().size() > 0)
        {
            for(; surveyJSFModel.getCurrentPageIndex() < surveyJSFModel.getSurvey().getPageList().size();  surveyJSFModel.setCurrentPageIndex(surveyJSFModel.getCurrentPageIndex() + 1))
            {
                surveyJSFModel.setCurrPage(surveyJSFModel.getSurvey().getPageList().get(surveyJSFModel.getCurrentPageIndex()));
                if(surveyJSFModel.getCurrPage() != null)
                {
                    break;
                }
            }

            surveyJSFModel.setAnsweringIndex(-1);
            notHiddenItemList.clear();
            notHiddenItemMap.clear();
            for(PageBranch pageBranch : surveyJSFModel.getCurrPage().getPageBranchList())
            {
                if(pageBranch.getMain())
                {
                    setHasDefaultBranch(pageBranch.getMain());
                    evaluateSurveyManager().setItemIndex(pageBranch.getItemList());
                    for(int i = 0; i < pageBranch.getItemList().size(); i ++)
                    {
                        BranchItem branchitem = (BranchItem)pageBranch.getItemList().get(i);
                        if(!branchitem.isHidden() && branchitem.getQuestion().getAnswered() == 0)
                        {
                            setAllItemsHidden(false);
                            surveyJSFModel.setAnsweringIndex(i);
                            break;
                        }
                    }
                    if(pageBranch.getItemList() != null && pageBranch.getItemList().size() > 0 && surveyJSFModel.getAnsweringIndex() != -1)
                    {
                        pageBranch.getItemList().get(surveyJSFModel.getAnsweringIndex()).getQuestion().setAnswering(true);
                        currItem=pageBranch.getItemList().get(surveyJSFModel.getAnsweringIndex());
                        notHiddenItemList.add(currItem);
                        notHiddenItemMap.put(currItem.getBranchitemid(), currItem);
                        for(BranchItem item : pageBranch.getItemList())
                        {
                            if(item.getBranchitemid().hashCode() != pageBranch.getItemList().get(surveyJSFModel.getAnsweringIndex()).getBranchitemid().hashCode())
                            {
                                item.getQuestion().setAnswering(false);
                            }
                        }
                    }
                    break;
                }
            }

            if(surveyJSFModel.getSurvey().getPageList().size() == 1)
            {
                surveyJSFModel.setIsFirstPage(true);
                surveyJSFModel.setIsLastPage(true);
            }
            else if(surveyJSFModel.getCurrentPageIndex() == surveyJSFModel.getSurvey().getPageList().size() - 1)
            {
                surveyJSFModel.setIsFirstPage(false);
                surveyJSFModel.setIsLastPage(true);
            }
            else if(surveyJSFModel.getCurrentPageIndex() == 0)
            {
                surveyJSFModel.setIsFirstPage(true);
                surveyJSFModel.setIsLastPage(false);
            }
            else
            {
                surveyJSFModel.setIsFirstPage(false);
                surveyJSFModel.setIsLastPage(false);
            }
            surveyJSFModel.setCurrPageAllFinished(evaluateSurveyManager().checkCurrPageAllFinished(surveyJSFModel));
        }
        else
        {
            surveyJSFModel.setCurrPage(null);
        }
    }
    
    /**
     * 
     * @return 
     */
    public void getNextPage(Integer origin)
    {
        if(origin == 1)
        {
            if(!surveyJSFModel.isCurrPageAllFinished())
            {
                setContinueBut(false);
                setFinishBut(false);
                showDialog("There is question(s) has not been answered, please press 'CONTINUE' button after answered all question(s).", "confirmGoToNextPage.show()");
                return;
            }
        }
        
        surveyJSFModel.setCurrentPageIndex(surveyJSFModel.getCurrentPageIndex() + 1);
        surveyJSFModel.setAnsweringIndex(0);
        surveyJSFModel.setCurrPageAllFinished(false);
        initCurrPage();
        setRatio();
        addPageResponse();
        RequestContext requestContext=RequestContext.getCurrentInstance();
        requestContext.execute("confirmGoToNextPage.hide()");
    }
    
    public void finishTheSurvey()
    {
        try {
            getFacesContext().getExternalContext().redirect(destinations.get("/wds/do-survey.xhtml"));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    
    /**
     * set accomplishment ratio
     */
    private void setRatio()
    {
        evaluateSurveyManager().calculateAccomplishmentRatio(surveyJSFModel);
    }
    
    /**
     * 
     * @return 
     */
    public void saveAnswerValue()
    {   
        Integer time = (int) ((System.currentTimeMillis()-startTime)/1000);
        SurveyManager surveyManager = evaluateSurveyManager();
        DataTable dt = (DataTable)findComponent(getViewRoot(), "itemsTable");
        BranchItem selectedItem = (BranchItem)dt.getRowData();
        selectedItemIndex = surveyJSFModel.getAnsweringIndex();
        surveyJSFModel.setAnsweringItem(selectedItem);
        
        // check answer value
        if(surveyManager.isAnswerValueEmpty(selectedItem))
        {
            if(selectedItem.isMandatory())
            {
                showDialog("This question is mandatory, it has to be answered before save.", "confirmSaveAnswer.show()");
                return;
            }
            else
            {
                showDialog("Question hasn't been answered, do you want skip it ?", "confirmSaveAnswer.show()");
                return;
            }
        }

        selectedItem.getQuestion().setAnswered(1);
        selectedItem.getQuestion().setAnswering(false);
        if(surveyJSFModel.getAnsweringItem().getDuration() == null){
            surveyJSFModel.getAnsweringItem().setDuration(time);
        }else{
            surveyJSFModel.getAnsweringItem().setDuration(time + surveyJSFModel.getAnsweringItem().getDuration());
        }
        // save answer to db.
        surveyManager.saveAnswer(surveyJSFModel, surveyResponse);
        
        surveyJSFModel.setAnsweringIndex(surveyJSFModel.getAnsweringIndex() + 1);
        
        surveyManager.changeSurveywithRules(false, selectedItem, surveyJSFModel, selectedItemIndex);
        
        // find next question which is not hidden.
        for(PageBranch pageBranch : surveyJSFModel.getCurrPage().getPageBranchList())
        {
            if(pageBranch.getMain())
            {
                surveyManager.setItemIndex(pageBranch.getItemList());
                if(surveyJSFModel.getAnsweringIndex() < pageBranch.getItemList().size())
                {
                    for(int itemIndex = surveyJSFModel.getAnsweringIndex(); itemIndex < pageBranch.getItemList().size(); itemIndex ++)
                    {
                        BranchItem item = pageBranch.getItemList().get(itemIndex);
                        if(!item.isHidden() && item.getQuestion().getAnswered() == 0)
                        {
                            surveyJSFModel.setAnsweringIndex(itemIndex);
                            pageBranch.getItemList().get(itemIndex).getQuestion().setAnswering(true);
                            currItem=pageBranch.getItemList().get(itemIndex);
                            if(notHiddenItemMap.get(currItem.getBranchitemid())==null){
                            notHiddenItemList.add(currItem);
                            notHiddenItemMap.put(currItem.getBranchitemid(), currItem);
                            }
                          
                            selectedItemId=currItem.getBranchitemid();
                            break;
                        }
                    }
                }
                break;
            }
        }
        setStartTime(System.currentTimeMillis());
        setRatio();
        surveyJSFModel.setCurrPageAllFinished(surveyManager.checkCurrPageAllFinished(surveyJSFModel));
        
        if(surveyJSFModel.isCurrPageAllFinished())
        {
            if(!surveyJSFModel.isIsLastPage())
            {
                setContinueBut(true);
                setFinishBut(false);
                showDialog("All questions in this page has been answered, do you want to go next page?", "confirmGoToNextPage.show()");
            }
            else
            {
                setContinueBut(false);
                setFinishBut(true);
                showDialog("All questions of this survey has been answered, do you want to finish this survey?", "confirmGoToNextPage.show()");
            }
        }
        
    }

    /**
     * 
     * @return 
     */
    public void redoQuestion()
    {   
        setStartTime(System.currentTimeMillis());
        DataTable dt = (DataTable)findComponent(getViewRoot(), "itemsTable");
        BranchItem selectedItem = (BranchItem)dt.getRowData();
        selectedItemIndex = surveyJSFModel.getAnsweringIndex();
        
        for(PageBranch pageBranch : surveyJSFModel.getCurrPage().getPageBranchList())
        {
            if(pageBranch.getMain())
            {
                for(BranchItem branchitem : pageBranch.getItemList())
                {
                    if(branchitem.getQuestion().isAnswering())
                    {
                        branchitem.getQuestion().setAnswering(false);
                        break;
                    }
                }
                break;
            }
        }
        surveyJSFModel.setAnsweringIndex(dt.getRowIndex());
        selectedItem.getQuestion().setAnswering(true);
        surveyJSFModel.setCurrPageAllFinished(false);
        surveyJSFModel.setAnsweringItem(selectedItem);
        surveyResponse = evaluateSurveyManager().cleanAnswer(surveyJSFModel, surveyResponse);
    }
    
    /**
     * 
     * @return 
     */
    public void skipCurrentQuestion(Integer origin)
    {
        SurveyManager surveyManager = evaluateSurveyManager();
        DataTable dt = null;
        BranchItem selectedItem = null;
        if(origin == 1)
        {
            dt = (DataTable)findComponent(getViewRoot(), "itemsTable");
            selectedItem = (BranchItem)dt.getRowData();
            surveyJSFModel.setAnsweringItem(selectedItem);
            selectedItemIndex = surveyJSFModel.getAnsweringIndex();
            
            // check answer value
            if(!surveyManager.isAnswerValueEmpty(selectedItem))
            {
                showDialog("You've answered this question, do you really want to skip ?", "confirmSaveAnswer.show()");
                return;
            }
        }
        
        for(PageBranch pageBranch : surveyJSFModel.getCurrPage().getPageBranchList())
        {
            if(pageBranch.getMain())
            {
                selectedItem = pageBranch.getItemList().get(surveyJSFModel.getAnsweringIndex());
                surveyManager.cleanAnswer(selectedItem);

                selectedItem.getQuestion().setAnswering(false);
                selectedItem.getQuestion().setAnswered(-1);
                surveyJSFModel.setAnsweringIndex(surveyJSFModel.getAnsweringIndex() + 1);
                surveyManager.setItemIndex(pageBranch.getItemList());
                if(surveyJSFModel.getAnsweringIndex() < pageBranch.getItemList().size())
                {
                    // find next question which is not hidden.
                    for(int itemIndex = surveyJSFModel.getAnsweringIndex(); itemIndex < pageBranch.getItemList().size(); itemIndex ++)
                    {
                        BranchItem item = pageBranch.getItemList().get(itemIndex);
                        if(!item.isHidden() && item.getQuestion().getAnswered() == 0)
                        {
                            surveyJSFModel.setAnsweringIndex(itemIndex);
                            pageBranch.getItemList().get(itemIndex).getQuestion().setAnswering(true);
                            currItem= pageBranch.getItemList().get(itemIndex);
                              if(notHiddenItemMap.get(currItem.getBranchitemid())==null){
                                    notHiddenItemList.add(currItem);
                                    notHiddenItemMap.put(currItem.getBranchitemid(), currItem);
                            }
                            selectedItemId=pageBranch.getItemList().get(itemIndex).getBranchitemid();
                            break;
                        }
                    }
                }
                break;
            }
        }
        RequestContext requestContext=RequestContext.getCurrentInstance();
        requestContext.execute("confirmSaveAnswer.hide()");
        
        surveyManager.changeSurveywithRules(true, selectedItem, surveyJSFModel, surveyJSFModel.getAnsweringIndex());
        
        // find next question which is not hidden.
        for(PageBranch pageBranch : surveyJSFModel.getCurrPage().getPageBranchList())
        {
            if(pageBranch.getMain())
            {
                if(surveyJSFModel.getAnsweringIndex() < pageBranch.getItemList().size())
                {
                    for(int itemIndex = surveyJSFModel.getAnsweringIndex(); itemIndex < pageBranch.getItemList().size(); itemIndex ++)
                    {
                        BranchItem item = pageBranch.getItemList().get(itemIndex);
                        if(!item.isHidden())
                        {
                            surveyJSFModel.setAnsweringIndex(itemIndex);
                            pageBranch.getItemList().get(itemIndex).getQuestion().setAnswering(true);
                            break;
                        }
                    }
                }
                break;
            }
        }
        Integer time = (int) ((System.currentTimeMillis()-startTime)/1000);
        if(surveyJSFModel.getAnsweringItem().getDuration() == null){
            surveyJSFModel.getAnsweringItem().setDuration(time);
        }else{
            surveyJSFModel.getAnsweringItem().setDuration(time + surveyJSFModel.getAnsweringItem().getDuration());
        }
        setRatio();
        surveyJSFModel.setCurrPageAllFinished(surveyManager.checkCurrPageAllFinished(surveyJSFModel));
        
        if(surveyJSFModel.isCurrPageAllFinished())
        {
            if(!surveyJSFModel.isIsLastPage())
            {
                setContinueBut(true);
                setFinishBut(false);
                showDialog("All questions in this page has been answered, do you want to go next page?", "confirmGoToNextPage.show()");
            }
            else
            {
                setContinueBut(false);
                setFinishBut(true);
                showDialog("All questions of this survey has been answered, do you want to finish this survey?", "confirmGoToNextPage.show()");
            }
        }
        
    }
    public String back(){
        return "doSurvey?faces-redirect=true";
    }
    
    /**
     * add pageResponse
     */
    private void addPageResponse()
    {
        if(surveyResponse.getPageresponseList().size() < (surveyJSFModel.getCurrentPageIndex() + 1))
        {
            surveyResponse.getPageresponseList().add(new PageResponse(surveyJSFModel.getCurrPage()));
        }
    }
    
    public void displayDescription(String description){
        setDescriptionString(description);
        RequestContext requestContext=RequestContext.getCurrentInstance();
        requestContext.execute("descriptionDlg.show()");
        
    }
    
    /**
     * show dialog.
     */
    private void showDialog(String message, String showDialog)
    {
        setConfirmMessage(message);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute(showDialog);
    }
    
    /**
     * @return the hasDefaultBranch
     */
    public boolean isHasDefaultBranch() {
        return hasDefaultBranch;
    }

    /**
     * @param hasDefaultBranch the hasDefaultBranch to set
     */
    public void setHasDefaultBranch(boolean hasDefaultBranch) {
        this.hasDefaultBranch = hasDefaultBranch;
    }

    /**
     * @return the allItemsHidden
     */
    public boolean isAllItemsHidden() {
        return allItemsHidden;
    }

    /**
     * @param allItemsHidden the allItemsHidden to set
     */
    public void setAllItemsHidden(boolean allItemsHidden) {
        this.allItemsHidden = allItemsHidden;
    }

    /**
     * @return the selectedViolationRule
     */
    public ViolationRuleJSFModel getSelectedViolationRule() {
        return selectedViolationRule;
    }

    /**
     * @param selectedViolationRule the selectedViolationRule to set
     */
    public void setSelectedViolationRule(ViolationRuleJSFModel selectedViolationRule) {
        this.selectedViolationRule = selectedViolationRule;
    }

    /**
     * @return the confirmMessage
     */
    public String getConfirmMessage() {
        return confirmMessage;
    }

    /**
     * @param confirmMessage the confirmMessage to set
     */
    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    /**
     * @return the surveyJSFModel
     */
    public SurveyJSFModel getSurveyJSFModel() {
        return surveyJSFModel;
    }

    /**
     * @param surveyJSFModel the surveyJSFModel to set
     */
    public void setSurveyJSFModel(SurveyJSFModel surveyJSFModel) {
        this.surveyJSFModel = surveyJSFModel;
    }

    /**
     * @return the selectedItemIndex
     */
    public Integer getSelectedItemIndex() {
        return selectedItemIndex;
    }

    /**
     * @param selectedItemIndex the selectedItemIndex to set
     */
    public void setSelectedItemIndex(Integer selectedItemIndex) {
        this.selectedItemIndex = selectedItemIndex;
    }

    /**
     * @return the surveyResponse
     */
    public SurveyResponse getSurveyResponse() {
        return surveyResponse;
    }

    /**
     * @param surveyResponse the surveyResponse to set
     */
    public void setSurveyResponse(SurveyResponse surveyResponse) {
        this.surveyResponse = surveyResponse;
    }

    public String getDescriptionString() {
        return descriptionString;
    }

    public void setDescriptionString(String descriptionString) {
        this.descriptionString = descriptionString;
    }

    /**
     * @return the continueBut
     */
    public boolean isContinueBut() {
        return continueBut;
    }

    /**
     * @param continueBut the continueBut to set
     */
    public void setContinueBut(boolean continueBut) {
        this.continueBut = continueBut;
    }

    /**
     * @return the finishBut
     */
    public boolean isFinishBut() {
        return finishBut;
    }

    /**
     * @param finishBut the finishBut to set
     */
    public void setFinishBut(boolean finishBut) {
        this.finishBut = finishBut;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public BranchItem getCurrItem() {
        return currItem;
    }

    public void setCurrItem(BranchItem currItem) {
        this.currItem = currItem;
    }

    public Integer getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(Integer selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

   
    public Map<Integer, BranchItem> getNotHiddenItemMap() {
        return notHiddenItemMap;
    }

    public void setNotHiddenItemMap(Map<Integer, BranchItem> notHiddenItemMap) {
        this.notHiddenItemMap = notHiddenItemMap;
    }

    public List<BranchItem> getNotHiddenItemList() {
        return notHiddenItemList;
    }

    public void setNotHiddenItemList(List<BranchItem> notHiddenItemList) {
        this.notHiddenItemList = notHiddenItemList;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

   

 
    
    
}
