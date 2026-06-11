# Job Tracker Android App — Full Project Spec

> Generated from DeepSeek conversation + opencode refinements
> Link: https://chat.deepseek.com/share/wzvjr4rjbg1paj7h1o

---

## 1. Problem Statement

User finds job opportunities scattered across multiple platforms:
- WhatsApp groups
- Telegram groups
- LinkedIn, email, job boards
- Screenshots, PDFs, links

It's difficult to track deadlines, roles, companies, industries, and reminders. Goal is a single app that centralizes capture, parsing, reminders, and calendar sync — with zero typing.

---

## 2. Core Philosophy

- **Free approach** — no paid APIs, no Google Cloud billing, no OpenAI
- **Local-first** — all data stays on device (privacy, offline)
- **1-tap capture** — share from any app, app auto-parses
- **Regex + rule-based parsing** (no ML dependency for MVP)
- **No backend** — Room DB locally, backup via JSON export

---

## 3. Architecture Overview

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Android App    │────▶│  Local Room DB  │────▶│  WorkManager    │
│  (Kotlin+Jetpack│     │  (Jobs, Cos,    │     │  (Reminders)    │
│   Compose)      │     │   Reminders)    │     │                 │
└────────┬────────┘     └─────────────────┘     └────────┬────────┘
         │                                                │
         ▼                                                ▼
┌─────────────────┐                             ┌─────────────────┐
│  Sharing        │                             │  Calendar API   │
│  (Receive text, │                             │  (CalendarContract│
│   links, images)│                             │   — no OAuth)   │
└────────┬────────┘                             └─────────────────┘
         │
         ▼
┌─────────────────┐     ┌─────────────────┐
│  Parser Engine  │────▶│  ML Kit OCR     │
│  (rules +       │     │  (for images)   │
│   tiny ML)      │     └─────────────────┘
└─────────────────┘
```

- Language: Kotlin
- UI: Jetpack Compose
- DB: Room (SQLite)
- Reminders: WorkManager + AlarmManager fallback
- Calendar: Android CalendarContract (no API key, no OAuth)
- OCR: ML Kit Text Recognition (on-device, free)
- URL scraping: Jsoup
- Telegram: Bot API (free, unlimited)

---

## 4. Visual Design — Liquid Glass + iOS 26

### 4.0 Design Language Overview

The app uses a **Liquid Glass** aesthetic inspired by modern iOS:

- **Glassmorphism** — frosted translucent layers with backdrop blur, subtle borders, and depth
- **Liquid** — organic blob-shaped gradient backgrounds, morphing transitions, fluid motion
- **iOS 26** — large bold typography, generous whitespace, spring animations, translucent chrome, continuous rounded corners
- **Neumorphic soft shadows** — light source from top-left, subtle inner glow on pressed states

### 4.1 Design System Tokens

#### Color Palette

```
Light Mode:
  Background:        #F2F2F7 (system gray 6)
  Glass card:        rgba(255,255,255,0.72) + backdropBlur(20px)
  Glass border:      rgba(255,255,255,0.5)
  Glass shadow:      rgba(0,0,0,0.08) / y:4 blur:12
  Primary accent:    #007AFF (iOS blue)
  Urgent accent:     #FF3B30 (iOS red)
  Warning accent:    #FF9500 (iOS orange)
  Success accent:    #34C759 (iOS green)
  Text primary:      #1C1C1E
  Text secondary:    #3A3A3C (opacity 0.6)
  Blob gradient 1:   #007AFF → #5856D6 (blue→purple)
  Blob gradient 2:   #34C759 → #00C7BE (green→teal)

Dark Mode:
  Background:        #1C1C1E (system gray)
  Glass card:        rgba(44,44,46,0.78) + backdropBlur(20px)
  Glass border:      rgba(255,255,255,0.12)
  Glass shadow:      rgba(0,0,0,0.3) / y:4 blur:12
  Primary accent:    #0A84FF (iOS blue dark)
  Blob gradient 1:   #0A84FF → #5E5CE6
  Blob gradient 2:   #30D158 → #62E7D4
```

#### Typography

```
Family:     SF Pro Display (iOS default) or Inter / Plus Jakarta Sans
Scale:
  Large title:   34pt / bold / -0.5 tracking   (dashboard headers)
  Title 1:       28pt / bold / 0 tracking      (screen titles)
  Title 2:       22pt / semibold / 0 tracking  (section headers)
  Headline:      17pt / semibold / 0 tracking  (card titles)
  Body:          17pt / regular / 0 tracking   (content)
  Subhead:       15pt / regular / -0.2 tracking (metadata)
  Footnote:      13pt / regular / 0 tracking   (auxiliary)
  Caption:       12pt / regular / 0 tracking   (badges, timestamps)
```

#### Corner Radius

```
  Cards:          28dp (continuous curve, not just rounded rect)
  Buttons:        14dp
  Bottom sheet:   18dp top corners
  Input fields:   12dp
  Badges/Pills:   8dp
```

#### Shadows & Elevation

```
  Card (rest):     blur 12, y 4, spread 0, rgba(0,0,0,0.08)
  Card (pressed):  blur 4, y 1, spread 0, rgba(0,0,0,0.12)
  Sheet:           blur 24, y -8, spread 0, rgba(0,0,0,0.15)
  FAB:             blur 16, y 8, spread 0, rgba(0,0,0,0.12)
```

#### Animation Tokens

```
Spring defaults (used everywhere):
  dampingRatio:    0.825
  stiffness:       300.0
  → Natural iOS bounce, 0.3-0.5s duration

Entrance (staggered):
  offset:          100ms between siblings
  initial:         alpha 0, translateY 24dp
  target:          alpha 1, translateY 0

Morphing (liquid shapes):
  duration:        600ms
  easing:          spring(damping=0.7, stiffness=200)
  Use:             blob shape keyframes

Press feedback:
  scale:           0.97
  duration:        100ms
  easing:          spring(damping=0.9, stiffness=500)
```

### 4.2 Glass Component Library

Every UI element follows these glass construction rules:

```
Glass Card:
  ┌─────────────────────────────────────┐
  │  background: rgba(255,255,255,0.72)  │  ← Light mode
  │  border: 0.5px solid rgba(255,255,255,0.5)
  │  cornerRadius: 28dp                  │
  │  shadow: 0 4px 12px rgba(0,0,0,0.08) │
  │  backdropFilter: blur(20px)          │
  │  padding: 20dp                       │
  └─────────────────────────────────────┘

Glass Tab Bar:
  ┌─────────────────────────────────────┐
  │  background: rgba(255,255,255,0.84)  │
  │  backdropFilter: blur(40px)          │
  │  border-top: 0.5px rgba(0,0,0,0.08)  │
  └─────────────────────────────────────┘

Glass Bottom Sheet:
  ┌─────────────────────────────────────┐
  │  background: rgba(255,255,255,0.90)  │
  │  cornerRadius: 18dp (top only)       │
  │  backdropFilter: blur(30px)          │
  │  grabHandle: 5dp × 36dp, center     │
  └─────────────────────────────────────┘

Glass Input:
  ┌─────────────────────────────────────┐
  │  background: rgba(118,118,128,0.12)  │  ← System fill
  │  cornerRadius: 12dp                  │
  │  placeholder: secondary text         │
  │  focused: primary accent border      │
  └─────────────────────────────────────┘
```

### 4.3 Background — Liquid Blob System

Instead of flat color, the background features subtle animated gradient blobs:

```
┌────────────────────────────────────────────┐
│  Base: #F2F2F7 (light) / #1C1C1E (dark)    │
│                                            │
│  ┌─── Animated blob 1 ───┐                │
│  │  gradient: blue→purple │  ← Morphing   │
│  │  opacity: 0.15        │    SVG path    │
│  │  scale: 0.6-0.8       │    6s cycle    │
│  └────────────────────────┘                │
│                                            │
│  ┌─── Animated blob 2 ───┐                │
│  │  gradient: green→teal  │  ← Counter-   │
│  │  opacity: 0.10        │    phase       │
│  │  scale: 0.4-0.6       │    8s cycle    │
│  └────────────────────────┘                │
└────────────────────────────────────────────┘

