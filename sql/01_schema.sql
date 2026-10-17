-- ============================================================
-- 旅馆客房管理系统 数据库建表脚本
-- MySQL 8.0  字符集 utf8mb4  排序规则 utf8mb4_0900_ai_ci  引擎 InnoDB
-- 执行方式：mysql -uroot -p < 01_schema.sql
-- ============================================================
CREATE DATABASE IF NOT EXISTS hotel_room_management
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE hotel_room_management;

-- ------------------------------------------------------------
-- 1. 系统用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  username    VARCHAR(50)  NOT NULL COMMENT '登录用户名',
  password    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密存储)',
  real_name   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  role        VARCHAR(20)  NOT NULL DEFAULT 'RECEPTIONIST' COMMENT '角色：ADMIN-管理员 / RECEPTIONIST-前台',
  status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE = InnoDB COMMENT = '系统用户表';

-- ------------------------------------------------------------
-- 2. 房间类型表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS room_type;
CREATE TABLE room_type (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  orientation VARCHAR(10)   NOT NULL COMMENT '房间朝向：SOUTH-朝南 / NORTH-朝北',
  type_name   VARCHAR(50)   NOT NULL COMMENT '类型名称，如：朝南两人间',
  bed_count   INT           NOT NULL COMMENT '床位数',
  daily_price DECIMAL(10,2) NOT NULL COMMENT '每日住宿费(元)',
  status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_orientation (orientation)
) ENGINE = InnoDB COMMENT = '房间类型表';

-- ------------------------------------------------------------
-- 3. 房间表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS room;
CREATE TABLE room (
  id             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  room_no        VARCHAR(20) NOT NULL COMMENT '房间号，全局唯一，如：南101',
  room_type_id   BIGINT      NOT NULL COMMENT '房间类型ID',
  orientation    VARCHAR(10) NOT NULL COMMENT '房间朝向：SOUTH / NORTH',
  floor          INT         DEFAULT NULL COMMENT '楼层',
  status         VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '房间状态：AVAILABLE-空闲 / OCCUPIED-已入住 / MAINTENANCE-维修',
  current_gender VARCHAR(10) DEFAULT NULL COMMENT '当前房间入住旅客性别：MALE / FEMALE / NULL(空房无限制)',
  version        INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_room_no (room_no),
  KEY idx_room_type_id (room_type_id),
  KEY idx_status (status),
  KEY idx_orientation_status (orientation, status),
  CONSTRAINT fk_room_type FOREIGN KEY (room_type_id) REFERENCES room_type (id)
) ENGINE = InnoDB COMMENT = '房间表';

-- ------------------------------------------------------------
-- 4. 床位表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS bed;
CREATE TABLE bed (
  id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  room_id     BIGINT      NOT NULL COMMENT '所属房间ID',
  bed_no      INT         NOT NULL COMMENT '床号，房间内唯一，如 1号床',
  status      VARCHAR(20) NOT NULL DEFAULT 'FREE' COMMENT '床位状态：FREE-空闲 / OCCUPIED-已占用 / MAINTENANCE-维修',
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_room_bed (room_id, bed_no),
  KEY idx_room_id (room_id),
  KEY idx_status (status),
  CONSTRAINT fk_bed_room FOREIGN KEY (room_id) REFERENCES room (id)
) ENGINE = InnoDB COMMENT = '床位表';

-- ------------------------------------------------------------
-- 5. 旅客表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS guest;
CREATE TABLE guest (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  name        VARCHAR(50)  NOT NULL COMMENT '姓名',
  id_card     VARCHAR(18)  NOT NULL COMMENT '身份证号，全局唯一',
  gender      VARCHAR(10)  NOT NULL COMMENT '性别：MALE-男 / FEMALE-女',
  phone       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  address     VARCHAR(255) DEFAULT NULL COMMENT '住址',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_id_card (id_card),
  KEY idx_name (name),
  KEY idx_phone (phone)
) ENGINE = InnoDB COMMENT = '旅客表';

