-- ============================================================
-- 旅馆客房管理系统 退房事务 SQL 示例
-- 事务隔离级别建议：REPEATABLE READ
-- 以下 :xxx 为参数占位符（对应后端 CheckInService.checkOut）
-- ============================================================
START TRANSACTION;

-- 1. 锁定入住记录行（防止同一记录被并发退房）
SELECT * FROM check_in_record WHERE id = :recordId FOR UPDATE;

-- 2. 校验记录状态为 CHECKED_IN，否则 ROLLBACK 提示“该记录已退房”

-- 3. 锁定房间行（串行化同房间并发退房，避免房间状态误判）
SELECT * FROM room WHERE id = :roomId FOR UPDATE;

-- 4. 计算居住天数：住一晚算一天，不足一天按一天计算（最少 1 天）
--    总金额 = 天数 × daily_price_snapshot
SET @days  := GREATEST(DATEDIFF(:checkOutDate, :checkInDate), 1);
SET @total := @days * :dailyPriceSnapshot;

-- 5. 更新入住记录：标记已退房、记录退房日期/天数/金额，释放 active 字段
UPDATE check_in_record
SET status = 'CHECKED_OUT',
    check_out_date = :checkOutDate,
    days = @days,
    total_amount = @total,
    active_bed_id = NULL,
    active_guest_id = NULL
WHERE id = :recordId;

-- 6. 释放床位
UPDATE bed SET status = 'FREE' WHERE id = :bedId;

-- 7. 判断房间是否还有在住记录
SELECT COUNT(*) INTO @activeCnt FROM check_in_record
WHERE room_id = :roomId AND status = 'CHECKED_IN';

-- 8. 房间无人入住时，恢复为空房并清除性别限制
IF @activeCnt = 0 THEN
  UPDATE room
  SET status = 'AVAILABLE',
      current_gender = NULL,
      version = version + 1
  WHERE id = :roomId;
END IF;

-- 9. 生成账单（未支付）
INSERT INTO bill
(bill_no, check_in_record_id, guest_id, room_no, bed_no, days, daily_price, total_amount, pay_status)
VALUES
(:billNo, :recordId, :guestId, :roomNo, :bedNo, @days, :dailyPriceSnapshot, @total, 'UNPAID');

-- 10. 提交事务（任一步骤异常则 ROLLBACK）
COMMIT;
