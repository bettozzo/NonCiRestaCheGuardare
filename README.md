<h1 align="center">NON CI RESTA CHE GUARDARE </h1>

**Non ci Resta che Guardare** è un progetto pensato per utenti che vogliono vedersi un **BEL** (scusate la presunzione) film di tanto in tanto.<br>
Il progetto ha sia un'app(questa repo) sia una versione web ([repo qui](https://github.com/bettozzo/nrg)).

L'app permette all'utente di avere una watchlist, separando i film dalle serie TV e con la possibilità di salvarsi delle informazioni aggiuntive.

# Project roadmap

- [x] Watchlist
  - [x] Separa i film dalle serie TV
  - [x] Mostra informazioni sul film/serie
  - [x] Mostra su quali piattaforme si può vedere il film/serie
  - [x] Ha sezione note per ogni entry
  - [ ] Possibilità di filtrare e/o ordinare contenuti
- [x] Cronologia
- [ ] Recensioni e/o valutazioni sui contenuti visti
- [ ] Ricevere consigli da altri utenti
- [ ] Mostrare statistiche _interessanti_
- [x] [Versione web](https://github.com/bettozzo/nrg)

# Requirements

### Hardware

Per usare quest'app, serve un dispositivo android (Od un emulatore) con `SDK >= 26`, idealmente `SDK = 34`.

### Software

Per poter buildare il progetto, serve:

- [Android studio](https://developer.android.com/?hl=it)

# Built with

- [TMDB - The Movie Database](https://www.themoviedb.org/?language=it-IT) - Ottenere tutte le informazioni su film e serie TV
- [Supabase](https://supabase.com/) - Database remoto: gratis e opensource, compatibile sia con Kotlin che con JavaScript.
- [Retrofit](https://square.github.io/retrofit/) - Trasforma chiamate API HTTP in interfacce Kotlin
- [Picasso](https://square.github.io/picasso/) - Caricare immagini da URL

# Project layout

```
├── README.md
└── app
     ├── build.gradle                             # Lista dipendenze e proprietà progetto
     └── src/main
          ├── AndroidManifest.xml
          ├── java/unitn/app
          │    ├── activities                     # Codice sorgente delle attività
          │    │    ├── aggiungiMedia             # Schermata per aggiungere un contenuto
          │    │    ├── auth                      # Schermate di autenticazione
          │    │    ├── customMedia               # Schermata per aggiungere un film non presente su TMDB
          │    │    ├── dettaglio                 # Schermata per avere maggiori informazioni di contento aggiunto alla watchlist
          │    │    ├── homepage                  # Watchlist
          │    │    ├── loadingScreen
          │    │    ├── profilo                   # Schermata del profilo e opzioni dell'utente
          │    │    ├── ricerca                   # Schermata per cercare nuovi film/serieTV
          │    │    └── LiveDatas.kt              # Alcuni dei dati condivisi tra schermate o dati importanti
          │    ├── api                            # chiamate API a TMDB
          │    │    ├── MediaDetails.kt           # Logica delle chiamate API
          │    │    └── RetrofitAPI.kt            # Effettive chiamate API
          │    ├── localdb
          │    │    └──  UserDAO.kt               # Effettive chiamate a DB locale
          │    └── remoteDB
          │         └──  remoteDao.kt             # Logica delle chiamate a DB remoto, per ottenere dati utenti
          └──  res                                # Risorse locali usate
               ├── anim                           # Animazioni usate
               ├── drawable                       # Immagini usate
               ├── layout                         # Layouts delle attività e frammenti
               ├── values-night                   # Colori e temi utilizzate in modalità notte
               └── values                         # Colori e temi utilizzate in modalità giorno
```

# Getting started

### Setting up the software

1. Clona repo utilizzando:

```sh
git clone https://github.com/bettozzo/NonCiRestaCheGuardare.git
```

2. Apri progetto su Android Studio
3. Build & run
