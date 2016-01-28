CREATE TABLE user(
  user_id BIGINT NOT NULL AUTO_INCREMENT,
  login_name VARCHAR(30) COLLATE utf8_spanish_ci NOT NULL UNIQUE,
  password VARCHAR(255),                                     /* BCrypt generates an implementation-dependent 448-bit hash value. */
  first_name VARCHAR(30) COLLATE utf8_spanish_ci NOT NULL,  /*You might need CHAR(56), CHAR(60), CHAR(76), BINARY(56) or BINARY(60) */
  last_name  VARCHAR(40) COLLATE utf8_spanish_ci NOT NULL,
  email VARCHAR(50),
  phone_number VARCHAR(13),
  role ENUM('USER', 'TEACHER', 'ADMIN'),
  enabled BOOLEAN,
  CONSTRAINT user_PK PRIMARY KEY (user_id));

create table authorities (
  login_name VARCHAR(30) COLLATE utf8_spanish_ci NOT NULL,
  authority VARCHAR(50) NOT NULL,
  FOREIGN KEY (login_name) references user (login_name),
  unique index authorities_idx_1 (login_name, authority));

CREATE TABLE course(
  course_id BIGINT NOT NULL AUTO_INCREMENT,
  course_name VARCHAR(30) COLLATE utf8_spanish_ci NOT NULL UNIQUE,
  course_level ENUM('INFANTIL', 'PRIMARIA', 'SECUNDARIA') NOT NULL,
  CONSTRAINT course_PK PRIMARY KEY(course_id));

CREATE TABLE school_year(
  school_year_id BIGINT NOT NULL AUTO_INCREMENT,
  school_year_name VARCHAR(255) COLLATE utf8_spanish_ci NOT NULL UNIQUE,
  visible BOOLEAN,
  CONSTRAINT school_year_PK PRIMARY KEY(school_year_id));

CREATE TABLE class_group(
  class_group_id BIGINT NOT NULL AUTO_INCREMENT,
  class_group_name VARCHAR(255) NOT NULL,
  course BIGINT,
  tutor BIGINT,
  school_year BIGINT,
  codigo_grupo VARCHAR(35),
  CONSTRAINT course_cg_FK FOREIGN KEY (course)  REFERENCES course(course_id),
  CONSTRAINT tutor_cg_FK FOREIGN KEY (tutor)  REFERENCES user(user_id),
  CONSTRAINT school_year_cg_FK FOREIGN KEY (school_year)  REFERENCES school_year(school_year_id),
  CONSTRAINT class_group_PK PRIMARY KEY(class_group_id));

CREATE TABLE class_hour_level(
  class_hour_level_id BIGINT NOT NULL AUTO_INCREMENT,
  course_level ENUM('INFANTIL', 'PRIMARIA', 'SECUNDARIA'),
  class_hour ENUM('FIRST_HOUR', 'SECOND_HOUR', 'THIRD_HOUR', 'FOURTH_HOUR', 'FIFTH_HOUR', 'SIXTH_HOUR'),
  class_start VARCHAR(255),
  class_end VARCHAR(255),
  CONSTRAINT class_hour_level_PK PRIMARY KEY(class_hour_level_id));

CREATE TABLE subject(
  subject_id BIGINT NOT NULL AUTO_INCREMENT,
  subject_name VARCHAR(255) NOT NULL,
  course BIGINT,
  codigo_materia_avaliable VARCHAR(35),
  CONSTRAINT subject_PK PRIMARY KEY(subject_id));

CREATE TABLE schedule(
  schedule_id BIGINT NOT NULL AUTO_INCREMENT,
  class_group BIGINT,
  subject BIGINT,
  class_hour BIGINT,
  week_day ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'),
  CONSTRAINT class_group_sc_FK FOREIGN KEY (class_group)  REFERENCES class_group(class_group_id),
  CONSTRAINT subject_sc_FK FOREIGN KEY (subject)  REFERENCES subject(subject_id),
  CONSTRAINT class_hour_sc_FK FOREIGN KEY (class_hour)  REFERENCES class_hour_level(class_hour_level_id),
  CONSTRAINT schedule_PK PRIMARY KEY(schedule_id));

