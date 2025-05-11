# Projet Android Météo

Ce projet est une application Android permettant de consulter les prévisions météorologiques actuelles et futures pour une localisation donnée. Elle intègre également un historique des recherches, la gestion des préférences utilisateur, et utilise diverses fonctionnalités clés d'Android.

## Grille d'Évaluation / Fonctionnalités Clés

Voici le statut des fonctionnalités par rapport aux exigences du projet :

*   [x] **Utilisation de l'Api Preference**: Fait avec `SettingsActivity`, `WeatherPreferences`, `preferences.xml`.
*   [x] **Ecriture/lecture dans un Fichier**: Fait dans `MainActivity` avec `saveSearchToLog`.
*   [x] **Utilisation de SQLite**: Fait avec `DatabaseHelper`.
*   [x] **Nombre d'activités ou fragment supérieur ou égal à 3**: Fait (`MainActivity`, `WeatherActivity`, `HistoryActivity`, `SettingsFragment`).
*   [x] **Gestion du bouton Back** (message pour confirmer que l'on veut réellement quitter l'application): Fait dans `MainActivity`. Les autres activités utilisent le comportement par défaut ou la flèche de la toolbar.
*   [x] **L'affichage d'une liste avec son adapter**: Fait dans `MainActivity` (`lvRecentSearches` avec `ArrayAdapter`).
*   [x] **L'affichage d'une liste avec un custom adapter** (avec gestion d’événement): Fait (`HistoryActivity` avec `HistoryAdapter`, `WeatherActivity` avec `ForecastAdapter`). Clics gérés. Long clic ajouté comme exemple pour `HistoryAdapter`.
*   [X] **La pertinence d'utilisation des layouts** 
*   [x] **L'utilisation d'événement améliorant l'UX** (pex: swipe). Préciser : `SwipeRefreshLayout` dans `WeatherActivity`.
*   [x] **La réalisation de composant graphique custom** (Paint 2D, Calendrier,...). Préciser : Icônes météo SVG (préfixées `ic_...`).
*   [x] **Les taches en background** (codage du démarrage d'un thread): Fait dans `WeatherService` avec `ExecutorService`.
*   [x] **Le codage d'un menu** (contextuel ou non, utilisation d'un menu en resource XML): Menu d'options dans `MainActivity` (`main_menu.xml`). Exemple de menu contextuel pour `HistoryAdapter` (`history_item_menu.xml`).
*   [x] **L'application de pattern** (Reactive programming, singleton, MVC,...). Liste :
    *   **MVC-like**: Les activités agissent comme des contrôleurs, les XMLs sont les vues, les classes `model` sont les modèles. `WeatherService` et `DatabaseHelper` font partie de la couche de données/service.
    *   **Singleton (Potentiel)**: `DatabaseHelper` n'est pas un singleton ici (instancié dans les activités), mais pourrait l'être pour une gestion centralisée. Idem pour `WeatherService`.
    *   *Pas de Reactive Programming utilisé ici.*
*   [x] **Demande des autorisations**: Fait dans `MainActivity` pour la localisation.
*   [x] **L'appel de WebServices**: Fait avec `WeatherService` pour OpenWeatherMap.
*   [x] **Utilisation des API Android** (géolocalisation, contacts, accéléromètre, ...): Géolocalisation (`FusedLocationProviderClient`) utilisée.

---

## Détails des Fonctionnalités Implémentées

### 1. API Preference (`SettingsActivity`, `WeatherPreferences`, `preferences.xml`)
L'application permet à l'utilisateur de configurer ses préférences (par exemple, les unités de température) via un écran de paramètres. Ces préférences sont stockées et lues à l'aide de l'API `SharedPreferences`.

### 2. Écriture/Lecture dans un Fichier (`MainActivity` - `saveSearchToLog`)
Un journal simple des termes de recherche est sauvegardé dans un fichier texte local. Ceci est géré dans `MainActivity` pour illustrer la lecture/écriture de fichiers.

### 3. Utilisation de SQLite (`DatabaseHelper`)
L'historique des recherches météorologiques (villes recherchées) est stocké dans une base de données SQLite. La classe `DatabaseHelper` gère la création de la base, les tables, et les opérations CRUD (Create, Read, Update, Delete) pour l'historique.

### 4. Nombre d'Activités/Fragments (≥ 3)
L'application est structurée avec plusieurs écrans :
*   `MainActivity`: Écran principal pour la recherche de villes et l'affichage des recherches récentes.
*   `WeatherActivity`: Affiche les détails météorologiques pour une ville sélectionnée.
*   `HistoryActivity`: Affiche l'historique complet des recherches stockées en base de données.
*   `SettingsFragment`: Fragment hébergé par `SettingsActivity` pour gérer les préférences.

### 5. Gestion du Bouton "Retour" (`MainActivity`)
Dans `MainActivity`, une confirmation est demandée à l'utilisateur s'il appuie sur le bouton "Retour" pour quitter l'application, améliorant l'UX en évitant les fermetures accidentelles.

### 6. Liste avec Adapter Simple (`MainActivity`)
Les recherches récentes sont affichées dans `MainActivity` à l'aide d'une `ListView` et d'un `ArrayAdapter` standard.

### 7. Liste avec Custom Adapter et Gestion d'Événements
*   **`HistoryActivity`**: Utilise `HistoryAdapter` (un adaptateur personnalisé) pour afficher l'historique des recherches. Les clics simples ouvrent la météo pour la ville, et un exemple de clic long (avec menu contextuel) est implémenté.
*   **`WeatherActivity`**: Utilise `ForecastAdapter` pour afficher les prévisions sur plusieurs jours. Les clics sur les éléments de prévision peuvent être gérés (non détaillé ici mais la structure est en place).

