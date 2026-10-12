import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { map, Observable, tap } from 'rxjs';

const TOKEN_KEY = 'jobtrack.token';

export interface Credentials {
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  readonly token = this.tokenSignal.asReadonly();
  readonly isLoggedIn = computed(() => this.tokenSignal() !== null);

  register(credentials: Credentials): Observable<void> {
    return this.http.post<void>('/api/auth/register', credentials);
  }

  login(credentials: Credentials): Observable<void> {
    return this.http.post<{ token: string }>('/api/auth/login', credentials).pipe(
      tap(({ token }) => {
        localStorage.setItem(TOKEN_KEY, token);
        this.tokenSignal.set(token);
      }),
      map(() => undefined),
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.tokenSignal.set(null);
  }
}