Implementation:
  - Draw as SVG paths with animated bezier control points
  - Morph between 3 keyframes per blob (regular pentagon → star → blob)
  - Use Compose Canvas + Path + Animatable
  - background: fixed, cards float above
```

### 4.4 Screen-by-Screen Visual Design

#### 4.4.1 Onboarding

```
┌────────────────────────────────────────────┐
│                                            │
│           ┌────────────────────┐           │
│           │  Glass demo card    │ ← Animate │
│           │  with parsed result │   in      │
│           │  "Google · UX"     │   spring   │
│           └────────────────────┘           │
│                                            │
│     Track your job search                  │  ← Large title 34pt
│     in one place                           │
│                                            │
│     Share a job post from any app.         │  ← Body 17pt
│     We'll extract the details.             │
│                                            │
│            ┌──────────────────┐            │
│            │ Get Started      │ ← Glass    │
│            └──────────────────┘   button   │
│                                            │
│  ┌────────────────────────────────────┐    │
│  │  Share from WhatsApp ↗           │    │  ← Glass prompt
│  └────────────────────────────────────┘    │
│                                            │
└────────────────────────────────────────────┘
```

**Animation sequence on launch:**
1. Background blobs fade in (600ms)
2. Demo card springs up from bottom (spring, 500ms)
3. Title text fades + slides up (400ms, delay 200ms)
4. Description fades in (300ms, delay 400ms)
5. Get Started button scales in (spring 0.95→1.0, delay 500ms)

#### 4.4.2 Home Dashboard

```
┌────────────────────────────────────────────┐
│                                            │
│  Blob gradient background (animated)       │
│                                            │
│  ┌─── Glass smart bar ──────────────────┐  │
│  │  5 deadlines this week · 3 follow-ups │  │
│  └───────────────────────────────────────┘  │
│                                            │
│  [Urgent]  [This Week]  [All]     ← Pills  │
│                                            │
│  ┌─── Glass card (28dp radius) ─────────┐  │
│  │  Google                               │  │  ← Headline 17pt
│  │  UX Designer                          │  │  ← Body 15pt (gray)
│  │                                       │  │
│  │  🔴 Deadline in 2 days · Jun 13      │  │  ← Red accent dot
│  │                                       │  │
│  │  Source: WhatsApp · IT Group         │  │  ← Subhead 13pt
│  │                                       │  │
│  │  [Mark Applied]            [→ Open]  │  │  ← Glass mini buttons
│  └───────────────────────────────────────┘  │
│                                            │
│  ┌─── Glass card ─────────────────────────┐ │
│  │  Microsoft                             │ │
│  │  SWE II                                │ │
│  │  🟡 Deadline in 5 days · Jun 16       │ │
│  │  Source: Telegram · Job Alerts        │ │
│  │  [Mark Applied]            [→ Open]   │ │
│  └───────────────────────────────────────┘ │
│                                            │
│  ┌─── Glass card ─────────────────────────┐ │
│  │  3 more this week →                    │ │
│  └───────────────────────────────────────┘ │
│                                            │
│  ┌─────┐  ┌─────┐  ┌──────────┐          │
│  │  5  │  │  3  │  │    2     │  ← Glass  │
│  │Saved│  │Applied│ │Interviews│    stat   │
│  └─────┘  └─────┘  └──────────┘    bubbles│
│                                            │
│  ┌───────────── Glass Tab Bar ───────────┐ │
│  │  📊 Home    📅 Calendar    ⚙️ Settings│ │
│  └────────────────────────────────────────┘ │
└────────────────────────────────────────────┘

FAB: Glass circle with "+", shadow, pressed scale 0.92
```

**Card entrance animation:** Each card springs in sequentially with stagger (80ms delay between siblings), from alpha 0 / translateY 20dp.

#### 4.4.3 Share → Save Bottom Sheet

```
┌────────────────────────────────────────────┐
│  Blurred overlay of WhatsApp behind        │
│                                            │
│  ┌─── Glass Bottom Sheet ────────────────┐ │
│  │ ─── (grab handle)                     │ │
│  │                                       │ │
│  │ From WhatsApp · IT Jobs Group         │ │  ← Subhead, gray
│  │                                       │ │
│  │ Company  [Google             ▼]  🟢  │ │  ← Glass input
│  │ Role     [UX Designer        ▼]  🟢  │ │  ← confidence dot
│  │ Deadline [Jun 13, 2026       📅]  🟡 │ │
│  │ Industry [Tech               ▼]  🟢  │ │
│  │ URL      [careers.google.com]     🔗 │ │
│  │                                       │ │
│  │ Raw text: "Hiring: Google UX..."      │ │  ← Expandable
│  │                                       │ │
│  │ ┌──────────────────────────┐ ┌──────┐ │ │
│  │ │ ✓ Save & Set Reminder    │ │ Edit │ │ │  ← Glass buttons
│  │ └──────────────────────────┘ └──────┘ │ │
│  └───────────────────────────────────────┘ │
└────────────────────────────────────────────┘
```

**Animation:**
1. Sheet slides up from bottom (spring, 400ms)
2. Header slides down from top of sheet (200ms)
3. Fields stagger in (each 80ms delay, from alpha 0 / x -8dp)
4. Confidence dots pulse once (scale 1→1.2→1, 300ms)

**Confidence indicator:**
- 🟢 Green — auto-extracted, high confidence
- 🟡 Yellow — guessed (needs review)  
- 🔴 Red — missing (user must fill)

#### 4.4.4 Job Detail Screen

```
┌────────────────────────────────────────────┐
│                                            │
│  Large blurred header photo (company logo  │
│  area or gradient blob)                    │
│                                            │
│  ← Back         [⋮ More]                  │  ← Translucent nav
│                                            │
│  ┌─── Glass card ───────────────────────┐  │
│  │  Google                                │  │  ← Title 1 28pt
│  │  UX Designer                           │  │  ← Title 2 22pt (gray)
│  │                                       │  │
│  │  🟢 Saved                             │  │  ← Status pill
│  │                                       │  │
│  │  📅 Jun 13, 2026                      │  │
│  │  ⏰ Reminder: Jun 11, 9 AM           │  │
│  │  🔗 careers.google.com               │  │
│  └───────────────────────────────────────┘  │
│                                            │
│  ┌─── Glass card ─── Timeline ──────────┐  │
│  │  📥 Found          Jun 10    ●━━━○   │  │  ← Filled dot
│  │  🟡 Saved          Jun 10    ●━━━○   │  │    → next
│  │  ⚪ Applied        —         ○━━━○   │  │  ← Tap to fill
│  │  ⚪ Interview       —        ○━━━○   │  │
│  │  ⚪ Offer/Reject    —        ○━━━○   │  │
│  │    [+ Add Event]                     │  │
│  └───────────────────────────────────────┘  │
│                                            │
│  ┌─── Glass card ───────────────────────┐  │
│  │  [✓ Mark Applied]  [📅 Schedule Int] │  │  ← Glass buttons
│  │  [📝 Notes]        [📄 Attachments]  │  │
│  └───────────────────────────────────────┘  │
│                                            │
└────────────────────────────────────────────┘
```

**Timeline interaction:** Tap an empty circle → spring fill animation (scale 0→1.2→1, color morphs gray→green). Haptic feedback on complete.

#### 4.4.5 Calendar Screen

```
┌────────────────────────────────────────────┐
│                                            │
│  ← June 2026 →         [Today]            │  ← Large title
│                                            │
│  ┌─── Glass calendar grid ──────────────┐  │
│  │  Mo  Tu  We  Th  Fr  Sa  Su          │  │
│  │       1    2    3    4    5    6     │  │
│  │   7    8    9   ●10  11   12  ●13   │  │
│  │ ●14  15  ●16  17   18   19   20     │  │
│  │  21   22   23   24   25   26   27    │  │
│  │  28   29   30                        │  │
│  └──────────────────────────────────────┘  │
│                                            │
│  ┌─── Glass card ───────────────────────┐  │
│  │  Jun 13 — 2 deadlines                │  │
│  │                                       │  │
│  │  🔴 Google · UX Designer             │  │
│  │     Apply by EOD                     │  │
│  │     [✓ Mark Applied]                 │  │
│  │                                       │  │
│  │  🟡 Amazon · PM                      │  │
│  │     Apply by 5 PM                    │  │
│  │     [✓ Mark Applied]                 │  │
│  └───────────────────────────────────────┘  │
│                                            │
└────────────────────────────────────────────┘
```

**Calendar day dots:** Small gradient circles (red for urgent, yellow for upcoming). Tap dot → date card list springs in below.

#### 4.4.6 Analytics Screen

```
┌────────────────────────────────────────────┐
│                                            │
│  My Job Search Stats                       │  ← Large title
│                                            │
│  ┌─────┐  ┌─────┐  ┌──────────┐          │
│  │ 42  │  │ 12  │  │    3     │  ← Glass  │
│  │ Apps│  │ Int │  │  Offers  │    stat    │
│  │     │  │     │  │          │    bubbles │
│  │ 7%→28%→25% conversion                   │
│  └─────┘  └─────┘  └──────────┘          │
│                                            │
│  ┌─── Glass card ───────────────────────┐  │
│  │  Applications by Source              │  │
│  │                                       │  │
│  │  WhatsApp  ██████████░░░  20         │  │  ← Glass bars
│  │  Telegram  ██████░░░░░░  12         │  │  ← Animated width
│  │  LinkedIn  ████░░░░░░░░   8         │  │
│  │  Other     ██░░░░░░░░░░   2         │  │
│  └──────────────────────────────────────┘  │
│                                            │
│  ┌─── Glass card ───────────────────────┐  │
│  │  Weekly Activity                     │  │
│  │  ┌─── Sparkline ──────────────┐     │  │
│  │  │  ╱╲    ╱╲    ╱╲           │     │  │  ← Animated path
│  │  │ ╱  ╲  ╱  ╲  ╱  ╲  ╱╲    │     │  │
│  │  │╱    ╲╱    ╲╱    ╲╱  ╲───│     │  │
│  │  └──────────────────────────┘     │  │
│  │  M  T  W  T  F  S  S  M  T  W   │  │
│  └──────────────────────────────────────┘  │
│                                            │
└────────────────────────────────────────────┘
```

**Bar animation:** Bars grow from bottom with spring animation when screen appears. Sparkline path is drawn with trimPathEnd animation (0→1 over 800ms).

### 4.5 Navigation Structure

```
Glass Tab Bar (translucent):
┌────────────────────────────────────────────┐
│  📊 Home       📅 Calendar       ⚙️ More   │
│  (active)      (inactive)       (inactive) │
│     ●                                 │
└────────────────────────────────────────────┘

