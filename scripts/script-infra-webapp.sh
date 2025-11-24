#!/bin/bash
# Criar App Service Plan
az appservice plan create \
  --name gs-app-service-plan \
  --resource-group gs-resource-group \
  --location "Brazil South" \
  --sku B1 \
  --is-linux

# Criar Web App para Java
az webapp create \
  --resource-group gs-resource-group \
  --plan gs-app-service-plan \
  --name gs-java-webapp \
  --runtime "JAVA|11-java11"
