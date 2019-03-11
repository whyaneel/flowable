package boilerplate.guru.flowable.listener;

import boilerplate.guru.flowable.states.Role;
import boilerplate.guru.flowable.states.TransitionStatus;
import boilerplate.guru.flowable.utils.WorkflowConstants;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class PMCheckerAssignmentListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //delegateTask.addCandidateUsers(Arrays.asList("who_checker_1", "who_checker_2"));
        delegateTask.addCandidateGroup(Role.PRODUCT_MANAGER_CHECKER.name());

        if(null == delegateTask.getVariable(WorkflowConstants.TRANSITION_STATUS))
        delegateTask.setVariable(WorkflowConstants.TRANSITION_STATUS, TransitionStatus.PENDING_FOR_APPROVAL);
    }
}