Tab bar properties:
  - background: rgba(255,255,255,0.84)
  - backdropFilter: blur(40px)
  - border-top: 0.5px rgba(0,0,0,0.08)
  - height: 84dp (includes safe area)
  - icon + label layout (iOS style)
  - active: tinted accent color + spring scale on tap
```

### 4.6 Notification UX (iOS 26 style)

| Type | Title | Body | Actions |
|------|-------|------|---------|
| Deadline | **Apply to Google** | UX Designer — deadline Jun 13 (2 days) | [Applied!] [Snooze] |
| Follow-up | **Follow up with Microsoft** | You applied 7 days ago | [Opened] [Snooze] |
| Interview | **Interview tomorrow: Google** | UX Designer · 10:00 AM · Virtual | [Reschedule] |
| Weekly | **Your week ahead** | 5 deadlines, 2 interviews, 3 follow-ups | [Open] |

**Grouping:** Multiple reminders for same day → notification summary (iOS style stack).
**Snooze:** 1h / 1d / After deadline.

### 4.7 Animation & Micro-interaction Spec

| Element | Animation | Spec |
|---------|-----------|------|
| Screen transition | Slide from right | spring(0.825, 300), 350ms |
| Card entrance | Fade + slide up | staggered 80ms, spring(0.825, 300) |
| Bottom sheet | Slide up from bottom | spring(0.8, 350), 400ms |
| Pill switch | Underline slide | spring(0.85, 400), 250ms |
| Status change | Badge morph + haptic | spring(0.9, 500), 200ms |
| Swipe to delete | Card follows finger + fade | threshold 40%, undo snackbar 5s |
| Tap feedback | Scale 1→0.97→1 | spring(0.9, 500), 100ms |
| Pull to refresh | Spinning arc + glass blur | spring(0.8, 200), max 60pt |
| Empty state → data | Illustration fades out, cards fade in | crossfade 400ms |
| Blob morph | Path keyframes | spring(0.7, 200), 6s cycle |
| Calendar dot tap | Dot → card spring expansion | spring(0.8, 300), 300ms |
| Tab switch | Icon spring scale 1→1.1→1 | spring(0.85, 400), 200ms |
| FAB press | Scale 1→0.9→1 + rotation for close | spring(0.9, 400), 200ms |

### 4.8 Haptic Feedback Map

| Action | Haptic Type |
|--------|-------------|
| Save job | Heavy impact |
| Status change | Medium impact |
| Swipe delete | Light impact |
| Tab switch | Selection feedback |
| Error / missing field | Notification feedback (gentle) |
| Milestone reached | Success feedback |

### 4.9 Empty States (Glass Style)

```
No jobs yet:
┌────────────────────────────────────────────┐
│                                            │
│       🎯 (large, subtle opacity)          │
│                                            │
│  No jobs saved yet                        │  ← Title 1
│                                            │
│  Share a job post from WhatsApp or         │  ← Body
│  Telegram to get started.                  │
│                                            │
│  ┌─── Glass tip card ──────────────────┐  │
│  │  💡 Open WhatsApp → long-press a    │  │
│  │     job message → Share → pick      │  │
│  │     "Job Tracker"                    │  │
│  └──────────────────────────────────────┘  │
│                                            │
└────────────────────────────────────────────┘

All caught up:
┌────────────────────────────────────────────┐
│                                            │
│    ✓ (animated checkmark)                  │
│                                            │
│  All caught up!                            │
│                                            │
│  No upcoming deadlines. You're on top     │
│  of things.                                │
│                                            │
└────────────────────────────────────────────┘
```

### 4.10 Interaction Map

| Gesture | Surface | Result |
|---------|---------|--------|
| Tap | Job card | Opens detail screen |
| Tap | Timeline circle | Marks stage complete |
| Tap | Pill filter | Filters card list |
| Swipe left | Job card | Shows "Archive" action |
| Swipe right | Job card | Shows "Mark Applied" action |
| Long press | Job card | Enters batch select mode |
| Pull down | Dashboard | Refreshes data |
| Tap | Calendar dot | Shows day's jobs |
| Swipe | Calendar header | Changes month |
| Tap + hold | FAB | Shows quick actions menu |

### 4.11 UX Anti-Patterns — Explicitly Avoid

| Anti-pattern | Why | Better |
|--------------|-----|--------|
| Ask all permissions on first launch | Users feel overwhelmed & deny | Request in context of first use |
| Show empty form on manual add | Feels like work | Show "Share from WhatsApp" first |
| Hidden share-to-app capability | Users won't discover it | Onboarding + permanent hint card |
| Full screen editor on share | Loses WhatsApp context | Bottom sheet (keeps user in context) |
| No undo | Users fear tapping wrong | Snackbar undo for 5 seconds |
| Forcing field inputs | Users abandon | Auto-save partial, prompt later |
| Sharp corners | Feels dated, not iOS | Continuous 28dp radius everywhere |
| Flat colors | Looks cheap | Glass + gradient blobs |
| Hard cut transitions | Jarring | Spring animations + blur transitions |

### 4.12 Compose Implementation Notes

```kotlin
// Glass modifier (reusable)
fun Modifier.glass(
    blurRadius: Dp = 20.dp,
    backgroundColor: Color = Color.White.copy(alpha = 0.72f),
    borderColor: Color = Color.White.copy(alpha = 0.5f),
    shadowColor: Color = Color.Black.copy(alpha = 0.08f),
    cornerRadius: Dp = 28.dp
): Modifier = this
    .background(backgroundColor)
    .clip(RoundedCornerShape(cornerRadius))
    .border(0.5.dp, borderColor, RoundedCornerShape(cornerRadius))
    .shadow(12.dp, RoundedCornerShape(cornerRadius), shadowColor)
    // Note: backdrop blur requires RenderEffect (API 31+)
    // or use third-party library for < API 31

