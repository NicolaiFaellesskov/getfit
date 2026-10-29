# getFitter

Backend API til en fitness-app, hvor man kan tracke sin mad over dagen.

Ideen er simpel: opret en bruger, log ind, lav en dagbog (daily log), tilføj måltider og put mad på måltiderne. Maden kan søges frem via Open Food Facts, så man ikke selv skal taste alle næringsværdier ind.

Vi havde også workout/exercise med på det oprindelige diagram, men det nåede vi ikke at implementere. Så det der er med nu, er nutrition-delen.

---

## Links

- API (production): https://api.getfitter.dk/api
- GitHub rep: *https://github.com/NicolaiFaellesskov/getfit*
- Nicolai Portfolio: *https://nicolaifaellesskov.github.io/portfolio/*
- Gabriel Portfolio: *https://gabriel080103.github.io/Portfolio/*

Lokalt kører den på `http://localhost:7070/api`

---

## Hvordan det er bygget

Det er en almindelig lagdelt Javalin-backend:

Client → Routes → Controllers → DAOs → PostgreSQL

Til food-søgning kalder vi Open Food Facts udefra.

Teknologier vi bruger:
- Java
- Javalin
- Hibernate / JPA
- PostgreSQL
- Maven
- JWT til login
- BCrypt til passwords
- JUnit, REST Assured og Testcontainers til tests

### Auth
Når man logger ind, får man et JWT-token. Tokenet skal sendes med på de fleste endpoints som `Authorization: Bearer ...`.

Login og opret bruger er åbne. Resten kræver token.

Security-logikken ligger i en `SecurityController` (authenticate / authorize), så det ikke er smurt ud i `ApplicationConfig`.

### Ownership
Man kan kun se og rette sine egne daily logs og meals. Prøver man at tilgå en andens, får man 403.

### Mad
Søgning går mod Open Food Facts. Når man tilføjer mad til et måltid, gemmes produktet lokalt. Hvis stregkoden allerede findes, genbruger vi den række i stedet for at oprette en ny.

---

## Data model

Det vi faktisk har implementeret:User ── UserDetails
│
└── DailyLog ── Meal ── FoodAmount ── Food

**User** – username, password (hash), roller  
**DailyLog** – en dag for en bruger  
**Meal** – fx “Breakfast”, hører til en daily log  
**FoodAmount** – mad + antal gram på et måltid  
**Food** – navn, brand, barcode, kalorier, protein osv.

---

## API (det vigtigste)

Base: `/api`

### Auth & users
- `POST /auth/login` – login, får token tilbage
- `POST /users` – opret bruger (ingen token)
- `GET /users` / `GET /users/{id}` – hent brugere
- `PUT /users/{id}` – opdater username/password
- `DELETE /users/{id}` – slet bruger
- `POST /users/{username}/roles` – giv en rolle

### Daily logs
- `POST /dailylogs` – opret log for en dato
- `GET /dailylogs` – alle mine logs
- `GET /dailylogs/{id}` – en log
- `PUT /dailylogs/{id}` – ret dato
- `DELETE /dailylogs/{id}` – slet

### Meals
- `POST /meals` – opret måltid på en daily log
- `GET /meals` – mine måltider
- `GET /meals/{id}` – et måltid
- `PUT /meals/{id}` – ret navn / daily log
- `DELETE /meals/{id}` – slet måltid
- `POST /meals/{id}/food` – tilføj mad + gram
- `DELETE /meals/food/{foodAmountId}` – fjern en madvare fra måltidet

### Food
- `GET /food?search=kylling` – søg i Open Food Facts


## User stories

## User stories

### Bruger og autentifikation

**US-01 – Opret bruger**  
Som ny bruger ønsker jeg at oprette en konto med et brugernavn og en adgangskode, så jeg kan bruge fitnessappen.

**US-02 – Log ind**  
Som bruger ønsker jeg at logge ind med mit brugernavn og min adgangskode og modtage et JWT-token, så jeg kan få adgang til beskyttede endpoints.