CREATE TABLE global_grade_level(
  global_grade_level_id BIGINT NOT NULL AUTO_INCREMENT,
  grade VARCHAR(50),
  grade_name VARCHAR(50),
  xade_grade VARCHAR(50),
  course_level ENUM('INFANTIL', 'PRIMARIA', 'SECUNDARIA') NOT NULL,
  CONSTRAINT global_grade_level_PK PRIMARY KEY(global_grade_level_id));

CREATE TABLE student(
  student_id BIGINT NOT NULL AUTO_INCREMENT,
  dni VARCHAR(10) UNIQUE,
  first_name VARCHAR(35),
  last_name VARCHAR(65),
  guardian BIGINT,
  birth_date DATE,
  current_class_group BIGINT,
  course BIGINT,
  codigo_alumno VARCHAR(35),
  CONSTRAINT student_PK PRIMARY KEY (student_id));

CREATE TABLE global_grade(
  global_grade_id BIGINT NOT NULL AUTO_INCREMENT,
  school_year BIGINT,
  student BIGINT,
  subject BIGINT,
  evaluation ENUM('FIRST', 'SECOND', 'THIRD'),
  grade BIGINT,
  observation VARCHAR(255),
  CONSTRAINT school_year_gg_FK FOREIGN KEY (school_year)  REFERENCES school_year(school_year_id),
  CONSTRAINT student_gg_FK FOREIGN KEY (student)  REFERENCES student(student_id),
  CONSTRAINT subject_gg_FK FOREIGN KEY (subject)  REFERENCES subject(subject_id),
  CONSTRAINT grade_gg_FK FOREIGN KEY (grade)  REFERENCES global_grade_level(global_grade_level_id),
  CONSTRAINT global_grade_PK PRIMARY KEY(global_grade_id));

CREATE TABLE attendance(
  attendance_id BIGINT NOT NULL AUTO_INCREMENT,
  student BIGINT,
  subject BIGINT,
  fault_date DATE,
  justified BOOLEAN,
  class_group BIGINT,
  fault_type ENUM('ATTENDANCE', 'PUNCTUALITY'),
  CONSTRAINT student_att_FK FOREIGN KEY (student)  REFERENCES student(student_id),
  CONSTRAINT subject_att_FK FOREIGN KEY (subject)  REFERENCES subject(subject_id),
  CONSTRAINT class_group_att_FK FOREIGN KEY (class_group)  REFERENCES class_group(class_group_id),
  CONSTRAINT attendance_PK PRIMARY KEY(attendance_id));

CREATE TABLE task(
  task_id BIGINT NOT NULL AUTO_INCREMENT,
  task_name VARCHAR(255),
  task_type ENUM('EXAM', 'HOMEWORK', 'PROJECT'),
  task_date DATE,
  subject BIGINT,
  class_group BIGINT,
  evaluation ENUM('FIRST', 'SECOND', 'THIRD'),
  CONSTRAINT subject_tk_FK FOREIGN KEY (subject)  REFERENCES subject(subject_id),
  CONSTRAINT class_group_tk_FK FOREIGN KEY (class_group)  REFERENCES class_group(class_group_id),
  CONSTRAINT task_PK PRIMARY KEY(task_id));

CREATE TABLE grade(
  grade_id BIGINT NOT NULL AUTO_INCREMENT,
  task  BIGINT,
  student BIGINT,
  grade INT,
  observation VARCHAR(255),
  CONSTRAINT student_gr_FK FOREIGN KEY (student)  REFERENCES student(student_id),
  CONSTRAINT task_gr_FK FOREIGN KEY (task)  REFERENCES task(task_id),
  CONSTRAINT grade_PK PRIMARY KEY(grade_id));

