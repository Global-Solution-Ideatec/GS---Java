# SmartLeader — Hub de Produtividade e Bem-Estar no Trabalho Híbrido

**Integrantes**
- RM 557323 - Carlos Eduardo Pacheco
- RM 558514 - Pedro Augusto Costa Ladeira
- RM 559213 - João Pedro Amorim

SmartLeader é uma plataforma (MVP) que combina inteligência artificial e gestão colaborativa para promover bem-estar e produtividade em equipes em modelo híbrido ou remoto. Este repositório contém a implementação do backend (Spring Boot), templates Thymeleaf para demonstração, integração com OpenAI (opcional), suporte a mensageria e cache.

---

Sumário rápido
- Visão geral e problema que resolvemos
- Benefícios e impacto
- Arquitetura e tecnologias usadas
- Como rodar localmente (passo-a-passo) — Windows (`cmd.exe`)
- Variáveis de ambiente e como habilitar integrações (OpenAI, Redis, RabbitMQ)
- Docker / Docker Compose
- Deploy em nuvem — instruções genéricas
- Endpoints principais (exemplos curl para `cmd.exe`)
- Migrações / scripts de banco
- Testes automatizados
- Checklist final de entrega e próximos passos

---

## Visão geral

O SmartLeader ajuda gestores a distribuir tarefas de forma inteligente, equilibrando carga de trabalho, habilidades e bem-estar dos colaboradores. A solução:
- Coleta perfis (habilidades, histórico de tarefas)
- Analisa carga e disponibilidade
- Recomenda automaticamente o colaborador mais adequado para cada tarefa
- Gera justificativas/explicações (quando integradas com IA generativa)
- Oferece UI simples para demonstração (Thymeleaf) e API REST para integração

Problemas que o SmartLeader endereça
- Distribuição desigual de tarefas
- Falta de visibilidade sobre skills reais
- Risco de burnout por sobrecarga
- Demandas mal alocadas que reduzem produtividade

---

## Benefícios
- Melhor balanceamento de carga entre colaboradores
- Aumento da eficiência ao alocar tarefas para quem tem skills adequadas
- Insights para gestores sobre gaps e sobrecarga
- Fluxo de recomendação transparente (fatores salvos e explicáveis)

---

## Arquitetura e stack tecnológica
- Backend: Spring Boot (Java 17)
- Persistência: Spring Data JPA (H2 para dev; suporte Oracle/Postgres/MySQL)
- Segurança: Spring Security + JWT
- Mensageria: RabbitMQ (opcional; app tem fallback quando Rabbit não disponível)
- Cache: Spring Cache (Simple em dev); suporte a Redis em produção
- IA generativa: integração com OpenAI via `OpenAIService` (RestTemplate). Pode ser adaptado para `spring-ai`.
- Frontend demo: Thymeleaf (templates em `src/main/resources/templates`)
- Documentação API: springdoc OpenAPI (Swagger UI)
- Contêiner: Docker + Docker Compose

---

## Como executar localmente (passo-a-passo)

Pré-requisitos:
- Java 17
- Maven 3.8+
- (Opcional) Docker & Docker Compose — para RabbitMQ/Redis em dev

1) Build do projeto

```bat
mvn -DskipTests package
```

2) Executar local (Spring Boot) — porta padrão: 8081

```bat
mvn spring-boot:run

REM ou
java -jar target/untitled-1.0-SNAPSHOT.jar
```

3) Páginas úteis
- Aplicação (UI demo): http://localhost:8081/
- H2 Console (dev): http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:smartleaderdb`
  - User: `sa` (sem senha)
- Swagger UI: http://localhost:8081/swagger-ui/index.html

Usuários de demonstração criados automaticamente (profile dev):
- Gestor: `gestor@demo` / `demo123`
- Colaborador: `colaborador@demo` / `demo123`

---

## Variáveis de ambiente importantes

- `OPENAI_API_KEY` ou propriedade `openai.api.key` — chave da OpenAI (opcional).
- `openai.enabled` — força habilitação da integração OpenAI.
- `app.jwt.secret` (ou `APP_JWT_SECRET`) — segredo HMAC para JWT.
- `app.jwt.expiration-ms` — expiração do token (ms).
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` — credenciais do banco.
- `SPRING_REDIS_HOST` / `spring.redis.host` — host do Redis.
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` — RabbitMQ.

Exemplo no Windows (`cmd.exe`):

```bat
set OPENAI_API_KEY=sk_xxx...
set APP_JWT_SECRET=minha_senha_secreta
set SPRING_REDIS_HOST=localhost
set SPRING_RABBITMQ_HOST=localhost
```

---

## Ativando integrações (rápido)

**OpenAI**
- Adicione `OPENAI_API_KEY` ou defina `openai.api.key` em `application.properties`.
- Quando a chave não estiver presente, o sistema usa um fallback textual que ainda grava justificativas (campo `fatores`).

**Redis (cache)**
- Exemplo para rodar Redis em Docker:

```bat
docker run -d --name smartleader-redis -p 6379:6379 redis:latest
```
- Em seguida, ajuste `spring.redis.host` para `localhost` (ou use variáveis de ambiente).

**RabbitMQ (mensageria)**
- Exemplo para rodar RabbitMQ com management UI:

```bat
docker run -d --hostname rabbit --name smartleader-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
- A aplicação tem fallback quando `RabbitTemplate` não está disponível — portanto é seguro testar sem Rabbit.

