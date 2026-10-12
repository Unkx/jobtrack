import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatError, MatFormField, MatHint, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { concatMap, Observable, of } from 'rxjs';
import {
  JobApplication,
  JobApplicationRequest,
  Note,
  Source,
  SOURCES,
  Status,
  StatusChange,
  STATUSES,
} from '../applications/application';
import { ApplicationsService } from '../applications/applications.service';
import { OUTLINED_FIELDS } from '../core/outlined-fields';

function salaryRange(group: AbstractControl): ValidationErrors | null {
  const min = group.get('salaryMin')?.value;
  const max = group.get('salaryMax')?.value;
  return min != null && max != null && min > max ? { salaryRange: true } : null;
}

function today(): string {
  return new Date().toISOString().slice(0, 10);
}

@Component({
  selector: 'app-application-dialog',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatError,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
    MatSuffix,
    MatOption,
    MatSelect,
  ],
  providers: [OUTLINED_FIELDS],
  templateUrl: './application-dialog.html',
  styleUrl: './application-dialog.scss',
})
export class ApplicationDialog {
  private readonly api = inject(ApplicationsService);
  // Closes with a confirmation message for the board's snackbar, or nothing when cancelled
  private readonly dialogRef = inject<MatDialogRef<ApplicationDialog, string>>(MatDialogRef);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly application = inject<JobApplication | undefined>(MAT_DIALOG_DATA);
  protected readonly sources = SOURCES;
  protected readonly saving = signal(false);
  protected readonly confirmingDelete = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly form = this.fb.group(
    {
      company: [this.application?.company ?? '', [Validators.required, Validators.maxLength(255)]],
      position: [this.application?.position ?? '', [Validators.required, Validators.maxLength(255)]],
      offerUrl: [this.application?.offerUrl ?? '', Validators.maxLength(2048)],
      salaryMin: this.fb.control<number | null>(this.application?.salaryMin ?? null, Validators.min(0)),
      salaryMax: this.fb.control<number | null>(this.application?.salaryMax ?? null, Validators.min(0)),
      source: this.fb.control<Source>(this.application?.source ?? 'LINKEDIN', Validators.required),
      appliedAt: [this.application?.appliedAt ?? today(), Validators.required],
    },
    { validators: salaryRange },
  );

  // A new application can start with a note; existing ones manage notes in their own list
  protected readonly firstNote = this.fb.control('', Validators.maxLength(2000));
  protected readonly newNote = this.fb.control('', Validators.maxLength(2000));
  protected readonly notes = signal<Note[]>([]);
  protected readonly history = signal<StatusChange[]>([]);
  protected readonly noteError = signal<string | null>(null);

  constructor() {
    if (this.application) {
      this.api.notes(this.application.id).subscribe((notes) => this.notes.set(notes));
      this.api.history(this.application.id).subscribe((history) => this.history.set(history));
    }
  }

  protected statusLabel(status: Status): string {
    return STATUSES.find((s) => s.value === status)?.label ?? status;
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const request: JobApplicationRequest = { ...value, offerUrl: value.offerUrl || null };
    const firstNote = this.firstNote.value.trim();
    const create = this.api
      .create(request)
      .pipe(concatMap((created) => (firstNote ? this.api.addNote(created.id, firstNote) : of(created))));
    this.run(
      this.application ? this.api.update(this.application.id, request) : create,
      this.application ? 'Zapisano zmiany' : `Dodano aplikację do ${request.company}`,
      'Nie udało się zapisać. Sprawdź, czy link zaczyna się od https://, i spróbuj ponownie.',
    );
  }

  protected addNote(): void {
    const content = this.newNote.value.trim();
    if (!this.application || !content) {
      return;
    }
    this.noteError.set(null);
    this.api.addNote(this.application.id, content).subscribe({
      next: (note) => {
        this.notes.update((notes) => [note, ...notes]);
        this.newNote.reset('');
      },
      error: () => this.noteError.set('Nie udało się dodać notatki. Spróbuj ponownie.'),
    });
  }

  protected deleteNote(note: Note): void {
    if (!this.application) {
      return;
    }
    this.noteError.set(null);
    this.api.deleteNote(this.application.id, note.id).subscribe({
      next: () => this.notes.update((notes) => notes.filter((n) => n.id !== note.id)),
      error: () => this.noteError.set('Nie udało się usunąć notatki. Spróbuj ponownie.'),
    });
  }

  protected remove(): void {
    if (this.application) {
      this.run(
        this.api.delete(this.application.id),
        `Usunięto aplikację do ${this.application.company}`,
        'Nie udało się usunąć aplikacji. Spróbuj ponownie.',
      );
    }
  }

  private run(request: Observable<unknown>, successMessage: string, errorMessage: string): void {
    this.saving.set(true);
    this.error.set(null);
    request.subscribe({
      next: () => this.dialogRef.close(successMessage),
      error: () => {
        this.saving.set(false);
        this.error.set(errorMessage);
      },
    });
  }
}