CREATE TABLE group_subject(
  group_subject_id BIGINT NOT NULL AUTO_INCREMENT,
  class_group BIGINT NOT NULL,
  subject BIGINT NOT NULL,
  teacher BIGINT NOT NULL,
  CONSTRAINT class_group_gs_FK FOREIGN KEY (class_group)  REFERENCES class_group(class_group_id),
  CONSTRAINT subject_gs_FK FOREIGN KEY (subject)  REFERENCES subject(subject_id),
  CONSTRAINT teacher_gs_FK FOREIGN KEY (teacher)  REFERENCES user(user_id),
  CONSTRAINT group_subject_PK PRIMARY KEY(group_subject_id));

CREATE TABLE student_class_group(
  class_group BIGINT NOT NULL,
  student BIGINT NOT NULL,
  CONSTRAINT student_class_group_PK PRIMARY KEY(class_group, student),
  CONSTRAINT student_FK FOREIGN KEY (student)  REFERENCES student(student_id),
  CONSTRAINT class_group_FK FOREIGN KEY (class_group)  REFERENCES class_group(class_group_id));

CREATE TABLE message(
  message_id BIGINT NOT NULL AUTO_INCREMENT,
  addressee BIGINT,
  sender BIGINT,
  copy BIGINT,
  subject VARCHAR(255),
  message VARCHAR(255),
  message_date TIMESTAMP,
  viewed BOOLEAN,
  CONSTRAINT addressee_ms_FK FOREIGN KEY (addressee)  REFERENCES user(user_id),
  CONSTRAINT sender_ms_FK FOREIGN KEY (sender)  REFERENCES user(user_id),
  CONSTRAINT copy_ms_FK FOREIGN KEY (copy)  REFERENCES user(user_id),
  CONSTRAINT message_PK PRIMARY KEY (message_id));

/* Views */

/* teacher schedule view */
CREATE VIEW view_teacher_schedule as(
select schedule.schedule_id, group_subject.class_group, group_subject.group_subject_id, group_subject.subject, subject.subject_name,
group_subject.teacher, schedule.week_day, class_hour_level.class_start, class_group.class_group_name
FROM schedule
INNER JOIN class_hour_level
ON schedule.class_hour = class_hour_level.class_hour_level_id
INNER JOIN group_subject
ON schedule.class_group = group_subject.class_group AND schedule.subject = group_subject.subject
INNER JOIN subject
ON group_subject.subject = subject.subject_id
INNER JOIN class_group
ON group_subject.class_group = class_group.class_group_id
ORDER BY schedule.week_day, class_hour_level.class_start);

/* teacher task view */
CREATE VIEW view_teacher_task as(
select task.task_id, task.task_name, task.task_type, task.task_date, class_group.class_group_name, group_subject.teacher,
  subject.subject_name
FROM task
INNER JOIN class_group
  ON task.class_group = class_group.class_group_id
INNER JOIN subject
  ON task.subject = subject.subject_id
INNER JOIN group_subject
  ON group_subject.class_group = class_group.class_group_id AND group_subject.subject = subject.subject_id
ORDER BY task_date DESC);

/* Data */
/* Admin users */
/* admin, taboleiro*/
INSERT INTO user VALUES (null, "admin", '$2a$10$HitP8MWMytlv/5V32ap5me/tZKL7H/2jutDOkoCJ4gsOu542MuR/2', "admin","admin","admin@gmail.com","0","ADMIN",true);
/* newton, gravitacion*/
INSERT INTO user VALUES (null, "newton", '$2a$10$1VqPvYHdSyEfQgqpVHEWWupOHAZ2odunE/FKUaJ6P7xQ9QGb.uQ.S', "Isaac","Newton","jkt@gmail.com","666666555","ADMIN",true);

