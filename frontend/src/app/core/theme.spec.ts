import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme';

describe('ThemeService', () => {
  beforeEach(() => {
    localStorage.clear();
    delete document.documentElement.dataset['theme'];
    vi.stubGlobal(
      'matchMedia',
      vi.fn(() => ({ matches: true, addEventListener: vi.fn(), removeEventListener: vi.fn() })),
    );
  });

  afterEach(() => vi.unstubAllGlobals());

  it('follows the system scheme by default without forcing a theme', () => {
    const theme = TestBed.inject(ThemeService);

    expect(theme.preference()).toBe('system');
    expect(theme.scheme()).toBe('dark');
    expect(document.documentElement.dataset['theme']).toBeUndefined();
  });

  it('cycles system → light → dark → system and remembers the choice', () => {
    const theme = TestBed.inject(ThemeService);

    theme.cycle();
    expect(document.documentElement.dataset['theme']).toBe('light');
    expect(theme.scheme()).toBe('light');
    expect(localStorage.getItem('jobtrack.theme')).toBe('light');

    theme.cycle();
    expect(document.documentElement.dataset['theme']).toBe('dark');

    theme.cycle();
    expect(document.documentElement.dataset['theme']).toBeUndefined();
    expect(localStorage.getItem('jobtrack.theme')).toBe('system');
  });

  it('restores a stored choice', () => {
    localStorage.setItem('jobtrack.theme', 'light');

    const theme = TestBed.inject(ThemeService);

    expect(theme.preference()).toBe('light');
    expect(document.documentElement.dataset['theme']).toBe('light');
  });
});
