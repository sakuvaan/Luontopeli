# Harjoitustyö: Luontopeli

Tämä sovellus on Android-alustalle kehitetty interaktiivinen peli, joka yhdistää ulkoilun, tekoälypohjaisen kasvien tunnistuksen ja aktiivisuuden seurannan. Sovellus on toteutettu osana mobiiliohjelmoinnin opintojaksoa.

## Ominaisuudet

* **Tekoälypohjainen tunnistus:** Sovellus tunnistaa kasveja laitteen kameralla hyödyntäen Google ML Kit -kirjastoa.
* **Aktiivisuuden seuranta:** Reaaliaikainen askelmittari (Step Counter) ja matkan laskenta GPS-tietojen perusteella.
* **Interaktiivinen kartta:** Käyttäjä voi tarkastella omia löytöjään ja kuljettuja reittejä OpenStreetMap-pohjaisella karttanäkymällä.
* **Tilastot ja historia:** Sovellus tallentaa kävelylenkit ja löydetyt kasvit paikalliseen tietokantaan sekä pilveen.
* **Käyttäjänhallinta:** Kirjautuminen ja tietojen synkronointi Firebase-palveluun.

## Tekninen toteutus

Sovellus noudattaa modernia Android-arkkitehtuuria (MVVM) ja on rakennettu seuraavilla teknologioilla:

* **UI:** Jetpack Compose (deklaratiivinen käyttöliittymä)
* **Ohjelmointikieli:** Kotlin
* **Tietokannat:** * **Room:** Paikallinen välimuisti ja offline-tuki.
    * **Firebase Firestore:** Käyttäjätietojen ja löytöjen pilvisynkronointi.
* **Tekoäly:** Google ML Kit Image Labeling
* **Laiterajatapinnat:** CameraX API ja Android Sensor Manager
* **Kartat:** OSMDroid (OpenStreetMap)
* **Riippuvuuksien hallinta:** Kotlin Symbol Processing (KSP) & Gradle

## Projektin rakenne

* `ui/` – Compose-näkymät (Map, Camera, Stats, Discover)
* `viewmodel/` – Sovelluslogiikan ja UI:n välinen kerros
* `data/` – Repositoriot, DAO-rajapinnat ja tietomallit
* `sensor/` – Antureiden (askelmittari) hallinta
* `ml/` – Tekoälymallin toteutus

## 📦 Asennus ja testaus

1.  Lataa GitHub-repon `Releases`-osiosta löytyvä `app-release.apk`.
2.  Asenna APK Android-laitteelle (vaatii "Unknown sources" -oikeuden).
3.  Sovellus pyytää käynnistettäessä tarvittavat luvat (Kamera, Sijainti, Fyysinen aktiivisuus).
