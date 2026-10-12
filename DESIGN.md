---
name: JobTrack
description: A job-application tracker set as airport wayfinding signage, by night and by day.
colors:
  sign-black: "#101113"
  panel-frame: "#1c1e22"
  panel-raised: "#24272c"
  rule: "#2c2f35"
  field-frame: "#6f726c"
  icon-white: "#f5f5f0"
  muted-ink: "#a2a49f"
  signal-yellow: "#ffd200"
  on-signal: "#0b0b0b"
  stop-red: "#e5533d"
  ground-day: "#f2f3f4"
  panel-day: "#ffffff"
  panel-raised-day: "#e8e9eb"
  rule-day: "#d6d8db"
  field-frame-day: "#8a8d93"
  ink-day: "#111214"
  muted-ink-day: "#55585e"
  overdue-day: "#a88400"
  stop-red-day: "#c43b26"
  on-stop-day: "#ffffff"
typography:
  numeral-band:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "52px"
    fontWeight: 800
    lineHeight: 0.9
    fontFeature: "tnum"
  numeral-sign:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "48px"
    fontWeight: 800
    lineHeight: 1
    fontFeature: "tnum"
  numeral-gate:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "44px"
    fontWeight: 800
    lineHeight: 0.9
    fontFeature: "tnum"
  headline:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "28px"
    fontWeight: 800
    letterSpacing: "-0.01em"
  title-lg:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "22px"
    fontWeight: 800
  band-text:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "18px"
    fontWeight: 400
    lineHeight: 1.35
  title:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "17px"
    fontWeight: 700
  label-stage:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "15px"
    fontWeight: 700
  body:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "14px"
    fontWeight: 400
    lineHeight: "20px"
    fontFeature: "tnum"
  meta:
    fontFamily: "'Hanken Grotesk Variable', 'Segoe UI', system-ui, sans-serif"
    fontSize: "13px"
    fontWeight: 400
rounded:
  sign: "4px"
spacing:
  xs: "8px"
  sm: "12px"
  md: "16px"
  lg: "24px"
  target: "44px"
  bar: "56px"
components:
  button-primary:
    backgroundColor: "{colors.icon-white}"
    textColor: "{colors.sign-black}"
    rounded: "{rounded.sign}"
    padding: "0 24px"
    height: "40px"
  button-primary-day:
    backgroundColor: "{colors.ink-day}"
    textColor: "{colors.ground-day}"
    rounded: "{rounded.sign}"
    padding: "0 24px"
    height: "40px"
  button-text:
    textColor: "{colors.icon-white}"
    rounded: "{rounded.sign}"
    padding: "0 12px"
    height: "40px"
  button-outlined:
    textColor: "{colors.icon-white}"
    rounded: "{rounded.sign}"
    padding: "0 24px"
    height: "40px"
  button-danger-confirm:
    backgroundColor: "{colors.stop-red}"
    textColor: "{colors.on-signal}"
    rounded: "{rounded.sign}"
    padding: "0 24px"
    height: "40px"
  button-danger-confirm-day:
    backgroundColor: "{colors.stop-red-day}"
    textColor: "{colors.on-stop-day}"
    rounded: "{rounded.sign}"
    padding: "0 24px"
    height: "40px"
  theme-toggle:
    textColor: "{colors.muted-ink}"
    rounded: "{rounded.sign}"
    size: "{spacing.target}"
  band:
    backgroundColor: "{colors.signal-yellow}"
    textColor: "{colors.on-signal}"
    padding: "12px 24px"
  band-dismiss:
    backgroundColor: "{colors.on-signal}"
    textColor: "{colors.signal-yellow}"
    rounded: "{rounded.sign}"
    padding: "0 16px"
    height: "40px"
  inset-arrow:
    backgroundColor: "{colors.icon-white}"
    textColor: "{colors.sign-black}"
    rounded: "{rounded.sign}"
    size: "28px"
  inset-arrow-band:
    backgroundColor: "{colors.on-signal}"
    textColor: "{colors.signal-yellow}"
    rounded: "{rounded.sign}"
    size: "44px"
  card:
    backgroundColor: "{colors.panel-frame}"
    textColor: "{colors.icon-white}"
    rounded: "{rounded.sign}"
    padding: "14px 14px 6px"
  card-day:
    backgroundColor: "{colors.panel-day}"
    textColor: "{colors.ink-day}"
    rounded: "{rounded.sign}"
    padding: "14px 14px 6px"
  sign-panel:
    backgroundColor: "{colors.panel-frame}"
    textColor: "{colors.icon-white}"
    rounded: "{rounded.sign}"
    padding: "20px 24px"
  input-outlined:
    textColor: "{colors.icon-white}"
    rounded: "{rounded.sign}"
  nav-link:
    textColor: "{colors.muted-ink}"
    typography: "{typography.body}"
  nav-link-active:
    textColor: "{colors.icon-white}"
  stage-tab:
    textColor: "{colors.muted-ink}"
    padding: "0 10px"
    height: "{spacing.target}"
  stage-tab-current:
    textColor: "{colors.icon-white}"
  topbar:
    backgroundColor: "{colors.sign-black}"
    height: "{spacing.bar}"
    padding: "0 24px"
