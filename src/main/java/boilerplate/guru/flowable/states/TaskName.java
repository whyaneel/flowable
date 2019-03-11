package boilerplate.guru.flowable.states;

public enum TaskName {
    PM_MAKER_TASK("PM Maker Task"),
    PM_CHECKER_TASK("PM Checker Task"),
    DEPARTMENT_1("Department 1"),
    DEPARTMENT_2("Department 2"),
    DEPARTMENT_3("Department 3"),
    DEPARTMENT_4("Department 4");

    public final String value;
    TaskName(String value){
        this.value = value;
    }
}
