import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { MatError, MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { Router } from '@angular/router';
import { concatMap, Observable } from 'rxjs';
import { AuthService } from '../core/auth';
import { OUTLINED_FIELDS } from '../core/outlined-fields';

type Mode = 'login' | 'register';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatButton,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardSubtitle,
    MatCardTitle,
    MatError,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
  ],
  providers: [OUTLINED_FIELDS],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly mode = signal<Mode>('login');
  protected readonly submitting = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly form = inject(NonNullableFormBuilder).group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
  });

  protected toggleMode(): void {
    this.mode.update((mode) => (mode === 'login' ? 'register' : 'login'));
    this.error.set(null);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const credentials = this.form.getRawValue();
    const request: Observable<void> =
      this.mode() === 'register'
        ? this.auth.register(credentials).pipe(concatMap(() => this.auth.login(credentials)))
        : this.auth.login(credentials);

    this.submitting.set(true);
    this.error.set(null);
    request.subscribe({
      next: () => this.router.navigate(['/']),
      error: (error: unknown) => {
        this.submitting.set(false);
        this.error.set(messageFor(error));
      },
    });
  }
}

function messageFor(error: unknown): string {
  const status = error instanceof HttpErrorResponse ? error.status : 0;
  switch (status) {
    case 401:
      return 'Nieprawidłowy email lub hasło. Sprawdź dane i spróbuj ponownie.';
    case 409:
      return 'Ten email ma już konto. Zaloguj się zamiast zakładać nowe.';
    case 400:
      return 'Sprawdź email i hasło (co najmniej 8 znaków).';
    default:
      return 'Brak połączenia z serwerem. Spróbuj ponownie za chwilę.';
  }
}
