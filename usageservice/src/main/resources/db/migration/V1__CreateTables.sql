create table if not exists hourly_usage (
    hour_time timestamp primary key,
    community_produced double precision,
    community_used double precision,
    grid_used double precision
);

create table if not exists current_percentage (
    hour_time timestamp primary key,
    community_depleted double precision,
    grid_portion double precision
);