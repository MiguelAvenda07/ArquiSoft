[![Known Vulnerabilities](https://snyk.io/test/github/MiguelAvenda07/ArquiSoft/badge.svg)](https://snyk.io/test/github/MiguelAvenda07/ArquiSoft)
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=coverage)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=duplicated_lines_density&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=ncloc&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=reliability_rating&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=security_rating&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Maintainability issues](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=software_quality_maintainability_issues&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Reliability issues](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=software_quality_reliability_issues&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Security issues](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=software_quality_security_issues&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=sqale_index&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MiguelAvenda07_ArquiSoft&metric=sqale_rating&token=2f8817a8222028b7b6d9c9826d4f5207b56678ce)](https://sonarcloud.io/summary/new_code?id=MiguelAvenda07_ArquiSoft)
[![CI/CD Pipeline](https://github.com/MiguelAvenda07/ArquiSoft/actions/workflows/build.yml/badge.svg)](https://github.com/MiguelAvenda07/ArquiSoft/actions/workflows/build.yml)
# UdeBank

Aplicación de gestión bancaria básica desarrollada para el curso de Arquitectura de Software (Universidad de Antioquia). Está compuesta por un backend REST en Spring Boot y un frontend en React, integrados mediante un pipeline de CI/CD y desplegados en la nube.

## Backend

API REST construida con **Spring Boot** y **Java 21**.

- **Persistencia:** Spring Data JPA con **PostgreSQL**.
- **Entidades:** `Customer` (clientes) y `Transaction` (transacciones).
- **Mapeo:** MapStruct para convertir entre entidades y DTOs, y Lombok para reducir código repetitivo.
- **Capas:** Controller → Service → Repository.
- **Pruebas:** JUnit 5 y Mockito sobre controllers y services, con cobertura medida por JaCoCo.

Ejemplo de endpoint:

```
GET /api/customers
```

## Frontend

Aplicación **React + Vite** ubicada en la carpeta `udebank-frontend`, que consume la API del backend.

## Estructura del repositorio

```
.
├── src/                    # Código del backend
│   ├── main/               # Controllers, services, repositories, entidades
│   └── test/               # Pruebas unitarias
├── udebank-frontend/       # Frontend React
├── Dockerfile              # Imagen del backend
├── pom.xml
└── .github/workflows/
    └── build.yml           # Pipeline de CI/CD
```

## Integración continua y despliegue

El pipeline se ejecuta con **GitHub Actions** en cada push a `main` y a las ramas de trabajo (estrategia *trunk based*: ramas cortas que se integran a `main` mediante pull request).

| Etapa     | Qué hace | Herramienta |
|-----------|---|---|
| `tests`   | Ejecuta las pruebas unitarias con PostgreSQL como servicio | Maven, JUnit |
| `quality` | Análisis de calidad y cobertura | SonarCloud, JaCoCo, Coveralls |
| `snyk`    | Búsqueda de vulnerabilidades en dependencias | Snyk |
| `build`   | Genera el JAR y lo publica como artefacto | Maven |
| `docker`  | Construye y sube la imagen a Docker Hub | Docker |
| `deploy`  | Despliega el backend en la nube | Render |

Los secretos (`SONAR_TOKEN`, `SNYK_TOKEN`, `DOCKER_USERNAME`, `DOCKER_PASSWORD`, etc.) se configuran en *Settings → Secrets and variables → Actions*.

## Cómo ejecutarlo

**Requisitos:** JDK 21, Maven, Docker y PostgreSQL (o Docker Compose).

Backend local:

```shell
./mvnw spring-boot:run
```

Pruebas:

```shell
./mvnw clean verify
```

Con Docker:

```shell
docker pull miguelavenda07/arquisoft:latest
docker run -p 8080:8080 miguelavenda07/arquisoft:latest
```
Con Docker Compose (backend + PostgreSQL):

```shell
docker compose up -d
```
La API queda disponible en `http://localhost:8080`.

Frontend:

```shell
cd udebank-frontend
npm install
npm run dev
```

## Despliegue

- **Backend y base de datos:** Render.
- **Imagen Docker:** `miguelavenda07/arquisoft` en Docker Hub.
- **URL del backend:** `https://arquisoft.onrender.com`