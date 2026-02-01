# Word Trainer API

API REST pour Word Trainer Kids - Java 21 + Spring Boot 3.2 + MongoDB + Lombok

## Prérequis

- Java 21+
- Maven 3.9+
- MongoDB 7+
- Lombok plugin dans votre IDE

## Démarrage rapide

```bash
# Lancer MongoDB
docker run -d -p 27017:27017 --name mongodb mongo:7

# Lancer l'application
./mvnw spring-boot:run
```

## Avec Docker Compose

```bash
docker-compose up -d
```

## Endpoints

- `POST /api/auth/register` - Inscription parent
- `POST /api/auth/login` - Connexion parent
- `POST /api/auth/login/child` - Connexion enfant
- `GET /api/children` - Liste des enfants
- `POST /api/children` - Créer un enfant
- `GET /api/children/{id}/lists` - Listes d'un enfant
- `POST /api/children/{id}/lists` - Créer une liste
- `POST /api/children/{id}/training` - Sauvegarder entraînement
