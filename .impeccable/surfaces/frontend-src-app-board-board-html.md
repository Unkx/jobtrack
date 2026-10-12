---
version: 1
slug: "frontend-src-app-board-board-html"
primary_target: "frontend/src/app/board/board.html"
related_targets: ["frontend/src/app/stats/stats.html","frontend/src/app/login/login.html","frontend/src/app/app.html"]
---

Scope: JobTrack app shell (toolbar, login, board with dialog, stats). Visitor mode: Operate.
Audience/job: job seeker in IT, evenings at a desk, a few minutes a day: see where every application stands, act on the overdue one, move cards after a call. Recruiter skims the GIF/demo.
Constraints: 5-column Kanban stays; UX copy from the clarify pass stays; WCAG 2.2 AA; must not feel showy.
Memorable moment: the yellow "next step" band.

## Direction contract

THESIS: The board is a terminal concourse at night: one yellow band names your next step and how long it has been waiting; everything else is quiet black signage. Refuses the category default of soft grey cards with coloured status pills.

OWN-WORLD: Sign-black ground #101113, panel frame #1c1e22, 1px rules #2c2f35, icon-white ink #f5f5f0, muted #a2a49f. Signal yellow #ffd200 only for wayfinding: the next-step band, the overdue card's outline, keyboard focus and the active nav mark. Red #e5533d only for rejection and errors. One humanist sans (Hanken Grotesk, self-hosted) in sentence case, bold for signs, monumental numerals for counts. Black inset squares carrying white arrows. Square-ish 4px corners, no shadows except overlays.

STORY: Opening the board, the visitor reads the band first (who to nudge, days waiting), then the gate-like column counts, then cards. They act from the band, move a card by dragging or "Przenieś", and check Statystyki weekly where numbers read like gate signs.

FIRST VIEWPORT: 56px black top bar: wordmark with inset arrow square left, Tablica/Statystyki with yellow underline on active, Wyloguj się right. Full-width yellow band under it: black inset square with arrow, "Następny krok: przypomnij się Acme · Junior Java Developer", monumental "20 dni" right, black "Ukryj". Then h1 "Moje aplikacje" and white "Dodaj aplikację". Five equal columns: monumental count (44px) beside the stage name, cards as dark panels; the overdue card carries a 2px yellow outline. Signature interaction: band messages swap with a short vertical sign-flip (240ms, ease-out-expo), instant under reduced motion.

FORM: airport wayfinding signage (catalog challenger "terminal yellow wayfinding"), presented as competitive, chosen by user over the assigned #6 (registered mail). Seed key 173d0445.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance
