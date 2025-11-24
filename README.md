# SmartLeader — Aplicação de Recomendações e Bem-Estar no Trabalho Híbrido

Este repositório contém a aplicação SmartLeader — um WebApp Spring Boot que gera recomendações de alocação de colaboradores com suporte a mensageria, integração com IA generativa e autenticação JWT. O projeto foi desenvolvido como entrega para a disciplina Java Advanced e implementa recursos para demonstração de arquitetura moderna (cache, filas, integração externa e interface web simples).

---

Índice
- Visão geral (ideia)
- Benefícios e impacto
- Arquitetura e stack tecnológica
- Como executar localmente (passo a passo)
- Variáveis de ambiente importantes
- Ativando OpenAI, Redis e RabbitMQ (habilitar recursos)
- Docker e Docker Compose (build & run)
- Deploy em nuvem — instruções rápidas (exemplos)
- Endpoints principais e exemplos de uso (curl)
- Migrações e scripts de banco
- Testes
- Checklist final para entrega
- Próximos passos recomendados

---

## Visão geral (ideia)

**SmartLeader** – Hub de Produtividade e Bem-Estar no Trabalho Híbrido

O SmartLeader é uma plataforma que combina inteligência artificial e gestão colaborativa para promover bem-estar e produtividade no ambiente de trabalho híbrido.

Resumo do conceito (MVP):
- Coleta perfil de usuários (habilidades, histórico de tarefas, carga atual) e permite que gestores criem tarefas.
- Um serviço responsável por análise e recomendação (**IAService**) sugere automaticamente o colaborador mais adequado para uma tarefa com base em critérios simples (menos tarefas abertas, maior número de habilidades) e, quando configurado, complementa a recomendação com explicações geradas por IA (OpenAI).
- Mensageria (RabbitMQ) é utilizada para processamento assíncrono; quando não disponível, há fallback síncrono para manter funcionalidade em ambientes simples.
- UI mínima (Thymeleaf) disponível para demonstração; a aplicação também expõe API REST para integração com frontends web/mobile.

Detalhes da proposta (trecho refinado do rascunho da ideia):
- Objetivo: equilibrar produtividade e bem-estar ao alocar tarefas de forma inteligente, evitando sobrecarga de colaboradores.
- Público-alvo: empresas híbridas, gestores de equipes e colaboradores que buscam melhor organização e qualidade de vida no trabalho.
- Funcionalidades chave do MVP:
  - Perfil de habilidades (**SkillMap**)
  - Distribuição inteligente de tarefas
  - Monitor de bem-estar (check-ins simples) — conceito para etapas futuras
  - Feed de feedbacks e dashbord gerencial
  - IA que sugere pausas, redistribuição e justifica recomendações

---

## Benefícios e impacto

- Melhora da distribuição de carga de trabalho e redução de risco de burnout.
- Aumento de produtividade: tarefas chegam para membros com habilidades mais adequadas.
- Visibilidade para gestores sobre gaps de habilidades e sobrecarga da equipe.
- Experiência humana: recomendações pensadas para equilibrar eficiência e bem-estar.

---

## Arquitetura e stack tecnológica

- Backend principal: **Spring Boot** (Java 17)
- Persistência: **Spring Data JPA** (H2 em dev; suporte a Oracle/Postgres/MySQL)
- Segurança: **Spring Security** + **JWT**
- Mensageria: **RabbitMQ** (opcional — app funciona sem ele com fallback)
- Cache: **Spring Cache** (Simple para dev); suporte a Redis em produção
- IA generativa: integração com **OpenAI** via `OpenAIService` (RestTemplate); projeto pode ser adaptado para `spring-ai` se necessário
- Frontend de demonstração: **Thymeleaf** (templates simples em `src/main/resources/templates`)
- Documentação API: **OpenAPI** (springdoc)
- Containerização: **Docker** + **Docker Compose**

---

## Como executar localmente (passo a passo)

Pré-requisitos:
- Java 17
- Maven 3.8+
- (Opcional) Docker & Docker Compose — para RabbitMQ/Redis em dev

1) Build do projeto

```bash
mvn -DskipTests package
```

2) Executar local (Spring Boot) — porta padrão definida em `application.properties` é 8081