/* Teacher users */
/* hemingway, viejo*/
INSERT INTO user VALUES (null, "hemingway", '$2a$10$Yfxr2Cq41tYhVQtBunN4l.97VUWOhr8D5cmADT9AqAiL3D52wh0li',"Ernest","Hemingway","ernestoh@gmail.com","621266555","TEACHER",true);
INSERT INTO user VALUES (null, "steinbeck", '$2a$10$5mRH7GppgpEJqCGOHX7u4O41T6KuvKlH4BKheY8VSNi09rneqbqCa',"John","Steinbeck","jstbk@gmail.com","637432313","TEACHER",true);
INSERT INTO user VALUES (null, "houellebecq", '$2a$10$SIUVMntCSuLzzmp79wZwWO.ldLktbfqHiwP4f6wpGCDS2hPIHhVjW',"Michel","Houellebecq","mhllbc@gmail.com","667432411","TEACHER",true);
INSERT INTO user VALUES (null, "melville", '$2a$10$2RWdbUDlw1aTxOPm7leWt.GTv50l7wHutRa7tcIb.Dc0TXELmOQIm',"Herman","Melville","hmelville@gmail.com","627232212","TEACHER",true);
INSERT INTO user VALUES (null, "castelao", '$2a$10$NfB5Sk/PyNdFRWhvV3pTneb6K43NEKiwlPu5J6X.WBb/hVM64AGKy',"Alfonso Daniel","Rodríguez Castelao","castelao@google.com","62723212","TEACHER",true);
INSERT INTO user VALUES (null, "lodge", '$2a$10$VZF.H96fVlzAHiXFo3TXWOZEfBPyVLu0AC//5NUXsmw4/cPZin2PO',"David","Lodge","dlodge@cambridge.com","621233212","TEACHER",true);

/* Family users */
/* picasso, guernica*/
INSERT INTO user VALUES (null, "picasso", '$2a$10$Kj9SpDrRI69NJWjMBdSzOOMa.NPuHfNsapEisCjZOWlcE9X2S7MmS',"Pablo","Picasso","ppicaso@gmail.com","622232212","USER",true);
INSERT INTO user VALUES (null, "dali", '$2a$10$axulksvg8WY7YPumNB0st.QAfqxA6GeOfcwO.wOYpGn2p7YXdPFZi',"Salvador","Dalí","sdali@gmail.com","629942312","USER",true);
INSERT INTO user VALUES (null, "miro", '$2a$10$2RWdbUDlw1aTxOPm7leWt.GTv50l7wHutRa7tcIb.Dc0TXELmOQIm',"Joan","Miró","miro@gmail.com","639942384","USER",true);
INSERT INTO user VALUES (null, "mondrian", '$2a$10$2RWdbUDlw1aTxOPm7leWt.GTv50l7wHutRa7tcIb.Dc0TXELmOQIm',"Piet","Mondrian","mondrian@gmail.com","688842384","USER",true);
INSERT INTO user VALUES (null, "goya", '$2a$10$FzT2WKFiH.L9BIhilXByY.jbsMlHyYcKqW1jW3/Cob0lTXFCigKF6',"Francisco","Goya","fgoya@gmail.com","635849386","USER",true);
INSERT INTO user VALUES (null, "velazquez", '$2a$10$DRCTOaT1SgjFJK1ShDoLpekJWesu8FZKI6gCSLuMD6gftoUgNgsO6',"Diego","Velazquez","velazquez@museodelprado.com","615819286","USER",true);
INSERT INTO user VALUES (null, "davinci", '$2a$10$AKfCKaKraBEbu4T74fg7ru5Lbj/XXSwCfUqFtpPI96Ga9Mu53a8Nu',"Leonardo","Davinci","davinci@gmail.it","315819286","USER",true);

