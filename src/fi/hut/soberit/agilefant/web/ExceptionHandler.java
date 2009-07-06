package fi.hut.soberit.agilefant.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

@Component("exceptionHandler")
@Scope("prototype")
public class ExceptionHandler extends ActionSupport {

    private static final long serialVersionUID = 8739605249663386007L;

    private Exception exception;

    private String trace = "";

    private String msg = "";

    private boolean dbException = false;

    public String handleException() {
        try {
            msg = exception.getMessage();
            StringWriter stackTrace = new StringWriter();
            PrintWriter pw = new PrintWriter(stackTrace);
            exception.printStackTrace(pw);
            trace = stackTrace.toString();
        } catch(Exception e) {
            trace = "Unknown exception.";
        }
        return Action.SUCCESS;
    }

    public String handleDbException() {
        dbException = true;
        return Action.SUCCESS;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getMsg() {
        return msg;
    }

    public String getTrace() {
        return trace;
    }

    public boolean isDbException() {
        return dbException;
    }
}
