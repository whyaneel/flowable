package flow.able.onetask.controller;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OneTaskController {
	@Autowired
	private RuntimeService runtimeService;
	
	@PostMapping("/one-task/{name}")
	private String startOneTaskProcess(@PathVariable(required = false) final String name) {
		Map<String, Object> processVariables = new HashMap<>();
		processVariables.put("name", name);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("OneTask", processVariables);
		return processInstance.getId();
	}
}
