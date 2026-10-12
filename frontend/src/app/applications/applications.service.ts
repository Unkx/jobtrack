import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JobApplication, JobApplicationRequest, Note, Status, StatusChange } from './application';

const URL = '/api/applications';

@Injectable({ providedIn: 'root' })
export class ApplicationsService {
  private readonly http = inject(HttpClient);

  list(): Observable<JobApplication[]> {
    return this.http.get<JobApplication[]>(URL);
  }

  create(request: JobApplicationRequest): Observable<JobApplication> {
    return this.http.post<JobApplication>(URL, request);
  }

  update(id: number, request: JobApplicationRequest): Observable<JobApplication> {
    return this.http.put<JobApplication>(`${URL}/${id}`, request);
  }

  changeStatus(id: number, status: Status): Observable<JobApplication> {
    return this.http.patch<JobApplication>(`${URL}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${URL}/${id}`);
  }

  history(id: number): Observable<StatusChange[]> {
    return this.http.get<StatusChange[]>(`${URL}/${id}/history`);
  }

  notes(id: number): Observable<Note[]> {
    return this.http.get<Note[]>(`${URL}/${id}/notes`);
  }

  addNote(id: number, content: string): Observable<Note> {
    return this.http.post<Note>(`${URL}/${id}/notes`, { content });
  }

  deleteNote(id: number, noteId: number): Observable<void> {
    return this.http.delete<void>(`${URL}/${id}/notes/${noteId}`);
  }
}
