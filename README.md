# SMSApp

SMSApp est une application Android minimaliste écrite en **Kotlin** avec **Jetpack Compose** et **Hilt**. Elle illustre l'affichage de conversations SMS, l'envoi de nouveaux messages et la réception en temps réel.

## Architecture

Le projet suit une structure **Model–View–ViewModel (MVVM)** :

- **Modèles** – `Conversation`, `Message` et autres data classes représentent le contenu SMS récupéré via le `ContentResolver`
- **ViewModels** – `HomeViewModel`, `ConversationViewModel` exposent les états à l'UI et orchestrent les appels au dépôt/service
- **Vue (UI Compose)** – `HomeScreen`, `ConversationScreen`, `SendTextField`, etc., affichent les données et relaient les actions de l'utilisateur

Cette séparation maintient la logique UI dans les composables, la logique métier dans les ViewModels et l'accès aux données dans les dépôts/services.

## Modules

### `app/`
Application Android principale :

#### Entrée
- **`MainActivity`** – gère les permissions, définit le thème Compose et la navigation

#### Dépôt
- **`SmsRepository`** – encapsule l'accès au `ContentResolver` et expose des flows

#### Services
- **`SmsService`** – envoie les SMS via `SmsManager`
- **`SmsBroadcastReceiver`** – écoute les messages entrants et informe l'application
- **`NotificationService`** – crée le canal et affiche les notifications

#### UI Compose
- **`HomeScreen`** – liste les conversations
- **`ConversationScreen`** – montre les messages d'un fil et propose l'envoi
- **`SendTextField`** – saisit le message avec hauteur dynamique

#### ViewModels
- **`HomeViewModel`** – stream les conversations et réagit aux nouveaux SMS
- **`ConversationViewModel`** – charge un fil, marque les messages lus et délègue l'envoi

### `processor/`
Module **KSP** qui analyse les modèles annotés et génère un `ModelFactory` pour des opérations CRUD génériques dans le dépôt.

## Envoi et réception des SMS

### Envoi

1. L'UI (`SendTextField`) déclenche l'action d'envoi
2. Le `ConversationViewModel` invoque `SmsService`
3. `SmsService` utilise `SmsManager.sendTextMessage` et enregistre des intents de suivi
4. Les résultats de livraison sont diffusés pour mettre l'UI à jour et envoyer des notifications

### Réception

1. Android diffuse les SMS entrants
2. `SmsBroadcastReceiver` intercepte la diffusion, convertit les PDUs en `Message`, puis identifie la conversation
3. Le récepteur émet un événement dans le `SharedFlow` de `SmsApplication` ; les ViewModels l'observent pour rafraîchir l'UI
4. `NotificationService` génère une notification

## Technologies utilisées

- **Kotlin** – Langage de programmation principal
- **Jetpack Compose** – Framework UI moderne et déclaratif
- **Hilt** – Injection de dépendances
- **KSP (Kotlin Symbol Processing)** – Génération de code à la compilation
- **Coroutines & Flow** – Programmation asynchrone et réactive
- **Android ContentResolver** – Accès aux données SMS système

## Installation

### Prérequis
- Android Studio Arctic Fox ou version ultérieure
- SDK Android 21+ (Android 5.0)
- Appareil ou émulateur avec capacités SMS

### Étapes d'installation
1. Cloner le repository
```bash
git clone [URL_DU_REPO]
cd SMSApp
```

2. Ouvrir le projet dans Android Studio

3. Synchroniser les dépendances Gradle

4. Lancer l'application sur un appareil/émulateur

## Permissions requises

L'application nécessite les permissions suivantes :

- `SEND_SMS` – Envoi de messages
- `RECEIVE_SMS` – Réception de messages
- `READ_SMS` – Lecture des conversations existantes
- `WRITE_SMS` – Modification du statut des messages

## Résumé

SMSApp offre une base simple pour consulter et échanger des SMS avec :
- Une UI réactive basée sur Jetpack Compose
- Une architecture MVVM claire et bien structurée
- Un module KSP pour le mapping automatique des modèles
- Une gestion complète de l'envoi et de la réception en temps réel
