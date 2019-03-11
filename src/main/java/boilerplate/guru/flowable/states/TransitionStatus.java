package boilerplate.guru.flowable.states;

public enum TransitionStatus {
    HOLD,
    DRAFT,
    PENDING_FOR_APPROVAL,
    APPROVED,
    REJECTED,
    DONE;

    TransitionStatus(){}
}
