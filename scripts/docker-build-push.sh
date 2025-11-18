#!/usr/bin/env bash
# Script para buildar e enviar imagem Docker para um registry
# Ajuste DOCKER_REPO e IMAGE_TAG conforme necessário

set -euo pipefail

DOCKER_REPO=${DOCKER_REPO:-youruser/smartleader}
IMAGE_TAG=${IMAGE_TAG:-latest}
REGISTRY=${REGISTRY:-docker.io}

IMAGE=${DOCKER_REPO}:${IMAGE_TAG}

echo "Building Docker image ${IMAGE}..."
docker build -t ${IMAGE} .

echo "Pushing to registry ${REGISTRY}..."
docker push ${IMAGE}

echo "Done. Image: ${IMAGE}"

# Example: docker run -e APP_JWT_SECRET=... -p 8080:8080 ${IMAGE}

