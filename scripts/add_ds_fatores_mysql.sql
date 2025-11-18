-- Script MySQL para adicionar coluna ds_fatores na tabela tb_recomendacao_ia
ALTER TABLE tb_recomendacao_ia
ADD COLUMN ds_fatores VARCHAR(2000);

-- Opcional: popular com valor default
-- UPDATE tb_recomendacao_ia SET ds_fatores = 'Sem fatores disponíveis' WHERE ds_fatores IS NULL;
-- COMMIT;