```bash
mvn spring-boot:run

# ou
java -jar target/untitled-1.0-SNAPSHOT.jar
```

3) Acesse:
- Aplicação web (Thymeleaf demo): http://localhost:8081/
- H2 Console (dev): http://localhost:8081/h2-console (URL: `jdbc:h2:mem:smartleaderdb`, user: `sa`, sem senha)
- OpenAPI UI (Swagger): http://localhost:8081/swagger-ui/index.html

Credenciais de demo (criadas pelo `DataLoader` / `CommandLineRunner` no profile dev):
- Gestor (demo): `gestor@demo` / `demo123`
- Colaborador (demo): `colaborador@demo` / `demo123`

Observação: o projeto contém seeds adicionais (ex.: `gestor@example.com` / `senha`) — são criados automaticamente em profile `!test`.

---

## Variáveis de ambiente importantes

As principais variáveis/propriedades que influenciam integrações e segurança:
- `OPENAI_API_KEY` — chave para chamadas à API OpenAI (opcional). Também pode ser definida via propriedade `openai.api.key`.
- `app.jwt.secret` (ou `APP_JWT_SECRET`) — segredo HMAC para tokens JWT.
- `app.jwt.expiration-ms` — expiração do token (ms).
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` — credenciais do banco de produção.
- `SPRING_REDIS_HOST` / `spring.redis.host` — host do Redis para cache.
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` — credenciais RabbitMQ.
- `openai.enabled` — força ativação da integração OpenAI mesmo sem API key (útil para testes controlados).

Exemplo de export (no Windows `cmd.exe` você pode setar variáveis assim antes de rodar):

```bat
set OPENAI_API_KEY=sk_xxx...
set APP_JWT_SECRET=trocasegura1234567890
set SPRING_REDIS_HOST=localhost
set SPRING_RABBITMQ_HOST=localhost
```

---

## Ativando OpenAI, Redis e RabbitMQ (modo rápido)

**OpenAI**
- Para habilitar a explicação/justificativa gerada por IA, defina `OPENAI_API_KEY` (ou `openai.api.key` no `application.properties`).
- Você também pode definir `openai.enabled=true` para forçar habilitação.
- Sem chave, o sistema usa um fallback: gera uma justificativa simples e salva em `fatores`.

**Redis (cache)**
- Em desenvolvimento o cache é `simple`. Para usar Redis em dev, rode um container Redis e aponte `spring.redis.host` para `localhost`.

```bat
docker run -d --name smartleader-redis -p 6379:6379 redis:latest
```

**RabbitMQ (mensageria)**
- Para rodar Rabbit localmente (UI de management em 15672):

