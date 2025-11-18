# Deploy & Publicação

Este documento descreve passos para empacotar a aplicação em Docker, publicar numa registry (Docker Hub, GHCR, ECR) e rodar em um ambiente com `docker-compose`. Também inclui opções rápidas para Cloud Run (GCP) e ECS (AWS).

## Pré-requisitos
- Ter o jar gerado: `mvn -DskipTests package` (arquivo em `target/untitled-1.0-SNAPSHOT.jar`)
- Docker instalado e logado (`docker login`)
- (Opcional) Conta em Docker Hub / GHCR / ECR
- Variáveis de ambiente configuradas para o ambiente de produção

## Variáveis de ambiente importantes
Defina as variáveis no ambiente ou usando um `.env` para o `docker-compose`:

- `APP_JWT_SECRET` — segredo para tokens JWT (ex: changeit...) 
- `APP_JWT_EXPIRATION_MS` — tempo de expiração em ms
- `SPRING_DATASOURCE_URL` — JDBC URL do banco
- `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`
- `OPENAI_API_KEY` — chave OpenAI (se aplicável)
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` — RabbitMQ
- (Opcional) `REDIS_HOST`, `REDIS_PORT` para cache em produção

## Docker build & push (exemplo Docker Hub)
1. Ajuste as variáveis:
```bash
export DOCKER_REGISTRY=docker.io
export DOCKER_REPO=<seu-usuario>/smartleader
export IMAGE_TAG=latest
```
2. Build da imagem:
```bash
docker build -t ${DOCKER_REPO}:${IMAGE_TAG} .
```
3. Push para registry:
```bash
docker push ${DOCKER_REPO}:${IMAGE_TAG}
```
4. Rodar com Docker:
```bash
docker run -e APP_JWT_SECRET=${APP_JWT_SECRET} -e SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL} -p 8080:8080 ${DOCKER_REPO}:${IMAGE_TAG}
```

## Usando Docker Compose
1. Ajuste `docker-compose.yml` (se necessário) para incluir serviços: db, rabbitmq, redis.
2. Crie `.env` com as variáveis necessárias.
3. Execute:
```bash
docker-compose up -d --build
```

## Publicar no Google Cloud Run (exemplo rápido)
1. Build e push para Google Container Registry (gcr.io):
```bash
gcloud auth configure-docker
docker build -t gcr.io/PROJECT_ID/smartleader:${IMAGE_TAG} .
docker push gcr.io/PROJECT_ID/smartleader:${IMAGE_TAG}
```
2. Deploy no Cloud Run:
```bash
gcloud run deploy smartleader --image gcr.io/PROJECT_ID/smartleader:${IMAGE_TAG} --platform managed --region us-central1 --allow-unauthenticated --set-env-vars "APP_JWT_SECRET=${APP_JWT_SECRET},SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL},OPENAI_API_KEY=${OPENAI_API_KEY}"
```

## Publicar no AWS ECS (exemplo rápido)
- Geralmente envolve criar um repositório ECR, push da imagem e configurar um Task Definition + Service.
- Recomendo usar `ecs-cli` ou Terraform para infra reprodutível.

## Notas sobre produção
- Nunca coloque segredos no `Dockerfile` — use `secrets`/envvars do provedor.
- Use um `CacheManager` com Redis para performance em produção.
- Configure health checks para o container (ex: `/actuator/health` se expor).  
- Configure logging/monitoring (CloudWatch / Stackdriver / Prometheus).

## Script de exemplo
- Use `scripts/docker-build-push.sh` (fornecido) e ajuste `DOCKER_REPO` e `DOCKER_REGISTRY`.

## Aplicar script SQL no banco
- Antes de rodar em produção, adicione a coluna `ds_fatores` no seu schema com os scripts em `scripts/`.

## Próximos passos recomendados
1. Criar `docker-compose.prod.yml` com serviços: app, db (Postgres), rabbitmq, redis.
2. Adicionar CI (GitHub Actions / GitLab CI) para build, teste e publicar imagens.
3. Teste E2E após deploy.

---

Se quiser, eu gero agora um `docker-compose.prod.yml` de exemplo e o script `scripts/docker-build-push.sh` que automatiza build + push (ex.: Docker Hub). Diga se prefere Docker Hub, GHCR, ou AWS ECR e eu adapto o script automaticamente.