---

# Design System: JobTrack

## Overview

**Creative North Star: "The Terminal Concourse at Night"**

JobTrack is set as airport wayfinding signage after dark. The ground is sign-black, panels are slightly lifted black frames, and one signal yellow is reserved for telling you where to go next. Everything that is not wayfinding stays quiet: white ink, muted grey metadata, 1px rules. Counts are monumental numerals set beside their labels on a shared baseline, the way gate numbers sit beside destinations.

The concourse also has a daylight face. Every colour is one `--jt-*` custom property declared once with `light-dark(day, night)`; the page follows the system scheme by default, and a three-state toggle in the top bar (system, light, dark) pins it through `html[data-theme]`, applied by a pre-boot script so the page never flashes the other scheme. By day the ground is pale concrete grey, panels are white, ink is near-black; the yellow band, the inset arrow squares, the numerals and the 4px geometry are identical in both. Only the thin marks change: a 2px yellow line disappears on white, so focus, the active nav mark and the field frame turn to ink by day, and the overdue frame deepens to a darker signal.

The system is dense and operational, built on Angular Material with every Material system token remapped onto the signage palette in one place, so dialogs, menus, fields and snackbars inherit both schemes without per-component overrides. Corners are uniformly 4px, surfaces are flat, and depth only appears on things that float (dialogs, menus, the drag preview). Motion is minimal: one signature sign-flip on the next-step band, short colour fades on navigation, and transform easing during drag; all collapse to instant under reduced motion. The world explicitly rejects the category default of soft grey cards with coloured status pills. Status is expressed by column position and gate-number counts, not by hue.

**Key Characteristics:**
- Two schemes from one token set: sign-black night and pale-concrete day, following the system unless the visitor pins one.
- Signal yellow is wayfinding only: the next-step band fill and text selection in both schemes; in the night scheme also keyboard focus, the active nav mark, the focused field, the caret and the overdue frame.
- One humanist sans (Hanken Grotesk Variable, self-hosted) in sentence case; weight 800 for signs and numerals, tabular figures throughout.
- Black or white inset squares carrying a square-capped arrow as the recurring sign glyph.
- 4px corners everywhere; flat at rest; shadows only on overlays.

## Colors

A near-monochrome palette in two schemes with a single high-visibility signal colour and a single stop colour. Each role below is one `--jt-*` property; night and day values are the paired frontmatter tokens.

### Primary
- **Signal Yellow** (`signal-yellow`, same in both schemes): The wayfinding colour. Fills the next-step band and text selection. In the night scheme it is also the mark colour: the 2px focus outline, the focused-field frame and label, the caret, the 3px active nav and stage-tab underline, and the overdue card's 2px inset frame. It is never a button fill on the page ground.
- **On-Signal Black** (`on-signal`, same in both schemes): Ink and inset squares on yellow; the label on the night destructive confirm.
- **Mark** (`--jt-mark`: `ink-day` by day, `signal-yellow` by night): the single colour for every thin wayfinding mark (focus ring, active nav, current stage tab, focused field frame and label, caret).
- **Overdue Signal** (`--jt-overdue`: `overdue-day` by day, `signal-yellow` by night): the overdue card frame; by day a deep signal so the 2px line holds on white while staying in the yellow family.

