-- ============================================================
-- 旅馆客房管理系统 常用业务查询 SQL
-- ============================================================
USE hotel_room_management;

-- ------------------------------------------------------------
-- 1. 查询所有可用床位：床位空闲 + 房间可用或在住未满 + 房间无性别限制或允许该性别
--    规则：空房可任意性别；非空房仅允许同性别续住（含剩余床位）
--    参数 :gender 取值 MALE / FEMALE
-- ------------------------------------------------------------
SELECT b.id AS bed_id, b.bed_no, r.id AS room_id, r.room_no, r.orientation, r.floor,
       rt.type_name, rt.daily_price, r.current_gender
FROM bed b
JOIN room r       ON b.room_id = r.id
JOIN room_type rt ON r.room_type_id = rt.id
WHERE b.status = 'FREE'
  AND r.status IN ('AVAILABLE', 'OCCUPIED')
  AND (r.current_gender IS NULL OR r.current_gender = 'MALE')
ORDER BY CASE WHEN r.current_gender IS NULL THEN 0 ELSE 1 END, r.id, b.bed_no;

-- ------------------------------------------------------------
-- 2. 按朝向、房型、性别推荐可用床位：空房间优先，其次同性别房间优先
-- ------------------------------------------------------------
SELECT b.id AS bed_id, b.bed_no, r.id AS room_id, r.room_no, r.orientation, r.floor,
       rt.type_name, rt.daily_price, r.current_gender
FROM bed b
JOIN room r       ON b.room_id = r.id
JOIN room_type rt ON r.room_type_id = rt.id
WHERE b.status = 'FREE'
  AND r.status IN ('AVAILABLE', 'OCCUPIED')
  AND (r.current_gender IS NULL OR r.current_gender = 'MALE')   -- 性别参数
  AND (rt.orientation = 'SOUTH' OR 'SOUTH' IS NULL)              -- 朝向参数，可空
  AND (r.room_type_id = 1 OR 1 IS NULL)                          -- 房型参数，可空
ORDER BY CASE WHEN r.current_gender IS NULL THEN 0
              WHEN r.current_gender = 'MALE' THEN 1 ELSE 2 END,  -- 空房间优先，其次同性别
         r.id, b.bed_no
LIMIT 10;

-- ------------------------------------------------------------
-- 3. 查询当前在住旅客列表
-- ------------------------------------------------------------
SELECT c.id, c.check_in_date, c.daily_price_snapshot,
       g.id AS guest_id, g.name AS guest_name, g.id_card, g.gender, g.phone,
       r.room_no, r.orientation, b.bed_no, rt.type_name,
       DATEDIFF(CURDATE(), c.check_in_date) AS stay_days
FROM check_in_record c
JOIN guest g       ON g.id = c.guest_id
JOIN room r        ON r.id = c.room_id
JOIN bed b         ON b.id = c.bed_id
JOIN room_type rt  ON rt.id = r.room_type_id
WHERE c.status = 'CHECKED_IN'
ORDER BY c.check_in_date ASC;

-- ------------------------------------------------------------
-- 4. 查询某房间所有床位状态（参数 :roomId）
-- ------------------------------------------------------------
SELECT b.id AS bed_id, b.bed_no, b.status AS bed_status,
       r.room_no, r.status AS room_status, r.current_gender,
       g.name AS guest_name, c.check_in_date
FROM bed b
JOIN room r ON b.room_id = r.id
LEFT JOIN check_in_record c ON c.bed_id = b.id AND c.status = 'CHECKED_IN'
LEFT JOIN guest g ON g.id = c.guest_id
WHERE b.room_id = 1
ORDER BY b.bed_no;

