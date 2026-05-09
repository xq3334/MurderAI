-- Database Migration Script
-- Generated: 2026-05-08
-- Description: Schema updates for ba_act_sign, dc_room, and dc_room_attendee tables

-- ============================================
-- 1. Add supplement_sign_name column to ba_act_sign table
-- ============================================
ALTER TABLE ba_act_sign 
ADD COLUMN supplement_sign_name VARCHAR(100) COMMENT '补签人名字';

-- ============================================
-- 2. Modify room_name column length in dc_room table
-- ============================================
ALTER TABLE dc_room 
MODIFY COLUMN room_name VARCHAR(1000);

-- ============================================
-- 3. Add multiple columns to dc_room_attendee table
-- ============================================
ALTER TABLE dc_room_attendee
ADD COLUMN ra_person_type TINYINT(1) DEFAULT NULL COMMENT '人员类型：1-内部人员，2-外部人员' AFTER ra_attendee_id,
ADD COLUMN ra_name VARCHAR(64) DEFAULT NULL COMMENT '参会姓名快照' AFTER ra_person_type,
ADD COLUMN ra_org VARCHAR(128) DEFAULT NULL COMMENT '参会单位快照' AFTER ra_name,
ADD COLUMN ra_phone VARCHAR(32) DEFAULT NULL COMMENT '参会手机号快照' AFTER ra_org,
ADD COLUMN ra_join_image VARCHAR(500) DEFAULT NULL COMMENT '入会照片' AFTER ra_phone;

-- ============================================
-- 4. Convert dc_room_attendee table to utf8mb4 character set
-- ============================================
ALTER TABLE dc_room_attendee
CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ============================================
-- 5. Verification query (optional - comment out if not needed)
-- ============================================
-- Uncomment the following query to verify the character set changes:
/*
SELECT TABLE_NAME, COLUMN_NAME, CHARACTER_SET_NAME, COLLATION_NAME
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'ba1.0'
AND TABLE_NAME = 'dc_room_attendee'
AND COLUMN_NAME IN ('ra_name', 'ra_org', 'ra_phone', 'ra_join_image');
*/
