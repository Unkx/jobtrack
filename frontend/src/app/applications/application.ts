export type Status = 'WYSLANO' | 'ROZMOWA_HR' | 'TECHNICZNA' | 'OFERTA' | 'ODRZUCONO';
export type Source = 'LINKEDIN' | 'JUST_JOIN_IT' | 'REFERRAL';

export interface JobApplication {
  id: number;
  company: string;
  position: string;
  offerUrl: string | null;
  salaryMin: number | null;
  salaryMax: number | null;
  source: Source;
  appliedAt: string;
  status: Status;
}

export type JobApplicationRequest = Omit<JobApplication, 'id' | 'status'>;

export interface Note {
  id: number;
  content: string;
  createdAt: string;
}

export interface StatusChange {
  fromStatus: Status;
  toStatus: Status;
  changedAt: string;
}

export const STATUSES: readonly { value: Status; label: string }[] = [
  { value: 'WYSLANO', label: 'Wysłano' },
  { value: 'ROZMOWA_HR', label: 'Rozmowa HR' },
  { value: 'TECHNICZNA', label: 'Techniczna' },
  { value: 'OFERTA', label: 'Oferta' },
  { value: 'ODRZUCONO', label: 'Odrzucono' },
];

export const SOURCES: readonly { value: Source; label: string }[] = [
  { value: 'LINKEDIN', label: 'LinkedIn' },
  { value: 'JUST_JOIN_IT', label: 'Just Join IT' },
  { value: 'REFERRAL', label: 'Polecenie' },
];
