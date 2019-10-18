create table department
(
  id          serial       not null
    constraint department_pk
      primary key,
  depcode     varchar(20)  not null,
  depjob      varchar(100) not null,
  description varchar(255),
  constraint department_depcode_depjob_key
    unique (depcode, depjob)
);

alter table department
  owner to postgres;

create unique index department_id_uindex
  on department (id);

INSERT INTO public.department (depcode, depjob, description) VALUES ('Mail.ru', 'Data Analyst', 'work with big data');
INSERT INTO public.department (depcode, depjob, description) VALUES ('VK', 'PHP Developer', 'php, html, mysql');
INSERT INTO public.department (depcode, depjob, description) VALUES ('IIS-SOFT', 'Java Developer', 'java, spring, docker');
INSERT INTO public.department (depcode, depjob, description) VALUES ('FIX', 'Android Developer', 'Kotlin, java');
INSERT INTO public.department (depcode, depjob, description) VALUES ('bars-group', 'Python developer', 'Django, JS');
INSERT INTO public.department (depcode, depjob, description) VALUES ('Sofcombank', 'Python developer', 'Python');
INSERT INTO public.department (depcode, depjob, description) VALUES ('Sberbank', 'Java developer', 'Java, Spring');