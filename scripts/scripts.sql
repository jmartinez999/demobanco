select count (1) from cuenta;

select nextval('control_auditoria_seq') as num;
select nextval('detalle_auditoria_seq') as num;

select num_cuenta, saldo from cuenta where cliente_id= 1;
select * from cliente;

select tipo_identificacion, identificacion, num_cuenta, saldo
from cliente, cuenta
where id = 21
and cliente_id = id;



select tipo_operacion, sum(valor) as suma
from movimiento where cuenta_id = 7
group by tipo_operacion;


select suma


select * from cliente;

select * from cuenta;

select current_date;

select * from movimiento;

insert into cuenta values(nextval('cuenta_num_cuenta_seq'),CURRENT_DATE,300000,21);

insert into movimiento values(nextval('movimiento_id_seq'),LOCALTIMESTAMP,'DEPOSITO',50000,7);

insert into movimiento values(nextval('movimiento_id_seq'),LOCALTIMESTAMP,'RETIRO',20000,7);
insert into movimiento values(nextval('movimiento_id_seq'),LOCALTIMESTAMP,'RETIRO',10000,7);
insert into movimiento values(nextval('movimiento_id_seq'),LOCALTIMESTAMP,'DEPOSITO',1000000,7);


update control_auditoria set estado_proceso = 'XXX'
where id_auditoria= 2 and total_cuentas = cuentas_procesadas;

update control_auditoria set cuentas_procesadas = cuentas_procesadas+1
where id_auditoria=99999;

delete from detalle_auditoria;
delete from control_auditoria;

delete from movimiento;
delete from cuenta;
delete from cliente;

select * from control_auditoria;
select * from detalle_auditoria;


-- Procedure to crear clientes
CREATE OR REPLACE FUNCTION add_clientes(cantidad int)
    RETURNS void AS $$
   DECLARE
    clientes_rec RECORD;
    BEGIN
	FOR i IN 1..cantidad LOOP
	   insert into cliente values (nextval('cliente_id_seq'),current_date,'M',100+i,'Cliente '|| i,'CC',1);
	END LOOP;
    END;
    $$ LANGUAGE plpgsql;

 select add_clientes(1000);
select * from cliente;


-- Procedure to crear cuentas
CREATE OR REPLACE FUNCTION add_cuentas()
    RETURNS void AS $$
   DECLARE
    clientes_rec RECORD;
    BEGIN
      FOR clientes_rec IN SELECT * FROM cliente LOOP
	INSERT INTO cuenta VALUES (nextval('cuenta_num_cuenta_seq'), current_date, 0,clientes_rec.id);
	INSERT INTO cuenta VALUES (nextval('cuenta_num_cuenta_seq'), current_date, 0,clientes_rec.id);
	--INSERT INTO cuenta VALUES (nextval('cuenta_num_cuenta_seq'), current_date, 0,clientes_rec.id);
      END LOOP;
    END;
    $$ LANGUAGE plpgsql;
-----
 select add_cuentas();
------



-- Procedure to crear movimientos
CREATE OR REPLACE FUNCTION add_movimientos()
    RETURNS void AS $$
   DECLARE
    cuentas_rec RECORD;
    index integer;
    movimiento varchar;
    valor decimal;
    acumSaldo decimal;
    BEGIN
      index := 1;
      acumSaldo:=0;
      FOR cuentas_rec IN SELECT * FROM cuenta LOOP
        acumSaldo:=0;
	    FOR i IN 1..100 LOOP
           IF (i % 2) = 0 THEN
                movimiento := 'RETIRO';
                valor:= 35000;
                acumSaldo:= acumSaldo - valor;
           ELSE
                movimiento := 'DEPOSITO';
                valor:= 750000;
                acumSaldo:= acumSaldo +valor;
           END IF;
           INSERT INTO movimiento VALUES (nextval('movimiento_id_seq'), current_date,movimiento,valor,cuentas_rec.num_cuenta);
	    END LOOP;
	    update cuenta set saldo=saldo+acumSaldo where num_cuenta=cuentas_rec.num_cuenta;
      END LOOP;
    END;
    $$ LANGUAGE plpgsql;

 select add_movimientos();

 select count(1) from movimiento;