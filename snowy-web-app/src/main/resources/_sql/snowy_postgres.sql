DROP TABLE IF EXISTS auth_third_user;

DROP TABLE IF EXISTS client_relation;

DROP TABLE IF EXISTS client_user;

DROP TABLE IF EXISTS dev_config;

DROP TABLE IF EXISTS dev_dict;

DROP TABLE IF EXISTS dev_email;

DROP TABLE IF EXISTS dev_file;

DROP TABLE IF EXISTS dev_job;

DROP TABLE IF EXISTS dev_log;

DROP TABLE IF EXISTS dev_message;

DROP TABLE IF EXISTS dev_relation;

DROP TABLE IF EXISTS dev_sms;

DROP TABLE IF EXISTS gen_basic;

DROP TABLE IF EXISTS gen_config;

DROP TABLE IF EXISTS sys_org;

DROP TABLE IF EXISTS sys_position;

DROP TABLE IF EXISTS sys_relation;

DROP TABLE IF EXISTS sys_resource;

DROP TABLE IF EXISTS sys_role;

DROP TABLE IF EXISTS sys_user;

/*==============================================================*/
/* Table: auth_third_user                                       */
/*==============================================================*/
create table auth_third_user (
   ID                   VARCHAR(20)          not null,
   THIRD_ID             VARCHAR(255)         null,
   USER_ID              VARCHAR(20)          null,
   AVATAR               TEXT                 null,
   NAME                 VARCHAR(255)         null,
   NICKNAME             VARCHAR(255)         null,
   GENDER               VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_auth_third_user primary key (ID)
);

comment on table auth_third_user is
'三方用户';

comment on column auth_third_user.THIRD_ID is
'三方用户id';

comment on column auth_third_user.USER_ID is
'系统用户id';

comment on column auth_third_user.AVATAR is
'头像';

comment on column auth_third_user.NAME is
'姓名';

comment on column auth_third_user.NICKNAME is
'昵称';

comment on column auth_third_user.GENDER is
'性别';

comment on column auth_third_user.CATEGORY is
'分类';

comment on column auth_third_user.EXT_JSON is
'扩展信息';

comment on column auth_third_user.DELETE_FLAG is
'删除标志';

comment on column auth_third_user.CREATE_TIME is
'创建时间';

comment on column auth_third_user.CREATE_USER is
'创建用户';

comment on column auth_third_user.UPDATE_TIME is
'修改时间';

comment on column auth_third_user.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: client_relation                                       */
/*==============================================================*/
create table client_relation (
   ID                   VARCHAR(20)          not null,
   OBJECT_ID            VARCHAR(255)         null,
   TARGET_ID            VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   EXT_JSON             TEXT                 null,
   constraint PK_client_relation primary key (ID)
);

comment on table client_relation is
'关系';

comment on column client_relation.OBJECT_ID is
'对象ID';

comment on column client_relation.TARGET_ID is
'目标ID';

comment on column client_relation.CATEGORY is
'分类';

comment on column client_relation.EXT_JSON is
'扩展信息';

/*==============================================================*/
/* Table: client_user                                           */
/*==============================================================*/
create table client_user (
   ID                   VARCHAR(20)          not null,
   AVATAR               TEXT                 null,
   SIGNATURE            TEXT                 null,
   ACCOUNT              VARCHAR(255)         null,
   PASSWORD             VARCHAR(255)         null,
   NAME                 VARCHAR(255)         null,
   NICKNAME             VARCHAR(255)         null,
   GENDER               VARCHAR(255)         null,
   AGE                  VARCHAR(255)         null,
   BIRTHDAY             VARCHAR(255)         null,
   NATION               VARCHAR(255)         null,
   NATIVE_PLACE         VARCHAR(255)         null,
   HOME_ADDRESS         TEXT                 null,
   MAILING_ADDRESS      TEXT                 null,
   ID_CARD_TYPE         VARCHAR(255)         null,
   ID_CARD_NUMBER       VARCHAR(255)         null,
   CULTURE_LEVEL        VARCHAR(255)         null,
   POLITICAL_OUTLOOK    VARCHAR(255)         null,
   COLLEGE              VARCHAR(255)         null,
   EDUCATION            VARCHAR(255)         null,
   EDU_LENGTH           VARCHAR(255)         null,
   DEGREE               VARCHAR(255)         null,
   PHONE                VARCHAR(255)         null,
   EMAIL                VARCHAR(255)         null,
   HOME_TEL             VARCHAR(255)         null,
   OFFICE_TEL           VARCHAR(255)         null,
   EMERGENCY_CONTACT    VARCHAR(255)         null,
   EMERGENCY_PHONE      VARCHAR(255)         null,
   EMERGENCY_ADDRESS    TEXT                 null,
   LAST_LOGIN_IP        VARCHAR(255)         null,
   LAST_LOGIN_ADDRESS   VARCHAR(255)         null,
   LAST_LOGIN_TIME      DATE                 null,
   LAST_LOGIN_DEVICE    TEXT                 null,
   LATEST_LOGIN_IP      VARCHAR(255)         null,
   LATEST_LOGIN_ADDRESS VARCHAR(255)         null,
   LATEST_LOGIN_TIME    DATE                 null,
   LATEST_LOGIN_DEVICE  TEXT                 null,
   USER_STATUS          VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_client_user primary key (ID)
);

comment on table client_user is
'C端用户';

comment on column client_user.AVATAR is
'头像';

comment on column client_user.SIGNATURE is
'签名';

comment on column client_user.ACCOUNT is
'账号';

comment on column client_user.PASSWORD is
'密码';

comment on column client_user.NAME is
'姓名';

comment on column client_user.NICKNAME is
'昵称';

comment on column client_user.GENDER is
'性别';

comment on column client_user.AGE is
'年龄';

comment on column client_user.BIRTHDAY is
'出生日期';

comment on column client_user.NATION is
'民族';

comment on column client_user.NATIVE_PLACE is
'籍贯';

comment on column client_user.HOME_ADDRESS is
'家庭住址';

comment on column client_user.MAILING_ADDRESS is
'通信地址';

comment on column client_user.ID_CARD_TYPE is
'证件类型';

comment on column client_user.ID_CARD_NUMBER is
'证件号码';

comment on column client_user.CULTURE_LEVEL is
'文化程度';

comment on column client_user.POLITICAL_OUTLOOK is
'政治面貌';

comment on column client_user.COLLEGE is
'毕业院校';

comment on column client_user.EDUCATION is
'学历';

comment on column client_user.EDU_LENGTH is
'学制';

comment on column client_user.DEGREE is
'学位';

comment on column client_user.PHONE is
'手机';

comment on column client_user.EMAIL is
'邮箱';

comment on column client_user.HOME_TEL is
'家庭电话';

comment on column client_user.OFFICE_TEL is
'办公电话';

comment on column client_user.EMERGENCY_CONTACT is
'紧急联系人';

comment on column client_user.EMERGENCY_PHONE is
'紧急联系人电话';

comment on column client_user.EMERGENCY_ADDRESS is
'紧急联系人地址';

comment on column client_user.LAST_LOGIN_IP is
'上次登录ip';

comment on column client_user.LAST_LOGIN_ADDRESS is
'上次登录地点';

comment on column client_user.LAST_LOGIN_TIME is
'上次登录时间';

comment on column client_user.LAST_LOGIN_DEVICE is
'上次登录设备';

comment on column client_user.LATEST_LOGIN_IP is
'最新登录ip';

comment on column client_user.LATEST_LOGIN_ADDRESS is
'最新登录地点';

comment on column client_user.LATEST_LOGIN_TIME is
'最新登录时间';

comment on column client_user.LATEST_LOGIN_DEVICE is
'最新登录设备';

comment on column client_user.USER_STATUS is
'用户状态';

comment on column client_user.SORT_CODE is
'排序码';

comment on column client_user.EXT_JSON is
'扩展信息';

comment on column client_user.DELETE_FLAG is
'删除标志';

comment on column client_user.CREATE_TIME is
'创建时间';

comment on column client_user.CREATE_USER is
'创建用户';

comment on column client_user.UPDATE_TIME is
'修改时间';

comment on column client_user.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_config                                            */
/*==============================================================*/
create table dev_config (
   ID                   VARCHAR(20)          not null,
   CONFIG_KEY           VARCHAR(255)         null,
   CONFIG_VALUE         TEXT                 null,
   CATEGORY             VARCHAR(255)         null,
   REMARK               VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_config primary key (ID)
);

comment on table dev_config is
'配置';

comment on column dev_config.CONFIG_KEY is
'配置键';

comment on column dev_config.CONFIG_VALUE is
'配置值';

comment on column dev_config.CATEGORY is
'分类';

comment on column dev_config.REMARK is
'备注';

comment on column dev_config.SORT_CODE is
'排序码';

comment on column dev_config.EXT_JSON is
'扩展信息';

comment on column dev_config.DELETE_FLAG is
'删除标志';

comment on column dev_config.CREATE_TIME is
'创建时间';

comment on column dev_config.CREATE_USER is
'创建用户';

comment on column dev_config.UPDATE_TIME is
'修改时间';

comment on column dev_config.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_dict                                              */
/*==============================================================*/
create table dev_dict (
   ID                   VARCHAR(20)          not null,
   PARENT_ID            VARCHAR(20)          null,
   DICT_LABEL           VARCHAR(255)         null,
   DICT_VALUE           VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_dict primary key (ID)
);

comment on table dev_dict is
'字典';

comment on column dev_dict.PARENT_ID is
'父id';

comment on column dev_dict.DICT_LABEL is
'字典文字';

comment on column dev_dict.DICT_VALUE is
'字典值';

comment on column dev_dict.CATEGORY is
'分类';

comment on column dev_dict.SORT_CODE is
'排序码';

comment on column dev_dict.EXT_JSON is
'扩展信息';

comment on column dev_dict.DELETE_FLAG is
'删除标志';

comment on column dev_dict.CREATE_TIME is
'创建时间';

comment on column dev_dict.CREATE_USER is
'创建用户';

comment on column dev_dict.UPDATE_TIME is
'修改时间';

comment on column dev_dict.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_email                                             */
/*==============================================================*/
create table dev_email (
   ID                   VARCHAR(20)          not null,
   ENGINE               VARCHAR(255)         null,
   SEND_ACCOUNT         VARCHAR(255)         null,
   SEND_USER            VARCHAR(255)         null,
   RECEIVE_ACCOUNTS     TEXT                 null,
   SUBJECT              TEXT                 null,
   CONTENT              TEXT                 null,
   TAG_NAME             VARCHAR(255)         null,
   TEMPLATE_NAME        TEXT                 null,
   TEMPLATE_PARAM       TEXT                 null,
   RECEIPT_INFO         TEXT                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_email primary key (ID)
);

