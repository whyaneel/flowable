package boilerplate.guru.flowable.delegate;

import boilerplate.guru.flowable.states.TransitionStatus;
import boilerplate.guru.flowable.utils.WorkflowConstants;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.util.Assert;

public class AssetCreation implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        Assert.isTrue(delegateExecution.getVariable("GlobalVariable").equals("GlobalValue"));

        delegateExecution.setVariable(WorkflowConstants.TRANSITION_STATUS, TransitionStatus.DONE);
    }
}
