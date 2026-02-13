package com.rin.novel.core.constant;

import lombok.Getter;

/**
 * 数据库 常量
 *
 * @author zhim00
 */
public class DatabaseConsts {

    /**
     * 用户信息表
     */
    public static class UserInfoTable {

        private UserInfoTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_USERNAME = "username";

    }

    /**
     * 用户反馈表
     */
    public static class UserFeedBackTable {

        private UserFeedBackTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_USER_ID = "user_id";

    }

    /**
     * 用户书架表
     */
    public static class UserBookshelfTable {

        private UserBookshelfTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_USER_ID = "user_id";

        public static final String COLUMN_BOOK_ID = "book_id";

    }

    /**
     * 用户每日阅读表
     */
    public static class UserDailyReadingTable {

        private UserDailyReadingTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_USER_ID = "user_id";

        public static final String COLUMN_READING_DATE = "reading_date";

        public static final String COLUMN_IS_CHECKED_IN = "is_checked_in";

    }

    /**
     * 用户打卡记录表
     */
    public static class UserCheckinRecordTable {

        private UserCheckinRecordTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_USER_ID = "user_id";

        public static final String COLUMN_CHECKIN_DATE = "checkin_date";

        public static final String COLUMN_CHECKIN_MONTH = "checkin_month";

    }

    /**
     * 用户优惠券表
     */
    public static class UserCouponTable {

        private UserCouponTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_USER_ID = "user_id";

        public static final String COLUMN_ACTIVITY_MONTH = "activity_month";

        public static final String COLUMN_COUPON_TYPE = "coupon_type";

        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_EXPIRE_TIME = "expire_time";

    }

    /**
     * 作家信息表
     */
    public static class AuthorInfoTable {

        private AuthorInfoTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_USER_ID = "user_id";

    }

    /**
     * 小说类别表
     */
    public static class BookCategoryTable {

        private BookCategoryTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_WORK_DIRECTION = "work_direction";

    }

    /**
     * 小说表
     */
    public static class BookTable {

        private BookTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_CATEGORY_ID = "category_id";

        public static final String COLUMN_BOOK_NAME = "book_name";

        public static final String AUTHOR_ID = "author_id";

        public static final String COLUMN_VISIT_COUNT = "visit_count";

        public static final String COLUMN_WORD_COUNT = "word_count";

        public static final String COLUMN_LAST_CHAPTER_UPDATE_TIME = "last_chapter_update_time";

    }

    /**
     * 小说章节表
     */
    public static class BookChapterTable {

        private BookChapterTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_BOOK_ID = "book_id";

        public static final String COLUMN_CHAPTER_NUM = "chapter_num";

        public static final String COLUMN_LAST_CHAPTER_UPDATE_TIME = "last_chapter_update_time";

    }

    /**
     * 小说内容表
     */
    public static class BookContentTable {

        private BookContentTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_CHAPTER_ID = "chapter_id";

    }

    /**
     * 小说评论表
     */
    public static class BookCommentTable {

        private BookCommentTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_BOOK_ID = "book_id";

        public static final String COLUMN_USER_ID = "user_id";

    }

    /**
     * 新闻内容表
     */
    public static class NewsContentTable {

        private NewsContentTable() {
            throw new IllegalStateException(SystemConfigConsts.CONST_INSTANCE_EXCEPTION_MSG);
        }

        public static final String COLUMN_NEWS_ID = "news_id";

    }

    /**
     * 通用列枚举类
     */
    @Getter
    public enum CommonColumnEnum {

        ID("id"),
        SORT("sort"),
        CREATE_TIME("create_time"),
        UPDATE_TIME("update_time");

        private String name;

        CommonColumnEnum(String name) {
            this.name = name;
        }

    }


    /**
     * SQL语句枚举类
     */
    @Getter
    public enum SqlEnum {

        LIMIT_1("limit 1"),
        LIMIT_2("limit 2"),
        LIMIT_5("limit 5"),
        LIMIT_30("limit 30"),
        LIMIT_500("limit 500");

        private String sql;

        SqlEnum(String sql) {
            this.sql = sql;
        }

    }

}