comment on table dev_email is
'邮件';

comment on column dev_email.ENGINE is
'邮件引擎';

comment on column dev_email.SEND_ACCOUNT is
'发件人邮箱';

comment on column dev_email.SEND_USER is
'发件人昵称';

comment on column dev_email.RECEIVE_ACCOUNTS is
'接收人';

comment on column dev_email.SUBJECT is
'邮件主题';

comment on column dev_email.CONTENT is
'邮件正文';

comment on column dev_email.TAG_NAME is
'标签名';

comment on column dev_email.TEMPLATE_NAME is
'模板名';

comment on column dev_email.TEMPLATE_PARAM is
'发送参数';

comment on column dev_email.RECEIPT_INFO is
'回执信息';

comment on column dev_email.EXT_JSON is
'扩展信息';

comment on column dev_email.DELETE_FLAG is
'删除标志';

comment on column dev_email.CREATE_TIME is
'创建时间';

comment on column dev_email.CREATE_USER is
'创建用户';

comment on column dev_email.UPDATE_TIME is
'修改时间';

comment on column dev_email.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_file                                              */
/*==============================================================*/
create table dev_file (
   ID                   VARCHAR(20)          not null,
   ENGINE               VARCHAR(255)         null,
   BUCKET               VARCHAR(255)         null,
   NAME                 TEXT                 null,
   SUFFIX               VARCHAR(255)         null,
   SIZE_KB              INT8                 null,
   SIZE_INFO            VARCHAR(255)         null,
   OBJ_NAME             TEXT                 null,
   STORAGE_PATH         TEXT                 null,
   DOWNLOAD_PATH        TEXT                 null,
   THUMBNAIL            TEXT                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_file primary key (ID)
);

comment on table dev_file is
'文件';

comment on column dev_file.ENGINE is
'存储引擎';

comment on column dev_file.BUCKET is
'存储桶';

comment on column dev_file.NAME is
'文件名称';

comment on column dev_file.SUFFIX is
'文件后缀';

comment on column dev_file.SIZE_KB is
'文件大小kb';

comment on column dev_file.SIZE_INFO is
'文件大小（格式化后）';

comment on column dev_file.OBJ_NAME is
'文件的对象名（唯一名称）';

comment on column dev_file.STORAGE_PATH is
'文件存储路径';

comment on column dev_file.DOWNLOAD_PATH is
'文件下载路径';

comment on column dev_file.THUMBNAIL is
'图片缩略图';

comment on column dev_file.EXT_JSON is
'扩展信息';

comment on column dev_file.DELETE_FLAG is
'删除标志';

comment on column dev_file.CREATE_TIME is
'创建时间';

comment on column dev_file.CREATE_USER is
'创建用户';

comment on column dev_file.UPDATE_TIME is
'修改时间';

comment on column dev_file.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_job                                               */
/*==============================================================*/
create table dev_job (
   ID                   VARCHAR(20)          not null,
   NAME                 VARCHAR(255)         null,
   CODE                 VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   ACTION_CLASS         VARCHAR(255)         null,
   CRON_EXPRESSION      VARCHAR(255)         null,
   JOB_STATUS           VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_job primary key (ID)
);

comment on table dev_job is
'定时任务';

comment on column dev_job.NAME is
'名称';

comment on column dev_job.CODE is
'编码';

comment on column dev_job.CATEGORY is
'分类';

comment on column dev_job.ACTION_CLASS is
'任务类名';

comment on column dev_job.CRON_EXPRESSION is
'cron表达式';

comment on column dev_job.JOB_STATUS is
'任务状态';

comment on column dev_job.SORT_CODE is
'排序码';

comment on column dev_job.EXT_JSON is
'扩展信息';

comment on column dev_job.DELETE_FLAG is
'删除标志';

comment on column dev_job.CREATE_TIME is
'创建时间';

comment on column dev_job.CREATE_USER is
'创建用户';

comment on column dev_job.UPDATE_TIME is
'修改时间';

comment on column dev_job.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_log                                               */
/*==============================================================*/
create table dev_log (
   ID                   VARCHAR(20)          not null,
   CATEGORY             VARCHAR(255)         null,
   NAME                 VARCHAR(255)         null,
   EXE_STATUS           VARCHAR(255)         null,
   EXE_MESSAGE          TEXT                 null,
   OP_IP                VARCHAR(255)         null,
   OP_ADDRESS           VARCHAR(255)         null,
   OP_BROWSER           VARCHAR(255)         null,
   OP_OS                VARCHAR(255)         null,
   CLASS_NAME           VARCHAR(255)         null,
   METHOD_NAME          VARCHAR(255)         null,
   REQ_METHOD           VARCHAR(255)         null,
   REQ_URL              TEXT                 null,
   PARAM_JSON           TEXT                 null,
   RESULT_JSON          TEXT                 null,
   OP_TIME              DATE                 null,
   OP_USER              VARCHAR(255)         null,
   SIGN_DATA            TEXT                 null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_log primary key (ID)
);

comment on table dev_log is
'日志';

comment on column dev_log.ID is
'主键';

comment on column dev_log.CATEGORY is
'日志分类';

comment on column dev_log.NAME is
'日志名称';

comment on column dev_log.EXE_STATUS is
'执行状态';

comment on column dev_log.EXE_MESSAGE is
'具体消息';

comment on column dev_log.OP_IP is
'操作ip';

comment on column dev_log.OP_ADDRESS is
'操作地址';

comment on column dev_log.OP_BROWSER is
'操作浏览器';

comment on column dev_log.OP_OS is
'操作系统';

comment on column dev_log.CLASS_NAME is
'类名称';

comment on column dev_log.METHOD_NAME is
'方法名称';

comment on column dev_log.REQ_METHOD is
'请求方式';

comment on column dev_log.REQ_URL is
'请求地址';

comment on column dev_log.PARAM_JSON is
'请求参数';

comment on column dev_log.RESULT_JSON is
'返回结果';

comment on column dev_log.OP_TIME is
'操作时间';

comment on column dev_log.OP_USER is
'操作人姓名';

comment on column dev_log.SIGN_DATA is
'签名数据';

comment on column dev_log.CREATE_TIME is
'创建时间';

comment on column dev_log.CREATE_USER is
'创建用户';

comment on column dev_log.UPDATE_TIME is
'修改时间';

comment on column dev_log.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_message                                           */
/*==============================================================*/
create table dev_message (
   ID                   VARCHAR(20)          not null,
   CATEGORY             VARCHAR(255)         null,
   SUBJECT              TEXT                 null,
   CONTENT              TEXT                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_message primary key (ID)
);

comment on table dev_message is
'站内信';

comment on column dev_message.CATEGORY is
'分类';

comment on column dev_message.SUBJECT is
'主题';

comment on column dev_message.CONTENT is
'正文';

comment on column dev_message.EXT_JSON is
'扩展信息';

comment on column dev_message.DELETE_FLAG is
'删除标志';

comment on column dev_message.CREATE_TIME is
'创建时间';

comment on column dev_message.CREATE_USER is
'创建用户';

comment on column dev_message.UPDATE_TIME is
'修改时间';

comment on column dev_message.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: dev_relation                                          */
/*==============================================================*/
create table dev_relation (
   ID                   VARCHAR(20)          not null,
   OBJECT_ID            VARCHAR(255)         null,
   TARGET_ID            VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   EXT_JSON             TEXT                 null,
   constraint PK_dev_relation primary key (ID)
);

comment on table dev_relation is
'关系';

comment on column dev_relation.OBJECT_ID is
'对象ID';

comment on column dev_relation.TARGET_ID is
'目标ID';

comment on column dev_relation.CATEGORY is
'分类';

comment on column dev_relation.EXT_JSON is
'扩展信息';

/*==============================================================*/
/* Table: dev_sms                                               */
/*==============================================================*/
create table dev_sms (
   ID                   VARCHAR(20)          not null,
   ENGINE               VARCHAR(255)         null,
   PHONE_NUMBERS        TEXT                 null,
   SIGN_NAME            TEXT                 null,
   TEMPLATE_CODE        TEXT                 null,
   TEMPLATE_PARAM       TEXT                 null,
   RECEIPT_INFO         TEXT                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_dev_sms primary key (ID)
);

comment on table dev_sms is
'短信';

comment on column dev_sms.ENGINE is
'短信引擎';

comment on column dev_sms.PHONE_NUMBERS is
'手机号';

comment on column dev_sms.SIGN_NAME is
'短信签名';

comment on column dev_sms.TEMPLATE_CODE is
'模板编码';

comment on column dev_sms.TEMPLATE_PARAM is
'发送参数';

comment on column dev_sms.RECEIPT_INFO is
'回执信息';

comment on column dev_sms.EXT_JSON is
'扩展信息';

comment on column dev_sms.DELETE_FLAG is
'删除标志';

comment on column dev_sms.CREATE_TIME is
'创建时间';

comment on column dev_sms.CREATE_USER is
'创建用户';

comment on column dev_sms.UPDATE_TIME is
'修改时间';

comment on column dev_sms.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: gen_basic                                             */
/*==============================================================*/
create table gen_basic (
   ID                   VARCHAR(20)          not null,
   DB_TABLE             VARCHAR(255)         null,
   DB_TABLE_KEY         VARCHAR(255)         null,
   TABLE_PREFIX         VARCHAR(255)         null,
   GENERATE_TYPE        VARCHAR(255)         null,
   MODULE               VARCHAR(255)         null,
   MENU_PID             VARCHAR(255)         null,
   FUNCTION_NAME        VARCHAR(255)         null,
   BUS_NAME             VARCHAR(255)         null,
   CLASS_NAME           VARCHAR(255)         null,
   FORM_LAYOUT          VARCHAR(255)         null,
   GRID_WHETHER         VARCHAR(255)         null,
   PACKAGE_NAME         VARCHAR(255)         null,
   AUTHOR_NAME          VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_gen_basic primary key (ID)
);

comment on table gen_basic is
'生成基础';

comment on column gen_basic.ID is
'主键';

comment on column gen_basic.DB_TABLE is
'主表';

comment on column gen_basic.DB_TABLE_KEY is
'主表主键';

comment on column gen_basic.TABLE_PREFIX is
'移除表前缀';

comment on column gen_basic.GENERATE_TYPE is
'生成方式';

comment on column gen_basic.MODULE is
'所属模块';

comment on column gen_basic.MENU_PID is
'上级目录';

