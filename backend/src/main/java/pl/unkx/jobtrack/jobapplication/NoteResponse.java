package pl.unkx.jobtrack.jobapplication;

import java.time.Instant;

record NoteResponse(Long id, String content, Instant createdAt) {

    static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getContent(), note.getCreatedAt());
    }
}
