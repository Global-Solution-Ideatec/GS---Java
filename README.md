# SmartLeader — Aplicação de Recomendações (SmartLeader)

Este repositório contém a aplicação SmartLeader — um WebApp Spring Boot que gera recomendações de alocação de colaboradores (integração com IA, mensageria e JWT).

Este README explica como criar, executar, testar e preparar para deploy em contêiner.

---

## Conteúdo principal
- Código fonte: `src/main/java` e `src/main/resources`
- Scripts DB: `scripts/add_ds_fatores_oracle.sql`, `scripts/add_ds_fatores_postgres.sql`, `scripts/add_ds_fatores_mysql.sql`

---

## Requisitos (local)
- Java 17
- Maven 3.8+
- Docker & Docker Compose (opcional para deploy)

---

## Build e execução local
1. Build:

```bash
mvn -DskipTests package
```

2. Executar com Spring Boot:

```bash
mvn spring-boot:run
# ou
java -jar target/untitled-1.0-SNAPSHOT.jar
```

A aplicação estará disponível em `http://localhost:8080`.

---

## Banco de dados (coluna DS_FATORES)
Se você usa migração manual (Oracle/Postgres/MySQL), execute o script apropriado para adicionar a nova coluna `DS_FATORES` na tabela de recomendações:

- Oracle:
  - `scripts/add_ds_fatores_oracle.sql`
- PostgreSQL:
  - `scripts/add_ds_fatores_postgres.sql`
- MySQL:
  - `scripts/add_ds_fatores_mysql.sql`

Exemplo (psql):
```sql
-- PostgreSQL
psql -h <host> -U <user> -d <db>
ALTER TABLE tb_recomendacao_ia ADD COLUMN ds_fatores varchar(2000);
```

Se estiver usando H2/Hibernate em dev, a coluna será criada automaticamente (se `spring.jpa.hibernate.ddl-auto=update` estiver ativado no `application.properties`).

---

## Variáveis de ambiente importantes
A aplicação usa propriedades via `application.properties` e variáveis de ambiente para credenciais e integrações. Principais variáveis:

- `APP_JWT_SECRET` (ou `app.jwt.secret`): segredo HMAC para gerar/validar JWT
- `APP_JWT_EXPIRATION_MS` (ou `app.jwt.expiration-ms`): tempo de expiração do token (ms)
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`: credenciais do DB
- `OPENAI_API_KEY` (se usar integração OpenAI)
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` (para mensageria)

Exemplo de `application.properties` em produção (ou variáveis de ambiente):

```
app.jwt.secret=${APP_JWT_SECRET:changeit123456789012345678901234}
app.jwt.expiration-ms=${APP_JWT_EXPIRATION_MS:3600000}
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
openai.api.key=${OPENAI_API_KEY}
```

---

## Endpoints principais
- POST `/api/auth/login` — body: `{ "email": "gestor@example.com", "password": "senha" }` → retorna `{ "token": "..." }`
- POST `/api/recomendacao/request` — envia requisição assíncrona (JSON `{ "area": "TI" }`)
- GET `/api/recomendacao/colaborador?area=TI` — retorna recomendação síncrona
- GET `/api/recomendacao/{id}/fatores` — retorna a explicação/fatores para uma recomendação

Exemplo de uso (login + chamar endpoint protegido):
```bash
# login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"gestor@example.com","password":"senha"}' | jq -r .token)

# usar token
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/recomendacao/1/fatores
```

---

## Docker (build e execução)
1. Build da imagem local:

```bash
docker build -t smartleader:latest .
```

2. Executar com docker-compose (exemplo básico):

```bash
docker-compose up --build
```

(Ver `docker-compose.yml` do repositório para serviços relacionados — adapte volumes e variáveis.)

---

## Deploy sugerido
- Criar imagem e publicar em registry (Docker Hub / GHCR / ECR)
- Configurar variáveis de ambiente no ambiente de nuvem (secrets)
- Expor porta 8080 com load balancer
- Configurar Redis para caching em produção (opcional, recomendado)

---

## Tests
Rodar testes unitários/integrados:

```bash
mvn test
```

---

## Observações finais
- Back-end já possui integração com mensageria e OpenAI (se configurado). O fluxo de recomendação grava `fatores` como fallback e atualiza quando a IA responde.
- Adicionei autenticação JWT (geração de token via `/api/auth/login`) e um `GlobalExceptionHandler` para respostas padronizadas de erro.

Se quiser, eu gero um `deploy.md` passo-a-passo com comandos para AWS ECS / Google Cloud Run / Heroku — diga qual provedor prefere.

