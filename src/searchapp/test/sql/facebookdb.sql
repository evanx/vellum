
drop all objects;

create table message_entry (
  message_entry_id integer auto_increment primary key,
  timestamp_ timestamp default now(),
  friend varchar not null,
  message varchar not null
);

insert into message_entry (friend, message) 
values ('Evan Summers', 'Finished that article so going to the beach');

insert into message_entry (friend, message) 
values ('Cameron', 'Summer is here');

