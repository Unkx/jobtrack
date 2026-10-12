import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./board/board').then((m) => m.Board),
    canActivate: [authGuard],
    title: 'Tablica · JobTrack',
  },
  {
    path: 'stats',
    loadComponent: () => import('./stats/stats').then((m) => m.Stats),
    canActivate: [authGuard],
    title: 'Statystyki · JobTrack',
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login').then((m) => m.Login),
    canActivate: [guestGuard],
    title: 'Zaloguj się · JobTrack',
  },
  { path: '**', redirectTo: '' },
];
