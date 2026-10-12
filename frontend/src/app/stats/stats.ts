import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective, provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { SOURCES } from '../applications/application';
import { ThemeService } from '../core/theme';
import { Stats as StatsData, StatsService } from './stats.service';

/** Resolves a CSS colour token (which may use light-dark()) to a value Chart.js can parse. */
function resolveColor(token: string): string {
  const probe = document.createElement('span');
  probe.style.color = `var(${token})`;
  document.body.appendChild(probe);
  const color = getComputedStyle(probe).color;
  probe.remove();
  return color;
}

function chartFont(): string {
  return getComputedStyle(document.documentElement).getPropertyValue('--jt-font').trim();
}

function shortDate(isoDate: string): string {
  const [, month, day] = isoDate.split('-');
  return `${day}.${month}`;
}

@Component({
  selector: 'app-stats',
  imports: [BaseChartDirective, DecimalPipe, MatButton, RouterLink],
  providers: [provideCharts(withDefaultRegisterables())],
  templateUrl: './stats.html',
  styleUrl: './stats.scss',
})
export class Stats {
  protected readonly stats = signal<StatsData | null>(null);
  protected readonly error = signal(false);

  protected readonly sourceLabel = (source: string) =>
    SOURCES.find((s) => s.value === source)?.label ?? source;

  private readonly theme = inject(ThemeService);

  // Chart.js paints resolved colours, so both computeds re-run when the theme changes
  protected readonly weeklyChart = computed<ChartData<'bar'>>(() => {
    this.theme.scheme();
    const weeks = this.stats()?.applicationsPerWeek ?? [];
    return {
      labels: weeks.map((week) => shortDate(week.weekStart)),
      datasets: [
        {
          data: weeks.map((week) => week.count),
          backgroundColor: resolveColor('--mat-sys-primary'),
          borderRadius: 4,
          borderSkipped: 'start',
          maxBarThickness: 32,
        },
      ],
    };
  });

  protected readonly weeklyChartOptions = computed<ChartConfiguration<'bar'>['options']>(() => {
    this.theme.scheme();
    return {
      responsive: true,
      maintainAspectRatio: false,
      // data appears settled: no entrance animation
      animation: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            title: (items) => `Tydzień od ${items[0].label}`,
            label: (item) => `Wysłane: ${item.parsed.y}`,
          },
        },
      },
      scales: {
        x: {
          grid: { display: false },
          border: { color: resolveColor('--jt-rule') },
          ticks: {
            color: resolveColor('--jt-muted'),
            font: { family: chartFont(), size: 12 },
          },
        },
        y: {
          beginAtZero: true,
          border: { display: false },
          ticks: {
            precision: 0,
            color: resolveColor('--jt-muted'),
            font: { family: chartFont(), size: 12 },
          },
          grid: { color: resolveColor('--jt-rule') },
        },
      },
    };
  });

  constructor() {
    inject(StatsService)
      .get()
      .subscribe({
        next: (stats) => this.stats.set(stats),
        error: () => this.error.set(true),
      });
  }
}
