# SMSApp

SMSApp est une application Android minimaliste écrite en Kotlin avec Jetpack Compose et Hilt.
Elle illustre l'affichage de conversations SMS, l'envoi de nouveaux messages et la réception en temps réel.

## Architecture

Le projet suit une structure **Model–View–ViewModel (MVVM)** :

- **Modèles** – `Conversation`, `Message` et autres data classes représentent le contenu SMS récupéré via le ContentResolver.
- **ViewModels** – `HomeViewModel`, `ConversationViewModel` exposent les états à l'UI et orchestrent les appels au dépôt/service.
- **Vue (UI Compose)** – `HomeScreen`, `ConversationScreen`, `SendTextField`, etc., affichent les données et relaient les actions de l'utilisateur.

Cette séparation maintient la logique UI dans les composables, la logique métier dans les ViewModels et l'accès aux données dans les dépôts/services.

## Structure du projet

Le dépôt contient deux modules :

### `app/` - Application Android principale

#### Entrée et utilitaires
- **MainActivity** – gère les permissions, définit le thème Compose et la navigation entre les écrans
- **DbUtils** – fonctions génériques pour interroger, mettre à jour ou supprimer via ContentResolver
- **SmsApplication** – expose un SharedFlow d'événements SMS et un scope coroutine global

#### Injection de dépendances
- **AppModule** – fournit ContentResolver, SmsManager, SmsService et le contexte via Hilt

#### Modèles
- **Conversation** – décrit un fil de discussion (id, adresse, dernier message, compteur…) et est annoté pour la génération de code KSP
- **Message** – modélise un SMS individuel (id, thread_id, type, corps…)

#### Accès aux données
- **SmsRepository** – encapsule l'accès au ContentResolver, expose des flux get/getAll génériques basés sur ModelFactory ainsi qu'une méthode pour marquer un message comme lu

#### Services
- **SmsService** – envoie les SMS via SmsManager et déclenche des intents pour le suivi d'envoi/livraison
- **SmsBroadcastReceiver** – écoute les SMS entrants, récupère le message/fil correspondant puis notifie l'application et le système
- **NotificationService** – crée le canal de notification et affiche des notifications de type messagerie

#### Interface utilisateur (Compose)
- **HomeScreen** – liste les conversations, applique une couleur aléatoire par thread et navigue vers la conversation sélectionnée
- **ConversationScreen** – affiche les messages d'un fil, marque ceux visibles comme lus et offre un champ d'envoi
- **PermissionsScreen** – demande les autorisations SMS et contacts, ou redirige vers les paramètres si nécessaire
- **SendTextField** – composant personnalisé pour saisir et envoyer un message, ajustant dynamiquement sa hauteur
- **Padding** – encapsule les marges Compose avec conversion en PaddingValues
- Le thème est défini dans `ui/theme` via `Color.kt`, `Theme.kt` et `Type.kt`

#### ViewModels
- **HomeViewModel** – récupère les conversations et réagit aux événements SMS pour mettre à jour la liste
- **ConversationViewModel** – charge les messages d'un fil, marque les messages lus et délègue l'envoi au service SMS

### `processor/` - Module KSP

Processeur d'annotations KSP qui :
- Analyse les modèles annotés pour le mapping base de données
- Génère une classe `ModelFactory` pour mapper les modèles annotés vers les colonnes/URI utilisés par ContentResolver
- Permet des opérations CRUD génériques dans le repository

## Envoi et réception des SMS

### Envoi

1. L'UI (`SendTextField`) déclenche l'action d'envoi
2. Le `ConversationViewModel` invoque `SmsService`
3. `SmsService` utilise `SmsManager.sendTextMessage` et enregistre des intents de suivi
4. Les résultats de livraison sont diffusés pour mettre l'UI à jour et envoyer des notifications

### Réception

1. Android diffuse les SMS entrants
2. `SmsBroadcastReceiver` intercepte la diffusion, convertit les PDUs en `Message`, puis identifie la conversation
3. Le récepteur émet un événement dans le `SharedFlow` de `SmsApplication`
4. Les ViewModels l'observent pour rafraîchir l'UI
5. `NotificationService` génère une notification

## Installation et tests

### Prérequis

- Android Studio (Electric Eel+)
- Émulateur ou appareil Android avec capacité SMS
- Permissions SMS accordées (SEND/RECEIVE/READ/WRITE)

### Installation

1. Cloner le projet
2. Importer dans Android Studio
3. Activer KSP si nécessaire
4. Build & Run sur un émulateur ou appareil physique

### Scénarios de test recommandés

- **Envoi d'un SMS** depuis l'écran de conversation
- **Réception d'un SMS** en avant-plan et arrière-plan
- **Vérification des notifications** et accusés de réception
- **Gestion d'un refus de permissions** et redirection vers les paramètres

## Résumé

SMSApp offre une base simple pour consulter et échanger des SMS avec :
- Une UI réactive basée sur Jetpack Compose
- Une architecture MVVM claire et séparée
- Un module KSP pour le mapping automatique des modèles
- Une gestion complète de l'envoi et de la réception de SMS
- Un système de notifications personnalisées
