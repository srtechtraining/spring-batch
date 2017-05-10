-- Autogenerated: do not edit this file
DROP TABLE  PLAYERS IF EXISTS;
DROP TABLE  GAMES IF EXISTS;
DROP TABLE  PLAYER_SUMMARY IF EXISTS;
DROP TABLE  ERROR_LOG IF EXISTS;

CREATE TABLE PLAYERS (
	PLAYER_ID CHAR(8) NOT NULL PRIMARY KEY,  
	LAST_NAME VARCHAR(35) NOT NULL,
	FIRST_NAME VARCHAR(25) NOT NULL,
	POS VARCHAR(10) ,
	YEAR_OF_BIRTH BIGINT NOT NULL,
	YEAR_DRAFTED BIGINT NOT NULL
) ;

CREATE TABLE GAMES (
   PLAYER_ID CHAR(8) NOT NULL,
   YEAR_NO      BIGINT NOT NULL,
   TEAM      CHAR(3) NOT NULL,
   WEEK      BIGINT NOT NULL,
   OPPONENT  CHAR(3) ,
   COMPLETES BIGINT ,
   ATTEMPTS  BIGINT ,
   PASSING_YARDS BIGINT ,
   PASSING_TD    BIGINT ,
   INTERCEPTIONS BIGINT ,
   RUSHES BIGINT ,
   RUSH_YARDS BIGINT ,
   RECEPTIONS BIGINT ,
   RECEPTIONS_YARDS BIGINT ,
   TOTAL_TD BIGINT 
) ;

CREATE TABLE PLAYER_SUMMARY  (
		  ID CHAR(8) NOT NULL, 
		  YEAR_NO BIGINT NOT NULL,
		  COMPLETES BIGINT NOT NULL , 
		  ATTEMPTS BIGINT NOT NULL , 
		  PASSING_YARDS BIGINT NOT NULL , 
		  PASSING_TD BIGINT NOT NULL , 
		  INTERCEPTIONS BIGINT NOT NULL , 
		  RUSHES BIGINT NOT NULL , 
		  RUSH_YARDS BIGINT NOT NULL , 
		  RECEPTIONS BIGINT NOT NULL , 
		  RECEPTIONS_YARDS BIGINT NOT NULL , 
		  TOTAL_TD BIGINT NOT NULL
) ;

CREATE TABLE ERROR_LOG  (
		JOB_NAME CHAR(20) ,
		STEP_NAME CHAR(20) ,
		MESSAGE VARCHAR(300) NOT NULL
) ;