---

## Docker e Docker Compose

**Build da imagem local:**

```bat
docker build -t smartleader:latest .
```

**Executar (com docker-compose quando existir configuração adequada):**

```bat
docker-compose up --build
```

**Rodar apenas o container do app (exemplo):**

```bat
docker run -p 8081:8081 -e APP_JWT_SECRET=minha_senha smartleader:latest
```

---

## Deploy em nuvem — instruções genéricas

**Opção 1** — Docker Hub + Google Cloud Run (resumo):
1. `docker build -t username/smartleader:latest .`
2. `docker push username/smartleader:latest`
3. No Cloud Run, crie serviço apontando para a imagem e configure variáveis de ambiente (DB, OPENAI_API_KEY, APP_JWT_SECRET, etc.).

**Opção 2** — Heroku (container-based): consulte `heroku container:push web` e `heroku container:release web`.

Dicas para produção:
- Use banco gerenciado (Postgres/Oracle) e configure `spring.datasource.*`.
- Guarde segredos em vaults ou secrets do provedor.
- Ative Redis para cache e uma instância RabbitMQ gerenciada, se for preciso escalabilidade assíncrona.

---

## Endpoints principais (exemplos `cmd.exe`)

1) Login (gera JWT):

```bat
curl -s -X POST http://localhost:8081/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"gestor@demo\",\"password\":\"demo123\"}"
```

Resposta: `{ "token": "<JWT>" }`

2) Recomendação síncrona (protegido):

```bat
curl -H "Authorization: Bearer <TOKEN>" "http://localhost:8081/api/recomendacao/colaborador?area=TI"
```

3) Solicitar recomendação assíncrona (enqueue):

```bat
curl -s -X POST http://localhost:8081/api/recomendacao/request -H "Content-Type: application/json" -d "{\"area\":\"TI\"}"
```

4) Obter fatores/explicação de uma recomendação:

```bat
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8081/api/recomendacao/1/fatores
```

5) Listagem paginada (ex.: usuários):

```bat
curl http://localhost:8081/api/usuarios?page=0&size=10
```

---

## Migrações e scripts de banco

- Scripts para adicionar coluna `DS_FATORES` em diferentes DBs estão em `scripts/`:
  - `add_ds_fatores_oracle.sql`, `add_ds_fatores_postgres.sql`, `add_ds_fatores_mysql.sql`
- Migração Flyway também disponível em `src/main/resources/db/migration/V1__add_ds_fatores.sql`.
- Em H2 (dev), `spring.jpa.hibernate.ddl-auto=update` cria as colunas automaticamente.

---

## Testes

Rodar todos os testes (unitários e de integração):

```bat
mvn test
```

Se for usar Testcontainers (RabbitMQ/Redis), certifique-se de que Docker está disponível.

---

## Checklist de entrega (o que incluir no envio)

- Link do repositório GitHub com código fonte completo
- Links dos deploys em nuvem (URLs) e instruções de acesso (usuário/senha de teste ou instruções para gerar token)
- Vídeo Pitch (link YouTube ou equivalente)
- Vídeo demonstrando o software funcionando (≤ 10 minutos) — mostrar UI e chamadas de API com JWT
- Instruções para rodar localmente (este README serve como guia)

---

## Notas técnicas e próximos passos recomendados

- Há atualmente dois `GlobalExceptionHandler` em pacotes diferentes. Recomendo unificar para um único handler (`@RestControllerAdvice`) com formato JSON consistente antes da entrega.
- A integração com OpenAI foi feita via `RestTemplate` em `OpenAIService`; se for requisito explícito usar `spring-ai`, podemos migrar.
- Rodar `mvn test` e corrigir quaisquer falhas antes de subir o repositório final.

---

## Contato

Se quiser que eu:
- Unifique os handlers de exceção automaticamente e rode os testes, ou
- Gere instruções de deploy passo-a-passo para um provedor específico (ex.: Google Cloud Run),
- Gere um PDF formatado da proposta (a partir do texto anexado),

diga qual ação prefere que eu execute em seguida e eu aplico imediatamente.
