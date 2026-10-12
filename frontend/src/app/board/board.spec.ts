import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { JobApplication, Status } from '../applications/application';
import { ApplicationsService } from '../applications/applications.service';
import { Reminder, RemindersService } from '../reminders/reminders.service';
import { Board } from './board';

const ACME: JobApplication = {
  id: 1,
  company: 'Acme',
  position: 'Junior Java Developer',
  offerUrl: null,
  salaryMin: 7000,
  salaryMax: 9000,
  source: 'LINKEDIN',
  appliedAt: '2026-10-01',
  status: 'WYSLANO',
};
const GLOBEX: JobApplication = { ...ACME, id: 2, company: 'Globex', status: 'TECHNICZNA' };
const REMINDER_FOR_ACME: Reminder = {
  id: 10,
  applicationId: 1,
  company: 'Acme',
  position: 'Junior Java Developer',
  appliedAt: '2026-10-01',
  createdAt: '2026-10-09T08:00:00Z',
};

describe('Board', () => {
  let fixture: ComponentFixture<Board>;
  let api: { list: ReturnType<typeof vi.fn>; changeStatus: ReturnType<typeof vi.fn> };
  let remindersApi: { list: ReturnType<typeof vi.fn>; markRead: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    api = { list: vi.fn(() => of([ACME, GLOBEX])), changeStatus: vi.fn() };
    remindersApi = {
      list: vi.fn(() => of([REMINDER_FOR_ACME, { ...REMINDER_FOR_ACME, id: 11, applicationId: 2, company: 'Globex' }])),
      markRead: vi.fn(() => of(undefined)),
    };
    TestBed.configureTestingModule({
      imports: [Board],
      providers: [
        { provide: ApplicationsService, useValue: api },
        { provide: RemindersService, useValue: remindersApi },
      ],
    });
    fixture = TestBed.createComponent(Board);
    await fixture.whenStable();
  });

  function column(status: Status): HTMLElement {
    return fixture.nativeElement.querySelector(`[data-status="${status}"]`);
  }

  function drop(application: JobApplication, target: Status): void {
    const event = { item: { data: application }, container: { data: target } };
    (fixture.componentInstance as unknown as { drop(e: CdkDragDrop<Status>): void }).drop(
      event as unknown as CdkDragDrop<Status>,
    );
  }

  it('renders five columns and places each application in its status column', () => {
    expect(fixture.nativeElement.querySelectorAll('[data-status]').length).toBe(5);
    expect(column('WYSLANO').textContent).toContain('Acme');
    expect(column('TECHNICZNA').textContent).toContain('Globex');
    expect(column('OFERTA').textContent).not.toContain('Acme');
  });

  it('dropping a card on another column changes its status via the API', async () => {
    api.changeStatus.mockReturnValue(of({ ...ACME, status: 'ROZMOWA_HR' }));

    drop(ACME, 'ROZMOWA_HR');
    await fixture.whenStable();

    expect(api.changeStatus).toHaveBeenCalledWith(1, 'ROZMOWA_HR');
    expect(column('ROZMOWA_HR').textContent).toContain('Acme');
    expect(column('WYSLANO').textContent).not.toContain('Acme');
  });

  it('dropping a card in its own column does not call the API', () => {
    drop(ACME, 'WYSLANO');

    expect(api.changeStatus).not.toHaveBeenCalled();
  });

  function reminders(): HTMLElement | null {
    return fixture.nativeElement.querySelector('.reminders');
  }

  it('shows reminders only for applications still in WYSLANO', () => {
    expect(reminders()?.textContent).toContain('Acme');
    // Globex already moved on to TECHNICZNA
    expect(reminders()?.textContent).not.toContain('Globex');
  });

  it('hides the reminder once the application is dragged out of WYSLANO', async () => {
    api.changeStatus.mockReturnValue(of({ ...ACME, status: 'ROZMOWA_HR' }));

    drop(ACME, 'ROZMOWA_HR');
    await fixture.whenStable();

    expect(reminders()).toBeNull();
  });

  it('dismissing a reminder marks it as read', async () => {
    const button = reminders()!.querySelector('button') as HTMLButtonElement;

    button.click();
    await fixture.whenStable();

    expect(remindersApi.markRead).toHaveBeenCalledWith(10);
    expect(reminders()).toBeNull();
  });

  it('shows one reminder at a time and the next one after hiding', async () => {
    const initech: JobApplication = { ...ACME, id: 3, company: 'Initech' };
    api.list.mockReturnValue(of([ACME, initech]));
    remindersApi.list.mockReturnValue(
      of([REMINDER_FOR_ACME, { ...REMINDER_FOR_ACME, id: 12, applicationId: 3, company: 'Initech' }]),
    );
    fixture = TestBed.createComponent(Board);
    await fixture.whenStable();

    expect(reminders()!.textContent).toContain('Acme');
    expect(reminders()!.textContent).not.toContain('Initech');
    expect(reminders()!.textContent).toContain('1 z 2');

    (reminders()!.querySelector('.band-dismiss') as HTMLButtonElement).click();
    await fixture.whenStable();

    expect(reminders()!.textContent).toContain('Initech');
    expect(reminders()!.textContent).not.toContain('1 z');
  });

  it('stage switcher lists every stage with its count and marks the chosen one', async () => {
    const scrollTo = vi.fn();
    (fixture.nativeElement.querySelector('.board') as HTMLElement).scrollTo = scrollTo;
    const buttons = Array.from(
      fixture.nativeElement.querySelectorAll('.stage-switcher button'),
    ) as HTMLButtonElement[];

    expect(buttons.map((b) => b.textContent!.replace(/\s+/g, ' ').trim())).toEqual([
      'Wysłano 1',
      'Rozmowa HR 0',
      'Techniczna 1',
      'Oferta 0',
      'Odrzucono 0',
    ]);

    buttons[2].click();
    await fixture.whenStable();

    expect(buttons[2].getAttribute('aria-current')).toBe('true');
    expect(buttons[0].getAttribute('aria-current')).toBeNull();
    expect(scrollTo).toHaveBeenCalled();
  });

  it('shows an empty state with a call to action when there are no applications', async () => {
    api.list.mockReturnValue(of([]));
    fixture = TestBed.createComponent(Board);
    await fixture.whenStable();

    expect(fixture.nativeElement.textContent).toContain('Nie masz jeszcze żadnych aplikacji');
    expect(fixture.nativeElement.querySelector('[data-status]')).toBeNull();
  });

  it('changes status from the "Przenieś" menu without dragging', async () => {
    api.changeStatus.mockReturnValue(of({ ...ACME, status: 'OFERTA' }));
    const moveButton = column('WYSLANO').querySelector('[aria-label^="Przenieś Acme"]') as HTMLButtonElement;

    moveButton.click();
    await fixture.whenStable();
    const target = Array.from(document.querySelectorAll<HTMLButtonElement>('[mat-menu-item]')).find(
      (item) => item.textContent!.trim() === 'Oferta',
    )!;
    target.click();
    await fixture.whenStable();

    expect(api.changeStatus).toHaveBeenCalledWith(1, 'OFERTA');
    expect(column('OFERTA').textContent).toContain('Acme');
  });

  it('does not offer the current stage as a move target', async () => {
    (column('WYSLANO').querySelector('[aria-label^="Przenieś Acme"]') as HTMLButtonElement).click();
    await fixture.whenStable();
    const labels = Array.from(document.querySelectorAll('[mat-menu-item]')).map((i) => i.textContent!.trim());

    expect(labels).toEqual(['Rozmowa HR', 'Techniczna', 'Oferta', 'Odrzucono']);
  });

  it('moves the card back when the API rejects the status change', async () => {
    api.changeStatus.mockReturnValue(throwError(() => new Error('500')));

    drop(ACME, 'OFERTA');
    await fixture.whenStable();

    expect(column('WYSLANO').textContent).toContain('Acme');
    expect(column('OFERTA').textContent).not.toContain('Acme');
  });
});
