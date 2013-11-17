
drop all objects;

create table blog_entry (
  blog_entry_id integer auto_increment primary key,
  timestamp_ timestamp default now(),
  title varchar not null,
  author varchar not null,
  abstract varchar,
  content varchar not null
);

insert into blog_entry (title, author, abstract, content) 
values ('Dual Control', 'Evan Summers', '', 'Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual Evan Summers content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. Herewith the dual content. ');

insert into blog_entry (title, author, abstract, content) 
values ('Password Salt', 'Evan Summers', '', 'Herewith the salty content');