/* Courses */
INSERT INTO course VALUES (null, "1º EP", "Primaria");
INSERT INTO course VALUES (null, "2º EP", "Primaria");
INSERT INTO course VALUES (null, "3º EP", "Primaria");
INSERT INTO course VALUES (null, "4º EP", "Primaria");
INSERT INTO course VALUES (null, "5º EP", "Primaria");
INSERT INTO course VALUES (null, "6º EP", "Primaria");
INSERT INTO course VALUES (null, "1º ESO", "Secundaria");
INSERT INTO course VALUES (null, "2º ESO", "Secundaria");
INSERT INTO course VALUES (null, "3º ESO", "Secundaria");
INSERT INTO course VALUES (null, "4º ESO", "Secundaria");
INSERT INTO course VALUES (null, "1º Bachillerato", "Secundaria");
INSERT INTO course VALUES (null, "2º Bachillerato", "Secundaria");

/* SchoolYears */
INSERT INTO school_year VALUES (null, "2013-2014", false);
INSERT INTO school_year VALUES (null, "2014-2015", false);
INSERT INTO school_year VALUES (null, "2015-2016", true);

/* Subjects for 6 primaria*/
INSERT INTO subject VALUES(null, "Matemáticas", "6", "MA-215486");
INSERT INTO subject VALUES(null, "Lengua", "6", "MA-212416");
INSERT INTO subject VALUES(null, "Gallego", "6", "MA-212417");
INSERT INTO subject VALUES(null, "Inglés", "6", "MA-212418");
INSERT INTO subject VALUES(null, "Historia", "6", "MA-212419");
INSERT INTO subject VALUES(null, "Educación Física", "6", "MA-212420");
/* Subjects for 1º ESO */
INSERT INTO subject VALUES(null, "Matemáticas", "7", "MA-220101");
INSERT INTO subject VALUES(null, "Lengua", "7", "MA-220102");
INSERT INTO subject VALUES(null, "Gallego", "7", "MA-220103");
INSERT INTO subject VALUES(null, "Inglés", "7", "MA-220104");
INSERT INTO subject VALUES(null, "Historia", "7", "MA-220105");
INSERT INTO subject VALUES(null, "Francés", "7", "MA-220106");

/* ClassGroups for 6 primaria and 1 ESO */
INSERT INTO class_group VALUES (null, "6º EP A", "6", "4","3", "grp0023");
INSERT INTO class_group VALUES (null, "6º EP B", "6", "5","3", "grp0024");
INSERT INTO class_group VALUES (null, "1º ESO A Ciencias", "7", "3","3", "grs0123");
INSERT INTO class_group VALUES (null, "1º ESO A Letras", "7", "7","3", "grs0124");

INSERT INTO class_group VALUES (null, "6º EP A", "6", "4","1", "grp0003");
INSERT INTO class_group VALUES (null, "6º EP B", "6", "5","1", "grp0004");
INSERT INTO class_group VALUES (null, "1º ESO A Ciencias", "7", "3","1", "grs0103");
INSERT INTO class_group VALUES (null, "1º ESO A Letras", "7", "7","1", "grs0104");

/* Picasso children */
INSERT INTO student VALUES (null, "658676433Q", "Paloma", "Picasso", "9", "2011-12-11", "1", "6", "765800342");
INSERT INTO student_class_group VALUES ("1","1");
INSERT INTO student VALUES (null, "644636433Q", "Claude", "Picasso", "9", "2011-02-11", "2", "6", "765800345");
INSERT INTO student_class_group VALUES ("2","2");
INSERT INTO student VALUES (null, "644636434Q", "Maya", "Widmaier-Picasso", "9", "2011-02-11", "3", "7", "765800347");
INSERT INTO student_class_group VALUES ("3","3");

/* Miró children */
INSERT INTO student VALUES (null, "658676436Z", "Maria Dolors", "Miró", "11", "2011-05-10", "4", "7", "765800352");
INSERT INTO student_class_group VALUES ("4","4");

