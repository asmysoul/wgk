
create database digg default character set utf8 collate utf8_bin;

grant all privileges on digg.* to dev@'localhost' IDENTIFIED BY 'dev';

flush privileges;