**US-03 – Opdater brugeroplysninger**  
Som bruger ønsker jeg at kunne ændre mit brugernavn og/eller min adgangskode, så jeg kan holde mine kontooplysninger opdaterede.

**US-04 – Tildel rolle**  
Som system ønsker jeg at tildele en bruger en rolle, så brugeren får de korrekte rettigheder.

**US-05 – Slet bruger**  
Som system ønsker jeg at kunne slette en bruger, så inaktive eller uønskede konti kan fjernes.

### Daglige logs

**US-06 – Opret daglig log**  
Som indlogget bruger ønsker jeg at oprette en daglig log for en bestemt dato, så jeg kan registrere mit fødeindtag for den pågældende dag.

**US-07 – Vis alle egne daglige logs**  
Som indlogget bruger ønsker jeg at kunne se alle mine egne daglige logs, så jeg kan få overblik over min historik.

**US-08 – Vis en specifik daglig log**  
Som indlogget bruger ønsker jeg at kunne se en bestemt daglig log, som tilhører mig, så jeg kan se detaljerne for den pågældende dag.

**US-09 – Opdater dato for daglig log**  
Som indlogget bruger ønsker jeg at kunne ændre datoen for min daglige log, så jeg kan rette den, hvis der er behov for det.

**US-10 – Slet daglig log**  
Som indlogget bruger ønsker jeg at kunne slette min egen daglige log, så jeg kan fjerne dage, jeg ikke længere har brug for.

### Måltider

**US-11 – Opret måltid**  
Som indlogget bruger ønsker jeg at oprette et måltid, der er knyttet til en af mine daglige logs, så jeg kan gruppere den mad, jeg spiser, f.eks. morgenmad og frokost.

**US-12 – Vis alle egne måltider**  
Som indlogget bruger ønsker jeg at kunne se alle mine måltider, så jeg kan få overblik over det, jeg har registreret.

**US-13 – Vis et specifikt måltid**  
Som indlogget bruger ønsker jeg at kunne se et bestemt måltid, som tilhører mig, så jeg kan se detaljerne for måltidet.

**US-14 – Opdater måltid**  
Som indlogget bruger ønsker jeg at kunne ændre et måltids navn og/eller den daglige log, det er knyttet til, så jeg kan rette eventuelle fejl.

**US-15 – Slet måltid**  
Som indlogget bruger ønsker jeg at kunne slette et måltid, så jeg kan fjerne måltider, jeg ikke længere har brug for.

### Fødevarer og mængder

**US-16 – Søg efter fødevarer**  
Som bruger ønsker jeg at kunne søge efter fødevarer ved hjælp af et søgeord, så jeg kan finde produkter via det eksterne fødevare-API.

**US-17 – Tilføj fødevare med en bestemt mængde**  
Som indlogget bruger ønsker jeg at kunne tilføje en fødevare med en bestemt mængde i gram til et af mine måltider, så jeg kan registrere, hvad jeg har spist, og hvor meget jeg har spist.

**US-18 – Genbrug eksisterende fødevare**  
Som system ønsker jeg at genbruge en eksisterende fødevare, hvis stregkoden allerede findes, så dubletter af fødevarer undgås.
---

## Lidt om processen

Lidt om processen

Det meste af arbejdet gik med at få login, JWT og ownership til at fungere korrekt, så brugere kun kan tilgå deres egne data.

Vi fik projektet connected til DigitalOcean, men vi fik ikke PostgreSQL-forbindelsen sat op, som vi ønskede. Derfor valgte vi at køre projektet lokalt på localhost, så vi nemmere kunne rette fejl og sikre os, at tabellerne og databaseopsætningen fungerede korrekt, inden vi gik videre med deployment.

Workout-delen nåede vi heller ikke at implementere, så den nuværende version fokuserer på nutrition-delen.

## Diagrammer

### Domain Diagram
![Domain Diagram](/Diagrams/domainDiagram.png)

### ER-Diagram
![ER Diagram](/Diagrams/ER-Diagram.png)
---

## Kør lokalt

1. PostgreSQL med databasen `fitness_app` (se `config.properties`)
3. Kør `app.Main` (port 7070)
4. Route-overview: http://localhost:7070/api/routes