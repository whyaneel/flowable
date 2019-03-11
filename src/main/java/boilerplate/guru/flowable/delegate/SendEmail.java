package boilerplate.guru.flowable.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.util.Assert;

public class SendEmail implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        Assert.isTrue(delegateExecution.getVariable("GlobalVariable").equals("GlobalValue"));
    }
}
