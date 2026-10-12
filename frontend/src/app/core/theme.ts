import { DestroyRef, inject, Injectable, signal } from '@angular/core';

export type ThemePreference = 'system' | 'light' | 'dark';

const STORAGE_KEY = 'jobtrack.theme';
const ORDER: ThemePreference[] = ['system', 'light', 'dark'];

/** Keeps the colour theme: the system setting by default, or the visitor's explicit pick. */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  // matchMedia is missing in some test environments; treat that as a light system scheme
  private readonly darkQuery: Pick<MediaQueryList, 'matches' | 'addEventListener' | 'removeEventListener'> =
    typeof window.matchMedia === 'function'
      ? window.matchMedia('(prefers-color-scheme: dark)')
      : { matches: false, addEventListener: () => undefined, removeEventListener: () => undefined };

  readonly preference = signal<ThemePreference>(readStored());
  /** The scheme actually on screen; components that paint with resolved colours (charts) watch it. */
  readonly scheme = signal<'light' | 'dark'>(this.resolve());

  constructor() {
    this.apply();
    const onSystemChange = () => this.scheme.set(this.resolve());
    this.darkQuery.addEventListener('change', onSystemChange);
    inject(DestroyRef).onDestroy(() => this.darkQuery.removeEventListener('change', onSystemChange));
  }

  cycle(): void {
    const next = ORDER[(ORDER.indexOf(this.preference()) + 1) % ORDER.length];
    this.preference.set(next);
    try {
      localStorage.setItem(STORAGE_KEY, next);
    } catch {
      // storage unavailable (private mode): the choice still applies for this visit
    }
    this.apply();
  }

  private apply(): void {
    const preference = this.preference();
    if (preference === 'system') {
      delete document.documentElement.dataset['theme'];
    } else {
      document.documentElement.dataset['theme'] = preference;
    }
    this.scheme.set(this.resolve());
  }

  private resolve(): 'light' | 'dark' {
    const preference = this.preference();
    if (preference !== 'system') {
      return preference;
    }
    return this.darkQuery.matches ? 'dark' : 'light';
  }
}

function readStored(): ThemePreference {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored === 'light' || stored === 'dark' ? stored : 'system';
  } catch {
    return 'system';
  }
}
