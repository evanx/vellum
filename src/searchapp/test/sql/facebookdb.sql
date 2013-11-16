
create table message_entry (
  message_entry_id integer auto_increment primary key,
  timestamp_ timestamp default now(),
  friend varchar not null,
  message varchar not null
);

insert into message_entry (friend, message) 
values ('Evan Summers', 'Article published');