### Secondary
- **Stop Red** (`--jt-stop`: `stop-red-day` by day, `stop-red` by night): Errors and destructive actions only (form errors, the "Usuń" text button, the filled delete-confirm, hover on "Usuń notatkę"). Mapped to Material's error token. Its label colour is `--jt-on-stop` (`on-stop-day` white by day, `on-signal` by night).

### Neutral
- **Ground** (`ground-day` / `sign-black`): Page ground, top bar, Material surface and background.
- **Panel** (`panel-day` / `panel-frame`): Cards, the stats sign panel and table panels, the login card; Material surface-container.
- **Panel Raised** (`panel-raised-day` / `panel-raised`): Menus and dialog containers (Material surface-container-high/highest).
- **Rule** (`rule-day` / `rule`): 1px dividers: top-bar bottom edge, column heading underline, stage-switcher baseline, dialog section tops, note and timeline rows, sign-panel dividers, table rows, chart grid, scrollbars.
- **Field Frame** (`--jt-frame`: `field-frame-day` / `field-frame`): Resting 1px outline of outlined form fields and outlined buttons (Material outline).
- **Ink** (`ink-day` / `icon-white`): Primary text, filled primary-button fill, chart bars, the topbar inset square.
- **Muted Ink** (`muted-ink-day` / `muted-ink`): Metadata, inactive nav and stage tabs, the theme toggle at rest, table headers, timeline dates, empty-state copy, the company name on rejected cards.
- **Hover wash** (`--jt-hover-wash`: black at 3% by day, white at 3% by night): the receiving drop column tint.

### Named Rules
**The Wayfinding-Only Rule.** Yellow appears only where it tells the user where to look or act next: the band, the overdue frame, focus, the active nav mark. If a yellow element does not direct attention, it is wrong.

**The One Mark Rule.** Thin wayfinding lines (focus, active nav, current stage, focused field, caret) all read from `--jt-mark`: yellow by night, ink by day. Yellow fills stay yellow in both schemes; yellow hairlines never sit on a white ground.

**The No Status Hue Rule.** Stages are not colour-coded. A rejected card dims its company name to muted ink; it does not turn red. Red is for errors and destruction.

**The One Source Rule.** Every colour is a `--jt-*` property declared once with `light-dark()`. Components never carry a raw hex; code that paints outside CSS (the Chart.js charts) resolves the property at runtime and re-resolves when the scheme changes.

## Typography

**Display Font:** Hanken Grotesk Variable (with Segoe UI, system-ui, sans-serif)
**Body Font:** Hanken Grotesk Variable (same stack)

**Character:** One humanist grotesk carries everything, as signage does: bold weights for sign text, extra-bold monumental numerals, regular weight for running copy. Sentence case throughout; no uppercase labels.

### Hierarchy
- **Band numeral** (800, 52px, line-height 0.9; 40px on phones): days waiting on the next-step band. The largest number in the product, read first.
- **Sign numeral** (800, 48px, line-height 1; 36px on phones): stats headline figures.
- **Gate numeral** (800, 44px, line-height 0.9): column counts, placed before the stage name on a shared baseline. In the phone stage switcher the count drops to body size at 800.
- **Headline** (800, 28px, -0.01em; 22px on phones): page h1 and the login card title.
- **Title large** (800, 22px): empty-state headings.
- **Band text** (400 with 700 lead-in, 18px, 1.35; 16px on phones): the next-step sentence.
- **Title** (700, 17px): company name on a card. Brand wordmark is 18px/800 (16px on phones).
- **Stage label** (700, 15px): column headings, stats section headings, dialog section headings ("Notatki", "Historia etapów").
- **Body** (400, 14px/20px via Material body-medium, tabular figures): position, table cells, dialog copy, note text. Nav, stage tabs, timeline stage names and quiet actions use 600.
- **Meta** (400, 13px, muted ink): dates, source, salary, note timestamps, table headers (600), notes under panels.

### Named Rules
**The Gate Number Rule.** Counts sit beside their label on one baseline, numeral first, at weight 800 with tight leading (0.9 to 1). The band numeral always outranks every other numeral on screen.

**The Sentence Case Rule.** All signs, buttons and headings are sentence case. Hierarchy comes from weight and size, never from uppercase tracking.