// Spring animation helper
val springSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

// Staggered entrance
items.forEachIndexed { index, item ->
    val visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 80L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically {
            it / 4   // 25% of height
        }
    ) { item() }
}
```

---

### 4.4.7 iOS 26 Complete Material Stack (Reference)

The iOS 26 Liquid Glass material is built from 6 visual layers that work in concert. This is the target specification.

| Layer | Purpose | Parameter Values | Android Equivalent |
|-------|---------|-----------------|--------------------|
| **Dynamic Translucency** | Real-time background blur | Blur radius: moderate (analogous to `.systemUltraThinMaterial`) | `RenderEffect.createBlurEffect()` or AGSL backdrop sampling |
| **Color Reflection** | Glass absorbs surrounding hue | `.glassEffect(.regular.tint(.myColor))` — .clear for minimal, .regular for prominent | `ColorFilter.tint(color, BlendMode.SrcAtop)` on GraphicsLayer |
| **Edge Refraction & Distortion** | Light bending through curved glass | IOR ≈ 1.5; distortion increases near capsule edges | AGSL: `offset = (uv - center) * IOR * (1.0 - dist)` |
| **Specular Highlights** | Directional glare following touch | Intensity responds to finger position | Shader uniform `u_lightPos` bound to touch coordinates |
| **Fresnel Reflections** | Edge lighting that intensifies at grazing angles | Enabled by default; most visible on dark backgrounds | AGSL: `fresnel = pow(1.0 - dist, 2.0)` |
| **Chromatic Dispersion** | Subtle prismatic color separation at edges | Very subtle — barely noticeable unless looked for | AGSL: separate R/G/B texture samples with increasing offsets |

**Background compatibility:** Glass samples whatever is behind it. Vibrant gradients or photos work best. Avoid extremely busy wallpapers for legibility. Dark mode reduces overall brightness but maintains reflective quality.

### 4.4.8 Shape Merging & Glass Identity

#### Shape Types

| Shape | Use Case | Code |
|-------|----------|------|
| **Capsule** | Default for buttons, pills, tags | `glassEffect()` (no `in:` parameter) |
| **Rounded Rect** | Cards, panels, containers | `glassEffect(in: .rect(cornerRadius: 16))` |
| **Circle** | Icons, avatars, status indicators | `glassEffect(in: .circle)` requires square frame |

#### Container Merging Rules

- Always wrap multiple glass elements in `GlassEffectContainer` — otherwise each renders independently and won't morph fluidly.
- Apply `.glassEffect()` to child views, not the container itself.
- `GlassEffectContainer(spacing: CGFloat)` controls merging distance:
  - **8–12**: Elements blend only when nearly touching
  - **24–32**: Balanced blending (recommended default)
  - **50+**: Elements blend even when far apart

#### Identity Tracking for Morphing

```kotlin
// Android equivalent of iOS @Namespace + glassEffectID
// Compose: use key() or remember { mutableStateOf() } for identity
@Namespace private var namespace  // iOS SwiftUI concept

// iOS: assign stable identity
.glassEffectID("uniqueID", in: namespace)

// Android manual equivalent:
// Use Modifier.key() and track morphing via AnimatedVisibility + animateDpAsState
```

**Transition types in iOS 26:**
1. **Add/Remove morphing** — Elements appear by expanding from an existing glass element
2. **Reposition morphing** — Elements flow between positions (iOS: `matchedGeometryEffect`)
3. **Shape change morphing** — Capsule smoothly expands to roundedRect as content grows

### 4.4.9 iOS 26 Interactive Physics — Exact Reference

When a user touches a glass element, **two independent events** occur simultaneously:

| Response | Effect | Android Implementation |
|----------|--------|----------------------|
| **Directional Glow** | Light-catching highlight at finger position | AGSL: bind `u_lightPos` from `Modifier.pointerInput` |
| **Physical Spring-Scale** | Gentle squish / bottom-anchored expansion | `animateFloatAsState` with spring + scale + offset |

#### Interaction Modes

```
.full      → Both glow + scale (default — Apple News / Safari behavior)
.glowOnly  → Directional glow only (flat nav bars that must stay stable)
.scaleOnly → Spring scaling only (when glow might clash with branding)
```

#### Apple's Reference Spring Parameters (Critically Damped)

```kotlin
// iOS 26 native (SwiftUI reference):
// interpolatingSpring(mass: 1.0, stiffness: 180.0, damping: 27.0, initialVelocity: 0.0)

// Android equivalent:
spring(
    dampingRatio = 1.0f,    // ≥ 1.0 = critically damped, zero bounce
    stiffness   = 180f      // Apple's reference stiffness
)

// Visual behavior:
// - Mass: 1.0
// - Stiffness: 180 (softer than default Compose spring)
// - Damping: 27 (ratio > 1 eliminates bounce, smooth settling)
// - Press: scale 1.0 → 0.97, bottom-anchored (translateY += 2dp)
// - Release: scale 0.97 → 1.0, translateY → 0
// - The glass behaves like a fluid-filled capsule — "squishes" under pressure
```

> **Note:** The current animation spec in 4.7 uses `spring(0.825, 300)` (under-damped, stiffer). For exact iOS 26 matching, prefer `spring(dampingRatio=1.0f, stiffness=180f)`.

### 4.4.10 SwiftUI Reference Implementation (iOS 26 Target Spec)

This is the exact iOS 26 behavior we are replicating on Android. Every animation, response, and visual detail below must be matched.

```swift
// SwiftUI — iOS 26 native. Android must replicate this behavior exactly.
struct LiquidGlassCard: View {
    @State private var isExpanded = false
    @Namespace private var glassNamespace

    var body: some View {
        ZStack {
            // Dynamic gradient background to showcase glass refraction
            LinearGradient(
                colors: [.purple, .blue, .cyan, .mint],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            ).ignoresSafeArea()

            GlassEffectContainer(spacing: 24) {
                VStack(spacing: 20) {
                    // Title card with edge distortion
                    Text("Liquid Glass")
                        .font(.largeTitle.weight(.bold))
                        .foregroundStyle(.white).padding()
                        .glassEffect(.regular.tint(.purple).interactive(),
                                     in: .rect(cornerRadius: 20))
                        .glassEffectID("title", in: glassNamespace)

                    // Interactive button with full physics
                    Button {
                        withAnimation(.interpolatingSpring(
                            mass: 1.0, stiffness: 180,
                            damping: 27, initialVelocity: 0.0
                        )) { isExpanded.toggle() }
                    } label: {
                        HStack {
                            Image(systemName: isExpanded ? "xmark" : "plus")
                            Text(isExpanded ? "Close" : "Expand")
                        }
                        .foregroundStyle(.white)
                        .padding(.horizontal, 24).padding(.vertical, 12)
                    }
                    .buttonStyle(.glassProminent)
                    .glassEffectID("expandButton", in: glassNamespace)

                    // Morphing content panel
                    if isExpanded {
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Morphing Glass").font(.title2.weight(.bold))
                            Text("This panel morphs from the button above...")
                                .font(.body)
                        }
                        .foregroundColor(.white).padding()
                        .glassEffect(.regular.tint(.blue).interactive(),
                                     in: .rect(cornerRadius: 16))
                        .glassEffectID("expandedContent", in: glassNamespace)
                        .transition(.asymmetric(
                            insertion: .opacity.combined(with: .scale(scale: 0.9)),
                            removal: .opacity.combined(with: .scale(scale: 0.9))
                        ))
                    }
                }.padding()
            }
        }
    }
}

