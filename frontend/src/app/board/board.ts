import { CdkDrag, CdkDragDrop, CdkDropList, CdkDropListGroup } from '@angular/cdk/drag-drop';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, ElementRef, inject, signal, viewChild } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatSnackBar } from '@angular/material/snack-bar';
import { JobApplication, SOURCES, Status, STATUSES } from '../applications/application';
import { ApplicationsService } from '../applications/applications.service';
import { Reminder, RemindersService } from '../reminders/reminders.service';
import { ApplicationDialog } from './application-dialog';

@Component({
  selector: 'app-board',
  imports: [
    CdkDrag,
    CdkDropList,
    CdkDropListGroup,
    DatePipe,
    DecimalPipe,
    MatButton,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
  ],
  templateUrl: './board.html',
  styleUrl: './board.scss',
})
export class Board {
  private readonly api = inject(ApplicationsService);
  private readonly remindersApi = inject(RemindersService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly applications = signal<JobApplication[]>([]);
  protected readonly loaded = signal(false);
  private readonly reminders = signal<Reminder[]>([]);
  // A reminder stops mattering as soon as its application leaves WYSLANO, e.g. after a drag
  protected readonly visibleReminders = computed(() =>
    this.reminders().filter((reminder) =>
      this.applications().some((a) => a.id === reminder.applicationId && a.status === 'WYSLANO'),
    ),
  );
  protected readonly overdueIds = computed(() => new Set(this.visibleReminders().map((r) => r.applicationId)));
  protected readonly columns = computed(() =>
    STATUSES.map((status) => ({
      ...status,
      items: this.applications().filter((application) => application.status === status.value),
    })),
  );

  constructor() {
    this.load();
  }

  protected daysSince(isoDate: string): number {
    const [year, month, day] = isoDate.split('-').map(Number);
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    return Math.round((today.getTime() - new Date(year, month - 1, day).getTime()) / 86_400_000);
  }

  // The band only animates when one reminder replaces another, never on first paint
  protected readonly flipNext = signal(false);

  protected dismiss(reminder: Reminder): void {
    this.remindersApi.markRead(reminder.id).subscribe({
      next: () => {
        this.flipNext.set(true);
        this.reminders.update((list) => list.filter((r) => r.id !== reminder.id));
      },
      error: () => this.notify('Nie udało się ukryć przypomnienia. Spróbuj ponownie.'),
    });
  }

  protected sourceLabel(application: JobApplication): string {
    return SOURCES.find((source) => source.value === application.source)?.label ?? application.source;
  }

  protected readonly statuses = STATUSES;

  private readonly board = viewChild<ElementRef<HTMLElement>>('board');
  protected readonly visibleColumn = signal(0);

  protected showColumn(index: number): void {
    const board = this.board()?.nativeElement;
    const column = board?.children.item(index) as HTMLElement | null;
    // scroll only the board; scrollIntoView would also move the page
    board?.scrollTo({ left: (column?.offsetLeft ?? 0) - board.offsetLeft, behavior: 'smooth' });
    this.visibleColumn.set(index);
  }

  protected onBoardScroll(): void {
    const board = this.board()?.nativeElement;
    if (!board) {
      return;
    }
    // the column whose left edge is closest to the scroll position is the one on screen
    const columns = Array.from(board.children) as HTMLElement[];
    const offsets = columns.map((column) => Math.abs(column.offsetLeft - board.offsetLeft - board.scrollLeft));
    this.visibleColumn.set(offsets.indexOf(Math.min(...offsets)));
  }

  protected drop(event: CdkDragDrop<Status, Status, JobApplication>): void {
    this.moveTo(event.item.data, event.container.data);
  }

  // Keyboard and touch alternative to dragging (WCAG 2.5.7)
  protected moveTo(application: JobApplication, target: Status): void {
    if (application.status === target) {
      return;
    }
    // Move the card immediately and roll back if the server rejects the change
    this.replace({ ...application, status: target });
    this.api.changeStatus(application.id, target).subscribe({
      next: (updated) => this.replace(updated),
      error: () => {
        this.replace(application);
        this.notify('Nie udało się zmienić statusu. Karta wróciła na miejsce – spróbuj ponownie.');
      },
    });
  }

  protected openDialog(application?: JobApplication): void {
    this.dialog
      .open<ApplicationDialog, JobApplication | undefined, string>(ApplicationDialog, {
        data: application,
        width: '560px',
        maxWidth: '95vw',
        // full screen on phones, see styles.scss
        panelClass: 'jt-dialog',
      })
      .afterClosed()
      .subscribe((confirmation) => {
        if (confirmation) {
          this.notify(confirmation);
          this.load();
        }
      });
  }

  private notify(message: string): void {
    this.snackBar.open(message, 'Zamknij', { duration: 4000 });
  }

  private load(): void {
    this.api.list().subscribe({
      next: (applications) => {
        this.applications.set(applications);
        this.loaded.set(true);
      },
      error: () => this.notify('Nie udało się wczytać aplikacji. Odśwież stronę, aby spróbować ponownie.'),
    });
    this.remindersApi.list().subscribe({
      next: (reminders) => this.reminders.set(reminders),
      error: () => this.reminders.set([]),
    });
  }

  private replace(updated: JobApplication): void {
    this.applications.update((list) => list.map((a) => (a.id === updated.id ? updated : a)));
  }
}
