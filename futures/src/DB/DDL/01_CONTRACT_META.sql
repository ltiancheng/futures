DROP TABLE IF EXISTS BASE_CONTRACT_META;
CREATE TABLE BASE_CONTRACT_META (
  SYMBOL varchar(5) NOT NULL,
  `DESC` varchar(8) NOT NULL,
  LEVERAGE float(13,3) NOT NULL,
  POINT_VAL int(10) NOT NULL,
  ATR float(13,3) NOT NULL,
  ATR_UPD_DATE timestamp default NULL,
  CTP_SYMBOL varchar(5) NOT NULL,
  PRIMARY KEY (SYMBOL) USING BTREE
)AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;