
create table tweet_entry (
  tweet_entry_id integer auto_increment primary key,
  timestamp_ timestamp default now(),
  author varchar not null,
  content varchar not null
);

insert into tweet_entry (author, content) 
values ('Joe Soap', 'Dual Control article published by Evan Summers');