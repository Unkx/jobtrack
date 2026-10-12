import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Reminder {
  id: number;
  applicationId: number;
  company: string;
  position: string;
  appliedAt: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class RemindersService {
  private readonly http = inject(HttpClient);

  list(): Observable<Reminder[]> {
    return this.http.get<Reminder[]>('/api/reminders');
  }

  markRead(id: number): Observable<void> {
    return this.http.post<void>(`/api/reminders/${id}/read`, null);
  }
}
