package fi.hut.soberit.agilefant.web.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;


@Component("iterationMetricsWidget")
@Scope("prototype")
public class IterationMetricsWidget extends CommonWidget {
    private static final long serialVersionUID = 4029492283643549647L;
    private Iteration iteration;
    private IterationMetrics metrics;

    @Autowired
    private IterationBusiness iterationBusiness;
    
    @Override
    public String execute() {
        iteration = iterationBusiness.retrieve(getObjectId());
        return SUCCESS;
    }

    public Iteration getIteration() {
        return iteration;
    }

}
