create table RankPlayer(username varchar(32) not null primary key,
                    pwd varchar(256) not null,
                    elo int,
                    no_match_played int,
                    no_match_won int,
                    check (username not like '*anon*'),
                    check (no_match_played >= 0),
                    check (no_match_won >=0));

create table GuestPlayer(displayname varchar(32) not null primary key);

create table SessionID(username varchar(32) not null,
                        session_id varchar(256) not null,
                        primary key (username, session_id));

create table MatchGame(player_1_username varchar(32) not null,
                        player_2_username varchar(32) not null,
                        match_id varchar(6) not null primary key,
                        result varchar(32));

create table LeaderBoard(username varchar(32) not null primary key,
                            usr_elo int,
                            usr_rank int,
                            usr_win_rate float,
                            check (usr_elo > 0),
                            check (usr_rank > 0),
                            check (usr_win_rate >=0),
                            foreign key (username) references RankPlayer(username));