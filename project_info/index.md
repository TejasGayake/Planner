# Project Info

> Project context and knowledge base. Every agent reads this before starting work.

## Contents

- [spec.md](./spec.md) — Requirements, features, user stories
- [architecture.md](./architecture.md) — Architecture decisions, diagrams, data flow
- [tech-stack.md](./tech-stack.md) — Languages, frameworks, tools, versions
- [environment.md](./environment.md) — Runtime env setup, deps, run commands
- [projects.json](./projects.json) — Multi-project configuration
- [templates/](./templates/) — Project templates (web-app, api, cli, docker)
- [decisions/](./decisions/) — Architecture Decision Records (ADRs)
- [designs/](./designs/) — Screenshots, mockups, UI references
- [reference/](./reference/) — Research papers, PDFs, external links

## How to Use

- **Agents**: Read `spec.md` and `architecture.md` before writing any code
- **Coordinator**: Check `tech-stack.md`, `projects.json`, and retry failed phases
- **Scaffolder**: Use `templates/` to pre-fill project context, auto-create env + Docker
- **Builder/Tester**: Check `environment.md` and `projects.json` before starting
- **Git-manager**: Update `CHANGELOG.md` on every commit
- **User**: Drop reference files into `designs/` or `reference/` for context

## Maintenance

Update this index when adding new files.
