-- ============================================================
-- 旅馆客房管理系统 视图 SQL（可选）
-- ============================================================
USE hotel_room_management;

-- ------------------------------------------------------------
-- 1. 房间床位状态视图：一房一行对应一张床位
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_room_bed_status AS
SELECT r.id                      AS room_id,
       r.room_no,
       r.orientation,
       r.floor,
       r.status                  AS room_status,
       r.current_gender,
       rt.type_name,
       rt.bed_count,
       b.id                      AS bed_id,
       b.bed_no,
       b.status                  AS bed_status,
       g.name                    AS guest_name,
       c.check_in_date
FROM room r
JOIN room_type rt ON rt.id = r.room_type_id
LEFT JOIN bed b ON b.room_id = r.id
LEFT JOIN check_in_record c ON c.bed_id = b.id AND c.status = 'CHECKED_IN'
LEFT JOIN guest g ON g.id = c.guest_id;

-- ------------------------------------------------------------
-- 2. 当前在住旅客视图
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_active_check_in AS
SELECT c.id                        AS record_id,
       c.check_in_date,
       c.daily_price_snapshot,
       g.id                        AS guest_id,
       g.name                      AS guest_name,
       g.id_card,
       g.gender,
       g.phone,
       g.address,
       r.room_no,
       r.orientation,
       b.bed_no,
       rt.type_name
FROM check_in_record c
JOIN guest g       ON g.id = c.guest_id
JOIN room r        ON r.id = c.room_id
JOIN bed b         ON b.id = c.bed_id
JOIN room_type rt  ON rt.id = r.room_type_id
WHERE c.status = 'CHECKED_IN';

-- ------------------------------------------------------------
-- 3. 账单明细视图
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_bill_detail AS
SELECT b.id                 AS bill_id,
       b.bill_no,
       b.check_in_record_id,
       b.guest_id,
       g.name               AS guest_name,
       g.id_card,
       b.room_no,
       b.bed_no,
       b.days,
       b.daily_price,
       b.total_amount,
       b.pay_status,
       b.pay_time,
       b.create_time,
       c.check_in_date,
       c.check_out_date
FROM bill b
JOIN guest g ON g.id = b.guest_id
LEFT JOIN check_in_record c ON c.id = b.check_in_record_id;

-- ------------------------------------------------------------
-- 4. 每日收入统计视图
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_daily_income AS
SELECT DATE(create_time)                 AS stat_date,
       COUNT(*)                          AS bill_count,
       IFNULL(SUM(total_amount), 0)      AS income,
       IFNULL(SUM(CASE WHEN pay_status = 'PAID' THEN total_amount ELSE 0 END), 0) AS paid_income
FROM bill
GROUP BY DATE(create_time);
