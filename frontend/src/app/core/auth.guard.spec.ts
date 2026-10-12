import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, RouterStateSnapshot, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  const runGuard = () =>
    TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );

  beforeEach(() => {
    localStorage.clear();
  });

  function configure(): void {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
  }

  it('redirects to /login without a token', () => {
    configure();

    const result = runGuard();

    expect(result instanceof UrlTree && result.toString()).toBe('/login');
  });

  it('allows access with a stored token', () => {
    localStorage.setItem('jobtrack.token', 'jwt-token');
    configure();

    expect(runGuard()).toBe(true);
  });
});
