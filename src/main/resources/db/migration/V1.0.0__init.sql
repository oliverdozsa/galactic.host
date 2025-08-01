create table poll_option (
    id bigint generated by default as identity,
    code integer not null,
    name text not null,
    poll_id bigint not null,
    primary key (id)
);

create table users (
    id bigint generated by default as identity,
    email varchar(255),
    primary key (id)
);

create table voting (
    id bigint generated by default as identity,
    asset_code varchar(20),
    ballot_type varchar(50) not null check (ballot_type in ('MULTI_POLL','MULTI_CHOICE')),
    created_at timestamp(6) with time zone not null,
    description text,
    encrypted_until timestamp(6) with time zone,
    encryption_key text,
    end_date timestamp(6) with time zone not null,
    internal_funding_account_secret text,
    is_on_test_network boolean,
    max_choices integer,
    max_voters integer not null,
    start_date timestamp(6) with time zone not null,
    title text,
    user_given_funding_account_secret text,
    visibility varchar(50) not null check (visibility in ('UNLISTED','PRIVATE')),
    created_by bigint not null,
    primary key (id)
);

create table voting_poll (
    id bigint generated by default as identity,
    description text,
    index integer not null,
    question text not null,
    voting_id bigint not null,
    primary key (id)
);

create table votings_partipicants (
    voting_id bigint not null,
    user_id bigint not null,
    primary key (voting_id, user_id)
);

alter table if exists users
   add constraint cstr_unique_email unique (email);

alter table if exists poll_option
   add constraint cstr_poll_option_voting_id
   foreign key (poll_id)
   references voting_poll;

alter table if exists voting
   add constraint cstr_voting_created_by_user
   foreign key (created_by)
   references users;

alter table if exists voting_poll
   add constraint cstr_voting_poll_voting_id
   foreign key (voting_id)
   references voting;

alter table if exists votings_partipicants
   add constraint cstr_voting_participants_user_id
   foreign key (user_id)
   references users;

alter table if exists votings_partipicants
   add constraint voting_participants_voting_id
   foreign key (voting_id)
   references voting;
