package org.allsparks.helm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.allsparks.helm.authority.AuthorityGate;
import org.allsparks.helm.decision.DecisionRecord;
import org.allsparks.helm.intent.IntentTree;
import org.allsparks.helm.observe.ExecutionHistory;
import org.allsparks.helm.observe.ObservedEvent;
import org.allsparks.helm.observe.StatedIntent;
import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.outcome.Outcome;
import org.allsparks.helm.snapshot.WorldSnapshot;
import org.allsparks.helm.task.Task;
import org.allsparks.helm.task.TaskEvaluation;
import org.allsparks.helm.task.TaskEvaluator;
import org.allsparks.helm.trace.TraceEvent;
import org.allsparks.helm.validate.PlanValidator;
import org.allsparks.helm.validate.ValidationReport;

/**
 * High-level Execution and Logic Manager facade.
 *
 * <p>This scaffold implements Phase 0 vocabulary, Phase 1 passive observation,
 * and Phase 2 offline validation. It never commands motors or servos.
 */
public final class Helm {
    private final HelmConfig config;
    private final TaskEvaluator evaluator;
    private final PlanValidator validator;
    private final AuthorityGate authorityGate;
    private final ExecutionHistory history;
    private long decisionCycle;

    private Helm(HelmConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        this.evaluator = new TaskEvaluator(config);
        this.validator = new PlanValidator(config);
        this.authorityGate = new AuthorityGate(config);
        this.history = new ExecutionHistory(64);
    }

    public static Helm create(HelmConfig config) {
        return new Helm(config);
    }

    public static Helm create() {
        return create(HelmConfig.defaults());
    }

    public HelmConfig config() {
        return config;
    }

    public HelmMode mode() {
        return config.mode();
    }

    public boolean allowsPhysicalOutput() {
        return authorityGate.allowsPhysicalOutput();
    }

    public String authorityDenial() {
        return authorityGate.denialExplanation();
    }

    public TaskEvaluation evaluate(Task task, WorldSnapshot snapshot) {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(snapshot, "snapshot");
        decisionCycle++;
        TaskEvaluation evaluation = evaluator.evaluate(task, snapshot);
        config.traceSink().record(new TraceEvent(
                "task-evaluation",
                config.clock().nanoTime(),
                snapshot.snapshotId(),
                decisionCycle,
                java.util.Map.of(
                        "task", task.name(),
                        "eligible", Boolean.toString(evaluation.isEligible()),
                        "explanation", evaluation.explanation())));
        return evaluation;
    }

    public DecisionRecord recommend(List<Task> candidateTasks, WorldSnapshot snapshot) {
        Objects.requireNonNull(candidateTasks, "candidateTasks");
        Objects.requireNonNull(snapshot, "snapshot");
        decisionCycle++;
        if (config.mode() == HelmMode.OFF) {
            return DecisionRecord.disabled(decisionCycle, snapshot.snapshotId(),
                    "HELM mode is OFF; no recommendation is produced");
        }
        if (!config.mode().allowsRecommendations() || !config.flags().isPhase4Shadow()) {
            return DecisionRecord.disabled(decisionCycle, snapshot.snapshotId(),
                    "Shadow selection is not enabled. Phase 4 remains unapproved.");
        }
        if (candidateTasks.size() > config.maxCandidates()) {
            return DecisionRecord.disabled(decisionCycle, snapshot.snapshotId(),
                    "Candidate count " + candidateTasks.size()
                            + " exceeds the limit of " + config.maxCandidates());
        }
        return DecisionRecord.disabled(decisionCycle, snapshot.snapshotId(),
                "Phase 4 shadow scoring is not implemented in this scaffold");
    }

    public Optional<ObservedEvent> observe(
            StatedIntent intent,
            Outcome outcome,
            FailureReason failureReason,
            long startedAtNanos,
            WorldSnapshot snapshot) {
        Objects.requireNonNull(intent, "intent");
        Objects.requireNonNull(outcome, "outcome");
        if (config.mode() == HelmMode.OFF || !config.flags().isPhase1Observe()
                || !config.mode().recordsObservations()) {
            return Optional.empty();
        }
        ObservedEvent event = new ObservedEvent(
                history.size() + 1L,
                intent,
                outcome,
                failureReason,
                startedAtNanos,
                config.clock().nanoTime(),
                snapshot == null ? "" : snapshot.snapshotId());
        history.add(event);
        config.traceSink().record(new TraceEvent(
                "stated-intent",
                config.clock().nanoTime(),
                event.snapshotId(),
                decisionCycle,
                java.util.Map.of(
                        "intent", intent.name(),
                        "outcome", outcome.name(),
                        "durationMs", Long.toString(event.duration().toMillis()),
                        "reason", event.failureReason().code())));
        return Optional.of(event);
    }

    public ValidationReport validate(Task task) {
        Objects.requireNonNull(task, "task");
        if (config.mode() == HelmMode.OFF || !config.flags().isPhase2Validate()
                || !config.mode().allowsValidation()) {
            return ValidationReport.notRun(task.name(), validationSkipReason());
        }
        ValidationReport report = validator.validate(task);
        config.traceSink().record(new TraceEvent(
                "plan-validation",
                config.clock().nanoTime(),
                "",
                decisionCycle,
                java.util.Map.of(
                        "subject", task.name(),
                        "valid", Boolean.toString(report.isValid()),
                        "explanation", report.explanation())));
        return report;
    }

    public ValidationReport validate(IntentTree tree) {
        Objects.requireNonNull(tree, "tree");
        if (config.mode() == HelmMode.OFF || !config.flags().isPhase2Validate()
                || !config.mode().allowsValidation()) {
            return ValidationReport.notRun(tree.name(), validationSkipReason());
        }
        ValidationReport report = validator.validate(tree);
        config.traceSink().record(new TraceEvent(
                "tree-validation",
                config.clock().nanoTime(),
                "",
                decisionCycle,
                java.util.Map.of(
                        "subject", tree.name(),
                        "valid", Boolean.toString(report.isValid()),
                        "explanation", report.explanation())));
        return report;
    }

    public ExecutionHistory history() {
        return history;
    }

    public long decisionCycle() {
        return decisionCycle;
    }

    /**
     * Convenience for student examples that evaluate a list without selecting.
     */
    public List<TaskEvaluation> evaluateAll(List<Task> tasks, WorldSnapshot snapshot) {
        List<TaskEvaluation> evaluations = new ArrayList<>();
        for (Task task : tasks) {
            evaluations.add(evaluate(task, snapshot));
        }
        return List.copyOf(evaluations);
    }

    private String validationSkipReason() {
        if (config.mode() == HelmMode.OFF) {
            return "HELM mode is OFF";
        }
        if (!config.flags().isPhase2Validate()) {
            return "Phase 2 validation is not enabled";
        }
        return "Mode " + config.mode() + " does not allow validation";
    }
}