## Layout

A 56px top bar (24px side padding and 32px gaps; 16px padding and 14px gaps on phones) with a 1px rule beneath: brand left, nav, a flexible spacer, then the theme toggle and log out on the right. The full-width next-step band sits directly under it with 12px by 24px padding. Page content pads 24px (16px on phones). The board is five equal columns (`minmax(220px, 1fr)`, 12px gap) that scroll horizontally if needed; cards stack with 8px gaps in a drop zone at least 160px tall. Stats is centred at max 1040px with a three-up sign panel divided by 1px rules.

Spacing runs on 8 / 12 / 16 / 24, with 14px and 20px used inside cards and panels. Every touch control on phones meets a 44px target (theme toggle, log out, stage tabs, card actions).

Two breakpoints. Below 600px: a stage-switcher row of text tabs on a 1px rule appears above the board, which becomes a horizontal scroll-snap strip of 85%-wide columns (tapping a tab scrolls to its column; scrolling updates the current tab); the band wraps with its numeral under the text; the stats signs stack with top rules instead of side rules; the application dialog fills the viewport (100vw by 100dvh, square edges, content flexes, actions wrap above the safe-area inset) and its form drops from two columns to one. At 420px and below the brand name is visually hidden and the inset arrow square carries the brand alone.

## Elevation & Depth