-- ------------------------------------------------------------
-- 6. 入住记录表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS check_in_record;
CREATE TABLE check_in_record (
  id                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  guest_id           BIGINT        NOT NULL COMMENT '旅客ID',
  room_id            BIGINT        NOT NULL COMMENT '房间ID',
  bed_id             BIGINT        NOT NULL COMMENT '床位ID',
  check_in_date      DATE          NOT NULL COMMENT '入住日期',
  check_out_date     DATE          DEFAULT NULL COMMENT '退房日期',
  status             VARCHAR(20)   NOT NULL DEFAULT 'CHECKED_IN' COMMENT '状态：CHECKED_IN-在住 / CHECKED_OUT-已退房',
  daily_price_snapshot DECIMAL(10,2) NOT NULL COMMENT '入住时房价快照(元/天)',
  days               INT           DEFAULT NULL COMMENT '居住天数(退房时计算)',
  total_amount       DECIMAL(10,2) DEFAULT NULL COMMENT '总金额(退房时计算)',
  active_bed_id      BIGINT        DEFAULT NULL COMMENT '在住时指向当前床位ID，退房后置NULL',
  active_guest_id    BIGINT        DEFAULT NULL COMMENT '在住时指向当前旅客ID，退房后置NULL',
  create_time        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_active_bed_id (active_bed_id),
  UNIQUE KEY uk_active_guest_id (active_guest_id),
  KEY idx_guest_id (guest_id),
  KEY idx_room_id (room_id),
  KEY idx_bed_id (bed_id),
  KEY idx_status (status),
  KEY idx_check_in_date (check_in_date),
  CONSTRAINT fk_record_guest FOREIGN KEY (guest_id) REFERENCES guest (id),
  CONSTRAINT fk_record_room  FOREIGN KEY (room_id)  REFERENCES room (id),
  CONSTRAINT fk_record_bed   FOREIGN KEY (bed_id)   REFERENCES bed (id)
) ENGINE = InnoDB COMMENT = '入住记录表';

-- ------------------------------------------------------------
-- 7. 账单表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS bill;
CREATE TABLE bill (
  id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  bill_no             VARCHAR(32)   NOT NULL COMMENT '账单号，全局唯一',
  check_in_record_id  BIGINT        NOT NULL COMMENT '入住记录ID',
  guest_id            BIGINT        NOT NULL COMMENT '旅客ID',
  room_no             VARCHAR(20)   NOT NULL COMMENT '房间号快照',
  bed_no              INT           NOT NULL COMMENT '床号快照',
  days                INT           NOT NULL COMMENT '居住天数',
  daily_price         DECIMAL(10,2) NOT NULL COMMENT '每日单价快照(元/天)',
  total_amount        DECIMAL(10,2) NOT NULL COMMENT '总金额(元)',
  pay_status          VARCHAR(20)   NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付 / PAID-已支付',
  pay_time            DATETIME      DEFAULT NULL COMMENT '支付时间',
  create_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_bill_no (bill_no),
  KEY idx_guest_id (guest_id),
  KEY idx_create_time (create_time),
  KEY idx_pay_status (pay_status),
  CONSTRAINT fk_bill_record FOREIGN KEY (check_in_record_id) REFERENCES check_in_record (id),
  CONSTRAINT fk_bill_guest  FOREIGN KEY (guest_id) REFERENCES guest (id)
) ENGINE = InnoDB COMMENT = '账单表';

-- ------------------------------------------------------------
-- 8. 操作日志表（可选）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id     BIGINT       DEFAULT NULL COMMENT '操作人ID',
  module      VARCHAR(50)  DEFAULT NULL COMMENT '模块，如：CHECK_IN',
  action      VARCHAR(100) DEFAULT NULL COMMENT '操作动作',
  detail      VARCHAR(500) DEFAULT NULL COMMENT '操作详情',
  ip          VARCHAR(50)  DEFAULT NULL COMMENT '操作IP',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_create_time (create_time)
) ENGINE = InnoDB COMMENT = '操作日志表';

-- ------------------------------------------------------------
-- 9. 支付记录表（可选）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS payment_record;
CREATE TABLE payment_record (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  bill_id     BIGINT        NOT NULL COMMENT '账单ID',
  pay_method  VARCHAR(20)   DEFAULT NULL COMMENT '支付方式：CASH-现金 / WECHAT-微信 / ALIPAY-支付宝',
  amount      DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  pay_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_bill_id (bill_id),
  CONSTRAINT fk_payment_bill FOREIGN KEY (bill_id) REFERENCES bill (id)
) ENGINE = InnoDB COMMENT = '支付记录表';