comment on column gen_basic.FUNCTION_NAME is
'功能名';

comment on column gen_basic.BUS_NAME is
'业务名';

comment on column gen_basic.CLASS_NAME is
'类名';

comment on column gen_basic.FORM_LAYOUT is
'表单布局';

comment on column gen_basic.GRID_WHETHER is
'使用栅格';

comment on column gen_basic.PACKAGE_NAME is
'包名';

comment on column gen_basic.AUTHOR_NAME is
'作者';

comment on column gen_basic.SORT_CODE is
'排序';

comment on column gen_basic.DELETE_FLAG is
'删除标志';

comment on column gen_basic.CREATE_TIME is
'创建时间';

comment on column gen_basic.CREATE_USER is
'创建用户';

comment on column gen_basic.UPDATE_TIME is
'修改时间';

comment on column gen_basic.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: gen_config                                            */
/*==============================================================*/
create table gen_config (
   ID                   VARCHAR(20)          not null,
   BASIC_ID             VARCHAR(20)          null,
   IS_TABLE_KEY         VARCHAR(255)         null,
   FIELD_NAME           VARCHAR(255)         null,
   FIELD_REMARK         VARCHAR(255)         null,
   FIELD_TYPE           VARCHAR(255)         null,
   FIELD_JAVA_TYPE      VARCHAR(255)         null,
   EFFECT_TYPE          VARCHAR(255)         null,
   DICT_TYPE_CODE       VARCHAR(255)         null,
   WHETHER_TABLE        VARCHAR(255)         null,
   WHETHER_RETRACT      VARCHAR(255)         null,
   WHETHER_ADD_UPDATE   VARCHAR(255)         null,
   WHETHER_REQUIRED     VARCHAR(255)         null,
   QUERY_WHETHER        VARCHAR(255)         null,
   QUERY_TYPE           VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   DELETE_FLAG          VARCHAR(20)          null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_gen_config primary key (ID)
);

comment on table gen_config is
'生成配置';

comment on column gen_config.ID is
'主键';

comment on column gen_config.BASIC_ID is
'基础ID';

comment on column gen_config.IS_TABLE_KEY is
'是否主键';

comment on column gen_config.FIELD_NAME is
'字段';

comment on column gen_config.FIELD_REMARK is
'名称';

comment on column gen_config.FIELD_TYPE is
'类型';

comment on column gen_config.FIELD_JAVA_TYPE is
'实体类型';

comment on column gen_config.EFFECT_TYPE is
'作用类型';

comment on column gen_config.DICT_TYPE_CODE is
'字典';

comment on column gen_config.WHETHER_TABLE is
'列表显示';

comment on column gen_config.WHETHER_RETRACT is
'列省略';

comment on column gen_config.WHETHER_ADD_UPDATE is
'是否增改';

comment on column gen_config.WHETHER_REQUIRED is
'必填';

comment on column gen_config.QUERY_WHETHER is
'查询';

comment on column gen_config.QUERY_TYPE is
'查询方式';

comment on column gen_config.SORT_CODE is
'排序';

comment on column gen_config.DELETE_FLAG is
'删除标志';

comment on column gen_config.CREATE_TIME is
'创建时间';

comment on column gen_config.CREATE_USER is
'创建用户';

comment on column gen_config.UPDATE_TIME is
'修改时间';

comment on column gen_config.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: sys_org                                               */
/*==============================================================*/
create table sys_org (
   ID                   VARCHAR(20)          not null,
   PARENT_ID            VARCHAR(20)          null,
   DIRECTOR_ID          VARCHAR(20)          null,
   NAME                 VARCHAR(255)         null,
   CODE                 VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_sys_org primary key (ID)
);

comment on table sys_org is
'组织';

comment on column sys_org.PARENT_ID is
'父id';

comment on column sys_org.DIRECTOR_ID is
'主管ID';

comment on column sys_org.NAME is
'名称';

comment on column sys_org.CODE is
'编码';

comment on column sys_org.CATEGORY is
'分类';

comment on column sys_org.SORT_CODE is
'排序码';

comment on column sys_org.EXT_JSON is
'扩展信息';

comment on column sys_org.DELETE_FLAG is
'删除标志';

comment on column sys_org.CREATE_TIME is
'创建时间';

comment on column sys_org.CREATE_USER is
'创建用户';

comment on column sys_org.UPDATE_TIME is
'修改时间';

comment on column sys_org.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: sys_position                                          */
/*==============================================================*/
create table sys_position (
   ID                   VARCHAR(20)          not null,
   ORG_ID               VARCHAR(20)          null,
   NAME                 VARCHAR(255)         null,
   CODE                 VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_sys_position primary key (ID)
);

comment on table sys_position is
'职位';

comment on column sys_position.ORG_ID is
'组织id';

comment on column sys_position.NAME is
'名称';

comment on column sys_position.CODE is
'编码';

comment on column sys_position.CATEGORY is
'分类';

comment on column sys_position.SORT_CODE is
'排序码';

comment on column sys_position.EXT_JSON is
'扩展信息';

comment on column sys_position.DELETE_FLAG is
'删除标志';

comment on column sys_position.CREATE_TIME is
'创建时间';

comment on column sys_position.CREATE_USER is
'创建用户';

comment on column sys_position.UPDATE_TIME is
'修改时间';

comment on column sys_position.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: sys_relation                                          */
/*==============================================================*/
create table sys_relation (
   ID                   VARCHAR(20)          not null,
   OBJECT_ID            VARCHAR(255)         null,
   TARGET_ID            VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   EXT_JSON             TEXT                 null,
   constraint PK_sys_relation primary key (ID)
);

comment on table sys_relation is
'关系';

comment on column sys_relation.OBJECT_ID is
'对象ID';

comment on column sys_relation.TARGET_ID is
'目标ID';

comment on column sys_relation.CATEGORY is
'分类';

comment on column sys_relation.EXT_JSON is
'扩展信息';

/*==============================================================*/
/* Table: sys_resource                                          */
/*==============================================================*/
create table sys_resource (
   ID                   VARCHAR(20)          not null,
   PARENT_ID            VARCHAR(255)         null,
   TITLE                VARCHAR(255)         null,
   NAME                 VARCHAR(255)         null,
   CODE                 VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   MODULE               VARCHAR(255)         null,
   MENU_TYPE            VARCHAR(255)         null,
   PATH                 TEXT                 null,
   COMPONENT            VARCHAR(255)         null,
   ICON                 VARCHAR(255)         null,
   COLOR                VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_sys_resource primary key (ID)
);

comment on table sys_resource is
'资源';

comment on column sys_resource.PARENT_ID is
'父id';

comment on column sys_resource.TITLE is
'标题';

comment on column sys_resource.NAME is
'别名';

comment on column sys_resource.CODE is
'编码';

comment on column sys_resource.CATEGORY is
'分类';

comment on column sys_resource.MODULE is
'模块';

comment on column sys_resource.MENU_TYPE is
'菜单类型';

comment on column sys_resource.PATH is
'路径';

comment on column sys_resource.COMPONENT is
'组件';

comment on column sys_resource.ICON is
'图标';

comment on column sys_resource.COLOR is
'颜色';

comment on column sys_resource.SORT_CODE is
'排序码';

comment on column sys_resource.EXT_JSON is
'扩展信息';

comment on column sys_resource.DELETE_FLAG is
'删除标志';

comment on column sys_resource.CREATE_TIME is
'创建时间';

comment on column sys_resource.CREATE_USER is
'创建用户';

comment on column sys_resource.UPDATE_TIME is
'修改时间';

comment on column sys_resource.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: sys_role                                              */
/*==============================================================*/
create table sys_role (
   ID                   VARCHAR(20)          not null,
   ORG_ID               VARCHAR(20)          null,
   NAME                 VARCHAR(255)         null,
   CODE                 VARCHAR(255)         null,
   CATEGORY             VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_sys_role primary key (ID)
);

comment on table sys_role is
'角色';

comment on column sys_role.ORG_ID is
'组织id';

comment on column sys_role.NAME is
'名称';

comment on column sys_role.CODE is
'编码';

comment on column sys_role.CATEGORY is
'分类';

comment on column sys_role.SORT_CODE is
'排序码';

comment on column sys_role.EXT_JSON is
'扩展信息';

comment on column sys_role.DELETE_FLAG is
'删除标志';

comment on column sys_role.CREATE_TIME is
'创建时间';

comment on column sys_role.CREATE_USER is
'创建用户';

comment on column sys_role.UPDATE_TIME is
'修改时间';

comment on column sys_role.UPDATE_USER is
'修改用户';

/*==============================================================*/
/* Table: sys_user                                              */
/*==============================================================*/
create table sys_user (
   ID                   VARCHAR(20)          not null,
   AVATAR               TEXT                 null,
   SIGNATURE            TEXT                 null,
   ACCOUNT              VARCHAR(255)         null,
   PASSWORD             VARCHAR(255)         null,
   NAME                 VARCHAR(255)         null,
   NICKNAME             VARCHAR(255)         null,
   GENDER               VARCHAR(255)         null,
   AGE                  VARCHAR(255)         null,
   BIRTHDAY             VARCHAR(255)         null,
   NATION               VARCHAR(255)         null,
   NATIVE_PLACE         VARCHAR(255)         null,
   HOME_ADDRESS         TEXT                 null,
   MAILING_ADDRESS      TEXT                 null,
   ID_CARD_TYPE         VARCHAR(255)         null,
   ID_CARD_NUMBER       VARCHAR(255)         null,
   CULTURE_LEVEL        VARCHAR(255)         null,
   POLITICAL_OUTLOOK    VARCHAR(255)         null,
   COLLEGE              VARCHAR(255)         null,
   EDUCATION            VARCHAR(255)         null,
   EDU_LENGTH           VARCHAR(255)         null,
   DEGREE               VARCHAR(255)         null,
   PHONE                VARCHAR(255)         null,
   EMAIL                VARCHAR(255)         null,
   HOME_TEL             VARCHAR(255)         null,
   OFFICE_TEL           VARCHAR(255)         null,
   EMERGENCY_CONTACT    VARCHAR(255)         null,
   EMERGENCY_PHONE      VARCHAR(255)         null,
   EMERGENCY_ADDRESS    TEXT                 null,
   EMP_NO               VARCHAR(255)         null,
   ENTRY_DATE           VARCHAR(255)         null,
   ORG_ID               VARCHAR(20)          null,
   POSITION_ID          VARCHAR(20)          null,
   POSITION_LEVEL       VARCHAR(255)         null,
   DIRECTOR_ID          VARCHAR(20)          null,
   POSITION_JSON        TEXT                 null,
   LAST_LOGIN_IP        VARCHAR(255)         null,
   LAST_LOGIN_ADDRESS   VARCHAR(255)         null,
   LAST_LOGIN_TIME      DATE                 null,
   LAST_LOGIN_DEVICE    TEXT                 null,
   LATEST_LOGIN_IP      VARCHAR(255)         null,
   LATEST_LOGIN_ADDRESS VARCHAR(255)         null,
   LATEST_LOGIN_TIME    DATE                 null,
   LATEST_LOGIN_DEVICE  TEXT                 null,
   USER_STATUS          VARCHAR(255)         null,
   SORT_CODE            INT4                 null,
   EXT_JSON             TEXT                 null,
   DELETE_FLAG          VARCHAR(255)         null,
   CREATE_TIME          DATE                 null,
   CREATE_USER          VARCHAR(20)          null,
   UPDATE_TIME          DATE                 null,
   UPDATE_USER          VARCHAR(20)          null,
   constraint PK_sys_user primary key (ID)
);

