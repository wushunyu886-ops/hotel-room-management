-- ============================================================
-- 旅馆客房管理系统 入住事务 SQL 示例
-- 事务隔离级别建议：REPEATABLE READ
-- 并发控制：SELECT ... FOR UPDATE 行锁 + 唯一索引兜底 + 乐观锁 version
-- 以下 :xxx 为参数占位符（对应后端 CheckInService.checkIn）
-- ============================================================
START TRANSACTION;

-- 1. 锁定床位行（防止并发把同一床位分配给多个旅客）
SELECT * FROM bed WHERE id = :bedId FOR UPDATE;

-- 2. 校验床位是否空闲
--    （若 b.status <> 'FREE' 则 ROLLBACK 并提示“该床位已被占用”）

-- 3. 锁定房间行（串行化同房间的入住/退房，配合乐观锁 version）
SELECT * FROM room WHERE id = :roomId FOR UPDATE;

-- 4. 校验房间是否可用、性别规则是否允许入住
--    空房(current_gender IS NULL)可入住任意性别；
--    非空房必须与 current_gender 相同，否则 ROLLBACK 提示“该房间已住男性/女性，不能混住”
SELECT status, current_gender FROM room WHERE id = :roomId;

-- 5. 校验旅客是否已在住（唯一索引 uk_active_guest_id 兜底）
SELECT COUNT(*) FROM check_in_record WHERE guest_id = :guestId AND status = 'CHECKED_IN';

-- 6. 校验房间未住满（在住床位 < 房型床位数）
SELECT (SELECT COUNT(*) FROM check_in_record c WHERE c.room_id = :roomId AND c.status = 'CHECKED_IN')
       < (SELECT bed_count FROM room_type WHERE id = :roomTypeId) AS has_bed;

-- 7. 插入入住记录（active_bed_id / active_guest_id 在在住期间非空）
INSERT INTO check_in_record
(guest_id, room_id, bed_id, check_in_date, status, daily_price_snapshot, active_bed_id, active_guest_id)
VALUES
(:guestId, :roomId, :bedId, :checkInDate, 'CHECKED_IN', :dailyPrice, :bedId, :guestId);
-- 若此处抛出 DuplicateKeyException（uk_active_bed_id / uk_active_guest_id），
-- 说明存在并发抢占，事务回滚并提示“该床位已被占用”或“该旅客已在住”

-- 8. 更新床位状态为已占用
UPDATE bed SET status = 'OCCUPIED' WHERE id = :bedId;

-- 9. 更新房间状态为已入住；空房首次入住时设置房间性别
UPDATE room
SET status = 'OCCUPIED',
    current_gender = IFNULL(current_gender, :gender),
    version = version + 1
WHERE id = :roomId;

-- 10. 提交事务（任一步骤异常则 ROLLBACK，保证原子性）
COMMIT;
