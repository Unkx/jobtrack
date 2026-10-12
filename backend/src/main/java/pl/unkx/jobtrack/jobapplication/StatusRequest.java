package pl.unkx.jobtrack.jobapplication;

import jakarta.validation.constraints.NotNull;

record StatusRequest(@NotNull Status status) {
}
