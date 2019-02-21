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
    tipo_instalacion integer NOT NULL,      -- Tipo de instalación 1 - equipo...
    tipo_horario integer NOT NULL,          -- Tipo de horario 0 - todos los dias iguales, 1-Horario comun de Lunes a Viernes, Shorario para sabdo  y domingo
    datos text,                             -- JSON con el horario de trabajo según el tipo de horario seleccionado y el tipo de instalacion

    constraint pk_tipos_horarios primary key(id),
    constraint uk_tipos_horarios_nombre unique(nombre)
    );

-- instalaciones:
create table susc_instalaciones (
    id integer not null,
    nombre varchar(64) not null,                  -- Nombre de la instalación
    aetitle varchar(64) not null,                 -- Aetitle
    modalidad character varying(2),
    tipo integer NOT NULL,                        -- Tipo de instalación 1 - equipo...
    tiempominimo integer NOT NULL,                -- Tiempo m inimo de demora de una consulta
    tipo_horario integer,                         -- referencia al tipo de horario de trabajo que se aplica

    constraint pk_instalaciones primary key(id),
    constraint uk_instalaciones_aetitle unique(aetitle),
    constraint uk_instalaciones_nombre unique(nombre),

    constraint fk_instalaciones_tipo_horario foreign key (ref_tipo_horario) references susc_tipos_horarios(id) on delete cascade
    );
create index ix_instalaciones_modalidad on susc_instalaciones(modalidad);

-- tipos de estudios
create table susc_tipos_estudios (
    id integer not null,
    nombre varchar(64) not null,   					-- Nombre del tipo de estudio
    duracion integer not null,						-- Duración de la prueba en minutos
	tipo integer NOT NULL,							-- Tipo de instalación 1 - equipo...
    constraint pk_tipos_estudios primary key(id),
	constraint uk_tipos_estudios_nombre unique(nombre)
    );

-- planificacion de estudos
create table susc_estudios (
    id integer not null,

    fecha bigint not null,							-- fecha/hora de inicio de la prueba
    fechafin bigint not null,						-- fecha/hora de fin de la prueba
    uid varchar(64) not null,   					-- uuid del estudio

    paciente integer not null,						-- paciente al que se le realiza la prueba
    instalacion integer not null,					-- instalacion en el que se realiza la prueba
    tipo integer not null, 							-- tipo de estudio
    referidor integer,   							-- referidor de estudio
    
    constraint pk_estudios primary key(id),
    constraint fk_estudios_paciente foreign key (paciente) references susc_pacientes(id) on delete cascade,
    constraint fk_estudios_instalacion foreign key (instalacion) references susc_instalaciones(id) on delete cascade,
    constraint fk_estudios_referidor foreign key (referidor) references susc_usuarios(id) on delete cascade,
    constraint fk_estudios_tipo foreign key (tipo) references susc_tipos_estudios(id) on delete cascade
    ); 

-- Derechos
insert into susc_derechos (id, codigo, descripcion) values (25, 'configuracion.tipos.estudios','Mantenimiento de catálogo de tipos de estudios');
insert into susc_derechos (id, codigo, descripcion) values (26, 'configuracion.instalaciones','Mantenimiento de catálogos de instalaciones');
insert into susc_derechos (id, codigo, descripcion) values (27, 'configuracion.pacientes','Mantenimiento a pacientes');
insert into susc_derechos (id, codigo, descripcion) values (28, 'administracion.citas','Mantenimiento de citas');
insert into susc_derechos (id, codigo, descripcion) values (29, 'configuracion.tipos.horarios','Configuracion de tipos de horarios');

--derechos por perfiles
insert into susc_perfiles_derechos (perfil, derecho) values (1, 25);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 26);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 27);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 28);
insert into susc_perfiles_derechos (perfil, derecho) values (1, 29);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 27);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 28);
insert into susc_perfiles_derechos (perfil, derecho) values (2, 29);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 27);
insert into susc_perfiles_derechos (perfil, derecho) values (3, 28);
insert into susc_perfiles_derechos (perfil, derecho) values (4, 27);
insert into susc_perfiles_derechos (perfil, derecho) values (5, 27);
