-- ============================================================
-- 旅馆客房管理系统 初始化测试数据脚本
-- 执行方式：mysql -uroot -p < 02_init_data.sql （需先执行 01_schema.sql）
-- 注意：入住/账单日期使用 CURDATE() 相对日期，保证演示数据始终“新鲜”
-- ============================================================
USE hotel_room_management;

-- ------------------------------------------------------------
-- 1. 系统用户（密码均为 123456，BCrypt 加密）
-- ------------------------------------------------------------
INSERT INTO sys_user (id, username, password, real_name, role, status) VALUES
(1, 'admin',        '$2b$10$YNuXN5TM.Pp3lCSMWV8ORuq3XaFgVxRrdpKevrn8hajb7sSoSoMmS', '系统管理员', 'ADMIN', 1),
(2, 'receptionist', '$2b$10$YNuXN5TM.Pp3lCSMWV8ORuq3XaFgVxRrdpKevrn8hajb7sSoSoMmS', '前台小李',   'RECEPTIONIST', 1);

-- ------------------------------------------------------------
-- 2. 房间类型
-- ------------------------------------------------------------
INSERT INTO room_type (id, orientation, type_name, bed_count, daily_price, status) VALUES
(1, 'SOUTH', '朝南两人间', 2, 180.00, 1),
(2, 'SOUTH', '朝南三人间', 3, 240.00, 1),
(3, 'NORTH', '朝北三人间', 3, 210.00, 1),
(4, 'NORTH', '朝北四人间', 4, 280.00, 1);

-- ------------------------------------------------------------
-- 3. 房间（南101/南102：朝南两人间；南201/南202：朝南三人间；
--            北101/北102：朝北三人间；北201/北202：朝北四人间）
-- ------------------------------------------------------------
INSERT INTO room (id, room_no, room_type_id, orientation, floor, status, current_gender, version) VALUES
(1, '南101', 1, 'SOUTH', 1, 'OCCUPIED',  'MALE',   0),
(2, '南102', 1, 'SOUTH', 1, 'AVAILABLE', NULL,     0),
(3, '南201', 2, 'SOUTH', 2, 'AVAILABLE', NULL,     0),
(4, '南202', 2, 'SOUTH', 2, 'AVAILABLE', NULL,     0),
(5, '北101', 3, 'NORTH', 1, 'AVAILABLE', NULL,     0),
(6, '北102', 3, 'NORTH', 1, 'OCCUPIED',  'FEMALE', 0),
(7, '北201', 4, 'NORTH', 2, 'AVAILABLE', NULL,     0),
(8, '北202', 4, 'NORTH', 2, 'AVAILABLE', NULL,     0);

-- ------------------------------------------------------------
-- 4. 床位（按房间类型床位数生成）
-- ------------------------------------------------------------
INSERT INTO bed (id, room_id, bed_no, status) VALUES
-- 南101（两人间 1-2号床）
(1,  1, 1, 'OCCUPIED'),
(2,  1, 2, 'OCCUPIED'),
-- 南102（两人间 1-2号床）
(3,  2, 1, 'FREE'),
(4,  2, 2, 'FREE'),
-- 南201（三人间 1-3号床）
(5,  3, 1, 'FREE'),
(6,  3, 2, 'FREE'),
(7,  3, 3, 'FREE'),
-- 南202（三人间 1-3号床）
(8,  4, 1, 'FREE'),
(9,  4, 2, 'FREE'),
(10, 4, 3, 'FREE'),
-- 北101（三人间 1-3号床）
(11, 5, 1, 'FREE'),
(12, 5, 2, 'FREE'),
(13, 5, 3, 'FREE'),
-- 北102（三人间 1-3号床，1号床已住女性旅客）
(14, 6, 1, 'OCCUPIED'),
(15, 6, 2, 'FREE'),
(16, 6, 3, 'FREE'),
-- 北201（四人间 1-4号床）
(17, 7, 1, 'FREE'),
(18, 7, 2, 'FREE'),
(19, 7, 3, 'FREE'),
(20, 7, 4, 'FREE'),
-- 北202（四人间 1-4号床）
(21, 8, 1, 'FREE'),
(22, 8, 2, 'FREE'),
(23, 8, 3, 'FREE'),
(24, 8, 4, 'FREE');

-- ------------------------------------------------------------
-- 5. 旅客
-- ------------------------------------------------------------
INSERT INTO guest (id, name, id_card, gender, phone, address) VALUES
(1, '张三', '320311199001011234', 'MALE',   '13800000001', '徐州市云龙区解放路1号'),
(2, '李四', '320311199202022345', 'MALE',   '13800000002', '徐州市鼓楼区中山路2号'),
(3, '王五', '320311199303033456', 'FEMALE', '13800000003', '徐州市泉山区淮海路3号'),
(4, '赵六', '320311199404044567', 'FEMALE', '13800000004', '徐州市铜山区北京路4号');

-- ------------------------------------------------------------
-- 6. 入住记录
--   - 张三：2 天前入住 南101-1号床（在住）
--   - 李四：1 天前入住 南101-2号床（在住）
--   - 王五：3 天前入住 北102-1号床（在住）
--   - 赵六：6 天前入住 南102-1号床，3 天前退房（已退，生成账单）
-- ------------------------------------------------------------
INSERT INTO check_in_record
(id, guest_id, room_id, bed_id, check_in_date, check_out_date, status,
 daily_price_snapshot, days, total_amount, active_bed_id, active_guest_id) VALUES
(1, 1, 1, 1, CURDATE() - INTERVAL 2 DAY, NULL, 'CHECKED_IN',  180.00, NULL, NULL, 1,  1),
(2, 2, 1, 2, CURDATE() - INTERVAL 1 DAY, NULL, 'CHECKED_IN',  180.00, NULL, NULL, 2,  2),
(3, 3, 6, 14, CURDATE() - INTERVAL 3 DAY, NULL, 'CHECKED_IN',  210.00, NULL, NULL, 14, 3),
(4, 4, 2, 3, CURDATE() - INTERVAL 6 DAY, CURDATE() - INTERVAL 3 DAY, 'CHECKED_OUT', 180.00, 3, 540.00, NULL, NULL);

-- ------------------------------------------------------------
-- 7. 账单（赵六退房账单，已支付）
-- ------------------------------------------------------------
INSERT INTO bill
(id, bill_no, check_in_record_id, guest_id, room_no, bed_no, days, daily_price, total_amount, pay_status, pay_time) VALUES
(1, CONCAT('BILL', DATE_FORMAT(CURDATE(), '%Y%m%d'), '001'), 4, 4, '南102', 1, 3, 180.00, 540.00, 'PAID', CURDATE() - INTERVAL 3 DAY);

-- ------------------------------------------------------------
-- 8. 支付记录（对应账单1）
-- ------------------------------------------------------------
INSERT INTO payment_record (id, bill_id, pay_method, amount, pay_time) VALUES
(1, 1, 'WECHAT', 540.00, CURDATE() - INTERVAL 3 DAY);
