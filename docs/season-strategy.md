# Season strategy

Keep game-specific policy **outside** HELM core.

Season modules (not in this repository's core) may define field targets, game-piece types, scoring values, task definitions, match deadlines, endgame thresholds, strategy profiles, scoring weights, alliance-side transforms, and legal regions.

HELM core understands goals, capabilities, tasks, time, confidence, outcomes, and resources without knowing the current FTC game.

Do not put one season's point values or field geometry in `org.allsparks.helm`.
