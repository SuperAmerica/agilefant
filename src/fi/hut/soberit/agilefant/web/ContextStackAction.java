package fi.hut.soberit.agilefant.web;

import java.util.Stack;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.web.context.ContextView;

public class ContextStackAction extends ActionSupport{
	
	public static final String DEFAULT_CONTEXT_VIEW_STACK_PARAM = "contextViewStack";
	private String contextName;
	private int contextObjectId;
	private boolean resetContext;
	private String contextViewStackName = DEFAULT_CONTEXT_VIEW_STACK_PARAM;
	
	public String execute(){
		if (resetContext){
			this.reset();
		}
		
		if (contextName == null){
			return this.popStack();
		} else {
			return this.pushStack();
		}
	}
	
	private String popStack(){
		contextObjectId = 0;
		Stack<ContextView> stack = this.getStack();
		if (stack == null || stack.size() == 0){
			return Action.SUCCESS;
		} else {
			ContextView context = stack.pop();
			contextName = context.getContextName();
			contextObjectId = context.getContextObject();
			return Action.SUCCESS + "_" + contextName;
		}		
	}
	
	private String pushStack(){
		ContextView context = new ContextView(contextName, contextObjectId);
		this.getStack().push(context);
		return Action.SUCCESS;
	}
	
	public String reset(){
		Stack stack = this.getStack();
		stack.clear();
		return Action.SUCCESS;		
	}
	
	private Stack<ContextView> getStack(){
		Stack<ContextView> stack = (Stack)ActionContext.getContext().getSession().get(contextViewStackName);
		if (stack == null){
			stack = new Stack<ContextView>();
			ActionContext.getContext().getSession().put(contextViewStackName, stack);
		}
		return stack;
	}
}