/* Goya children */
INSERT INTO student VALUES (null, "618676433Q", "Antonio", "Goya", "13", "2011-05-10", "1", "6", "765800353");
INSERT INTO student_class_group VALUES ("1","5");
INSERT INTO student VALUES (null, "612676433Q", "Eusebio", "Goya", "13", "2011-05-10", "2", "6", "765800355");
INSERT INTO student_class_group VALUES ("2","6");
INSERT INTO student VALUES (null, "628676433Q", "Vicente", "Goya", "13", "2011-05-10", "3", "7", "765803302");
INSERT INTO student_class_group VALUES ("3","7");
INSERT INTO student VALUES (null, "638676433P", "María del Pilar", "Goya", "13", "2011-05-10", "4", "7", "765803305");
INSERT INTO student_class_group VALUES ("4","8");
INSERT INTO student VALUES (null, "638676412R", "Francisco de Paula", "Goya", "13", "2011-05-10", "3", "7", "765803306");
INSERT INTO student_class_group VALUES ("3","9");

/* Mondrian children */
INSERT INTO student VALUES (null, "418676411Q", "Paula", "Mondrian", "12", "2011-01-29", "4", "7", "765803304");
INSERT INTO student_class_group VALUES ("4","10");

/* Velazquez children */
INSERT INTO student VALUES (null, "411626433Q", "Pilar", "Velázquez", "14", "2011-01-11", "1", "6", "765803306");
INSERT INTO student_class_group VALUES ("1","11");
INSERT INTO student VALUES (null, "111111121A", "Ana", "Velázquez", "14", "2011-11-26", "4", "7", "765803307");
INSERT INTO student_class_group VALUES ("4","12");

/* Dalí children */
INSERT INTO student VALUES (null, "9962643Q", "Diego", "Dalí", "10", "2012-01-11", "2", "6", "565813306");
INSERT INTO student_class_group VALUES ("2","13");
INSERT INTO student VALUES (null, "143211121A", "Pablo", "Dalí", "10", "2011-10-16", "3", "7", "365803307");
INSERT INTO student_class_group VALUES ("3","14");

/* Davinci children */
INSERT INTO student VALUES (null, "239962643D", "Luca", "Davinci", "10", "2010-01-10", "1", "6", "565813309");
INSERT INTO student_class_group VALUES ("1","15");

/* ClassHour level to PRIMARIA */
INSERT INTO class_hour_level VALUES(null, 'PRIMARIA', 'FIRST_HOUR', "10:00", "11:00");
INSERT INTO class_hour_level VALUES(null, 'PRIMARIA', 'SECOND_HOUR', "11:00", "12:00");
INSERT INTO class_hour_level VALUES(null, 'PRIMARIA', 'THIRD_HOUR', "12:30", "13:30");
INSERT INTO class_hour_level VALUES(null, 'PRIMARIA', 'FOURTH_HOUR', "14:00", "15:00");

/* ClassHour level to SECUNDARIA */
INSERT INTO class_hour_level VALUES(null, 'SECUNDARIA', 'FIRST_HOUR', "09:00", "10:00");
INSERT INTO class_hour_level VALUES(null, 'SECUNDARIA', 'SECOND_HOUR', "10:15", "11:15");
INSERT INTO class_hour_level VALUES(null, 'SECUNDARIA', 'THIRD_HOUR', "11:30", "12:30");
INSERT INTO class_hour_level VALUES(null, 'SECUNDARIA', 'FOURTH_HOUR', "13:30", "14:30");

/* Group subject 6 ep a*/
INSERT INTO group_subject VALUES(null, "1", "1", "3");
INSERT INTO group_subject VALUES(null, "1", "2", "4");
INSERT INTO group_subject VALUES(null, "1", "3", "7");
INSERT INTO group_subject VALUES(null, "1", "4", "8");
INSERT INTO group_subject VALUES(null, "1", "6", "6");
/* Group subject 6 ep b*/
INSERT INTO group_subject VALUES(null, "2", "1", "3");
INSERT INTO group_subject VALUES(null, "2", "2", "4");
INSERT INTO group_subject VALUES(null, "2", "3", "7");
INSERT INTO group_subject VALUES(null, "2", "4", "8");
INSERT INTO group_subject VALUES(null, "2", "6", "6");

