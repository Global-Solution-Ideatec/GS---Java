-- Script Oracle para adicionar coluna DS_FATORES na tabela TB_RECOMENDACAO_IA
-- Executar no schema do aplicativo (ajuste o tablespace/segment se necessário)

ALTER TABLE TB_RECOMENDACAO_IA
ADD (DS_FATORES VARCHAR2(2000));

-- opcional: atualizar colunas existentes com valor padrão para evitar nulls (exemplo)
-- UPDATE TB_RECOMENDACAO_IA SET DS_FATORES = 'Sem fatores disponíveis' WHERE DS_FATORES IS NULL;
-- COMMIT;
