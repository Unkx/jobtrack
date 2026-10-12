import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthService } from './auth';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => httpTesting.verify());

  function logIn(): void {
    auth.login({ email: 'a@example.com', password: 'password123' }).subscribe();
    httpTesting.expectOne('/api/auth/login').flush({ token: 'jwt-token' });
  }

  it('does not add a header when logged out', () => {
    http.get('/api/applications').subscribe();

    expect(httpTesting.expectOne('/api/applications').request.headers.has('Authorization')).toBe(false);
  });

  it('adds the bearer token when logged in', () => {
    logIn();

    http.get('/api/applications').subscribe();

    expect(httpTesting.expectOne('/api/applications').request.headers.get('Authorization')).toBe(
      'Bearer jwt-token',
    );
  });

  it('logs out and redirects to login when an authenticated request gets 401', () => {
    logIn();
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    http.get('/api/applications').subscribe({ error: () => undefined });
    httpTesting.expectOne('/api/applications').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(auth.isLoggedIn()).toBe(false);
    expect(localStorage.getItem('jobtrack.token')).toBeNull();
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });
});
