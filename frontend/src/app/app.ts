import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth';
import { ThemeService } from './core/theme';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  protected readonly auth = inject(AuthService);
  protected readonly theme = inject(ThemeService);
  private readonly router = inject(Router);

  protected readonly themeLabel = computed(
    () =>
      ({ system: 'Motyw: jak w systemie', light: 'Motyw: jasny', dark: 'Motyw: ciemny' })[this.theme.preference()],
  );

  protected logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
