package flow.able.onetask.task;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogHelloTask implements JavaDelegate {
  private static final Logger LOG = LoggerFactory.getLogger(LogHelloTask.class);
  @Autowired
  RepositoryService repositoryService;

  @Override
  public void execute(DelegateExecution delegateExecution) {
    LOG.info(
        "JobID:[{}], Task:[{}]",
        delegateExecution.getProcessInstanceId(),
        ((ExecutionEntityImpl) delegateExecution).getActivityName());
    LOG.info(
        "Welcome {}, your Github Handle is {}",
        delegateExecution.getVariable("name").toString(),
        delegateExecution.getVariable("githubHandle").toString());
  }
}
