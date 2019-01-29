--// centros
-- Migration SQL that makes the change goes here.

-- secuencia datos
CREATE SEQUENCE gendat START 1000000;
CREATE SEQUENCE gencfg START 1000;

-- Parámetros generales del suscriptor, intentamos evitar los ficheros de propiedades
create table susc_parametros (
	id integer not null,
	codigo varchar(32),
	descripcion varchar(128) not null,
	valor varchar(256),
	constraint pk_parametros primary key(id),
	constraint uk_parametros_centro_codigo unique (codigo)	
	);

-- imágenes utilizadas en el sistema
create table susc_imagenes (
    id integer not null,
	datos bytea, 									-- Png de la imagen
	nombre varchar(64) not null,					-- nombre de la imagen
	centro integer not null, 						-- centro al que pertenece la imagen
	
    constraint pk_imagenes primary key(id)
    );

-- monedas
create table susc_monedas (
    id integer not null,
    
    nombre varchar(32) not null,					   	-- nombre de la moneda
    codigo varchar(3) not null,					    	-- codigo ISO 4217 de la moneda
    simbolo varchar(1),					   				-- símbolo de la moneda
    
    constraint pk_monedas primary key(id)
);
    
    -- zonas horarias
create table susc_horarios (
    id integer not null,
    
    nombre varchar(256),					    	-- nombre de la zona horaria
    
    constraint pk_horarios primary key(id)
);

-- centros de la aplicación
create table susc_centros (
	id integer not null,
	descripcion varchar(128) not null, 			-- Nombre del centro
	direccion text, 							-- json de direccion
	contactos text, 							-- json de contactos
	coordenadas varchar(64),					-- Coordenadas de posición para localizar en mapa
	imagen integer,   							-- Imagen
	esquema integer,							-- esquema de la instalación del centro
	orden integer not null,						-- para indicar orden en que se desee mostrar centros
	nombre varchar(32) not null,
	horario integer not null,         			-- franja horaria aplicada en el centro
	moneda integer not null,					-- tipo de moneda usada
	ip varchar(128),							-- direccion ip
	puerto integer not null, 					-- numero de puerto de la aplicación
	serie varchar(32) not null, 				-- numero de serie del centro
	constraint pk_centros primary key(id),
    constraint fk_centros_imagen foreign key (imagen) references susc_imagenes(id),
    constraint fk_centros_esquema foreign key (esquema) references susc_imagenes(id),
    constraint fk_centros_horario foreign key (horario) references susc_horarios(id),
    constraint fk_centros_moneda foreign key (moneda) references susc_monedas(id),
    constraint ck_centros_nombre unique (nombre),
    constraint ck_centros_serie unique (serie)
	);
	
    alter table susc_imagenes add constraint fk_imagenes_centro foreign key (centro) references susc_centros(id);

-- derechos de las autorizaciones
create table susc_derechos (
	id integer not null,
	codigo varchar(128),
	descripcion varchar(128) not null,
	constraint pk_derechos primary key(id),
	constraint uk_derechos_codigo unique (codigo)	
	);
	
-- perfiles de derechos de las autorizaciones
create table susc_perfiles (
	id integer not null,
	descripcion varchar(128) not null,
	constraint pk_perfiles primary key(id),
	constraint uk_perfiles_descripcion unique (descripcion)	
	);
	
-- relación perfiles-derechos
create table susc_perfiles_derechos (
    perfil integer not null,
    derecho integer not null,
    constraint pk_perfiles_derechos primary key(perfil,derecho),
    constraint fk_perfiles_derechos_perfil foreign key (perfil) references susc_perfiles(id),
    constraint fk_perfiles_derechos_derecho foreign key (derecho) references susc_derechos(id)		
    );
	
-- usuarios 
create table susc_usuarios (
    id integer not null,
    login varchar(32) not null,    							-- login
    email varchar(128) not null,    						-- email al que se envian las notificaciones
	pwd varchar(32) not null,       						-- hash md5 de la contraseña
    titulo varchar(16),   									-- Titulo que precede al nombre
    nombre varchar(32) not null,   							-- El nombre es obligatorio
    ape1 varchar(32) not null,     							-- y el primer apellido también
    ape2 varchar(32),              							-- pero el segundo apellido es opcional  
	perfil integer not null,   								-- perfil del usuario
    contactos text,  										-- json
	estado integer not null,        						-- 0=Autentificado correctamente -- 1=Nunca se ha autentificado -- 2=Ha solicitado nueva contraseña -- 3=contraseña expirada -- 4=reintentos superados
	cambio integer not null,        						-- fecha de actualización de la contraseña
	intentos integer not null,      						-- intentos fallidos de acceso
    filtro varchar(32) not null,  							-- filtro de búsqueda por referidor
    query varchar(32) not null,								-- query por defecto al entrar
    compresion varchar(32) not null,						-- tipo de compresion
    constraint pk_usuarios primary key(id),
    constraint fk_autorizaciones_perfil foreign key (perfil) references susc_perfiles(id),
	constraint uk_autorizaciones_login unique(login),
	constraint uk_autorizaciones_email unique(email)
    );
create index ix_usuarios_email on susc_usuarios(email);

-- centros en los que está activo el usuario
create table susc_usuarios_centros (
    usuario integer not null,
    centro integer not null,
    constraint pk_usuarios_centros primary key(usuario,centro),
    constraint fk_usuarios_centros_usuario foreign key (usuario) references susc_usuarios(id),
    constraint fk_usuarios_centros_centro foreign key (centro) references susc_centros(id)
    );

