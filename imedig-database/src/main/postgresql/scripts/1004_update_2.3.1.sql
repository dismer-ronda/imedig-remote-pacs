---
-- tablas para la versión 2.3.1
---

-- paciente 
create table susc_pacientes (
    id integer not null,
    uid varchar(64) not null,    					-- identificador unico
    email varchar(128),    							-- email al que se envian notificaciones
    nombre varchar(50) not null,   				    -- nombre del paciente
    apellido1 varchar(50) not null,   				-- primer apellido del paciente
    apellido2 varchar(50),   						-- segundo apellido del paciente
	fecha_nacimiento integer not null,				-- fecha de nacimiento
	sexo varchar(1) not null,						-- sexo 
	telefono varchar(20),							-- telefono del paciente
	movil varchar(20),								-- numero del movil del paciente
	
    constraint pk_pacientes primary key(id),
	constraint uk_pacientes_uid unique(uid),
	constraint uk_pacientes_email unique(email)
    );
create index ix_pacientes_uid on susc_pacientes(uid);
create index ix_pacientes_email on susc_pacientes(email);
create index ix_pacientes_sexo on susc_pacientes(sexo);

-- tipos de horarios
create table susc_tipos_horarios (
    id integer not null,
    nombre varchar(64) not null,            -- Nombre del tipo de horario
    tipo_recurso integer NOT NULL,          -- Tipo de recurso 1 - equipo...
    tipo_horario integer NOT NULL,          -- Tipo de horario 0 - todos los dias iguales, 1-Horario comun de Lunes a Viernes, Shorario para sabdo  y domingo
    datos text,                             -- JSON con el horario de trabajo según el tipo de horario seleccionado y el tipo de recurso

    constraint pk_tipos_horarios primary key(id),
    constraint uk_tipos_horarios_nombre unique(nombre)
    );

-- recursos:
create table susc_recursos (
    id integer not null,
    nombre varchar(64) not null,                  -- Nombre del recurso
    aetitle varchar(64) not null,                 -- Aetitle
    modalidad character varying(2),
    tipo integer NOT NULL,                        -- Tipo de recurso 1 - equipo...
    tiempominimo integer NOT NULL,                -- Tiempo m inimo de demora de una consulta
    tipo_horario integer,                         -- referencia al tipo de horario de trabajo que se aplica

    constraint pk_recursos primary key(id),
    constraint uk_recursos_aetitle unique(aetitle),
    constraint uk_recursos_nombre unique(nombre),

    constraint fk_recursos_tipo_horario foreign key (tipo_horario) references susc_tipos_horarios(id) on delete cascade
    );
create index ix_recursos_modalidad on susc_recursos(modalidad);

-- tipos de estudios
create table susc_tipos_estudios (
    id integer not null,
    nombre varchar(64) not null,   					-- Nombre del tipo de estudio
    duracion integer not null,						-- Duración de la prueba en minutos
    modalidad character varying(2),
	tipo integer NOT NULL,							-- Tipo de recurso 1 - equipo...
    constraint pk_tipos_estudios primary key(id),
	constraint uk_tipos_estudios_nombre unique(nombre)
    );

-- planificacion de citas
create table susc_citas (
    id integer not null,

    fecha bigint not null,							-- fecha/hora de inicio de la prueba
    fechafin bigint not null,						-- fecha/hora de fin de la prueba
    uid varchar(64) not null,   					-- uuid del estudio

    paciente integer not null,						-- paciente al que se le realiza la prueba
    recurso integer not null,					    -- recurso en el que se realiza la prueba
    tipo integer not null, 							-- tipo de estudio
    referidor integer,   							-- referidor de estudio
    estado integer NOT NULL,						-- 0-planificada, 1- En ejecuión, 3-terminada
    
    constraint pk_estudios primary key(id),
    constraint fk_estudios_paciente foreign key (paciente) references susc_pacientes(id) on delete cascade,
    constraint fk_estudios_recurso foreign key (recurso) references susc_recursos(id) on delete cascade,
    constraint fk_estudios_referidor foreign key (referidor) references susc_usuarios(id) on delete cascade,
    constraint fk_estudios_tipo foreign key (tipo) references susc_tipos_estudios(id) on delete cascade
    ); 