// Key Android implementation targets:
// 1. GlassEffectContainer(spacing: 24) → GlassContainer with spacing parameter
// 2. glassEffect(.regular.tint(...).interactive()) → GlassBox with tint + interactive touch
// 3. glassEffectID(...) → unique key per composable for morphing identity
// 4. interpolatingSpring(mass:1, stiffness:180, damping:27) → spring(dampingRatio=1.0, stiffness=180)
// 5. Morphing add/remove → AnimatedVisibility + animateDpAsState for shape transition
// 6. .glassProminent button style → Custom Modifier with elevated glass appearance
```

### 4.4.11 AI Prompt Template (For Instructing Code Generation Models)

Copy this when asking an AI to generate the Android Liquid Glass UI:

```
Build an Android Jetpack Compose screen that replicates the iOS 26 Liquid Glass UI exactly.

Requirements:
1. Glass cards: semi-transparent rgba(255,255,255,0.72) background, 28dp continuous rounded corners,
   backdrop blur 20px (via RenderEffect), 0.5dp white border, soft shadow blur 12dp y 4dp.
2. Animated gradient blob background: two SVG paths morphing on 6s/8s cycles using Compose Canvas.
3. Spring physics: every interactive element uses spring(dampingRatio=1.0, stiffness=180).
   On press: scale 1→0.97, bottom-anchored squish. On release: spring back.
4. Touch-following specular highlight: shader uniform bound to pointer position.
5. Fresnel edge glow: stronger at capsule boundaries, color rgba(0.3,0.5,0.8,0.4).
6. Chromatic dispersion: subtle RGB separation at element edges (offset ~0.02).
7. Shape morphing: capsule↔roundedRect transitions via animateDpAsState.
8. Glass container: multiple glass elements wrapped in GlassContainer for distance-based merging.
9. Hierarchy: content sits UNDER glass (glass is for navigation layers and floating controls only).
10. Background: vibrant gradient that showcases the glass refraction effect.

Tech stack: Kotlin, Jetpack Compose, AGSL shaders, RenderEffect.
Use AndroidLiquidGlassView library or custom AGSL shader. All free/no paid APIs.
```

### 4.4.12 Design Philosophy Summary (iOS 26 Core Principles)

```
1. Content First
   → Glass is used for navigation layers and floating controls.
   → Content sits underneath as the foundation.
   → Glass exists to enhance content, not replace it.

2. Dynamic Material
   → Glass reflects and refracts its surroundings.
   → It intelligently adapts between light and dark environments.
   → The material is alive — it responds to touch, motion, and context.

3. Depth & Dimensionality
   → Physical layers inspired by visionOS.
   → Real-time rendering creates a sense of presence.
   → Elements exist in a 3D space, not a flat 2D surface.

4. Liquid Behavior
   → Glass doesn't just blur — it flows.
   → Shapes merge when close, morph between states, and ripple on touch.
   → The material has physical properties: squish, spring, stretch.
```

---

#### Library Comparison

| Library | Framework | API Min | Key Features | Gradle |
|---------|-----------|---------|-------------|--------|
| **AndroidLiquidGlassView** (QmDeve) | XML / Compose | 21+ | Physically-based refraction & dispersion, customizable blur & tone, real-time edge glow | `com.qmdeve.liquidglass:core:1.0.3` |
| **liquid-glass-android** (Mortd3kay) | Compose | 21+ (AGSL: 33+) | AGSL shaders, blur, distortion, shadows | GitHub |
| **KMPLiquidGlass** (Kashif-E) | Compose MP | 21+ | Blur, refraction, highlights, cross-platform | `com.github.Kashif-E:KMPLiquidGlass:1.0.0` |
| **Prismal** | XML / Kotlin | 21+ | OpenGL ES 2.0, Snell's law double refraction, Fresnel rim highlights, spring physics | GitHub |
| **AppleLiquidGlassForAndroid** | XML / Compose | 21+ | Frosted glass blur, magnification, vibrancy boost, lightweight | GitHub |

#### Jetpack Compose (Recommended — Library)

```kotlin
// Using liquid-glass-android
@Composable
fun LiquidGlassCard() {
    GlassContainer {
        GlassBox(
            modifier = Modifier.size(200.dp),
            blur = 0.7f,
            scale = 0.2f,
            shape = RoundedCornerShape(16.dp),
            tint = Color.White.copy(alpha = 0.1f)
        ) {
            Text("Liquid Glass")
        }
    }
}
```

**Key note:** `GlassContainer` is mandatory for multiple glass elements to merge. Use `animateDpAsState` for morphing corners and `animateFloatAsState` for opacity transitions.

#### XML with View Library

```xml
<com.qmdeve.liquidglass.LiquidGlassView
    android:layout_width="300dp"
    android:layout_height="200dp"
    app:cornerRadius="24dp"
    app:refractionIntensity="1.5"
    app:blurRadius="15dp" />
```

### 4.14 AGSL Shader — Complete Reference (API 33+)

For full control over refraction, Fresnel edge glow, and chromatic dispersion, write a custom AGSL fragment shader.

#### Fragment Shader (`glass_frag.agsl`)

```glsl
uniform shader content;
uniform vec2 iResolution;
uniform vec2 iTouch;
uniform float iTime;

float ior = 1.5;
float fresnelPower = 2.0;
float dispersionStrength = 0.02;
float blurStrength = 0.15;

vec4 main(vec2 fragCoord) {
    vec2 uv = fragCoord / iResolution.xy;
    vec2 center = vec2(0.5);
    float dist = distance(uv, center);

    // — Refraction (Snell's law approximation) —
    float distortion = ior * (1.0 - dist) * 0.08;
    vec2 refractUv = uv + (uv - center) * distortion;

    // — Chromatic dispersion (RGB separation) —
    float rOffset = dispersionStrength * (1.0 - dist);
    float r = content.eval((refractUv + vec2(rOffset, 0.0)) * iResolution.xy).r;
    float g = content.eval(refractUv * iResolution.xy).g;
    float b = content.eval((refractUv - vec2(rOffset, 0.0)) * iResolution.xy).b;
    vec4 color = vec4(r, g, b, 1.0);

    // — Fresnel edge glow —
    float fresnel = pow(1.0 - dist, fresnelPower);
    vec3 edgeColor = vec3(0.3, 0.5, 0.8) * fresnel * 0.4;
    color.rgb += edgeColor;

    // — Specular highlight (touch-following) —
    vec2 touchNorm = iTouch / iResolution.xy;
    float touchDist = distance(uv, touchNorm);
    float specular = smoothstep(0.3, 0.0, touchDist) * 0.25;
    color.rgb += specular;

    return color;
}
```

#### Apply Shader in Compose

```kotlin
@Composable
fun GlassShaderBox(modifier: Modifier = Modifier) {
    val shader = remember { RuntimeShader(ShaderSource.fromAsset("glass_frag.agsl")) }
    Box(
        modifier
            .graphicsLayer {
                renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "content")
            }
    )
}
```

#### Spring Press Animation

```kotlin
val isPressed by remember { mutableStateOf(false) }
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.97f else 1f,
    animationSpec = spring(
        stiffness = 300f,
        dampingRatio = 0.75f
    )
)
Modifier
    .scale(scale)
    .pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                isPressed = true
                tryAwaitRelease()
                isPressed = false
            }
        )
    }
