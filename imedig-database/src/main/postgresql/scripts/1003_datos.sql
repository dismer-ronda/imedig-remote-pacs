--// centros
-- Migration SQL that makes the change goes here.

---------------------------------------------    
-- zonas horarias
---------------------------------------------
insert into susc_horarios ( id, nombre ) values ( 1, 'Europe/Madrid' );
insert into susc_horarios ( id, nombre ) values ( 2, 'America/Havana' );
insert into susc_horarios ( id, nombre ) values ( 3, 'America/Santo_Domingo' );

---------------------------------------------    
-- monedas
---------------------------------------------
insert into susc_monedas ( id, nombre, codigo, simbolo ) values ( 978, 'Euro', 'EUR', '€' );
insert into susc_monedas ( id, nombre, codigo, simbolo ) values ( 840, 'United States Dollar', 'USD', '$' );
insert into susc_monedas ( id, nombre, codigo ) values ( 192, 'Peso cubano', 'CUP' );
insert into susc_monedas ( id, nombre, codigo ) values ( 193, 'Peso convertible', 'CUC' );
insert into susc_monedas ( id, nombre, codigo ) values ( 214, 'Peso Dominicano', 'DOP' );

insert into susc_perfiles (id, descripcion) values (1, 'Administrador');
insert into susc_perfiles (id, descripcion) values (2, 'Médico');
insert into susc_perfiles (id, descripcion) values (3, 'Imagenólogo');
insert into susc_perfiles (id, descripcion) values (4, 'Administrador de centros');
insert into susc_perfiles (id, descripcion) values (5, 'Estudiante');
insert into susc_perfiles (id, descripcion) values (6, 'Administrativo');

insert into susc_derechos (id, codigo, descripcion) values (1, 'login','Acceso a la aplicación');

insert into susc_derechos (id, codigo, descripcion) values (2, 'configuracion','Acceso a configuración del sistema');

insert into susc_derechos (id, codigo, descripcion) values (22, 'configuracion.todo','Acceso a toda la configuracion');

insert into susc_derechos (id, codigo, descripcion) values (3, 'configuracion.imagenes','Mantenimiento de catálogo de imágenes');
insert into susc_derechos (id, codigo, descripcion) values (4, 'configuracion.imagenes.adicionar','Adicionar imágenes');
insert into susc_derechos (id, codigo, descripcion) values (5, 'configuracion.imagenes.modificar','Modificar imágenes');
insert into susc_derechos (id, codigo, descripcion) values (6, 'configuracion.imagenes.borrar','Borrar imágenes');

insert into susc_derechos (id, codigo, descripcion) values (7, 'configuracion.centros','Mantenimiento de catálogo de centros');
insert into susc_derechos (id, codigo, descripcion) values (8, 'configuracion.centros.adicionar','Adicionar centros');
insert into susc_derechos (id, codigo, descripcion) values (9, 'configuracion.centros.modificar','Modificar centros');
insert into susc_derechos (id, codigo, descripcion) values (10, 'configuracion.centros.borrar','Borrar centros');

insert into susc_derechos (id, codigo, descripcion) values (11, 'configuracion.usuarios','Mantenimiento de catálogo de usuarios');
insert into susc_derechos (id, codigo, descripcion) values (12, 'configuracion.usuarios.adicionar','Adicionar usuarios');
insert into susc_derechos (id, codigo, descripcion) values (13, 'configuracion.usuarios.modificar','Modificar usuarios');
insert into susc_derechos (id, codigo, descripcion) values (14, 'configuracion.usuarios.borrar','Borrar usuarios');

insert into susc_derechos (id, codigo, descripcion) values (15,'administracion.acceso','Log de accesos al sistema') ;

insert into susc_derechos (id, codigo, descripcion) values (16,'informes.crear','Creación y modificación de informes') ;
insert into susc_derechos (id, codigo, descripcion) values (17,'informes.aprobar','Aprobación de informes') ;
insert into susc_derechos (id, codigo, descripcion) values (23,'informes.solicitar','Solicitud de informes') ;
insert into susc_derechos (id, codigo, descripcion) values (24,'informes.terminar','Terminar informes') ;

insert into susc_derechos (id, codigo, descripcion) values (18, 'configuracion.informes.plantillas','Mantenimiento de catálogo de plantillas de informes');
insert into susc_derechos (id, codigo, descripcion) values (19, 'configuracion.informes.plantillas.adicionar','Adicionar plantillas');
insert into susc_derechos (id, codigo, descripcion) values (20, 'configuracion.informes.plantillas.modificar','Modificar plantillas');
insert into susc_derechos (id, codigo, descripcion) values (21, 'configuracion.informes.plantillas.borrar','Borrar plantillas');

insert into susc_derechos (id, codigo, descripcion) values (25, 'configuracion.tipos.estudios','Mantenimiento de catálogo de tipos de estudios');
insert into susc_derechos (id, codigo, descripcion) values (26, 'configuracion.instalaciones','Mantenimiento de catálogos de instalaciones');


insert into susc_perfiles_derechos (perfil, derecho) values (1, 1);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 2);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 3);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 4);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 5);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 6);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 7);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 8);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 9);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 10);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 11);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 12);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 13);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 14);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 15);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 16);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 17);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 18);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 19);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 20);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 21);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 22);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 23);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 24);
--nuevos
insert into susc_perfiles_derechos (perfil, derecho) values (1, 25);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 26);


insert into susc_perfiles_derechos (perfil, derecho) values (2, 1);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 23);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 23);

insert into susc_perfiles_derechos (perfil, derecho) values (3, 1);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 16);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 17);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 23);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 24);

insert into susc_perfiles_derechos (perfil, derecho) values (4, 1);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 2);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 3);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 4);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 5);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 6);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 7);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 9);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 11);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 12);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 13);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 14);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 16);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 18);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 19);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 20);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 21);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 23);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 24);

insert into susc_perfiles_derechos (perfil, derecho) values (5, 1);

insert into susc_perfiles_derechos (perfil, derecho) values (6, 1);
insert into susc_perfiles_derechos (perfil, derecho) values (6, 16);
insert into susc_perfiles_derechos (perfil, derecho) values (6, 17);
insert into susc_perfiles_derechos (perfil, derecho) values (6, 23);
insert into susc_perfiles_derechos (perfil, derecho) values (6, 24);

---------------------------------------------    
-- usuarios 
---------------------------------------------
insert into susc_usuarios (id, login, email, pwd, nombre, ape1, ape2, perfil, estado, cambio, intentos, filtro, query, compresion) 
values (1, 'imedig', 'imedig@pryades.com', '6819358a976bc847577d5407ae8c8945', 'Administrador', 'Sistema', '', 1, 0, extract(year from current_date)*10000 + extract(month from current_date)*100 + extract(day from current_date), 0, '*', 0, 'image/png' );

INSERT INTO susc_centros( id, descripcion, orden, nombre, horario, moneda, serie, puerto) 
VALUES (1, 'IMEDIG Local', 1, 'IMEDIG Local', 2, 192, 'IMEDIG', 8080);

insert into susc_usuarios_centros (usuario, centro) 
values( 1, 1 );

--//@UNDO
-- SQL to undo the change goes here.