-- registro de acceso a la plataforma
create table susc_accesos (
    id integer not null,
    usuario integer not null, 
	cuando bigint not null,						-- fecha y hora del acceso

    constraint pk_accesos primary key(id),
    constraint fk_accesos_usuario foreign key (usuario) references susc_usuarios(id)
    );
    
-- modelos de informes
create table susc_informes_modelos (
    id integer not null,
    nombre varchar(64) not null, 					-- Nombre del modelo
    texto text, 									-- Texto del modelo
    constraint pk_informes_modelos primary key(id)
    ); 

-- plantillas para generación de informes en pdf
create table susc_informes_plantillas (
    id integer not null,
    nombre varchar(64) not null, 					-- Nombre de la plantilla
    datos text, 									-- Texto de la plantilla
    centro integer,									-- Centro al que pertenece la plantilla o null si es una plantilla global
    
    constraint pk_informes_plantillas primary key(id),
    constraint fk_informes_pantillas_centro foreign key (centro) references susc_centros(id)
    ); 

-- informes de estudios
create table susc_informes (
    id integer not null,
	fecha bigint not null,							-- fecha de creación del informe
    paciente_id varchar(128), 						-- Identificador del paciente
    paciente_nombre varchar(128),					-- Nombre del paciente
    estudio_id varchar(128),						-- Identificador del estudio 
    estudio_acceso varchar(128),					-- Numero de acceso 
    estudio_uid text,								-- Identificador único del estudio 
    modalidad varchar(4),							-- Modalidad 
    claves text, 									-- Palabras clave
    texto text, 									-- Texto del informe
    centro integer not null,						-- Centro al que pertenece el informe
    informa integer,								-- Médico que informa
    refiere integer,								-- Médico que refiere el estudio
    icd10cm varchar(16), 							-- Codigo IDC-10-CM de resultado de informe
	pdf bytea, 										-- informe en PDF
    estado integer not null default 0,				-- estado de aprobación del informe: 0 - solicitado, 1 - no aprobado, 2 - aprobado
    protegido integer not null default 0,			-- Proteger el informe solo para el referidor
    
    constraint pk_informes primary key(id),
    constraint fk_informes_centro foreign key (centro) references susc_centros(id),
    constraint fk_informes_informa foreign key (informa) references susc_usuarios(id)
    ); 
create index ix_informes_fecha on susc_informes(fecha);
create index ix_informes_paciente_id on susc_informes(paciente_id);
create index ix_informes_estudio_id on susc_informes(estudio_id);
create index ix_informes_modalidad on susc_informes(modalidad);
create index ix_informes_centro on susc_informes(centro);
create index ix_informes_claves on susc_informes(claves);
create index ix_informes_informa on susc_informes(informa);
create index ix_informes_icd10cm on susc_informes(icd10cm);
create index ix_informes_refiere on susc_informes(refiere);
create index ix_informes_estado on susc_informes(estado);

-- imagenes asociadas a los informes
create table susc_informes_imagenes (
    id integer not null,
	informe integer not null,						-- informe al que pertenece la imagen
    url text, 										-- url de la imagen
    icon text, 										-- icono de la imagen
    constraint pk_informes_imagenes primary key(id),
    constraint fk_informes_imagenes_informe foreign key (informe) references susc_informes(id)
    ); 

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

-- instalaciones 
create table susc_instalaciones (
    id integer not null,
    nombre varchar(64) not null,   					-- Nombre de la instalación
    aetitle varchar(64) not null, 					-- Aetitle
    modalidad character varying(2),
    tipo integer NOT NULL,
    constraint pk_instalaciones primary key(id),
	constraint uk_instalaciones_aetitle unique(aetitle),
	constraint uk_instalaciones_nombre unique(nombre)
    );
create index ix_instalaciones_modalidad on susc_instalaciones(modalidad);

-- tipos de estudios
create table susc_tipos_estudios (
    id integer not null,
    nombre varchar(64) not null,   					-- Nombre del tipo de estudio
    duracion integer not null,						-- Duración de la prueba en minutos
	
    constraint pk_tipos_estudios primary key(id),
	constraint uk_tipos_estudios_nombre unique(nombre)
    );

-- planificacion de estudos
create table susc_estudios (
    id integer not null,

    fecha bigint not null,							-- fecha/hora de la prueba

    uid varchar(64) not null,   					-- uuid del estudio

    paciente integer not null,						-- paciente al que se le realiza la prueba
    instalacion integer not null,					-- instalacion en el que se realiza la prueba
    tipo integer not null, 							-- tipo de estudio
    referidor integer,   							-- referidor de estudio
    duracion integer not null,						-- Duración de la prueba en minutos
    
    constraint pk_estudios primary key(id),
    constraint fk_estudios_paciente foreign key (paciente) references susc_pacientes(id) on delete cascade,
    constraint fk_estudios_instalacion foreign key (instalacion) references susc_instalaciones(id) on delete cascade,
    constraint fk_estudios_referidor foreign key (referidor) references susc_usuarios(id) on delete cascade,
    constraint fk_estudios_tipo foreign key (tipo) references susc_tipos_estudios(id) on delete cascade
    ); 
    
--//@UNDO
-- SQL to undo the change goes here.