comment on table sys_user is
'用户';

comment on column sys_user.AVATAR is
'头像';

comment on column sys_user.SIGNATURE is
'签名';

comment on column sys_user.ACCOUNT is
'账号';

comment on column sys_user.PASSWORD is
'密码';

comment on column sys_user.NAME is
'姓名';

comment on column sys_user.NICKNAME is
'昵称';

comment on column sys_user.GENDER is
'性别';

comment on column sys_user.AGE is
'年龄';

comment on column sys_user.BIRTHDAY is
'出生日期';

comment on column sys_user.NATION is
'民族';

comment on column sys_user.NATIVE_PLACE is
'籍贯';

comment on column sys_user.HOME_ADDRESS is
'家庭住址';

comment on column sys_user.MAILING_ADDRESS is
'通信地址';

comment on column sys_user.ID_CARD_TYPE is
'证件类型';

comment on column sys_user.ID_CARD_NUMBER is
'证件号码';

comment on column sys_user.CULTURE_LEVEL is
'文化程度';

comment on column sys_user.POLITICAL_OUTLOOK is
'政治面貌';

comment on column sys_user.COLLEGE is
'毕业院校';

comment on column sys_user.EDUCATION is
'学历';

comment on column sys_user.EDU_LENGTH is
'学制';

comment on column sys_user.DEGREE is
'学位';

comment on column sys_user.PHONE is
'手机';

comment on column sys_user.EMAIL is
'邮箱';

comment on column sys_user.HOME_TEL is
'家庭电话';

comment on column sys_user.OFFICE_TEL is
'办公电话';

comment on column sys_user.EMERGENCY_CONTACT is
'紧急联系人';

comment on column sys_user.EMERGENCY_PHONE is
'紧急联系人电话';

comment on column sys_user.EMERGENCY_ADDRESS is
'紧急联系人地址';

comment on column sys_user.EMP_NO is
'员工编号';

comment on column sys_user.ENTRY_DATE is
'入职日期';

comment on column sys_user.ORG_ID is
'机构id';

comment on column sys_user.POSITION_ID is
'职位id';

comment on column sys_user.POSITION_LEVEL is
'职级';

comment on column sys_user.DIRECTOR_ID is
'主管id';

comment on column sys_user.POSITION_JSON is
'兼任信息';

comment on column sys_user.LAST_LOGIN_IP is
'上次登录ip';

comment on column sys_user.LAST_LOGIN_ADDRESS is
'上次登录地点';

comment on column sys_user.LAST_LOGIN_TIME is
'上次登录时间';

comment on column sys_user.LAST_LOGIN_DEVICE is
'上次登录设备';

comment on column sys_user.LATEST_LOGIN_IP is
'最新登录ip';

comment on column sys_user.LATEST_LOGIN_ADDRESS is
'最新登录地点';

comment on column sys_user.LATEST_LOGIN_TIME is
'最新登录时间';

comment on column sys_user.LATEST_LOGIN_DEVICE is
'最新登录设备';

comment on column sys_user.USER_STATUS is
'用户状态';

comment on column sys_user.SORT_CODE is
'排序码';

comment on column sys_user.EXT_JSON is
'扩展信息';

comment on column sys_user.DELETE_FLAG is
'删除标志';

comment on column sys_user.CREATE_TIME is
'创建时间';

comment on column sys_user.CREATE_USER is
'创建用户';

comment on column sys_user.UPDATE_TIME is
'修改时间';

comment on column sys_user.UPDATE_USER is
'修改用户';


