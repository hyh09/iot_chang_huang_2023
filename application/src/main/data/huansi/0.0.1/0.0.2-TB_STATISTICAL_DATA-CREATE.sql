create table hs_statistical_data
(
    id                   uuid not null
        constraint hs_statistical_data_pkey
            primary key,
    created_time         bigint,
    created_user         uuid,
    tenant_id            uuid,
    updated_time         bigint,
    updated_user         uuid,
    capacity_added_value varchar(255),
    capacity_first_time  bigint,
    capacity_first_value varchar(255),
    capacity_last_time   bigint,
    capacity_value       varchar(255),
    date                 date,
    electric_added_value varchar(255),
    electric_first_time  bigint,
    electric_first_value varchar(255),
    electric_last_time   bigint,
    electric_value       varchar(255),
    entity_id            uuid,
    gas_added_value      varchar(255),
    gas_first_time       bigint,
    gas_first_value      varchar(255),
    gas_last_time        bigint,
    gas_value            varchar(255),
    ts                   bigint,
    water_added_value    varchar(255),
    water_first_time     bigint,
    water_first_value    varchar(255),
    water_last_time      bigint,
    water_value          varchar(255)
);