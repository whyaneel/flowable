package boilerplate.guru.flowable;

import boilerplate.guru.flowable.states.Role;
import boilerplate.guru.flowable.states.TaskName;
import boilerplate.guru.flowable.states.TransitionStatus;
import boilerplate.guru.flowable.utils.WorkflowConstants;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.api.FlowableTaskAlreadyClaimedException;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FlowableApplicationTests {
	@Autowired private RuntimeService runtimeService;
	@Autowired private TaskService taskService;
	@Autowired private RepositoryService repositoryService;
	@Autowired private HistoryService historyService;

	private Role requestHasThisRole;
	private String transitionLink;
	private Map<String, Object> tasksAndRoles = new HashMap<>();

	@Test
	public void contextLoads() {
		check_WorkflowDeployed();

		requestHasThisRole = Role.PRODUCT_MANAGER_MAKER;
		String processInstanceId = initAssetCreation("BoilerplateGuru-InitiationProcess");


		pmMaker_FillTheForm();


		getTransitionStatus(processInstanceId); // Looking for HOLD or DRAFT
		transitionLink = "SaveAsDraft";
		pmMaker_Action(processInstanceId, transitionLink);

		getTransitionStatus(processInstanceId); // Looking for HOLD or DRAFT
        transitionLink = "SendForApproval";
		pmMaker_Action(processInstanceId, transitionLink);


		check_NextPendingTasks(processInstanceId);


		getTransitionStatus(processInstanceId); // Looking for PENDING_FOR_APPROVAL
		requestHasThisRole = Role.PRODUCT_MANAGER_CHECKER;
        transitionLink = "Reject";
        pmChecker_Action(processInstanceId, transitionLink);


		getTransitionStatus(processInstanceId); // Looking for REJECT
		requestHasThisRole = Role.PRODUCT_MANAGER_MAKER;
		transitionLink = "SendForApproval";
		pmMaker_Action(processInstanceId, transitionLink);


		getTransitionStatus(processInstanceId); // Looking for PENDING_FOR_APPROVAL
		requestHasThisRole = Role.PRODUCT_MANAGER_CHECKER;
        transitionLink = "Approve";
        pmChecker_Action(processInstanceId, transitionLink);


        check_NextPendingTasks(processInstanceId);

        /*
        	Call
        	markComplete_ForParallelTasks(processInstanceId);
        	To make the Test case pass.
         */

		check_JobCompleted(processInstanceId);
	}

	private void markComplete_ForParallelTasks(String processInstanceId) {
		Task currentTask = getTask(processInstanceId, TaskName.DEPARTMENT_1);
		taskService.complete(currentTask.getId(), null);

		currentTask = getTask(processInstanceId, TaskName.DEPARTMENT_2);
		taskService.complete(currentTask.getId(), null);

		currentTask = getTask(processInstanceId, TaskName.DEPARTMENT_3);
		taskService.complete(currentTask.getId(), null);

		currentTask = getTask(processInstanceId, TaskName.DEPARTMENT_4);
		taskService.complete(currentTask.getId(), null);
	}

	private void getTransitionStatus(String processInstanceId) {
		Map<String, Object> processVariables = historyService.createHistoricProcessInstanceQuery().includeProcessVariables()
				.processInstanceId(processInstanceId).singleResult().getProcessVariables();

		Assert.notNull(processVariables.get(WorkflowConstants.TRANSITION_STATUS), "TransitionStatus not found.");

		getTasksWithTransitionStatus(processVariables.get(WorkflowConstants.TRANSITION_STATUS));
	}

	private void getTasksWithTransitionStatus(Object transitionStatus){
		List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().includeProcessVariables()
				.unfinished().list().stream()
				.filter(one -> one.getProcessVariables().get(WorkflowConstants.TRANSITION_STATUS).equals(transitionStatus))
				.collect(Collectors.toList());

		Assert.isTrue(historicTaskInstanceList.size() > 0, "No tasks found with transitionStatus - " + transitionStatus);
	}

	private void check_NextPendingTasks(String processInstanceId) {
		System.out.println("Pending Tasks are " + historyService.createHistoricTaskInstanceQuery().unfinished()
				.list().stream().map(t->t.getName()).collect(Collectors.toList()).toString());
	}


	private void check_JobCompleted(String processInstanceId) {
		Assert.notNull(historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult()
				.getEndTime(), "Job (" + processInstanceId + ") is not completed.");
	}

	private void check_WorkflowDeployed() {
		List<String> keys = repositoryService.createProcessDefinitionQuery()
				.latestVersion()
				.list()
				.stream()
				.map(ProcessDefinition::getKey)
				.collect(Collectors.toList());

		Assert.isTrue(keys.size() >= 1);

		tasksAndRoles.put(TaskName.PM_MAKER_TASK.value, Role.PRODUCT_MANAGER_MAKER);
		tasksAndRoles.put(TaskName.PM_CHECKER_TASK.value, Role.PRODUCT_MANAGER_CHECKER);
	}

	private void pmMaker_FillTheForm() {
	}


	private Task getTask(String processInstanceId, TaskName taskName){
		List<Task> taskList = taskService.createTaskQuery()
				.active()
				.taskName(taskName.value)
				.includeProcessVariables()
				.list();

		Assert.isTrue( taskList.size() > 0 );

		return taskList.stream().filter(t -> t.getProcessInstanceId().equals(processInstanceId)).collect(Collectors.toList()).get(0);
	}

	private void saveTheForm() {
	}

	private void pmMaker_Action(String processInstanceId, String transitionLink) {
		Task currentTask = getTask(processInstanceId, TaskName.PM_MAKER_TASK);
		try {
			if(requestHasThisRole == tasksAndRoles.get(TaskName.PM_MAKER_TASK.value))
				taskService.claim(currentTask.getId(), "aneel");
			//otherwise throw CustomActionAccessException
		}catch (FlowableTaskAlreadyClaimedException e) {
			//Task Already Claimed
			//FlowableTaskAlreadyClaimedException: Task 'bec75ce2-3f0a-11e9-8238-a61b2b3b8adb' is already claimed by someone else.
            return;
		}catch (FlowableObjectNotFoundException e){
			//Task Already Completed
			//FlowableObjectNotFoundException: Cannot find task with id 9d178dbd-3f0a-11e9-805d-a61b2b3b8adb
            return;
		}

		saveTheForm();

		if("SaveAsDraft".equals(transitionLink)) {
			taskService.setVariable(currentTask.getId(), WorkflowConstants.TRANSITION_STATUS, TransitionStatus.DRAFT.name());
		}else if("SendForApproval".equals(transitionLink)){
			taskService.setVariable(currentTask.getId(), WorkflowConstants.TRANSITION_STATUS, TransitionStatus.PENDING_FOR_APPROVAL.name());
		}

		Map<String, Object> transition = new HashMap<>();
		transition.put("transition", transitionLink.equals("SendForApproval")? 1 : 2);
		taskService.complete(currentTask.getId(), transition);
	}

    private void pmChecker_Action(String processInstanceId, String transitionLink) {
        Task currentTask = getTask(processInstanceId, TaskName.PM_CHECKER_TASK);
        try {
			if(requestHasThisRole == tasksAndRoles.get(TaskName.PM_CHECKER_TASK.value))
            	taskService.claim(currentTask.getId(), "who_checker_1");
			//otherwise throw CustomActionAccessException
        }catch (FlowableTaskAlreadyClaimedException e) {
            //Task Already Claimed
            //FlowableTaskAlreadyClaimedException: Task 'bec75ce2-3f0a-11e9-8238-a61b2b3b8adb' is already claimed by someone else.
            return;
        }catch (FlowableObjectNotFoundException e){
            //Task Already Completed
            //FlowableObjectNotFoundException: Cannot find task with id 9d178dbd-3f0a-11e9-805d-a61b2b3b8adb
            return;
        }

        saveTheForm();

		if("Reject".equals(transitionLink)) {
			taskService.setVariable(currentTask.getId(), WorkflowConstants.TRANSITION_STATUS, TransitionStatus.REJECTED.name());
		}else if("Approve".equals(transitionLink)){
			taskService.setVariable(currentTask.getId(), WorkflowConstants.TRANSITION_STATUS, TransitionStatus.APPROVED.name());
		}

        Map<String, Object> transition = new HashMap<>();
        transition.put("transition", transitionLink.equals("Approve")? 1 : 2);

        taskService.complete(currentTask.getId(), transition);
    }

	private String initAssetCreation(String processKey){
		Map<String, Object> processVariables = new HashMap<>();
		processVariables.put("GlobalVariable", "GlobalValue"); // for testing purpose
		processVariables.put(WorkflowConstants.TRANSITION_STATUS, TransitionStatus.HOLD);// Initiated
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, processVariables);
		Assert.notNull(processInstance.getId(), "The Process Instance Id Can't be null.");
		return processInstance.getId();
	}
}
