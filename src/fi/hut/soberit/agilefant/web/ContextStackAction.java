package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.web.context.ContextView;
import fi.hut.soberit.agilefant.web.context.ContextViewManager;

public class ContextStackAction extends ActionSupport {

    private ContextViewManager contextViewManager;

    private ContextView view;

    public String execute() {
        view = contextViewManager.getParentContext();
        if (view == null) {
            return Action.SUCCESS;
        } else {
            return Action.SUCCESS + "_" + view.getContextName();
        }
    }

    public void setContextViewManager(ContextViewManager contextViewManager) {
        this.contextViewManager = contextViewManager;
    }

    public ContextView getView() {
        return view;
    }
}
