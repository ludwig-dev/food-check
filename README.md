# Foodcheck

Add foods from **Livsmedelsverket** to recipes and get full nutrition breakdown of macros, vitamins, minerals, %RDI and more.

## Features
- 🔎 Search foods and add to a recipe
- 🧪 Totals for macros, vitamins, minerals, %RDI
- 🧩 Update grams (single or batch), remove, rename recipe
- 🌐 Publish/unpublish recipes (public view)
- 🔐 Auth + user profile, admin management

### Backend
- Java
- Spring Boot   
- PostgreSQL

### Frontend
- React
- TypeScript  

## Frontend
Clone and run:
```bash
git clone https://github.com/ludwig-dev/godmansbok-fe.git
cd godmansbok-fe
npm install
npm run dev
```

## Backend
Clone and run:
```bash
git clone https://github.com/ludwig-dev/food-check.git
cd food-check
```

### Environment Variables
Create a `.env` in the project root:
```env
DB_URL2=jdbc:postgresql://localhost:5432/yourdbname
DB_USER=yourdbuser
DB_PASSWORD=yourdbpassword
JWT_SECRET=yourjwtsecret
```

### Data Import (Livsmedelsverket)
You need to obtain the food dataset from **Livsmedelsverket** and import it into PostgreSQL before using search/nutrition features. You can find it on [Livsmedelverket](https://www.livsmedelsverket.se/om-oss/psidata/livsmedelsdatabasen/)


Start the backend:
```bash
./mvnw spring-boot:run
```

API base URL: `http://localhost:8080`

## Repositories
- Backend: [food-check](https://github.com/ludwig-dev/food-check)
- Frontend: [godmansbok-fe](https://github.com/ludwig-dev/godmansbok-fe)

## Endpoints Overview

| Category | Endpoints |
|---|---|
| **Auth** | `/api/auth/register`, `/api/auth/login`, `/api/auth/logout` |
| **User** | `/api/users/me` (GET, PATCH, DELETE) |
| **Admin** | `/api/admin/users` (GET), `/api/admin/users/{id}` (PATCH role, DELETE) |
| **Recipes** | `/api/recipes` (GET, POST), `/api/recipes/{id}` (GET, DELETE), `/api/recipes/{id}/rename` (PUT) |
| **Ingredients** | `/api/recipes/{id}/ingredients/add` (PUT add), `/api/recipes/{id}/ingredients/{foodId}` (PUT update, DELETE remove), `/api/recipes/{id}/ingredients` (PUT batch update) |
| **Nutrition** | `/api/recipes/{id}/nutrition` (GET) |
| **Public Recipes** | `/api/recipes/public` (GET), `/api/recipes/public/{id}` (GET), `/api/recipes/public/{id}/nutrition` (GET), `/api/recipes/public/{id}/publish` (PUT), `/api/recipes/public/{id}/private` (PUT) |
| **Food Search** | `/api/food/search?query=`, `/api/food/{id}` |