-- ------------------------------------------------------------
-- 5. 退房结算查询：居住天数 = GREATEST(DATEDIFF(退房, 入住), 1)，总金额 = 天数 × 单价快照
-- ------------------------------------------------------------
SELECT c.id, g.name AS guest_name, r.room_no, b.bed_no,
       c.check_in_date, CURDATE() AS check_out_date,
       GREATEST(DATEDIFF(CURDATE(), c.check_in_date), 1) AS days,
       c.daily_price_snapshot,
       GREATEST(DATEDIFF(CURDATE(), c.check_in_date), 1) * c.daily_price_snapshot AS total_amount
FROM check_in_record c
JOIN guest g ON g.id = c.guest_id
JOIN room r  ON r.id = c.room_id
JOIN bed b   ON b.id = c.bed_id
WHERE c.id = 1 AND c.status = 'CHECKED_IN';

-- ------------------------------------------------------------
-- 6. 统计今日入住人数、今日退房人数、今日收入（今日生成账单金额）
-- ------------------------------------------------------------
SELECT
  (SELECT COUNT(*) FROM check_in_record WHERE status = 'CHECKED_IN'  AND check_in_date = CURDATE()) AS today_check_ins,
  (SELECT COUNT(*) FROM check_in_record WHERE status = 'CHECKED_OUT' AND check_out_date = CURDATE()) AS today_check_outs,
  (SELECT IFNULL(SUM(total_amount), 0) FROM bill WHERE DATE(create_time) = CURDATE())                AS today_income;

-- ------------------------------------------------------------
-- 7. 统计当前空房数、在住人数、入住率
-- ------------------------------------------------------------
SELECT
  COUNT(*) AS total_rooms,
  SUM(CASE WHEN status = 'AVAILABLE'  THEN 1 ELSE 0 END) AS available_rooms,
  SUM(CASE WHEN status = 'OCCUPIED'   THEN 1 ELSE 0 END) AS occupied_rooms,
  SUM(CASE WHEN status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maintenance_rooms,
  ROUND(SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS occupancy_rate,
  (SELECT COUNT(*) FROM check_in_record WHERE status = 'CHECKED_IN') AS in_stay_guests
FROM room;

-- ------------------------------------------------------------
-- 8. 按房型统计入住率和收入（收入按退房日期归属）
-- ------------------------------------------------------------
SELECT rt.id AS room_type_id, rt.type_name, rt.orientation,
       COUNT(DISTINCT r.id) AS total_rooms,
       SUM(CASE WHEN r.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_rooms,
       ROUND(SUM(CASE WHEN r.status = 'OCCUPIED' THEN 1 ELSE 0 END) * 100.0 / COUNT(DISTINCT r.id), 2) AS occupancy_rate,
       IFNULL(SUM(CASE WHEN c.status = 'CHECKED_OUT' THEN c.total_amount ELSE 0 END), 0) AS income
FROM room_type rt
LEFT JOIN room r ON r.room_type_id = rt.id
LEFT JOIN check_in_record c ON c.room_id = r.id AND c.status = 'CHECKED_OUT'
GROUP BY rt.id, rt.type_name, rt.orientation
ORDER BY rt.id;

-- ------------------------------------------------------------
-- 9. 按性别统计在住旅客人数
-- ------------------------------------------------------------
SELECT g.gender, COUNT(*) AS cnt
FROM guest g
JOIN check_in_record c ON c.guest_id = g.id AND c.status = 'CHECKED_IN'
GROUP BY g.gender;

-- ------------------------------------------------------------
-- 10. 近 7 日收入趋势（使用递归 CTE 补全无收入日期）
-- ------------------------------------------------------------
WITH RECURSIVE date_series AS (
  SELECT CURDATE() - INTERVAL 6 DAY AS dt
  UNION ALL
  SELECT dt + INTERVAL 1 DAY FROM date_series WHERE dt < CURDATE()
)
SELECT ds.dt AS stat_date, IFNULL(SUM(b.total_amount), 0) AS income
FROM date_series ds
LEFT JOIN bill b ON DATE(b.create_time) = ds.dt
GROUP BY ds.dt
ORDER BY ds.dt;