-- Derechos
insert into susc_derechos (id, codigo, descripcion) values (25, 'configuracion.tipos.estudios','Mantenimiento de catálogo de tipos de estudios');
insert into susc_derechos (id, codigo, descripcion) values (26, 'configuracion.tipos.estudios.adicionar','Adicionar tipo de estudios');
insert into susc_derechos (id, codigo, descripcion) values (27, 'configuracion.tipos.estudios.modificar','Modificar tipo de estudios');
insert into susc_derechos (id, codigo, descripcion) values (28, 'configuracion.tipos.estudios.borrar','Borrar tipo de estudios');
insert into susc_derechos (id, codigo, descripcion) values (29, 'configuracion.recursos','Mantenimiento de catálogos de recursos');
insert into susc_derechos (id, codigo, descripcion) values (30, 'configuracion.recursos.adicionar','Adicionar recurso');
insert into susc_derechos (id, codigo, descripcion) values (31, 'configuracion.recursos.modificar','Modificar recurso');
insert into susc_derechos (id, codigo, descripcion) values (32, 'configuracion.recursos.borrar','Borrar recurso');
insert into susc_derechos (id, codigo, descripcion) values (33, 'configuracion.pacientes','Mantenimiento a pacientes');
insert into susc_derechos (id, codigo, descripcion) values (34, 'configuracion.pacientes.adicionar','Adicionar paciente');
insert into susc_derechos (id, codigo, descripcion) values (35, 'configuracion.pacientes.modificar','Modificar paciente');
insert into susc_derechos (id, codigo, descripcion) values (36, 'configuracion.pacientes.borrar','Borrar paciente');
insert into susc_derechos (id, codigo, descripcion) values (37, 'administracion.citas','Mantenimiento de citas');
insert into susc_derechos (id, codigo, descripcion) values (38, 'administracion.citas.adicionar','Adicionar cita');
insert into susc_derechos (id, codigo, descripcion) values (39, 'administracion.citas.modificar','Modificar cita');
insert into susc_derechos (id, codigo, descripcion) values (40, 'administracion.citas.borrar','Borrar cita');
insert into susc_derechos (id, codigo, descripcion) values (41, 'configuracion.tipos.horarios','Configuracion de tipos de horarios');
insert into susc_derechos (id, codigo, descripcion) values (42, 'configuracion.tipos.horarios.adicionar','Adicionar tipo de horario');
insert into susc_derechos (id, codigo, descripcion) values (43, 'configuracion.tipos.horarios.modificar','Modificar tipo de horario');
insert into susc_derechos (id, codigo, descripcion) values (44, 'configuracion.tipos.horarios.borrar','Borrar tipo de horario');

--derechos por perfiles
--Perfil Administrador: todos los derechos
--tipo de estudios
insert into susc_perfiles_derechos (perfil, derecho) values (1, 25);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 26);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 27);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 28);
--configuracion de recursos
insert into susc_perfiles_derechos (perfil, derecho) values (1, 29);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 30);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 31);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 32);
--pacientes
insert into susc_perfiles_derechos (perfil, derecho) values (1, 33);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 34);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 35);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 36);
--citas
insert into susc_perfiles_derechos (perfil, derecho) values (1, 37);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 38);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 39);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 40);
--tipos de horarios
insert into susc_perfiles_derechos (perfil, derecho) values (1, 41);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 42);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 43);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 44);

--Perfil Médico:
--tipo de estudios
insert into susc_perfiles_derechos (perfil, derecho) values (2, 25);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 26);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 27);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 28);
--configuracion de recursos
insert into susc_perfiles_derechos (perfil, derecho) values (2, 29);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 30);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 31);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 32);
--pacientes
insert into susc_perfiles_derechos (perfil, derecho) values (2, 33);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 34);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 35);
--citas
insert into susc_perfiles_derechos (perfil, derecho) values (2, 37);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 38);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 39);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 40);
--tipos de horarios
insert into susc_perfiles_derechos (perfil, derecho) values (2, 41);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 42);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 43);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 44);

--Perfil Imagenólogo:
--tipo de estudios
insert into susc_perfiles_derechos (perfil, derecho) values (3, 25);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 26);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 27);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 28);
--configuracion de recursos
insert into susc_perfiles_derechos (perfil, derecho) values (3, 29);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 30);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 31);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 32);
--pacientes
insert into susc_perfiles_derechos (perfil, derecho) values (3, 33);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 34);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 35);
--citas
insert into susc_perfiles_derechos (perfil, derecho) values (3, 37);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 38);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 39);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 40);
--tipos de horarios
insert into susc_perfiles_derechos (perfil, derecho) values (3, 41);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 42);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 43);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 44);


--Perfil Estudiante:
--pacientes
insert into susc_perfiles_derechos (perfil, derecho) values (5, 33);
--citas
insert into susc_perfiles_derechos (perfil, derecho) values (5, 37);