```

### 4.15 Performance Optimizations

| Technique | Benefit | Implementation |
|-----------|---------|---------------|
| **Hardware acceleration** | Mandatory for GPU effects | `android:hardwareAccelerated="true"` in manifest |
| **RenderEffect blur (API 31+)** | GPU-accelerated native blur | `RenderEffect.createBlurEffect()` — no CPU cost |
| **Downsample large surfaces** | ~75% pixel shader reduction | Render glass at 0.5x resolution → scale up |
| **Limit glass to panels** | Battery preservation | Only cards, nav bars — never full-screen |
| **Fallback: API 21–32** | Graceful degradation | Gradient + semi-transparent scrim (no live blur) |
| **Fallback: API < 21** | Wide compatibility | Solid color with shadow, no glass effect |
| **Equatable conformance** | Prevents redundant recomposition | `@Stable` or `.equals()` on glass view models |

### 4.16 Android Liquid Glass — Complete Checklist

#### Visual Material
- [ ] Real-time background blur (RenderEffect or AGSL)
- [ ] Color tint reflecting surrounding content
- [ ] Refraction with IOR ~1.5 (Snell's law)
- [ ] Position-based specular highlights (touch-following)
- [ ] Fresnel edge reflections (stronger at grazing angles)
- [ ] Subtle chromatic dispersion (RGB separation at edges)
- [ ] Edge distortion / bevel at capsule boundaries

#### Interaction Physics
- [ ] Spring scale/offset on press (`spring(stiffness=300, dampingRatio=0.75)`)
- [ ] Directional touch glow (shader uniform `u_lightPos`)
- [ ] Critically damped settle — no bounce
- [ ] Smooth release animation (scale 0.97→1.0)
- [ ] Haptic feedback on press/release

#### Container & Morphing
- [ ] Glass elements wrapped in GlassContainer
- [ ] Shape morphing (capsule ↔ roundedRect) via `animateDpAsState`
- [ ] Distance-based blending merge when < 8–16dp apart
- [ ] Unique ID per glass element for morphing identity
- [ ] Spring-based add/remove transitions

#### Cross-Platform & Fallbacks
- [ ] AGSL shader path (API 33+)
- [ ] Library path for API 21–32 (AndroidLiquidGlassView / liquid-glass-android)
- [ ] Gradient + scrim fallback for API < 21
- [ ] Tested on physical device (not just emulator)

#### Performance Verification
- [ ] Hardware acceleration enabled
- [ ] Blur radius ≤ 20–24dp on target device
- [ ] Downsample factor ≤ 0.5 for heavy scenes
- [ ] Profile GPU Rendering — stays green
- [ ] No frame drops during spring animations

---

## 5. Data Models (Room Entities)

### 5.1 Company Table
```kotlin
@Entity(tableName = "companies")
data class Company(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val industry: String?,
    val website: String?,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)
