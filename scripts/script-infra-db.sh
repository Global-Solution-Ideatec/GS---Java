#!/bin/bash
# Criar Azure SQL Server
az sql server create \
  --name gs-sql-server \
  --resource-group gs-resource-group \
  --location "Brazil South" \
  --admin-user gsadmin \
  --admin-password SenhaForte123!

# Criar banco de dados
az sql db create \
  --resource-group gs-resource-group \
  --server gs-sql-server \
  --name gsdb \
  --service-objective S0

# Permitir acesso (ajuste conforme necessário)
az sql server firewall-rule create \
  --resource-group gs-resource-group \
  --server gs-sql-server \
  --name AllowAllAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255