```bat
docker run -d --hostname rabbit --name smartleader-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
- Usuário padrão: `guest` / `guest` (útil apenas em dev). A aplicação já tratou fallback quando `RabbitTemplate` não está disponível.
- Verifique a UI: http://localhost:15672

---

## Docker e Docker Compose

**Build da imagem local:**

```bat
docker build -t smartleader:latest .
```

**Executar com Docker Compose** (exemplo mínimo com app + rabbit + redis) — caso seu `docker-compose.yml` já defina serviços, use-o; caso não, execute os containers separados como mostrado anteriormente.

```bat
docker-compose up --build
```

**Para rodar apenas o app em container:**

```bat
docker run -p 8081:8081 --env APP_JWT_SECRET=trocasegura123 -e SPRING_PROFILES_ACTIVE=dev smartleader:latest
```

Observação: atente para variáveis de ambiente em produção (DB, secrets, OPENAI_API_KEY, etc.).

---

## Deploy em nuvem — instruções rápidas (exemplos)

Abaixo estão fluxos genéricos que podem ser adaptados ao provedor escolhido (Google Cloud Run, Heroku, Render, AWS ECS):

**Opção A** — Docker Hub + Google Cloud Run (exemplo)
1. Build e tag localmente:

```bat
docker build -t your-dockerhub-username/smartleader:latest .
```

2. Push para registry:

```bat
docker push your-dockerhub-username/smartleader:latest
```

3. Deploy no Cloud Run (via console ou `gcloud`):
- No console Cloud Run, aponte para a imagem no Docker Hub e configure variáveis de ambiente (`OPENAI_API_KEY`, `APP_JWT_SECRET`, DB URL).

**Opção B** — Heroku (container-based)
1. Faça login no Heroku Container Registry, build e push, depois release. Veja documentação Heroku para `heroku container:push web`.

Observações importantes para produção:
- Use um banco gerenciado (Postgres, Oracle, etc.). Configure `spring.datasource.*` adequadamente.
- Use um segredo forte para `APP_JWT_SECRET` e nunca comite no repo.
- Ative Redis para caching em produção (melhora performance).
- Habilite uma instância RabbitMQ (ou serviço gerenciado) caso deseje processamento assíncrono em larga escala.

---

## Endpoints principais e exemplos de uso (curl — `cmd.exe`)

**Login (obter token JWT):**

```bat
curl -s -X POST http://localhost:8081/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"gestor@demo\",\"password\":\"demo123\"}"
```

**Gerar recomendação síncrona (protegido — precisa token):**

```bat
curl -H "Authorization: Bearer <TOKEN>" "http://localhost:8081/api/recomendacao/colaborador?area=TI"
```

**Solicitar recomendação assíncrona (envia request para fila; fallback síncrono quando Rabbit indisponível):**

```bat
curl -s -X POST http://localhost:8081/api/recomendacao/request -H "Content-Type: application/json" -d "{\"area\":\"TI\"}"
```

**Obter fatores/explicação para uma recomendação existente (REST):**

```bat
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8081/api/recomendacao/1/fatores
```

**Listar recursos (paginação suportada — `page` e `size`):**

```bat
curl http://localhost:8081/api/usuarios?page=0&size=10
```

---

## Migrações e scripts de banco

- O projeto contém scripts em `scripts/` para adicionar a coluna `DS_FATORES` em Oracle/Postgres/MySQL (`add_ds_fatores_*.sql`) e um migration Flyway `src/main/resources/db/migration/V1__add_ds_fatores.sql`.
- Em dev com H2, `spring.jpa.hibernate.ddl-auto=update` cria as colunas automaticamente.

---

## Testes

Rodar testes unitários e de integração (maven):

```bat
mvn test
```

Se quiser executar com Testcontainers (integração com RabbitMQ/Redis), garanta que Docker esteja rodando e que as propriedades de teste sejam configuradas.

---

## Checklist final para entrega (o que incluir no arquivo ZIP/portal do curso)

- Link do repositório GitHub (código fonte completo)
- Links dos deploys em nuvem (URL do app) e instruções de acesso (usuário/senha de teste, se aplicável)
- Vídeo Pitch (link do YouTube ou equivalente)
- Vídeo demonstrando o software funcionando (máx. 10 minutos) — inclua navegação pela UI e exemplos de endpoints protegidos com JWT
- Instruções para rodar localmente (este README já contém)
- Observações sobre chaves e secrets (instrua o avaliador a usar credenciais de demo ou informe credenciais temporárias)

---

## Próximos passos recomendados (antes da entrega final)

1. Consolidar o `GlobalExceptionHandler` (há dois arquivos de tratamento de exceção no repo; unificar formato de erro JSON).
2. Rodar `mvn test` e corrigir falhas nos testes.
3. Fazer deploy em um provedor público (Cloud Run / Heroku / Render) e preencher os links no portal de entrega.
4. Gravar o vídeo pitch e o vídeo demo (≤ 10 min) explicando a ideia SmartLeader, o fluxo de recomendação e mostrando o app em funcionamento.
5. (Opcional) Migrar `OpenAIService` para uso da biblioteca `spring-ai` se for requisito explícito do avaliador; caso contrário, documentar a escolha de usar `RestTemplate`.

---

## Contato e notas finais

- Este README agora contém: visão do projeto (ideia), benefícios, instruções completas para rodar, habilitar integrações e passos de deploy. Se quiser, eu posso:
  - Gerar um `deploy.md` com comandos passo-a-passo para um provedor específico (ex.: Google Cloud Run), ou
  - Consolidar o handler de exceções e rodar os testes automatizados aqui no repo.

Boa sorte na entrega — se desejar que eu aplique mais mudanças (ex.: unificar handlers, criar instruções de deploy detalhadas para um provedor específico ou preparar o script de CI/CD), diga qual prioridade e eu continuo imediatamente.