/* Group subject 1 eso a*/
INSERT INTO group_subject VALUES(null, "3", "7", "3");
INSERT INTO group_subject VALUES(null, "3", "8", "5");
INSERT INTO group_subject VALUES(null, "3", "9", "7");
INSERT INTO group_subject VALUES(null, "3", "10", "6");
INSERT INTO group_subject VALUES(null, "3", "11", "8");
/* Group subject 1 eso b*/
INSERT INTO group_subject VALUES(null, "4", "7", "3");
INSERT INTO group_subject VALUES(null, "4", "8", "5");
INSERT INTO group_subject VALUES(null, "4", "9", "7");
INSERT INTO group_subject VALUES(null, "4", "10", "6");
INSERT INTO group_subject VALUES(null, "4", "11", "8");

/* SCHEDULE 6A */
INSERT INTO schedule VALUES(null, "1", "1", "1", "MONDAY");
INSERT INTO schedule VALUES(null, "1", "2", "2", "MONDAY");
INSERT INTO schedule VALUES(null, "1", "3", "3", "MONDAY");

INSERT INTO schedule VALUES(null, "1", "6", "1", "TUESDAY");
INSERT INTO schedule VALUES(null, "1", "6", "2", "TUESDAY");
INSERT INTO schedule VALUES(null, "1", "4", "3", "TUESDAY");

INSERT INTO schedule VALUES(null, "1", "1", "1", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "1", "2", "2", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "1", "3", "3", "WEDNESDAY");

INSERT INTO schedule VALUES(null, "1", "4", "1", "THURSDAY");
INSERT INTO schedule VALUES(null, "1", "1", "2", "THURSDAY");
INSERT INTO schedule VALUES(null, "1", "2", "3", "THURSDAY");

INSERT INTO schedule VALUES(null, "1", "4", "1", "FRIDAY");
INSERT INTO schedule VALUES(null, "1", "1", "2", "FRIDAY");
INSERT INTO schedule VALUES(null, "1", "2", "3", "FRIDAY");
/* SCHEDULE 6B */
INSERT INTO schedule VALUES(null, "2", "2", "1", "MONDAY");
INSERT INTO schedule VALUES(null, "2", "1", "2", "MONDAY");
INSERT INTO schedule VALUES(null, "2", "4", "3", "MONDAY");

INSERT INTO schedule VALUES(null, "2", "3", "1", "TUESDAY");
INSERT INTO schedule VALUES(null, "2", "4", "2", "TUESDAY");
INSERT INTO schedule VALUES(null, "2", "6", "3", "TUESDAY");

INSERT INTO schedule VALUES(null, "2", "2", "1", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "2", "1", "2", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "2", "4", "3", "WEDNESDAY");

INSERT INTO schedule VALUES(null, "2", "6", "1", "THURSDAY");
INSERT INTO schedule VALUES(null, "2", "2", "2", "THURSDAY");
INSERT INTO schedule VALUES(null, "2", "3", "3", "THURSDAY");

INSERT INTO schedule VALUES(null, "2", "6", "1", "FRIDAY");
INSERT INTO schedule VALUES(null, "2", "2", "2", "FRIDAY");
INSERT INTO schedule VALUES(null, "2", "3", "3", "FRIDAY");

/* Schedule for 1º ESO - Ciencias */
INSERT INTO schedule VALUES(null, "3", "8", "5", "MONDAY");
INSERT INTO schedule VALUES(null, "3", "11", "7", "MONDAY");
INSERT INTO schedule VALUES(null, "3", "10", "8", "MONDAY");

INSERT INTO schedule VALUES(null, "3", "8", "5", "TUESDAY");
INSERT INTO schedule VALUES(null, "3", "11", "7", "TUESDAY");
INSERT INTO schedule VALUES(null, "3", "10", "8", "TUESDAY");

INSERT INTO schedule VALUES(null, "3", "7", "5", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "3", "9", "6", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "3", "9", "8", "WEDNESDAY");