Flat by default in both schemes. Depth at rest is conveyed by tonal steps (ground, panel, panel raised) and 1px rules, never by drop shadows. Inset box-shadows are used as rules and frames (the active nav and stage-tab underline, the overdue card's 2px frame), not as elevation. Real shadows appear only on things that float above the board: Material dialogs and menus keep their default overlay elevation, and the card drag preview casts one deep shadow whose opacity is a scheme token.

### Shadow Vocabulary
- **Drag lift** (`box-shadow: 0 12px 32px var(--jt-lift)`; black at 18% by day, 50% by night): the card while it is being dragged.
- **Inset frame** (`box-shadow: inset 0 0 0 2px var(--jt-overdue)`): the overdue card. A frame, not elevation.
- **Active mark** (`box-shadow: inset 0 -3px var(--jt-mark)`): underline of the active nav link and the current stage tab.

### Named Rules
**The Overlays-Only Rule.** A surface casts a shadow only if it floats over other content (dialog, menu, drag preview). Cards, panels and the band are flat.

## Shapes

One corner radius: 4px, applied to cards, panels, buttons, fields, inset squares, the theme toggle, drop zones and chart bars. Material's full and extra-large corner tokens are pinned to 4px, so nothing in the product is a pill or a circle. The single exception is the phone dialog, which meets the viewport edge with square corners because it is the screen. The recurring glyph is a square-capped arrow (stroke 2.5) inside a filled 4px-cornered square: ink on ground in the top bar (28px), black on yellow in the band (44px).

## Components

### Buttons
Quiet, rectangular, sentence case.
- **Shape:** gently squared (4px).
- **Primary (filled):** ink fill with ground-coloured label (white on black by night, near-black on grey-white by day), Material 40px height. Used for the single main action per view ("Dodaj aplikację", "Zapisz zmiany", "Zaloguj się").
- **Outlined:** 1px field-frame outline, ink label; secondary in-dialog actions ("Dodaj notatkę").
- **Text:** ink label, no fill; card actions ("Edytuj", "Przenieś", 44px tall), cancel, mode toggle.
- **Destructive:** "Usuń" is a stop-red text button; the confirm step is a stop-red filled button with the on-stop label.
- **Band dismiss:** black fill, yellow label, 700 weight, 40px tall, 16px side padding; focus outline switches to black so it stays visible on yellow.
- **Focus:** global 2px mark-colour outline, 2px offset.

### Cards / Containers
- **Corner Style:** 4px.
- **Background:** panel on ground (white on pale grey by day, panel frame on sign-black by night).
- **Shadow Strategy:** flat; overdue cards get the 2px inset overdue frame; drag preview gets the drag lift.
- **Border:** none at rest.
- **Internal Padding:** 14px top and sides, 6px bottom (text-button row tucks to the edge with -12px side margin).
- **Drag:** grab cursor; receiving column takes the hover wash; placeholder at 30% opacity; transforms ease over 200ms.

### Inputs / Fields
- **Style:** Material outline appearance (provided per component), 4px frame, 1px field-frame outline at rest, ink on hover.
- **Focus:** 2px mark-colour frame and floating label; the global focus ring is suppressed inside fields so the frame is the single indicator. Caret is the mark colour.
- **Error:** stop red via Material's error token; inline form errors use the same stop colour.

### Navigation
- **Top bar:** ground, 56px, 1px rule beneath. Wordmark (18px/800) led by the ink inset arrow square.
- **Links:** 600 weight, muted ink, turning ink on hover; active link is ink with a 3px mark-colour inset underline running the full bar height. 150ms colour and underline fade on the expo ease-out.
- **Theme toggle:** 44px square, 4px corners, no fill, muted ink turning ink on hover. Cycles system, light, dark; the stroked 20px SVG glyph shows the current preference (half-filled disc, sun, moon) and the accessible label names it.
- **Log out:** muted text button, right-aligned, 44px minimum target.

### Stage Switcher (phones)
A row of text tabs above the board under 600px, sitting on a 1px rule and scrolling horizontally without a scrollbar. Each tab is the stage name with its count at 800, 600 weight, muted, 44px tall; the current tab is ink with the 3px mark underline, the same device as the active nav link.

### Next-Step Band (signature)
Full-width signal-yellow strip under the top bar, identical in both schemes. Black inset arrow square (44px), one sentence with a bold lead-in, the band numeral with "dni bez odpowiedzi", a queue count ("1 z N", 13px/700), and the black dismiss button. Shows one message at a time; after dismiss the next flips in from above (translateY -40% and fade, 240ms, `cubic-bezier(0.16, 1, 0.3, 1)`), instant under reduced motion.

### Application Dialog
Panel-raised Material dialog with the two-column form (12px column gap; one column on phones, full-screen on phones). When editing, two rule-topped sections follow the form, each headed at stage-label size: **Notes**, an add row (2-line textarea beside an outlined "Dodaj notatkę" button, stacked on phones) over a list of notes separated by 1px rules, each with a muted 13px timestamp and a muted underlined "Usuń notatkę" that turns stop red on hover; **Stage history**, an ordered list read top to bottom like a departures board: a 110px muted date column beside the stage name at 600, rows divided by 1px rules. The delete confirmation replaces the action row in place.

### Stats Sign Panel
One panel block split into three signs by 1px rules; each sign sets its 48px numeral before its label on a shared baseline. Data panels below hold a bar chart (ink 4px-cornered bars, muted ticks, rule-coloured grid, no animation; colours resolved from the tokens and re-resolved on scheme change) and a right-aligned numeric table with rule-separated rows.

## Do's and Don'ts

### Do:
- **Do** map new Material components through the `--mat-sys-*` remap in the global stylesheet rather than styling them one by one.
- **Do** declare every new colour once as a `--jt-*` property with `light-dark(day, night)` and check it in both schemes.
- **Do** draw thin wayfinding marks (focus, active state, focused field) with `--jt-mark`, never with raw yellow.
- **Do** keep every corner at 4px (`--mat-sys-corner-*` are all 4px).
- **Do** set counts as 800-weight numerals beside their label on one baseline, with tabular figures.
- **Do** use the inset arrow square (ink on ground, or black on yellow) as the product's sign glyph, drawn as inline SVG.
- **Do** keep keyboard focus as a 2px mark-colour outline with 2px offset, switching to black on yellow surfaces.
- **Do** give every touch control a 44px minimum target on phones.
- **Do** give every transition a reduced-motion fallback of none.

### Don't:
- **Don't** use signal yellow for anything that is not wayfinding: no yellow primary buttons, decorative fills or chart colours.
- **Don't** put a yellow hairline, outline or text on the day ground; use the mark or overdue token.
- **Don't** hard-code hex values in component styles or chart options; read the `--jt-*` tokens.
- **Don't** colour-code stages or statuses; no coloured status pills. Rejected cards dim, they do not turn red.
- **Don't** put drop shadows on cards, panels or the band; shadows are for overlays and the drag preview only.
- **Don't** use pill or circular shapes; Material's full-corner token is pinned to 4px.
- **Don't** set signs or buttons in uppercase with letter-spacing.