### 8. Événement Améliorant l'UX (`SwipeRefreshLayout` dans `WeatherActivity`)
Dans `WeatherActivity`, l'utilisateur peut "tirer pour rafraîchir" (`SwipeRefreshLayout`) afin de mettre à jour les données météorologiques.

### 9. Composants Graphiques Custom (Icônes SVG)
Des icônes météorologiques au format SVG (ex: `ic_sun.xml`, `ic_cloud.xml`) sont utilisées pour une meilleure représentation visuelle des conditions climatiques.

### 10. Tâches en Arrière-Plan (`WeatherService` avec `ExecutorService`)
Les appels réseau à l'API OpenWeatherMap sont effectués en arrière-plan via `WeatherService` qui utilise un `ExecutorService` pour ne pas bloquer le thread UI.

### 11. Codage de Menus (Options et Contextuel)
*   Un menu d'options (défini dans `main_menu.xml`) est disponible dans `MainActivity` pour accéder aux paramètres et à l'historique.
*   Un exemple de menu contextuel (défini dans `history_item_menu.xml`) est implémenté pour les éléments de la liste dans `HistoryActivity` (via `HistoryAdapter`).

### 12. Application de Patterns de Conception
*   **MVC-like**: Les activités/fragments servent de contrôleurs, les layouts XML de vues, et les classes de données (POJOs) de modèles. `WeatherService` et `DatabaseHelper` font partie de la couche de service/données.
*   **Singleton (Potentiel)**: Bien que `DatabaseHelper` et `WeatherService` ne soient pas implémentés comme des singletons stricts dans ce projet (ils sont instanciés là où c'est nécessaire), ils sont de bons candidats pour une telle approche dans une application plus vaste.
*   *La Programmation Réactive n'est pas utilisée.*

### 13. Demande des Autorisations (`MainActivity` pour la localisation)
L'application demande la permission d'accéder à la localisation de l'utilisateur (`ACCESS_FINE_LOCATION`) au runtime si elle n'est pas déjà accordée, afin de pouvoir récupérer la météo de la position actuelle.

### 14. Appel de WebServices (`WeatherService` pour OpenWeatherMap)
Le `WeatherService` est responsable de la communication avec l'API externe OpenWeatherMap pour récupérer les données météorologiques.

### 15. Utilisation des API Android (Géolocalisation)
L'API de géolocalisation d'Android (`FusedLocationProviderClient`) est utilisée pour obtenir les coordonnées géographiques actuelles de l'utilisateur.

## Fonctionnalités Non Implémentées (ou non couvertes spécifiquement)

*   **Room Persistence Library**: SQLite est utilisé directement via `DatabaseHelper` au lieu de l'ORM Room.
*   **Firebase**: Aucune fonctionnalité Firebase n'est intégrée.
*   **Layouts Responsives (Portrait/Paysage/Tablette)**: Bien que l'application fonctionne, une optimisation et des tests poussés pour tous les formats et orientations n'ont pas été une priorité principale de cette version.

## Structure du Projet (Classes clés)

*   `MainActivity.java` : Point d'entrée, recherche, affichage des recherches récentes.
*   `WeatherActivity.java` : Affichage détaillé de la météo.
*   `HistoryActivity.java` : Affichage de l'historique des recherches.
*   `SettingsFragment.java` / `SettingsActivity.java` : Gestion des préférences.
*   `WeatherService.java` : Logique d'appel à l'API OpenWeatherMap et gestion des tâches en arrière-plan.
*   `DatabaseHelper.java` : Gestionnaire de la base de données SQLite pour l'historique.
*   `WeatherPreferences.java` : Classe utilitaire pour la gestion des préférences.
*   `Adapters/` : Contient les adaptateurs personnalisés (`HistoryAdapter`, `ForecastAdapter`).
*   `res/xml/preferences.xml` : Définition des clés de préférences.
*   `res/layout/` : Fichiers XML pour les interfaces utilisateur.
*   `res/menu/` : Fichiers XML pour les menus (`main_menu.xml`, `history_item_menu.xml`).
*   `res/drawable/` : Contient les icônes SVG (ex: `ic_sun.xml`, `ic_cloud.xml`).

## Prérequis

*   Android Studio (dernière version recommandée)
*   SDK Android (Niveau d'API cible à préciser si nécessaire)
*   Une clé API valide pour [OpenWeatherMap](https://openweathermap.org/appid).

## Installation et Configuration

1.  Clonez ce dépôt : `git clone [URL_DU_DEPOT]`
2.  Ouvrez le projet avec Android Studio.
3.  **IMPORTANT :** Obtenez une clé API gratuite sur [OpenWeatherMap](https://openweathermap.org/appid).
    Vous devrez ensuite l'intégrer dans le code, typiquement dans la classe `WeatherService.java` ou un fichier de constantes dédié. Recherchez un placeholder comme `YOUR_API_KEY_HERE`.
4.  Compilez et exécutez l'application sur un émulateur Android ou un appareil physique.

### ⚠️ Attention

1.Il peut y avoir un problème avec la version d’AGP.  
2.On a remarqué que dans un cas sur deux, lorsqu’on clone le projet et qu’on installe l’application sur le téléphone, il est impossible d’accéder aux paramètres ou de voir l’historique — alors que parfois, tout fonctionne normalement.

---
