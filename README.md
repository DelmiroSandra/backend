# Freelancer Backend API (Spring Boot + JWT + PostgreSQL)

Este é o backend de uma aplicação Freelancer, desenvolvido em **Spring Boot** com autenticação baseada em **JWT**, usando **PostgreSQL** como banco de dados.

---

## 📦 Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- Maven
- Swagger (SpringDoc)

---

## 🚀 Como Rodar o Projeto

### 1. Pré-requisitos

- Java JDK 17+
- Maven
- PostgreSQL instalado e rodando
- Postman (opcional, para testar API)

---

### 2. Configuração do Banco de Dados

Crie o banco:

```sql
CREATE DATABASE freelancerdb;
```
### 3. Depois, configure o arquivo application.properties:
```pom.xml

spring.datasource.url=jdbc:postgresql://localhost:5432/freelancerdb
spring.datasource.username=postgres
spring.datasource.password=8526
```
⚠️ Altere o username e password conforme seu ambiente

# Como Usar com Postman
1. Criar Usuário
POST http://localhost:8080/api/auth/signup

Body → raw → JSON:

```Postma

{
  "name": "Delmiro",
  "username": "delmiro",
  "email": "delmiro@email.com",
  "password": "123456"
}

```
2. Fazer Login
POST http://localhost:8080/api/auth/login

Body → raw → JSON:
```Postma

{
  "usernameOrEmail": "delmiro",
  "password": "123456"
}


```

🧑‍💻 Autores
Delmiro Sandra & Miguel Ncuna
Desenvolvedor backend iniciante em Spring Boot, construindo APIs seguras com JWT.
