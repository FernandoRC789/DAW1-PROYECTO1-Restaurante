use billing_db;

#tabla estado registros
INSERT INTO tb_estado (nom_estado)
values ("PENDIENTE"),
		("EN PROCESO"),
        ("LISTO"),
		("PAGADO"),
        ("ANULADO");
        

INSERT INTO tb_estado_comprobante (nombre) VALUES
('PAGADO'),
('ANULADO'),
('PENDIENTE');

select * from tb_estado;
