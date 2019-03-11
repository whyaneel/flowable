package boilerplate.guru.flowable.listener;

import boilerplate.guru.flowable.states.Role;
import boilerplate.guru.flowable.states.TransitionStatus;
import boilerplate.guru.flowable.utils.WorkflowConstants;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class PMMakerAssignmentListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //String loggedInUser = "aneel";
        //delegateTask.addCandidateUsers(Arrays.asList(loggedInUser, "who_maker_1", "who_maker_2"));
        delegateTask.addCandidateGroup(Role.PRODUCT_MANAGER_MAKER.name());

        if(null == delegateTask.getVariable(WorkflowConstants.TRANSITION_STATUS))
        delegateTask.setVariable(WorkflowConstants.TRANSITION_STATUS, TransitionStatus.HOLD);
    }
}