```

### 5.2 Job Table (core)
```kotlin
@Entity(
    tableName = "jobs",
    foreignKeys = [ForeignKey(
        entity = Company::class,
        parentColumns = ["id"],
        childColumns = ["companyId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index(value = ["deadline"]), Index(value = ["status"])]
)
data class Job(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val companyId: Long?,
    val role: String,
    val industry: String?,
    val deadline: Long?,
    val source: String,              // "WhatsApp", "Telegram", "LinkedIn", "Manual"
    val sourceGroup: String?,
    val originalText: String?,
    val status: JobStatus,
    val notes: String?,
    val applicationUrl: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class JobStatus {
    SAVED, APPLIED, INTERVIEW_SCHEDULED, REJECTED, OFFER, ARCHIVED
}
```

### 5.3 Reminder Table (multiple per job)
```kotlin
@Entity(
    tableName = "reminders",
    foreignKeys = [ForeignKey(
        entity = Job::class,
        parentColumns = ["id"],
        childColumns = ["jobId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jobId: Long,
    val remindAt: Long,
    val type: ReminderType,
    val message: String,
    val triggered: Boolean = false
)

enum class ReminderType { APPLY, FOLLOW_UP, INTERVIEW }
```

### 5.4 ParsingRule Table (customizable user rules)
```kotlin
@Entity(tableName = "parsing_rules")
data class ParsingRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val regexPattern: String,
    val targetField: String,    // "deadline", "company", "role"
    val priority: Int = 0,
    val enabled: Boolean = true
)
```

---

## 5. Sharing & Capture

### 5.1 Android Manifest
```xml
<activity android:name=".ShareReceiverActivity">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="image/*" />
    </intent-filter>
</activity>
```

### 5.2 ShareReceiver logic
- Receive `text/plain` → parse with ParserEngine → show edit dialog
- Receive `image/*` → run ML Kit OCR → parse extracted text
- User taps "Save" → stores in Room + creates reminders + writes calendar event

---

## 6. Parser Engine — The Core

Takes raw text (or scraped URL content) → outputs `ParsedJob`:

```kotlin
data class ParsedJob(
    var company: String? = null,
    var role: String? = null,
    var deadline: Long? = null,
    var industry: String? = null,
    var url: String? = null,
    var confidence: Float = 0f
)
```

### 6.1 Two-Stage Strategy (opencode suggestion)
1. **Fast regex pass** — patterns for company, role, deadline
2. **Fallback heuristic pass** — if confidence < 0.6, show quick-edit dialog

### 6.2 Regex Patterns

**Company:**
```
(?:at|@|from|join(?:ing)?|company[:：\s]+)([A-Z][a-z0-9]+(?:\s+[A-Z][a-z0-9]+){0,2})
```

**Role:**
```
(?:hiring|looking for|role[:：\s]+|position[:：\s]+)([A-Za-z0-9\s]+(?:Engineer|Developer|Analyst|Manager|Designer|Consultant|Specialist|Lead|Director|Intern|Associate|Architect))
```

**Deadline (multiple patterns):**
```
(?:deadline|apply by|closing date|last date)[:：\s]+(\d{1,2}[/-]\d{1,2}[/-]\d{2,4})
(?:in|within)\s+(\d+)\s+days?
(?:by\s+)(\d{1,2}(?:st|nd|rd|th)?\s+[A-Za-z]+\s+\d{4})
tomorrow|next week|next month
```

**URL:**
```
(https?://[\w\-._~:/?#\[\]@!$&'()*+,;=]+)
```

### 6.3 Industry Detection
Simple keyword mapping:
- "tech", "software", "engineer", "developer", "IT" → "Tech"
- "bank", "finance", "accountant", "audit" → "Finance"
- "health", "medical", "doctor", "nurse" → "Healthcare"
- Otherwise → null (user picks)

---

## 7. Reminder System

### 7.1 Default Reminders on Job Creation
- 2 days before deadline (type = APPLY)
- 1 day before deadline (type = APPLY)
- User can add custom reminders (e.g., "Follow up with HR")

### 7.2 WorkManager Setup
```kotlin
val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
    .setInitialDelay(calculateDelay(remindAt), TimeUnit.MILLISECONDS)
    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
    .addTag("reminder_${jobId}")
    .build()
WorkManager.getInstance(context).enqueue(workRequest)
```

---

## 8. Calendar Sync — Free Method

Use `CalendarContract` (no OAuth, no API key, no Google Cloud):

```kotlin
fun addCalendarEvent(context: Context, job: Job, companyName: String) {
    val values = ContentValues().apply {
        put(CalendarContract.Events.DTSTART, job.deadline)
        put(CalendarContract.Events.DTEND, job.deadline + 3600000)
        put(CalendarContract.Events.TITLE, "Apply: ${job.role} @ $companyName")
        put(CalendarContract.Events.DESCRIPTION, "Source: ${job.source}\nNotes: ${job.notes}")
        put(CalendarContract.Events.CALENDAR_ID, getDefaultCalendarId(context))
        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
    }
    context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
}
```

Requires `WRITE_CALENDAR` permission once. Events sync to Google/Outlook automatically.

---

## 9. Telegram Auto-Pull (Advanced)

**Method (free):** User forwards job posts to a private Telegram channel → bot reads via Bot API.

Steps:
1. Create bot via @BotFather (free, no billing)
2. Create private Telegram channel
3. Add bot as admin (can read messages)
4. App polls `https://api.telegram.org/bot<TOKEN>/getUpdates` via WorkManager (every 30 min)
5. Parse each message → create job entry or notify user

---

## 10. Permissions (All Free)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

---

## 11. Dependencies (All Open Source / Free)

```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Networking (URL scraping)
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // OCR
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // JSON
    implementation("com.google.code.gson:gson:2.11.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

---

## 12. User Interface (Jetpack Compose Screens)

### 12.1 Main Dashboard
- Upcoming deadlines (sorted, limited to 20)
- Jobs grouped by status (SAVED, APPLIED, INTERVIEW, REJECTED, OFFER, ARCHIVED)
- FAB for quick manual add
- "Maybe later" section — auto-archives after 14 days (opencode suggestion)

### 12.2 Job Edit/Create Screen
- Company (autocomplete from existing)
- Role
- Deadline (DatePicker)
- Industry (dropdown/autosuggest)
- Source (prefilled from share)
- Application URL
- Notes
- Reminder section: toggles + custom reminders

### 12.3 Calendar View (optional)
- Embedded calendar or "Open in Calendar" intent

### 12.4 Settings Screen
- Default reminder toggles
- Parsing rules manager
- Telegram bot token setup
- Export/Import JSON

---

## 13. Build Plan (Recommended Order — opencode suggestion)

| Phase | Module | Why First |
|-------|--------|-----------|
| **1** | **ParserEngine + tests** | Core value, hardest part. Test with 20-30 real messages |
| **2** | **Data layer (Room entities + DAOs)** | Foundation for everything |
| **3** | **ShareReceiverActivity** | Capture pipeline |
| **4** | **Reminder system (WorkManager)** | Core feature |
| **5** | **Calendar integration** | CalendarContract (no OAuth) |
| **6** | **Compose UI (Dashboard + Edit Screen)** | Visual layer |
| **7** | **Telegram bot polling** | Optional, advanced |
| **8** | **OCR integration** | Nice-to-have polish |

---

## 14. Edge Cases & Handling

| Edge Case | Solution |
|-----------|----------|
| No deadline extracted | Prompt user; store null (no reminders) |
| OCR fails (blurry) | Show error, fallback to manual |
| Calendar permission denied | Store event as "reminder only" |
| Duplicate job sharing | Check existing (company+role+deadline) → "Already exists" |
| Telegram bot rate limit | Exponential backoff |
| Offline mode | All features work; no network needed |

---

## 15. Feature Roadmap (Tiered by Impact)

### Tier 0 — UX Improvements to Existing Features

| Improvement | Why |
|-------------|-----|
| **Parser Learns from Corrections** | User corrects a field → parser logs the correction → future parses improve. Closed feedback loop. |
| **Batch Operations** | Select multiple jobs → change status, set reminders, or archive in bulk. |
| **Quick Actions from Notification** | "Mark as Applied" or "Snooze" directly from the notification shade. No app open needed. |
| **Smart Snooze Mode** | If you have an interview today, auto-snooze all other reminders until tomorrow. |
| **Undo / Trash Bin** | Accidental delete? Swipe to undo. Deleted jobs go to trash for 7 days. |
| **Search + Advanced Filters** | Full-text search across company, role, notes. Combined filters: status + source + deadline range + fit score. |
| **Dark Mode** | Match system theme. Essential for heavy daily use. |
| **Offline-first with Conflict Resolution** | All edits save locally immediately. If user has app on two devices, last-write-wins merge. |

### Tier 1 — High Value, Low Effort (MVP+)

| Feature | Why |
|---------|-----|
| **Application Timeline** | Chronological log per job: Found → Applied → Replied → Interview 1 → Interview 2 → Offer/Reject. Collapsible per job. |
| **Smart Follow-up Scheduler** | After marking "Applied," auto-reminder: "Follow up with {company} in 7 days if no reply." Biggest silent killer of job apps. |
| **Source Analytics Dashboard** | After 20+ jobs, see which WhatsApp group / Telegram channel produces the most interviews. Prioritize what works. |
| **Weekly Digest** | Every Sunday: "5 deadlines this week, 2 interviews, 3 follow-ups due." Control without opening the app. |
| **Daily Action Plan** | Every morning: "Today — apply to X, follow up on Y, prep for Z interview." Auto-generated from your data. |

### Tier 2 — Differentiators

| Feature | Why |
|---------|-----|
| **Job Fit Score (1-5)** | Rate each job on skill match + interest + salary. Filter by score when deciding where to spend time. |
| **Cover Letter / Follow-up Templates** | Store 2-3 templates. One-tap copy when changing status to "Applied" or "Follow-up." |
| **Offer Comparison View** | Side-by-side: company, role, salary, location, deadline to respond, fit score, pros/cons. |
| **"Maybe Later" Auto-Archive** | Untouched for 14 days → auto-archive with "Still interested?" Keeps feed clean. |
| **One-tap Withdraw** | Archive + cancel all reminders for that job in one tap. |
| **Resume Version Linker** | Attach which resume version per job. Later see which version landed the most interviews. |

### Tier 3 — Power User Features

| Feature | Why |
|---------|-----|
| **Salary Tracker** | Track salary ranges per job. Later see average offers by industry, role, and source. |
| **Interview Prep Checklist** | Per company: what they asked, what you prepared, what you'd do differently. Evolving playbook. |
| **Rejection Insights** | Tag reason (no response, skills mismatch, visa, overqualified) → see patterns. "Ah, these 3 companies all rejected for the same reason." |
| **Skill Gap Analyzer** | Tag required skills per job. App aggregates: "83% of jobs you want require Python. You've marked Python on 2 jobs." Identifies what to learn next. |
| **Application Streak (Gamification)** | "Applied for 7 days in a row!" Keeps motivation up during long hunts. |
| **Document Manager** | Store resume PDF, cover letter, portfolio links per job. Quick-access during applications. |
| **Networking Tracker** | Track who referred you, referral bonus status, thank-you-note reminders, and past conversations. |
| **Expense Tracker** | Interview travel costs, formal clothes, internet costs → export for tax deductions. |
| **Decision Matrix** | Rate offers on 5 weighted criteria (salary, growth, commute, culture, work-life) → objective score. |

### Tier 4 — Job Search Intelligence

| Feature | Why |
|---------|-----|
| **Response Rate Predictor** | Based on your history, estimate: "Jobs from Source A have 40% response rate vs 10% from Source B." Data-driven strategy. |
| **Salary Benchmarking** | Your tracked salary data vs market averages. "Your offers average 15% below market for this role." Negotiation ammunition. |
| **Time-to-Apply Tracker** | Track gap between finding a job and applying. Goal: reduce it. "You found this 12 days ago and haven't applied." |
| **Location Heatmap** | See where jobs cluster geographically (city, area). Helps relocation and commute decisions. |
| **Visa / Sponsorship Flag** | Track which companies sponsor visas. Filter by sponsorship status. |
| **Smart Prioritization Engine** | Algorithm suggests: "Apply to {company} today — deadline in 2 days, high fit score, source has 60% response rate." |

### Tier 5 — Lifecycle & Career Management

| Feature | Why |
|---------|-----|
| **Contract / Notice Period Tracker** | For employed seekers: track notice period end date, countdown widget. |
| **Interview Feedback Logger** | After rejection, log feedback received → pattern analysis over time. |
| **Referral Tracker** | Who referred you, which stage they're at, referral bonus status, thank-you reminders. |
| **Application Questionnaire Saver** | Save answers to common questions ("Why this company?", "Salary expectations") per company. Reuse later. |
| **Resume Tailoring Tracker** | Track which keywords from the job description you included in your resume version. Score your match. |
| **Company Research Dashboard** | Quick-access: Glassdoor rating, LinkedIn followers, Crunchbase funding, news. Links open in-browser. |

### Tier 6 — Psychology & Motivation

| Feature | Why |
|---------|-----|
| **Milestone Celebrations** | "50 applications sent!" "10 interviews completed!" "First offer!" Positive reinforcement during a draining process. |
| **Mood / Energy Tracker** | Log how you feel after each application or interview. "You're most positive after FinTech interviews." Identify burnout patterns. |
| **Job Search Journal** | Free-text daily reflection tied to your timeline. "Felt good about Google interview but bombed the system design round." |
| **Burnout Detector** | If you've applied to 20+ jobs in a week with no breaks, suggest: "Take a day off. Your accuracy drops after 4 applications." |

### Tier 7 — Stretch / Future

| Feature | Why |
|---------|-----|
| **One-click Apply Log** | For jobs with external links, log when you clicked through. Track response rate by platform. |
| **Shared Job Feed** | Share entries with a friend or job-hunt group. Collaborative tracking. |
| **Auto-fetch from Email** | Parse job alerts from Gmail (IMAP / Gmail API). Passive capture. |
| **Google Calendar Read-back** | Two-way sync: see existing events alongside job deadlines. |
| **Pomodoro Apply Mode** | "Apply to 3 jobs in 25 minutes" built-in timer + progress counter. Wins the day in focused sprints. |
| **Export Portfolio (PDF/CSV)** | Generate a "My Job Search Report" — timeline, stats, outcomes. Useful for career counselors or self-reflection. |

---

## 16. Free Checklist (To Set Up)

```
☐ Install Android Studio (free download)
☐ Create new Android project (empty Compose activity)
☐ Add dependencies to build.gradle.kts
☐ Add permissions to AndroidManifest.xml
☐ Create Telegram bot via @BotFather (free, 2 min)
☐ (Optional) Create private Telegram channel + add bot as admin
☐ Collect 20-30 real job messages as test data
☐ Connect physical device or create emulator
☐ No Google Cloud account needed
☐ No credit card required anywhere
```

---

## 17. Session Log

### 2026-06-11 — Initial Setup
- Read conversation.md (DeepSeek share link)
- Read conversation.txt (full DeepSeek plan: 974 lines)
- Added opencode suggestions:
  1. Build ParserEngine first (before UI)
  2. Skip OCR and Telegram for v0.1
  3. Two-stage parser: regex + fallback heuristics with confidence scoring
  4. "Maybe later" status that auto-archives after 14 days
  5. Considered PWA alternative (user chose Android native)
- Created link.txt with the DeepSeek conversation URL
- Created this file (info.md) as the living project document

### 2026-06-11 — Feature Roadmap Added
- Added Section 15: Feature Roadmap (Tier 1–4) with 10 new feature suggestions beyond the original spec:
  - Tier 1: Application Timeline, Smart Follow-up, Source Analytics, Weekly Digest
  - Tier 2: Job Fit Score, Templates, Offer Comparison, Auto-Archive, One-tap Withdraw, Resume Linker
  - Tier 3: Salary Tracker, Interview Prep, Rejection Insights, Streak, Documents, Networking, Expenses, Decision Matrix
  - Tier 4: Apply Log, Shared Feed, Email Parse, Calendar Read-back
- Refined numbering: old Section 15 (Free Checklist) → Section 16, old Section 16 (Session Log) → Section 17

### 2026-06-11 — Massive Feature Expansion
- Replaced Section 15 with 8 tiers (Tier 0 through Tier 7) covering 40+ features total.
- **Tier 0 (NEW)**: UX improvements — parser feedback loop, batch ops, notification actions, smart snooze, undo/trash, search/filters, dark mode
- **Tier 1**: Added Daily Action Plan alongside existing MVP features
- **Tier 3**: Added Skill Gap Analyzer — identifies what skills to learn based on job requirements
- **Tier 4 (NEW)**: Job Search Intelligence — Response Rate Predictor, Salary Benchmarking, Time-to-Apply Tracker, Location Heatmap, Visa/Sponsorship Flag, Smart Prioritization Engine
- **Tier 5 (NEW)**: Lifecycle & Career Management — Notice Period Tracker, Interview Feedback Logger, Referral Tracker, Questionnaire Saver, Resume Tailoring Tracker, Company Research Dashboard
- **Tier 6 (NEW)**: Psychology & Motivation — Milestone Celebrations, Mood/Energy Tracker, Job Search Journal, Burnout Detector
- **Tier 7 (NEW)**: Added Pomodoro Apply Mode, Export Portfolio alongside existing stretch features

### 2026-06-11 — Full UX Design Added
- Inserted Section 4: UX Design — Complete Surface Map (12 subsections)
- 4.1 Design Principles (5 core principles)
- 4.2 Onboarding Flow (contextual permission requests, show-don't-tell)
- 4.3 Home Dashboard (urgency-based sections, smart summary bar, cards per deadline)
- 4.4 Share→Save Flow (bottom sheet, confidence indicators, inline edit, 3-5 sec target)
- 4.5 Job Detail Screen (vertical timeline, one-tap stage progression, attachments)
- 4.6 Calendar Screen (dot indicators, tap-to-expand, swipe months)
- 4.7 Analytics Screen (conversion funnel, source breakdown, weekly sparkline)
- 4.8 Settings Screen (reminder defaults, parsing rules editor, data export)
- 4.9 Notification UX (4 notification types with actions, snooze, grouping)
- 4.10 Micro-interactions & Animations (staggered field entry, haptics, card animations)
- 4.11 Navigation Structure (3-item vs 4-item bottom nav)
- 4.12 UX Anti-Patterns — Explicitly Avoid (10 anti-patterns with better alternatives)
- Renumbered old sections 5→13 accordingly (Data Models now Section 5)

### 2026-06-11 — Liquid Glass + iOS 26 Redesign
- **Replaced entire Section 4** with Liquid Glass / iOS 26 visual design language
- Added 4.0 Design Language Overview (glassmorphism, liquid, iOS 26)
- Added 4.1 Design System Tokens (color palette for light/dark, typography scale, corner radius, shadows, animation spring tokens)
- Added 4.2 Glass Component Library (reusable glass card, tab bar, bottom sheet, input specs)
- Added 4.3 Liquid Blob System (animated gradient blobs, SVG path morphing, Compose Canvas)
- Rewrote all screen designs with glass aesthetics (4.4.1-4.4.6) with spring entrance animations
- Added animation spec table with exact spring parameters
- Added haptic feedback map + gesture interaction map
- Added Compose implementation snippets (glass modifier, spring spec, staggered entrance)
- Fixed broken sub-section numbering in Section 5 (Data Models): 4.x → 5.x

### 2026-06-11 — iOS 26 Complete Detail Integration
- Added missing iOS 26 details from ui_related_details.md as subsections 4.4.7–4.4.12:
  - 4.4.7 iOS 26 Complete Material Stack (6-layer visual layers table with Android equivalents)
  - 4.4.8 Shape Merging & Glass Identity (Capsule/RoundedRect/Circle, spacing rules 8–12 vs 50+, identity tracking)
  - 4.4.9 iOS 26 Interactive Physics — Exact Reference (.full/.glowOnly/.scaleOnly modes, Apple's exact spring stiffness=180 damping=27, critically damped zero-bounce)
  - 4.4.10 SwiftUI Reference Implementation (complete iOS 26 target code with Android mapping annotations)
  - 4.4.11 AI Prompt Template (copy-pasteable prompt for instructing code generation models)
  - 4.4.12 Design Philosophy Summary (4 core iOS 26 principles: Content First, Dynamic Material, Depth & Dimensionality, Liquid Behavior)
- Added note in 4.7 that current spring(0.825, 300) is under-damped; Apple reference is critically damped spring(1.0, 180)

### 2026-06-11 — Android Implementation Details Added
- Added 4.13–4.16 from ui_related_details.md research
  - 4.13 Android Implementation Libraries (5 libraries compared: AndroidLiquidGlassView, liquid-glass-android, KMPLiquidGlass, Prismal, AppleLiquidGlassForAndroid) with Gradle deps and Compose/XML code
  - 4.14 AGSL Shader — Complete Reference with full fragment shader code (refraction, chromatic dispersion, Fresnel edge glow, specular highlight) + Compose apply snippet + spring press animation code
  - 4.15 Performance Optimizations table (7 techniques with benefits)
  - 4.16 Android Liquid Glass — Complete Checklist (6 categories, 30+ checkable items)
