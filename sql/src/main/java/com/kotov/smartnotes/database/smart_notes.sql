create table category (
	id serial primary key not null,
	names varchar(100) not null unique
);
create table notes (
	id serial primary key not null,
	title varchar(100),
	description text,
	create_date timestamp NOT NULL,
    update_date timestamp NOT NULL,
	priority integer,
	password varchar(100),
	fix_note boolean default(false),
	category_id int not null references category(id)
);
create table images (	
	id serial primary key not null,
	image bytea NOT NULL,
	create_date timestamp NOT NULL,
	update_date timestamp NOT NULL,
	notes_images_id int not null references notes(id)
);
create table checkbox (	
	id serial primary key not null,
	title varchar(100),
    checking boolean check (checking IN (true, false)) default(false),
	notes_checkbox_id int not null references notes(id)
);
create table audio (	
	id serial primary key not null,
	directory text not null unique,
	notes_audio_id int not null references notes(id)
);
insert into category (names) values ('All notes');
--select * from category;

function getId (dates timestamp) returns integer
    language sql
        as $$
            select id from notes where create_date = dates;
        $$;
function getCategoryId (name varchar(100)) returns integer
    language sql
        as $$
            select id from category where names like name;
        $$;

--insert into notes (title, description, create_date, update_date, category_id)
--values('test', 'testing', '2020-10-17 17:57:32', '2020-10-17 17:57:32',
--	   getCategoryId('All notes'));
--select * from notes;
function getParameters (dates timestamp) returns notes
    language sql
        as $$
            select * from notes where create_date = dates;
        $$;
--select * from getParameters('2020-10-17 17:57:32');
create view getCategoryName as select names from category;	
--select * from getCategoryName;