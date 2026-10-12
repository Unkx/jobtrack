# Product

## Register

brand

## Users

Dwie grupy, traktowane po równo:

- **Osoba szukająca pracy w IT** (junior/mid). Wysyła kilka–kilkanaście aplikacji tygodniowo z różnych źródeł
  (LinkedIn, Just Join IT, polecenia), gubi się w tym, kto odpisał i komu trzeba się przypomnieć. Wraca do
  aplikacji codziennie na kilka minut: przesuwa karty po rozmowach, dodaje nowe wysyłki, raz w tygodniu
  patrzy na statystyki.
- **Rekruter / tech lead oglądający portfolio.** Ma 30–60 sekund na GIF w README albo uruchomione demo.
  Ocenia, czy autor umie dowieźć dopracowany produkt, a nie tylko CRUD.

## Product Purpose

JobTrack porządkuje proces rekrutacyjny: tablica Kanban z etapami aplikacji, przypomnienia o follow-upie
i analityka skuteczności liczona z historii zmian. Sukces dla użytkownika: w każdej chwili wie, na czym stoi,
i widzi, które źródła ofert faktycznie działają. Sukces dla projektu jako portfolio: produkt wygląda
i zachowuje się jak coś, za co ktoś płaci, mimo że zakres jest celowo mały.

## Brand Personality

**Spokojny, precyzyjny, rzeczowy.** Punkt odniesienia: Linear – gęstość informacji bez przytłoczenia, cicha
typografia, akcent kolorystyczny tylko tam, gdzie coś naprawdę wymaga uwagi (przypomnienie, oferta,
odrzucenie). Ton tekstów: konkretny i życzliwy, bez wykrzykników i bez oceniania użytkownika.

Główna emocja: **kontrola** – „wiem, na czym stoję”. Rekrutacja jest chaotyczna; interfejs jest jej
przeciwieństwem.

## Anti-references

- **Domyślny Angular Material** – niebieski azure, standardowe komponenty Google bez własnej tożsamości
  (obecny stan projektu, od którego odchodzimy).
- **Jira i korpo-narzędzia** – przeładowane panele, szarość, biurokratyczna gęstość opcji, wszystko
  równie ważne.

## Design Principles

1. **Stan na pierwszy rzut oka.** Najważniejsza informacja ekranu (gdzie są moje aplikacje, co wymaga
   działania) czytelna bez klikania i bez czytania wszystkiego.
2. **Akcent oznacza działanie.** Kolor i waga wizualna zarezerwowane dla rzeczy, które wymagają reakcji;
   reszta jest cicha.
3. **Dane z historii, pokazane uczciwie.** Statystyki tłumaczą, skąd się biorą; puste i niepewne stany
   mówią to wprost zamiast udawać zera.
4. **Mały zakres, pełne wykończenie.** Lepiej mniej elementów dopracowanych w każdym stanie (pusty, ładowanie,
   błąd, klawiatura) niż więcej funkcji na pół gwizdka.

## Accessibility & Inclusion

WCAG 2.2 AA: kontrast tekstu ≥ 4.5:1 (duży ≥ 3:1), pełna obsługa klawiaturą – także zmiana etapu bez
przeciągania myszą, widoczny focus, `prefers-reduced-motion` respektowane, kolor nigdy jedynym nośnikiem
informacji (etap, status przypomnienia zawsze także tekstem).
