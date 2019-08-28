INSERT INTO user (email, name, password) VALUES ('a@a.a', 'dpudpu', '1234');
INSERT INTO user (email, name, password) VALUES ('a@a.ab', 'dpudpud', '1234');
INSERT INTO user (email, name, password) VALUES ('a@a.ac', 'dpudpua', '1234');
INSERT INTO user (email, name, password) VALUES ('a@a.ad', 'dpudpux', '1234');
INSERT INTO user (email, name, password) VALUES ('a@a.ae', 'fdpudpu', '1234');
INSERT INTO user (email, name, password) VALUES ('a@a.af', 'edpudpu', '1234');
INSERT INTO user (email, name, password) VALUES ('a@a.ah', 'dpudpu', '1234');
INSERT INTO user (email, name, password) VALUES ('ab@a.a', 'chulsea', '1234');
INSERT INTO user (email, name, password) VALUES ('ac@a.a', 'olaf', '1234');

INSERT INTO post (contents, author_id, created_at, updated_at) VALUES ('contents', 1, '2019-01-31 23:59:59', '2019-08-12 23:59:59');
INSERT INTO post (contents, author_id, created_at) VALUES ('contents', 1, '2019-01-31 23:59:59');

INSERT INTO comment(contents, user_id, post_id, created_at, updated_at) VALUES ('comment1', 1, 1, '2019-08-21 08:31:25', '2019-08-21 12:31:25');
INSERT INTO comment(contents, user_id, post_id, created_at, updated_at) VALUES ('comment2', 1, 1, '2019-08-21 08:31:25', '2019-08-21 08:31:25');
INSERT INTO comment(contents, user_id, post_id, created_at, updated_at) VALUES ('comment3', 1, 1, '2019-08-19 08:31:25', '2019-08-19 08:31:25');
INSERT INTO comment(contents, user_id, post_id, created_at, updated_at) VALUES ('comment4', 1, 1, '2019-07-12 08:31:25', '2019-07-12 08:31:25');

INSERT INTO comment(contents, user_id, post_id, parent_id) VALUES ('comment4', 1, 1, 1);
INSERT INTO comment(contents, user_id, post_id, parent_id) VALUES ('comment5', 1, 1, 1);