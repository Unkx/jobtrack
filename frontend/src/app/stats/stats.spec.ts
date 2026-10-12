import { registerLocaleData } from '@angular/common';
import localePl from '@angular/common/locales/pl';
import { LOCALE_ID } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Stats } from './stats';
import { Stats as StatsData, StatsService } from './stats.service';

const STATS: StatsData = {
  totalApplications: 5,
  responseRate: 60,
  averageDaysToFirstResponse: 3.5,
  applicationsPerWeek: [
    { weekStart: '2026-10-05', count: 2 },
    { weekStart: '2026-10-12', count: 2 },
  ],
  bySource: [
    { source: 'LINKEDIN', applications: 2, responses: 1, offers: 1, responseRate: 50 },
    { source: 'JUST_JOIN_IT', applications: 2, responses: 1, offers: 0, responseRate: 50 },
    { source: 'REFERRAL', applications: 0, responses: 0, offers: 0, responseRate: null },
  ],
};

describe('Stats', () => {
  beforeAll(() => {
    registerLocaleData(localePl);
    // jsdom has no canvas; the chart itself is not under test here
    vi.spyOn(HTMLCanvasElement.prototype, 'getContext').mockReturnValue(null);
  });

  async function render(stats: ReturnType<StatsService['get']>): Promise<ComponentFixture<Stats>> {
    TestBed.configureTestingModule({
      imports: [Stats],
      providers: [
        { provide: StatsService, useValue: { get: () => stats } },
        { provide: LOCALE_ID, useValue: 'pl' },
        provideRouter([]),
      ],
    });
    const fixture = TestBed.createComponent(Stats);
    await fixture.whenStable();
    return fixture;
  }

  it('shows headline numbers in Polish number format', async () => {
    const fixture = await render(of(STATS));
    const tiles = (fixture.nativeElement as HTMLElement).querySelector('.tiles')!.textContent!;

    expect(tiles).toContain('5');
    expect(tiles).toContain('60%');
    expect(tiles).toContain('3,5');
  });

  it('lists every source with a dash when there is no rate', async () => {
    const fixture = await render(of(STATS));
    const rows = Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr')).map((row) =>
      Array.from(row.children)
        .map((cell) => cell.textContent!.trim())
        .join(' | '),
    );

    expect(rows).toEqual([
      'LinkedIn | 2 | 1 | 50% | 1',
      'Just Join IT | 2 | 1 | 50% | 0',
      'Polecenie | 0 | 0 | – | 0',
    ]);
  });

  it('shows an error message when stats cannot be loaded', async () => {
    const fixture = await render(throwError(() => new Error('500')));

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Nie udało się wczytać statystyk');
  });

  it('shows an empty state instead of zeros when there are no applications', async () => {
    const fixture = await render(of({ ...STATS, totalApplications: 0 }));
    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('Brak danych do analizy');
    expect(element.querySelector('.tiles')).toBeNull();
  });
});
