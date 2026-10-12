import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { JobApplication } from '../applications/application';
import { ApplicationsService } from '../applications/applications.service';
import { ApplicationDialog } from './application-dialog';

const ACME: JobApplication = {
  id: 1,
  company: 'Acme',
  position: 'Junior Java Developer',
  offerUrl: null,
  salaryMin: null,
  salaryMax: null,
  source: 'LINKEDIN',
  appliedAt: '2026-10-01',
  status: 'WYSLANO',
};

describe('ApplicationDialog', () => {
  let api: Record<'delete' | 'create' | 'notes' | 'history' | 'addNote' | 'deleteNote', ReturnType<typeof vi.fn>>;
  let dialogRef: { close: ReturnType<typeof vi.fn> };
  let fixture: ComponentFixture<ApplicationDialog>;

  async function open(data: JobApplication | undefined): Promise<void> {
    api = {
      delete: vi.fn(() => of(undefined)),
      create: vi.fn(() => of({ ...ACME, id: 7 })),
      notes: vi.fn(() => of([{ id: 3, content: 'Rozmowa z Anną z HR', createdAt: '2026-10-08T10:00:00Z' }])),
      history: vi.fn(() =>
        of([{ fromStatus: 'WYSLANO', toStatus: 'ROZMOWA_HR', changedAt: '2026-10-08T10:00:00Z' }]),
      ),
      addNote: vi.fn((_id: number, content: string) =>
        of({ id: 4, content, createdAt: '2026-10-12T10:00:00Z' }),
      ),
      deleteNote: vi.fn(() => of(undefined)),
    };
    dialogRef = { close: vi.fn() };
    TestBed.configureTestingModule({
      imports: [ApplicationDialog],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: data },
        { provide: MatDialogRef, useValue: dialogRef },
        { provide: ApplicationsService, useValue: api },
      ],
    });
    fixture = TestBed.createComponent(ApplicationDialog);
    await fixture.whenStable();
  }

  function button(label: string): HTMLButtonElement | undefined {
    return Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('button')).find(
      (b) => b.textContent!.trim() === label,
    );
  }

  async function click(label: string): Promise<void> {
    button(label)!.click();
    await fixture.whenStable();
  }

  it('labels the primary action "Dodaj aplikację" for a new application', async () => {
    await open(undefined);

    expect(button('Dodaj aplikację')).toBeDefined();
    expect(button('Usuń')).toBeUndefined();
  });

  it('labels the primary action "Zapisz zmiany" when editing', async () => {
    await open(ACME);

    expect(button('Zapisz zmiany')).toBeDefined();
  });

  it('asks for confirmation before deleting', async () => {
    await open(ACME);

    await click('Usuń');

    expect(api.delete).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Usunąć aplikację do Acme?');
  });

  it('deletes after confirming and closes with a confirmation message', async () => {
    await open(ACME);
    await click('Usuń');

    await click('Usuń aplikację');

    expect(api.delete).toHaveBeenCalledWith(1);
    expect(dialogRef.close).toHaveBeenCalledWith('Usunięto aplikację do Acme');
  });

  it('shows stage history and notes when editing', async () => {
    await open(ACME);
    const text = (fixture.nativeElement as HTMLElement).textContent!;

    expect(api.history).toHaveBeenCalledWith(1);
    expect(text).toContain('Historia etapów');
    expect(text).toContain('Rozmowa HR');
    expect(text).toContain('Rozmowa z Anną z HR');
  });

  it('adds a note immediately and lists it first', async () => {
    await open(ACME);
    const textarea = (fixture.nativeElement as HTMLElement).querySelector(
      '.note-form textarea',
    ) as HTMLTextAreaElement;
    textarea.value = 'Follow-up w piątek';
    textarea.dispatchEvent(new Event('input'));

    await click('Dodaj notatkę');

    expect(api.addNote).toHaveBeenCalledWith(1, 'Follow-up w piątek');
    expect(fixture.nativeElement.querySelector('.note p').textContent).toBe('Follow-up w piątek');
  });

  it('deletes a note from the list', async () => {
    await open(ACME);

    await click('Usuń notatkę');

    expect(api.deleteNote).toHaveBeenCalledWith(1, 3);
    expect(fixture.nativeElement.textContent).toContain('Brak notatek');
  });

  it('saves the first note together with a new application', async () => {
    await open(undefined);
    const root = fixture.nativeElement as HTMLElement;
    const set = (selector: string, value: string) => {
      const input = root.querySelector(selector) as HTMLInputElement;
      input.value = value;
      input.dispatchEvent(new Event('input'));
    };
    set('input[formControlName="company"]', 'Acme');
    set('input[formControlName="position"]', 'Junior Java Developer');
    set('textarea', 'Polecenie od Kuby');

    await click('Dodaj aplikację');

    expect(api.create).toHaveBeenCalled();
    expect(api.addNote).toHaveBeenCalledWith(7, 'Polecenie od Kuby');
    expect(dialogRef.close).toHaveBeenCalledWith('Dodano aplikację do Acme');
  });

  it('lets the user back out of deleting', async () => {
    await open(ACME);
    await click('Usuń');

    await click('Nie usuwaj');

    expect(api.delete).not.toHaveBeenCalled();
    expect(button('Zapisz zmiany')).toBeDefined();
  });
});
