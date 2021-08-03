package flow.able.onetask;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class FlowableOneTaskApplication implements CommandLineRunner {
  @Autowired private RuntimeService runtimeService;
  private static Logger LOG = LoggerFactory.getLogger(FlowableOneTaskApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(FlowableOneTaskApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info("EXECUTING : command line runner");
    Map<String, Object> processVariables = new HashMap<>();
    processVariables.put("githubUsername", "whyaneel");
    processVariables.put("fullName", "Anil");

    ProcessInstance processInstance =
        runtimeService.startProcessInstanceByKey("OneTaskKafkaProducer", processVariables);

    LOG.info("Triggered OneTaskKafkaProducer Process, JobID {}", processInstance.getId());
  }
}
