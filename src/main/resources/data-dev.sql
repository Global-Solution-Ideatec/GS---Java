-- Seed mínimo para ambiente de desenvolvimento (perfil: dev)
-- Cria uma empresa inicial para uso manual no frontend

INSERT INTO tb_empresa (id_empresa, nm_empresa, ds_cnpj, ds_politica_hibrida, dt_cadastro)
VALUES (1, 'SmartLeader Ltda', '00.000.000/0001-00', 'Modelo híbrido: 3 dias remoto/2 dias escritório', CURRENT_TIMESTAMP);

-- NOTA: não criamos usuários aqui porque a aplicação possui um CommandLineRunner (perfil não-test)
-- que popula usuários em ambientes de desenvolvimento. Se preferir ter usuários via SQL,
-- adicione INSERTs em tb_usuario com senhas já encodeadas (BCrypt) ou ajuste o encoder.

