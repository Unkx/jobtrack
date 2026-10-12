import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Source } from '../applications/application';

export interface Stats {
  totalApplications: number;
  /** Percentage 0-100, null when there are no applications. */
  responseRate: number | null;
  averageDaysToFirstResponse: number | null;
  applicationsPerWeek: { weekStart: string; count: number }[];
  bySource: {
    source: Source;
    applications: number;
    responses: number;
    offers: number;
    responseRate: number | null;
  }[];
}

@Injectable({ providedIn: 'root' })
export class StatsService {
  private readonly http = inject(HttpClient);

  get(): Observable<Stats> {
    return this.http.get<Stats>('/api/stats');
  }
}
