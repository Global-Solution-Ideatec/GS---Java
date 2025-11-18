-- Flyway migration: adiciona coluna ds_fatores na tabela de recomendações
-- Funciona em Postgres/MySQL/H2

ALTER TABLE tb_recomendacao_ia
ADD COLUMN ds_fatores VARCHAR(2000);

-- Observação: ajustar tipo para Oracle se necessário (usar scripts/oracle separado).