INSERT INTO schedule VALUES(null, "3", "7", "5", "THURSDAY");
INSERT INTO schedule VALUES(null, "3", "10", "6", "THURSDAY");
INSERT INTO schedule VALUES(null, "3", "9", "8", "THURSDAY");

INSERT INTO schedule VALUES(null, "3", "10", "5", "FRIDAY");
INSERT INTO schedule VALUES(null, "3", "9", "6", "FRIDAY");
INSERT INTO schedule VALUES(null, "3", "7", "8", "FRIDAY");

/* Schedule for 1º ESO - Ciencias */
INSERT INTO schedule VALUES(null, "4", "10", "5", "MONDAY");
INSERT INTO schedule VALUES(null, "4", "11", "6", "MONDAY");
INSERT INTO schedule VALUES(null, "4", "8", "8", "MONDAY");

INSERT INTO schedule VALUES(null, "4", "10", "5", "TUESDAY");
INSERT INTO schedule VALUES(null, "4", "11", "6", "TUESDAY");
INSERT INTO schedule VALUES(null, "4", "8", "8", "TUESDAY");

INSERT INTO schedule VALUES(null, "4", "9", "5", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "4", "11", "6", "WEDNESDAY");
INSERT INTO schedule VALUES(null, "4", "7", "8", "WEDNESDAY");

INSERT INTO schedule VALUES(null, "4", "9", "5", "THURSDAY");
INSERT INTO schedule VALUES(null, "4", "11", "7", "THURSDAY");
INSERT INTO schedule VALUES(null, "4", "7", "8", "THURSDAY");

INSERT INTO schedule VALUES(null, "4", "11", "5", "FRIDAY");
INSERT INTO schedule VALUES(null, "4", "11", "7", "FRIDAY");
INSERT INTO schedule VALUES(null, "4", "9", "8", "FRIDAY");


INSERT INTO global_grade_level VALUES(null, "CD", "Con dificultade", "C-92", 'INFANTIL');
INSERT INTO global_grade_level VALUES(null, "EP", "En proceso", "c-91", 'INFANTIL');
INSERT INTO global_grade_level VALUES(null, "SD", "Sen dificultade", "c-90", 'INFANTIL');

INSERT INTO global_grade_level VALUES(null, "1", "Insuficiente", "c-4", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "2", "Insuficiente", "c-5", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "3", "Insuficiente", "c-6", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "4", "Insuficiente", "c-7", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "5", "Suficiente", "c-8", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "6", "Bien", "c-9", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "7", "Notable", "c-10", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "8", "Notable", "c-11", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "9", "Sobresaliente", "c-12", 'PRIMARIA');
INSERT INTO global_grade_level VALUES(null, "10", "Sobresaliente", "c-14", 'PRIMARIA');

INSERT INTO global_grade_level VALUES(null, "NP", "Non Presentado", "c-23", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "1", "Insuficiente", "c-4", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "2", "Insuficiente", "c-5", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "3", "Insuficiente", "c-6", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "4", "Insuficiente", "c-7", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "5", "Suficiente", "c-8", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "6", "Bien", "c-9", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "7", "Notable", "c-10", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "8", "Notable", "c-11", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "9", "Sobresaliente", "c-12", 'SECUNDARIA');
INSERT INTO global_grade_level VALUES(null, "10", "Sobresaliente", "c-14", 'SECUNDARIA');

/*
INSERT INTO message VALUES(null, "3", "4", "3", "Help", "Hi, can you help me with an exam?", "2015-10-19 03:14:07", false);
INSERT INTO message VALUES(null, "3", "4", "4", "Help", "Hi, can you help me with an exam?", "2015-10-19 03:14:07", false);
INSERT INTO message VALUES(null, "3", "4", "3", "Dont worry", "Hi, Don't worry, I've found out...", "2015-10-20 04:14:07", false);
INSERT INTO message VALUES(null, "3", "4", "4", "Dont worry", "Hi, Don't worry, I've found out...", "2015-10-20 04:14:07", false);
*/