-- ----------------------------
-- Records of DEV_CONFIG
-- ----------------------------
INSERT INTO DEV_CONFIG VALUES ('1554740179362967455', 'SNOWY_SYS_LOGO', 'data:image/png;base64,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', 'SYS_BASE', '系统LOGO', 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967456', 'SNOWY_SYS_NAME', 'Snowy', 'SYS_BASE', '系统名称', 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967457', 'SNOWY_SYS_VERSION', 'V2.0.0', 'SYS_BASE', '系统版本', 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967458', 'SNOWY_SYS_COPYRIGHT', 'Snowy ©2022 Created by 小诺开源技术', 'SYS_BASE', '系统版权', 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967459', 'SNOWY_SYS_COPYRIGHT_URL', 'https://www.xiaonuo.vip', 'SYS_BASE', '系统版权链接地址', 5, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967462', 'SNOWY_SYS_DEFAULT_CAPTCHA_OPEN', 'false', 'SYS_BASE', '登录验证码开关', 8, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967463', 'SNOWY_SYS_DEFAULT_FILE_ENGINE', 'LOCAL', 'SYS_BASE', '默认文件存储引擎', 9, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967464', 'SNOWY_SYS_DEFAULT_PASSWORD', '123456', 'SYS_BASE', '默认用户密码', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967465', 'SNOWY_SYS_DEFAULT_DESCRRIPTION', 'Snowy是一款国内首例国产密码算法加密框架，采用Vue3.0+AntDesignVue3.0+SpringBoot2.8前后分离技术打造，技术框架与密码的结合，让前后分离‘密’不可分！', 'SYS_BASE', '系统描述', 11, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967466', 'SNOWY_SYS_DEFAULT_WORKBENCH_DATA', '{"shortcut":[{"id":"1548901111999770526","title":"系统首页","icon":"home-outlined","path":"/index"},{"id":"1548901111999770527","title":"个人中心","icon":"appstore-outlined","path":"/usercenter"}]}', 'SYS_BASE', '系统默认工作台数据', 12, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967558', 'SNOWY_THIRD_GITEE_CLIENT_ID', 'GiteeClientId', 'THIRD_GITEE', 'GiteeClientId', 13, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967559', 'SNOWY_THIRD_GITEE_CLIENT_SECRET', 'GiteeClientSecret', 'THIRD_GITEE', 'GiteeClientSecret', 14, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967560', 'SNOWY_THIRD_GITEE_REDIRECT_URL', 'http://localhost:81/callback?platform=gitee', 'THIRD_GITEE', 'Gitee重定向地址', 15, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967561', 'SNOWY_THIRD_WECHAT_CLIENT_ID', 'WechatClientId', 'THIRD_WECHAT', 'WechatClientId', 16, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967562', 'SNOWY_THIRD_WECHAT_CLIENT_SECRET', 'WechatClientSecret', 'THIRD_WECHAT', 'WechatClientSecret', 17, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967563', 'SNOWY_THIRD_WECHAT_REDIRECT_URL', 'Wechat重定向地址', 'THIRD_WECHAT', 'Wechat重定向地址', 18, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967564', 'SNOWY_FILE_LOCAL_FOLDER_FOR_WINDOWS', 'D:/defaultUploadFolder', 'FILE_LOCAL', '本地文件Windows存储位置', 19, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967565', 'SNOWY_FILE_LOCAL_FOLDER_FOR_UNIX', '/defaultUploadFolder', 'FILE_LOCAL', '本地文件Linux存储位置', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967566', 'SNOWY_FILE_TENCENT_SECRET_ID', '腾讯云文件SecretId', 'FILE_TENCENT', '腾讯云文件SecretId', 21, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967567', 'SNOWY_FILE_TENCENT_SECRET_KEY', '腾讯云文件SecretKey', 'FILE_TENCENT', '腾讯云文件SecretKey', 22, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967568', 'SNOWY_FILE_TENCENT_REGION_ID', '腾讯云文件RegionId', 'FILE_TENCENT', '腾讯云文件RegionId', 23, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967569', 'SNOWY_FILE_TENCENT_DEFAULT_BUCKET_NAME', 'defaultbucket', 'FILE_TENCENT', '腾讯云文件默认存储桶', 24, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967570', 'SNOWY_FILE_ALIYUN_ACCESS_KEY_ID', '阿里云文件AccessKeyId', 'FILE_ALIYUN', '阿里云文件AccessKeyId', 25, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967571', 'SNOWY_FILE_ALIYUN_ACCESS_KEY_SECRET', '阿里云文件AccessKeySecret', 'FILE_ALIYUN', '阿里云文件AccessKeySecret', 26, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967572', 'SNOWY_FILE_ALIYUN_END_POINT', '阿里云文件EndPoint', 'FILE_ALIYUN', '阿里云文件EndPoint', 27, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967573', 'SNOWY_FILE_ALIYUN_DEFAULT_BUCKET_NAME', 'defaultbucket', 'FILE_ALIYUN', '阿里云文件默认存储桶', 28, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967574', 'SNOWY_FILE_MINIO_ACCESS_KEY', 'MINIO文件AccessKey', 'FILE_MINIO', 'MINIO文件AccessKey', 29, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967575', 'SNOWY_FILE_MINIO_SECRET_KEY', 'MINIO文件SecetKey', 'FILE_MINIO', 'MINIO文件SecetKey', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967576', 'SNOWY_FILE_MINIO_END_POINT', 'MINIO文件EndPoint', 'FILE_MINIO', 'MINIO文件EndPoint', 31, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967577', 'SNOWY_FILE_MINIO_DEFAULT_BUCKET_NAME', 'defaultbucket', 'FILE_MINIO', 'MINIO文件默认存储桶', 32, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967578', 'SNOWY_EMAIL_LOCAL_FROM', '本地邮件发件账号', 'EMAIL_LOCAL', '本地邮件发件账号', 33, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967579', 'SNOWY_EMAIL_LOCAL_PASSWORD', '本地邮件发件密码', 'EMAIL_LOCAL', '本地邮件发件密码', 34, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967580', 'SNOWY_EMAIL_TENCENT_SECRET_ID', '腾讯云邮件SecretId', 'EMAIL_TENCENT', '腾讯云邮件SecretId', 35, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967581', 'SNOWY_EMAIL_TENCENT_SECRET_KEY', '腾讯云邮件SecretKey', 'EMAIL_TENCENT', '腾讯云邮件SecretKey', 36, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967582', 'SNOWY_EMAIL_TENCENT_REGION_ID', '腾讯云邮件RegionId', 'EMAIL_TENCENT', '腾讯云邮件RegionId', 37, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967583', 'SNOWY_EMAIL_ALIYUN_ACCESS_KEY_ID', '阿里云邮件AccessKeyId', 'EMAIL_ALIYUN', '阿里云邮件AccessKeyId', 38, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967584', 'SNOWY_EMAIL_ALIYUN_ACCESS_KEY_SECRET', '阿里云邮件AccessKeySecret', 'EMAIL_ALIYUN', '阿里云邮件AccessKeySecret', 39, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967585', 'SNOWY_EMAIL_ALIYUN_REGION_ID', '阿里云邮件RegionId', 'EMAIL_ALIYUN', '阿里云邮件RegionId', 40, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967586', 'SNOWY_SMS_TENCENT_SECRET_ID', '腾讯云短信SecretId', 'SMS_TENCENT', '腾讯云短信SecretId', 41, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967587', 'SNOWY_SMS_TENCENT_SECRET_KEY', '腾讯云短信SecretKey', 'SMS_TENCENT', '腾讯云短信SecretKey', 42, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967588', 'SNOWY_SMS_TENCENT_REGION_ID', '腾讯云短信RegionId', 'SMS_TENCENT', '腾讯云短信RegionId', 43, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967589', 'SNOWY_SMS_TENCENT_DEFAULT_SDK_APP_ID', '腾讯云短信默认SdkAppId', 'SMS_TENCENT', '腾讯云短信默认SdkAppId', 44, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967590', 'SNOWY_SMS_TENCENT_DEFAULT_SIGN_NAME', '腾讯云短信默认签名', 'SMS_TENCENT', '腾讯云短信默认签名', 45, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967591', 'SNOWY_SMS_ALIYUN_ACCESS_KEY_ID', '阿里云短信AccessKeyId', 'SMS_ALIYUN', '阿里云短信AccessKeyId', 46, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967592', 'SNOWY_SMS_ALIYUN_ACCESS_KEY_SECRET', '阿里云短信AccessKeySecret', 'SMS_ALIYUN', '阿里云短信AccessKeySecret', 47, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967593', 'SNOWY_SMS_ALIYUN_END_POINT', '阿里云短信EndPoint', 'SMS_ALIYUN', '阿里云短信EndPoint', 48, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_CONFIG VALUES ('1554740179362967594', 'SNOWY_SMS_ALIYUN_DEFAULT_SIGN_NAME', '阿里云短信默认签名', 'SMS_ALIYUN', '阿里云短信默认签名', 49, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);


-- ----------------------------
-- Records of DEV_DICT
-- ----------------------------
INSERT INTO DEV_DICT VALUES ('1543839774776291330', '0', '用户性别类型', 'GENDER', 'FRM', 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1543839901037424642', '1543839774776291330', '男', '男', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1543840033980084226', '1543839774776291330', '女', '女', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1543860103661809666', '0', '系统菜单类型', 'MENU_TYPE', 'FRM', 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1543860239020388354', '1543860103661809666', '目录', 'CATALOG', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1543860305508495361', '1543860103661809666', '菜单', 'MENU', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1543860423485878274', '1543860103661809666', '内链', 'IFRAME', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1543860477512708098', '1543860103661809666', '外链', 'LINK', 'FRM', 40, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1544329873407946753', '0', '系统通用状态', 'COMMON_STATUS', 'FRM', 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1545397556652027906', '1544329873407946753', '启用', 'ENABLE', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1545397556652027907', '1544329873407946753', '停用', 'DISABLED', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547207669861064706', '0', '系统角色分类', 'ROLE_CATEGORY', 'FRM', 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547207891009937409', '1547207669861064706', '全局', 'GLOBAL', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547207990075203585', '1547207669861064706', '机构', 'ORG', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547227094995705858', '0', '系统机构分类', 'ORG_CATEGORY', 'FRM', 5, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547227670693289985', '1547227094995705858', '部门', 'DEPT', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547228161267474434', '1547227094995705858', '公司', 'COMPANY', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547409689434742786', '0', '系统职位分类', 'POSITION_CATEGORY', 'FRM', 6, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547409794837602305', '1547409689434742786', '高层', 'HIGH', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547409844779180033', '1547409689434742786', '中层', 'MIDDLE', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547409906406088706', '1547409689434742786', '基层', 'LOW', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658113', '0', '用户民族类型', 'NATION', 'FRM', 7, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658114', '1547641470701658113', '汉族', '汉族', 'FRM', 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658115', '1547641470701658113', '壮族', '壮族', 'FRM', 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658116', '1547641470701658113', '回族', '回族', 'FRM', 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658117', '1547641470701658113', '满族', '满族', 'FRM', 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658118', '1547641470701658113', '维吾尔族', '维吾尔族', 'FRM', 5, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658119', '1547641470701658113', '苗族', '苗族', 'FRM', 6, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658120', '1547641470701658113', '彝族', '彝族', 'FRM', 7, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658121', '1547641470701658113', '土家族', '土家族', 'FRM', 8, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658122', '1547641470701658113', '藏族', '藏族', 'FRM', 9, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658123', '1547641470701658113', '蒙古族', '蒙古族', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658124', '1547641470701658113', '侗族', '侗族', 'FRM', 11, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658125', '1547641470701658113', '布依族', '布依族', 'FRM', 12, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658126', '1547641470701658113', '瑶族', '瑶族', 'FRM', 13, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658127', '1547641470701658113', '白族', '白族', 'FRM', 14, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658128', '1547641470701658113', '朝鲜族', '朝鲜族', 'FRM', 15, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658129', '1547641470701658113', '哈尼族', '哈尼族', 'FRM', 16, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658130', '1547641470701658113', '黎族', '黎族', 'FRM', 17, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658131', '1547641470701658113', '哈萨克族', '哈萨克族', 'FRM', 18, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658132', '1547641470701658113', '傣族', '傣族', 'FRM', 19, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658133', '1547641470701658113', '畲族', '畲族', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658134', '1547641470701658113', '傈僳族', '傈僳族', 'FRM', 21, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658135', '1547641470701658113', '东乡族', '东乡族', 'FRM', 22, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658136', '1547641470701658113', '仡佬族', '仡佬族', 'FRM', 23, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658137', '1547641470701658113', '拉祜族', '拉祜族', 'FRM', 24, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658138', '1547641470701658113', '佤族', '佤族', 'FRM', 25, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658139', '1547641470701658113', '水族', '水族', 'FRM', 26, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658140', '1547641470701658113', '纳西族', '纳西族', 'FRM', 27, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658141', '1547641470701658113', '羌族', '羌族', 'FRM', 28, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658142', '1547641470701658113', '土族', '土族', 'FRM', 29, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658143', '1547641470701658113', '仫佬族', '仫佬族', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658144', '1547641470701658113', '锡伯族', '锡伯族', 'FRM', 31, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658145', '1547641470701658113', '柯尔克孜族', '柯尔克孜族', 'FRM', 32, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658146', '1547641470701658113', '景颇族', '景颇族', 'FRM', 33, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658147', '1547641470701658113', '达斡尔族', '达斡尔族', 'FRM', 34, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658148', '1547641470701658113', '撒拉族', '撒拉族', 'FRM', 35, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658149', '1547641470701658113', '布朗族', '布朗族', 'FRM', 36, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658150', '1547641470701658113', '毛南族', '毛南族', 'FRM', 37, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658151', '1547641470701658113', '塔吉克族', '塔吉克族', 'FRM', 38, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658152', '1547641470701658113', '普米族', '普米族', 'FRM', 39, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658153', '1547641470701658113', '阿昌族', '阿昌族', 'FRM', 40, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658154', '1547641470701658113', '怒族', '怒族', 'FRM', 41, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658155', '1547641470701658113', '鄂温克族', '鄂温克族', 'FRM', 42, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658156', '1547641470701658113', '京族', '京族', 'FRM', 43, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658157', '1547641470701658113', '基诺族', '基诺族', 'FRM', 44, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658158', '1547641470701658113', '德昂族', '德昂族', 'FRM', 45, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658159', '1547641470701658113', '保安族', '保安族', 'FRM', 46, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658160', '1547641470701658113', '俄罗斯族', '俄罗斯族', 'FRM', 47, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658161', '1547641470701658113', '裕固族', '裕固族', 'FRM', 48, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658162', '1547641470701658113', '乌孜别克族', '乌孜别克族', 'FRM', 49, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658163', '1547641470701658113', '门巴族', '门巴族', 'FRM', 50, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658164', '1547641470701658113', '鄂伦春族', '鄂伦春族', 'FRM', 51, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658165', '1547641470701658113', '独龙族', '独龙族', 'FRM', 52, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658166', '1547641470701658113', '赫哲族', '赫哲族', 'FRM', 53, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658167', '1547641470701658113', '高山族', '高山族', 'FRM', 54, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658168', '1547641470701658113', '珞巴族', '珞巴族', 'FRM', 55, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1547641470701658169', '1547641470701658113', '塔塔尔族 ', '塔塔尔族 ', 'FRM', 56, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1549019269252104194', '0', '登录设备类型', 'AUTH_DEVICE_TYPE', 'FRM', 8, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1549019748883349506', '1549019269252104194', 'PC端', 'PC', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1549019813924421634', '1549019269252104194', '移动端', 'APP', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1549019864537088002', '1549019269252104194', '小程序', 'MINI', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554678566166323202', '0', '系统字典分类', 'DICT_CATEGORY', 'FRM', 9, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554678761742524417', '1554678566166323202', '框架', 'FRM', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554678863617974273', '1554678566166323202', '业务', 'BIZ', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554679788378120194', '0', '短信发送引擎', 'SMS_ENGINE', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554679872054484993', '1554679788378120194', '阿里云', 'ALIYUN', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554679958398427138', '1554679788378120194', '腾讯云', 'TENCENT', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554726376265744386', '0', '文件上传引擎', 'FILE_ENGINE', 'FRM', 11, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554726639571566593', '1554726376265744386', '本地', 'LOCAL', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554726696068841474', '1554726376265744386', '阿里云', 'ALIYUN', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554726762338844674', '1554726376265744386', '腾讯云', 'TENCENT', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1554726830844411905', '1554726376265744386', 'MINIO', 'MINIO', 'FRM', 40, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1556317797993218049', '0', '邮件发送引擎', 'EMAIL_ENGINE', 'FRM', 12, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1556319651447767041', '1556317797993218049', '本地', 'LOCAL', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1556319726962016258', '1556317797993218049', '阿里云', 'ALIYUN', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1556319786349166593', '1556317797993218049', '腾讯云', 'TENCENT', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1559942543251079169', '0', '系统通用开关', 'COMMON_SWITCH', 'FRM', 13, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1559942706694717442', '1559942543251079169', '开', 'true', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1559942777674924034', '1559942543251079169', '关', 'false', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560306347516461058', '0', '用户证件类型', 'IDCARD_TYPE', 'FRM', 14, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560306502135283714', '1560306347516461058', '身份证', '身份证', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560306768913989633', '1560306347516461058', '出生证', '出生证', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560307009365049346', '1560306347516461058', '军官证', '军官证', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560307180937248769', '1560306347516461058', '护照', '护照', 'FRM', 40, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309358598914050', '0', '通用文化程度', 'CULTURE_LEVEL', 'FRM', 15, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309494892822530', '1560309358598914050', '小学', '小学', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309602136981505', '1560309358598914050', '中学', '中学', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309700136894465', '1560309358598914050', '高中', '高中', 'FRM', 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309744118366209', '1560309358598914050', '中专', '中专', 'FRM', 40, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309783037313026', '1560309358598914050', '大专', '大专', 'FRM', 50, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309816423972866', '1560309358598914050', '本科', '本科', 'FRM', 60, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560309855661686785', '1560309358598914050', '硕士研究生', '硕士研究生', 'FRM', 70, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560310085471797250', '1560309358598914050', '博士研究生', '博士研究生', 'FRM', 80, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560338934867791874', '0', '定时任务分类', 'JOB_CATEGORY', 'FRM', 16, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560339092900777985', '1560338934867791874', '框架', 'FRM', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560339156134105089', '1560338934867791874', '业务', 'BIZ', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560342111344234497', '0', '定时任务状态', 'JOB_STATUS', 'FRM', 17, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560342186812346370', '1560342111344234497', '运行', 'RUNNING', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1560342250096005121', '1560342111344234497', '停止', 'STOPPED', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1561595062998102017', '0', '三方用户分类', 'THIRD_CATEGORY', 'FRM', 18, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1561595252714860545', '1561595062998102017', '码云GITEE', 'GITEE', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1561595322336112641', '1561595062998102017', '微信WECHAT', 'WECHAT', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1567580351742619650', '0', '系统消息类型', 'MESSAGE_CATEGORY', 'FRM', 19, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1567580424270524418', '1567580351742619650', '系统', 'SYS', 'FRM', 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO DEV_DICT VALUES ('1567580487684206594', '1567580351742619650', '业务', 'BIZ', 'FRM', 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of DEV_JOB
-- ----------------------------
INSERT INTO DEV_JOB VALUES ('1555471535453827073', '定时任务', 'qi06nah38m', 'FRM', 'vip.xiaonuo.dev.modular.job.task.DevJobTimerTaskRunner', '0 0 0 1 * ?', 'STOPPED', 1, NULL, 'NOT_DELETE', '2022-08-05 16:31:29', '-1', NULL, NULL);

-- ----------------------------
-- Records of SYS_ORG
-- ----------------------------
INSERT INTO SYS_ORG VALUES ('1543842934270394368', '0', NULL, '小诺科技有限公司', 'yfqtrbd5qz', 'COMPANY', 1, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394370', '1543842934270394368', '1543837863788879880', '工会办公室', '5ruzr6n7g7', 'DEPT', 3, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394371', '1543842934270394368', '1543837863788879883', '综合管理部', 'l4sdfnw27p', 'DEPT', 4, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394372', '1543842934270394368', '1543837863788879887', '财务资产部', 'h7yq9t1q0t', 'DEPT', 5, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394373', '1543842934270394368', '1543837863788879892', '人力资源部', '245ryxcbqh', 'DEPT', 6, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394374', '1543842934270394368', '1543837863788879898', '党群工作部', 'sc6jkffc4d', 'DEPT', 7, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394375', '1543842934270394368', '1543837863788879903', '纪检监督部', '39t022fx1m', 'DEPT', 8, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394376', '1543842934270394368', '1543837863788879907', '生产技术部', '34m3lam984', 'DEPT', 9, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394377', '1543842934270394368', '1543837863788879912', '计划营销部', 'w742mipwer', 'DEPT', 10, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);
INSERT INTO SYS_ORG VALUES ('1543842934270394378', '1543842934270394368', '1543837863788879917', '后勤保卫部', 'b71pvf46do', 'DEPT', 11, NULL, 'NOT_DELETE', '2022-07-18 19:42:43', '-1', NULL, NULL);

-- ----------------------------
-- Records of SYS_POSITION
-- ----------------------------
INSERT INTO SYS_POSITION VALUES ('1543899639134019583', '1543842934270394368', '董事长', 'rsz5dmh762', 'HIGH', 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_POSITION VALUES ('1543899639134019584', '1543842934270394368', '总经理', 'wo1araqs2z', 'HIGH', 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_POSITION VALUES ('1543899639134019585', '1543842934270394368', '副总经理', 'asqvwbsc16', 'HIGH', 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_POSITION VALUES ('1543899639134019587', '1543842934270394368', '总经理助理', 'aln9y4tno6', 'HIGH', 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_POSITION VALUES ('1543899639134019591', '1543842934270394368', '部门经理', 'krq0kj7oio', 'MIDDLE', 5, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of SYS_RELATION
-- ----------------------------
INSERT INTO SYS_RELATION VALUES ('1569556138947522560', '1543837863788879873', '1570687866138206209', 'SYS_USER_HAS_ROLE', NULL);
INSERT INTO SYS_RELATION VALUES ('1569556138947522561', '1543837863788879871', '1570687866138206208', 'SYS_USER_HAS_ROLE', NULL);
INSERT INTO SYS_RELATION VALUES ('1570792235898695682', '1570687866138206209', '/biz/org/add', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/add"}');
INSERT INTO SYS_RELATION VALUES ('1570792235898695683', '1570687866138206209', '/biz/org/delete', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/delete"}');
INSERT INTO SYS_RELATION VALUES ('1570792235898695684', '1570687866138206209', '/biz/org/detail', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/detail"}');
INSERT INTO SYS_RELATION VALUES ('1570792235898695685', '1570687866138206209', '/biz/org/edit', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/edit"}');
INSERT INTO SYS_RELATION VALUES ('1570792235898695686', '1570687866138206209', '/biz/org/orgTreeSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/orgTreeSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235898695687', '1570687866138206209', '/biz/org/page', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/page"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084290', '1570687866138206209', '/biz/org/tree', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/tree"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084291', '1570687866138206209', '/biz/org/userSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/userSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084292', '1570687866138206209', '/biz/position/add', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/add"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084293', '1570687866138206209', '/biz/position/delete', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/delete"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084294', '1570687866138206209', '/biz/position/detail', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/detail"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084295', '1570687866138206209', '/biz/position/edit', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/edit"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084296', '1570687866138206209', '/biz/position/orgTreeSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/orgTreeSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084297', '1570687866138206209', '/biz/position/page', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/page"}');
INSERT INTO SYS_RELATION VALUES ('1570792235907084298', '1570687866138206209', '/biz/position/positionSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/positionSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472898', '1570687866138206209', '/biz/user/add', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/add"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472899', '1570687866138206209', '/biz/user/delete', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/delete"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472900', '1570687866138206209', '/biz/user/detail', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/detail"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472901', '1570687866138206209', '/biz/user/disableUser', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/disableUser"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472902', '1570687866138206209', '/biz/user/edit', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/edit"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472903', '1570687866138206209', '/biz/user/enableUser', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/enableUser"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472904', '1570687866138206209', '/biz/user/export', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/export"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472905', '1570687866138206209', '/biz/user/grantRole', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/grantRole"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472906', '1570687866138206209', '/biz/user/import', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/import"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472907', '1570687866138206209', '/biz/user/orgListSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/orgListSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472908', '1570687866138206209', '/biz/user/orgTreeSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/orgTreeSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472909', '1570687866138206209', '/biz/user/ownRole', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/ownRole"}');
INSERT INTO SYS_RELATION VALUES ('1570792235915472910', '1570687866138206209', '/biz/user/page', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/page"}');
INSERT INTO SYS_RELATION VALUES ('1570792235923861505', '1570687866138206209', '/biz/user/positionSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/positionSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235923861506', '1570687866138206209', '/biz/user/resetPassword', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/resetPassword"}');
INSERT INTO SYS_RELATION VALUES ('1570792235923861507', '1570687866138206209', '/biz/user/roleSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/roleSelector"}');
INSERT INTO SYS_RELATION VALUES ('1570792235923861508', '1570687866138206209', '/biz/user/userSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/userSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991599063041', '1570687866138206208', '/biz/org/add', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/add"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257345', '1570687866138206208', '/biz/org/delete', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/delete"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257346', '1570687866138206208', '/biz/org/detail', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/detail"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257347', '1570687866138206208', '/biz/org/edit', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/edit"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257348', '1570687866138206208', '/biz/org/orgTreeSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/orgTreeSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257349', '1570687866138206208', '/biz/org/page', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/page"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257350', '1570687866138206208', '/biz/org/tree', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/tree"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257351', '1570687866138206208', '/biz/org/userSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/org/userSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257352', '1570687866138206208', '/biz/position/add', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/add"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257353', '1570687866138206208', '/biz/position/delete', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/delete"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257354', '1570687866138206208', '/biz/position/detail', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/detail"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257355', '1570687866138206208', '/biz/position/edit', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/edit"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257356', '1570687866138206208', '/biz/position/orgTreeSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/orgTreeSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257357', '1570687866138206208', '/biz/position/page', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/page"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257358', '1570687866138206208', '/biz/position/positionSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/position/positionSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257359', '1570687866138206208', '/biz/user/add', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/add"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257360', '1570687866138206208', '/biz/user/delete', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/delete"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257361', '1570687866138206208', '/biz/user/detail', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/detail"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257362', '1570687866138206208', '/biz/user/disableUser', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/disableUser"}');
INSERT INTO SYS_RELATION VALUES ('1571089991603257363', '1570687866138206208', '/biz/user/edit', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/edit"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645954', '1570687866138206208', '/biz/user/enableUser', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/enableUser"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645955', '1570687866138206208', '/biz/user/export', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/export"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645956', '1570687866138206208', '/biz/user/grantRole', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/grantRole"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645957', '1570687866138206208', '/biz/user/import', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/import"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645958', '1570687866138206208', '/biz/user/orgListSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/orgListSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645959', '1570687866138206208', '/biz/user/orgTreeSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/orgTreeSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645960', '1570687866138206208', '/biz/user/ownRole', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/ownRole"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645961', '1570687866138206208', '/biz/user/page', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/page"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645962', '1570687866138206208', '/biz/user/positionSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/positionSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645963', '1570687866138206208', '/biz/user/resetPassword', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/resetPassword"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645964', '1570687866138206208', '/biz/user/roleSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/roleSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571089991611645965', '1570687866138206208', '/biz/user/userSelector', 'SYS_ROLE_HAS_PERMISSION', '{"scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[],"apiUrl":"/biz/user/userSelector"}');
INSERT INTO SYS_RELATION VALUES ('1571133996951502849', '1570687866138206209', '1548901111999773978', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773978","buttonInfo":["1571129529564758017","1571129929961406466","1571130756155408386","1571130811058847745"]}');
INSERT INTO SYS_RELATION VALUES ('1571133996951502850', '1570687866138206209', '1548901111999773979', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773979","buttonInfo":["1571130973294526465","1571131043532341249","1571131137006600193","1571131427361488898","1571131544973967361","1571131727656878081","1571132076853657601"]}');
INSERT INTO SYS_RELATION VALUES ('1571133996951502851', '1570687866138206209', '1548901111999773980', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773980","buttonInfo":["1571132393993371649","1571132468178026497","1571132576143605761","1571132658851086338"]}');
INSERT INTO SYS_RELATION VALUES ('1571134629653872641', '1543837863788879872', NULL, 'SYS_USER_WORKBENCH_DATA', '{"shortcut":[{"id":"1548901111999770527","title":"个人中心","icon":"appstore-outlined","path":"/usercenter"},{"id":"1548901111999772426","title":"系统配置","icon":"appstore-outlined","path":"/sys/config"}]}');
INSERT INTO SYS_RELATION VALUES ('1571134629653872642', '1543837863788879873', NULL, 'SYS_USER_WORKBENCH_DATA', '{"shortcut":[{"id":"1548901111999770527","title":"个人中心","icon":"appstore-outlined","path":"/usercenter"},{"id":"1548901111999773978","title":"机构管理","icon":"appstore-outlined","path":"/biz/org"},{"id":"1548901111999773979","title":"人员管理","icon":"appstore-outlined","path":"/biz/user"},{"id":"1548901111999773980","title":"岗位管理","icon":"appstore-outlined","path":"/biz/position"}]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570305', '1570687866138206208', '1548901111999770826', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999770826","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570306', '1570687866138206208', '1548901111999770926', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999770926","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570307', '1570687866138206208', '1548901111999771026', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771026","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570308', '1570687866138206208', '1548901111999771226', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771226","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570309', '1570687866138206208', '1548901111999771326', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771326","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570310', '1570687866138206208', '1548901111999771426', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771426","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570311', '1570687866138206208', '1548901111999771526', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771526","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570312', '1570687866138206208', '1548901111999771726', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771726","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570313', '1570687866138206208', '1548901111999771826', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771826","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570314', '1570687866138206208', '1548901111999771926', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999771926","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570315', '1570687866138206208', '1548901111999772026', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772026","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570316', '1570687866138206208', '1548901111999772226', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772226","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570317', '1570687866138206208', '1548901111999772326', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772326","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570318', '1570687866138206208', '1548901111999772426', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772426","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570319', '1570687866138206208', '1548901111999772526', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772526","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570320', '1570687866138206208', '1548901111999772626', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772626","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570321', '1570687866138206208', '1548901111999772726', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772726","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570322', '1570687866138206208', '1548901111999772826', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772826","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570323', '1570687866138206208', '1548901111999772926', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999772926","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570324', '1570687866138206208', '1548901111999773226', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773226","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570325', '1570687866138206208', '1548901111999773326', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773326","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570326', '1570687866138206208', '1548901111999773254', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773254","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570327', '1570687866138206208', '1548901111999773427', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773427","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570328', '1570687866138206208', '1548901111999773428', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773428","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570329', '1570687866138206208', '1548901111999773429', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773429","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570330', '1570687866138206208', '1548901111999773430', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773430","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570331', '1570687866138206208', '1548901111999773431', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773431","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570332', '1570687866138206208', '1548901111999773432', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773432","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570333', '1570687866138206208', '1548901111999773433', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773433","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570334', '1570687866138206208', '1548901111999773434', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773434","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570335', '1570687866138206208', '1548901111999773435', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773435","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570336', '1570687866138206208', '1548901111999773436', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773436","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570337', '1570687866138206208', '1548901111999773437', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773437","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570338', '1570687866138206208', '1548901111999773438', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773438","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570339', '1570687866138206208', '1548901111999773439', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773439","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570340', '1570687866138206208', '1548901111999773440', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773440","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570341', '1570687866138206208', '1548901111999773441', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773441","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570342', '1570687866138206208', '1548901111999773442', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773442","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570343', '1570687866138206208', '1548901111999773443', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773443","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570344', '1570687866138206208', '1548901111999773444', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773444","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570345', '1570687866138206208', '1548901111999773445', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773445","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570346', '1570687866138206208', '1548901111999773978', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773978","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570347', '1570687866138206208', '1548901111999773979', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773979","buttonInfo":[]}');
INSERT INTO SYS_RELATION VALUES ('1587843428137570348', '1570687866138206208', '1548901111999773980', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1548901111999773980","buttonInfo":[]}');

-- ----------------------------
-- Records of SYS_RESOURCE
-- ----------------------------
INSERT INTO SYS_RESOURCE VALUES ('1548901111999770525', NULL, '系统', NULL, 'system', 'MODULE', NULL, NULL, NULL, NULL, 'appstore-add-outlined', '#05a045', 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999770526', NULL, '系统首页', 'index', 'system', 'SPA', NULL, 'MENU', '/index', 'index/index', 'home-outlined', NULL, 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999770527', NULL, '个人中心', 'userCenter', 'system', 'SPA', NULL, 'MENU', '/usercenter', 'sys/user/userCenter', 'appstore-outlined', NULL, 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999770726', '0', '组织架构', NULL, 'wxq116zcyp', 'MENU', '1548901111999770525', 'CATALOG', '/e4y8y7ib2p', NULL, 'apartment-outlined', NULL, 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999770826', '1548901111999770726', '组织管理', 'sysOrg', '7scuix7595', 'MENU', '1548901111999770525', 'MENU', '/sys/org', 'sys/org/index', 'cluster-outlined', NULL, 5, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999770926', '1548901111999770726', '用户管理', 'sysUser', 'ue0wd497yb', 'MENU', '1548901111999770525', 'MENU', '/sys/user', 'sys/user/index', 'user-outlined', NULL, 6, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771026', '1548901111999770726', '职位管理', 'sysPosition', 'fkbckffqxi', 'MENU', '1548901111999770525', 'MENU', '/sys/position', 'sys/position/index', 'apartment-outlined', NULL, 7, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771126', '0', '权限管控', NULL, 'k7av3f3rg6', 'MENU', '1548901111999770525', 'CATALOG', '/5k9uuuzafi', NULL, 'user-switch-outlined', NULL, 8, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771226', '1548901111999771126', '角色管理', 'sysRole', 'hwjxhcg122', 'MENU', '1548901111999770525', 'MENU', '/sys/role', 'sys/role/index', 'deployment-unit-outlined', NULL, 9, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771326', '1548901111999771126', '模块管理', 'sysModule', 'opli26z18q', 'MENU', '1548901111999770525', 'MENU', '/sys/module', 'sys/resource/module/index', 'appstore-add-outlined', NULL, 10, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771426', '1548901111999771126', '菜单管理', 'sysMenu', 'q38j3bb839', 'MENU', '1548901111999770525', 'MENU', '/sys/menu', 'sys/resource/menu/index', 'pic-left-outlined', NULL, 11, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771526', '1548901111999771126', '单页管理', 'sysSpa', '8mm06l6wwu', 'MENU', '1548901111999770525', 'MENU', '/sys/spa', 'sys/resource/spa/index', 'pic-center-outlined', NULL, 12, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771626', '0', '基础工具', NULL, 'nplvu771br', 'MENU', '1548901111999770525', 'CATALOG', '/ozmlc6eyw5', NULL, 'tool-outlined', NULL, 13, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771726', '1548901111999771626', '文件管理', 'devFile', 'n25k83x4sy', 'MENU', '1548901111999770525', 'MENU', '/dev/file/index', 'dev/file/index', 'copy-outlined', NULL, 14, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771826', '1548901111999771626', '邮件推送', 'devEmail', 'x4fx2a91tq', 'MENU', '1548901111999770525', 'MENU', '/dev/email/index', 'dev/email/index', 'send-outlined', NULL, 15, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999771926', '1548901111999771626', '短信发送', 'devSms', 'nnjsr7tkrs', 'MENU', '1548901111999770525', 'MENU', '/dev/sms/index', 'dev/sms/index', 'mail-outlined', NULL, 16, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772026', '1548901111999771626', '站内信息', 'devMessage', '0yitzu8786', 'MENU', '1548901111999770525', 'MENU', '/dev/message/index', 'dev/message/index', 'message-outlined', NULL, 17, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772126', '0', '系统运维', NULL, '3poiqgf7zx', 'MENU', '1548901111999770525', 'CATALOG', '/a0l7fxfq3m', NULL, 'hdd-outlined', NULL, 18, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772226', '1548901111999772126', '三方用户', 'authThird', 'xf89fmzrtz', 'MENU', '1548901111999770525', 'MENU', '/auth/third', 'auth/third/index', 'team-outlined', NULL, 19, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772326', '1548901111999772126', '数据字典', 'devDict', 'pzr1auhqf3', 'MENU', '1548901111999770525', 'MENU', '/sys/dict', 'dev/dict/index', 'file-search-outlined', NULL, 20, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772426', '1548901111999772126', '系统配置', 'devConfig', '38zmn86vxg', 'MENU', '1548901111999770525', 'MENU', '/sys/config', 'dev/config/index', 'setting-outlined', NULL, 21, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772526', '1548901111999772126', '任务调度', 'devJob', 'mj2p3y3hzq', 'MENU', '1548901111999770525', 'MENU', '/dev/job', 'dev/job/index', 'field-time-outlined', NULL, 22, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772626', '1548901111999772126', '会话管理', 'authMonitor', '4x1fpyaxys', 'MENU', '1548901111999770525', 'MENU', '/auth/monitor', 'auth/monitor/index', 'usergroup-delete-outlined', NULL, 23, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772726', '1548901111999772126', '系统监控', 'devMonitor', 'sugg84qph2', 'MENU', '1548901111999770525', 'MENU', '/dev/monitor', 'dev/monitor/index', 'database-outlined', NULL, 24, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772826', '1548901111999772126', '连接监控', '连接监控', '1xw98nknao', 'MENU', '1548901111999770525', 'IFRAME', 'http://localhost:82/druid/index.html', NULL, 'console-sql-outlined', NULL, 25, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999772926', '1548901111999772126', '接口文档', 'jieKouWenDang', 'ookzmx37dt', 'MENU', '1548901111999770525', 'IFRAME', 'http://localhost:82/doc.html', NULL, 'file-word-outlined', NULL, 26, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773126', '1548901111999772126', '日志审计', NULL, 'i7wpmggo6a', 'MENU', '1548901111999770525', 'CATALOG', '/x1vjuegii4', NULL, 'robot-outlined', NULL, 27, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773226', '1548901111999773126', '访问日志', 'devVislog', 'gr29jwaigx', 'MENU', '1548901111999770525', 'MENU', '/dev/vislog', 'dev/log/vislog/index', 'bars-outlined', NULL, 28, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773250', '0', '在线开发', NULL, 'fk5m5c6b3u', 'MENU', '1548901111999770525', 'CATALOG', '/94mcv3octp', NULL, 'project-outlined', NULL, 41, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773254', '1548901111999773250', '代码生成', 'genIndex', 'ymyd8xlp5i', 'MENU', '1548901111999770525', 'MENU', '/gen', 'gen/index', 'rocket-outlined', NULL, 45, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773326', '1548901111999773126', '操作日志', 'devOplog', '4jbjjrz8h7', 'MENU', '1548901111999770525', 'MENU', '/dev/oplog', 'dev/log/oplog/index', 'bars-outlined', NULL, 29, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773426', '0', '开发示例', NULL, 'kyd5idwiwr', 'MENU', '1548901111999770525', 'CATALOG', '/e2re4evf5y', NULL, 'project-outlined', NULL, 30, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773427', '1548901111999773426', '图标选择', 'tuBiaoXuanZe', '97ygt0hy8t', 'MENU', '1548901111999770525', 'MENU', '/exm/iconSelect', 'exm/iconSelect/index', 'appstore-outlined', NULL, 31, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773428', '1548901111999773426', 'ECK线图', 'eCKXianTu', '7voetv0mru', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCKXianTu', 'exm/chart/eCKXianTu', 'appstore-outlined', NULL, 32, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773429', '1548901111999773426', 'EC仪表图', 'eCYiBiaoTu', 'c4uor9wg1b', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCYiBiaoTu', 'exm/chart/eCYiBiaoTu', 'appstore-outlined', NULL, 33, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773430', '1548901111999773426', 'EC散点图', 'eCSanDianTu', '6r6ti8izxi', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCSanDianTu', 'exm/chart/eCSanDianTu', 'appstore-outlined', NULL, 34, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773431', '1548901111999773426', 'EC柱状图', 'eCZhuZhuangTu', 's3ft1ri9qz', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCZhuZhuangTu', 'exm/chart/eCZhuZhuangTu', 'appstore-outlined', NULL, 35, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773432', '1548901111999773426', 'EC树形图', 'eCShuXingTu', '63lz6owubp', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCShuXingTu', 'exm/chart/eCShuXingTu', 'appstore-outlined', NULL, 36, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773433', '1548901111999773426', 'EC漏斗图', 'eCLouDouTu', 'd46vov3j2d', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCLouDouTu', 'exm/chart/eCLouDouTu', 'appstore-outlined', NULL, 37, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773434', '1548901111999773426', 'EC线形图', 'eCXianXingTu', 'dgp8hclhlr', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCXianXingTu', 'exm/chart/eCXianXingTu', 'appstore-outlined', NULL, 38, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773435', '1548901111999773426', 'EC饼状图', 'eCBingZhuangTu', '2y1g7u2p1k', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/eCBingZhuangTu', 'exm/chart/eCBingZhuangTu', 'appstore-outlined', NULL, 39, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773436', '1548901111999773426', 'G2进度图', 'g2JinDuTu', '8vvhyctv2w', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2JinDuTu', 'exm/chart/g2JinDuTu', 'appstore-outlined', NULL, 40, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773437', '1548901111999773426', 'G2子弹图', 'g2ZiDanTu', '3lgc3ci5f3', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2ZiDanTu', 'exm/chart/g2ZiDanTu', 'appstore-outlined', NULL, 41, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773438', '1548901111999773426', 'G2散点图', 'g2SanDianTu', 'e22qm4b30d', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2SanDianTu', 'exm/chart/g2SanDianTu', 'appstore-outlined', NULL, 42, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773439', '1548901111999773426', 'G2柱状图', 'g2ZhuZhuangTu', '92huf33fcf', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2ZhuZhuangTu', 'exm/chart/g2ZhuZhuangTu', 'appstore-outlined', NULL, 43, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773440', '1548901111999773426', 'G2漏斗图', 'g2LouDouTu', '7w3gnlts80', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2LouDouTu', 'exm/chart/g2LouDouTu', 'appstore-outlined', NULL, 44, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773441', '1548901111999773426', 'G2折线图', 'g2ZheXianTu', '4g3gr90z1i', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2ZheXianTu', 'exm/chart/g2ZheXianTu', 'appstore-outlined', NULL, 45, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773442', '1548901111999773426', 'G2词云图', 'g2CiYunTu', 'atpbicf8em', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2CiYunTu', 'exm/chart/g2CiYunTu', 'appstore-outlined', NULL, 46, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773443', '1548901111999773426', 'G2面积图', 'g2MianJiTu', 'm5lnxo3d56', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2MianJiTu', 'exm/chart/g2MianJiTu', 'appstore-outlined', NULL, 47, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773444', '1548901111999773426', 'G2饼状图', 'g2BingZhuangTu', 'tmn482a18x', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2BingZhuangTu', 'exm/chart/g2BingZhuangTu', 'appstore-outlined', NULL, 48, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773445', '1548901111999773426', 'G2条形图', 'g2TiaoXingTu', 'aej0gwpe43', 'MENU', '1548901111999770525', 'MENU', '/exm/chart/g2TiaoXingTu', 'exm/chart/g2TiaoXingTu', 'appstore-outlined', NULL, 49, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773976', NULL, '业务', NULL, 'system', 'MODULE', NULL, NULL, NULL, NULL, 'profile-outlined', '#d81b43', 50, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773977', '0', '公司架构', NULL, '3xavzjxt5z', 'MENU', '1548901111999773976', 'CATALOG', '/1nlpdpnief', NULL, 'cluster-outlined', NULL, 51, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773978', '1548901111999773977', '机构管理', 'bizOrg', 'mnt1f21q40', 'MENU', '1548901111999773976', 'MENU', '/biz/org', 'biz/org/index', 'cluster-outlined', NULL, 52, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773979', '1548901111999773977', '人员管理', 'bizUser', '38dptg40lo', 'MENU', '1548901111999773976', 'MENU', '/biz/user', 'biz/user/index', 'user-outlined', NULL, 53, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1548901111999773980', '1548901111999773977', '岗位管理', 'bizPosition', 'l6b7kcqaji', 'MENU', '1548901111999773976', 'MENU', '/biz/position', 'biz/position/index', 'apartment-outlined', NULL, 54, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571129529564758017', '1548901111999773978', '新增机构', NULL, 'bizOrgAdd', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571129929961406466', '1548901111999773978', '批量删除', NULL, 'bizOrgBatchDelete', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571130756155408386', '1548901111999773978', '编辑机构', NULL, 'bizOrgEdit', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571130811058847745', '1548901111999773978', '删除机构', NULL, 'bizOrgDelete', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571130973294526465', '1548901111999773979', '新增人员', NULL, 'bizUserAdd', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571131043532341249', '1548901111999773979', '批量删除', NULL, 'bizUserBatchDelete', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571131137006600193', '1548901111999773979', '编辑人员', NULL, 'bizUserEdit', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571131427361488898', '1548901111999773979', '授权角色', NULL, 'bizUserGrantRole', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571131544973967361', '1548901111999773979', '重置密码', NULL, 'bizUserPwdReset', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 5, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571131727656878081', '1548901111999773979', '删除人员', NULL, 'bizUserDelete', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 6, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571132076853657601', '1548901111999773979', '启用禁用', NULL, 'bizUserUpdataStatus', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 7, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571132393993371649', '1548901111999773980', '新增岗位', NULL, 'bizPositionAdd', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571132468178026497', '1548901111999773980', '批量删除', NULL, 'bizPositionBatchDelete', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571132576143605761', '1548901111999773980', '编辑岗位', NULL, 'bizPositionEdit', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_RESOURCE VALUES ('1571132658851086338', '1548901111999773980', '删除岗位', NULL, 'bizPositionDelete', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 4, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of SYS_ROLE
-- ----------------------------
INSERT INTO SYS_ROLE VALUES ('1570687866138206208', NULL, '超级管理员', 'superAdmin', 'GLOBAL', 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_ROLE VALUES ('1570687866138206209', NULL, '业务管理员', 'bizAdmin', 'GLOBAL', 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of SYS_USER
-- ----------------------------
INSERT INTO SYS_USER VALUES ('1543837863788879871', 'data:image/jpg;base64,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', NULL, 'superAdmin', '207cf410532f92a47dee245ce9b11ff71f578ebd763eb3bbea44ebd043d018fb', '超管', NULL, '男', NULL, '1990-01-01', '汉', '', '', '', '身份证', '', NULL, NULL, NULL, NULL, NULL, NULL, 'eb77186abe605f8de5958df60ef4a279', 'superAdmin@foxmail.com', NULL, NULL, '', '', '', '0000', '2022-07-04', '1543842934270394368', '1543899639134019584', 'C1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ENABLE', 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO SYS_USER VALUES ('1543837863788879873', 'data:image/jpg;base64,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', NULL, 'bizAdmin', '207cf410532f92a47dee245ce9b11ff71f578ebd763eb3bbea44ebd043d018fb', '业管', NULL, '男', NULL, '1995-01-01', '汉', '', '', '', '身份证', '', NULL, NULL, NULL, NULL, NULL, NULL, '9c8f683ccff14071f90f1f51ba83f069', 'bizAdmin@foxmail.com', NULL, NULL, '', '', '', '0001', '2022-07-04', '1543842934270394368', '1543899639134019584', 'C1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ENABLE', 2, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
