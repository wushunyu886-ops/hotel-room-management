# 旅馆客房管理系统（Hotel Room Management System）

一个完整可运行的旅馆客房管理系统，覆盖 \*\* 数据库（MySQL 8）→ 后端（Spring Boot 3）→ 前端（Vue 3 + Element Plus）\*\* 三层。

## 一、功能与业务规则

### 房间 / 床位



* 朝向：`SOUTH`（朝南）、`NORTH`（朝北）

* 房型（由朝向 + 床位数决定每日住宿费）：


  * 朝南两人间：2 床，180 元 / 天

  * 朝南三人间：3 床，240 元 / 天

  * 朝北三人间：3 床，210 元 / 天

  * 朝北四人间：4 床，280 元 / 天

* 房间号全局唯一；床号在房间内唯一；新增房间时按房型自动生成床位

### 入住规则



* 空房间可分配给任意性别旅客；非空房间只能续住同性别旅客，禁止混住

* 分配床位校验：床位空闲、房间可用、房间未住满、旅客未在住

* 并发安全：事务 + `SELECT ... FOR UPDATE` 行锁 + 唯一索引兜底 + 乐观锁 `version`

### 退房结算



* 居住天数 = `GREATEST(DATEDIFF(退房日期, 入住日期), 1)`，住一晚算一天、不足一天按一天

* 金额 = 居住天数 × 入住时房价快照（`daily_price_snapshot`）

* 退房后：释放床位、房间无人则恢复空房并清除性别限制、自动生成账单

## 二、技术栈



| 层   | 技术                                                                                                                   |
| --- | -------------------------------------------------------------------------------------------------------------------- |
| 后端  | JDK 17+、Spring Boot 3.5、Spring Web、Validation、MyBatis-Plus 3.5.9、MySQL 8、Lombok、Springdoc OpenAPI 3、JWT(jjwt)、BCrypt |
| 前端  | Vue 3、TypeScript、Vite 5、Element Plus、Vue Router、Pinia、Axios、ECharts、dayjs                                            |
| 数据库 | MySQL 8.0、utf8mb4、utf8mb4\_0900\_ai\_ci、InnoDB、DECIMAL(10,2)、DATE/DATETIME                                           |

## 三、目录结构



```
hotel-room-management/
├── sql/                      # 数据库脚本（按编号顺序执行）
│   ├── 01_schema.sql         # 建库建表（9 张表 + 索引 + 外键）
│   ├── 02_init_data.sql      # 初始化数据（房型/房间/床位/旅客/入住/账单/账号）
│   ├── 03_queries.sql        # 常用业务查询 SQL（10 例）
│   ├── 04_checkin_tx.sql     # 入住事务 SQL 示例
│   ├── 05_checkout_tx.sql    # 退房事务 SQL 示例
│   └── 06_views.sql          # 可选视图（4 个）
├── backend/                  # Spring Boot 后端（Maven 工程）
│   └── src/main/java/com/hotel/room/
│       ├── common/           # 统一响应 Result、状态码、全局异常
│       ├── config/           # MyBatis-Plus/Swagger/WebMvc/启动数据初始化
│       ├── controller/       # RESTful 接口（9 个）
│       ├── dto/              # 入参 DTO（含校验注解）
│       ├── entity/           # 实体（9 个）
│       ├── enums/            # 枚举（性别/朝向/房间/床位/入住/支付/角色）
│       ├── interceptor/      # JWT 登录拦截器
│       ├── mapper/           # MyBatis-Plus Mapper + 自定义 SQL（行锁/统计）
│       ├── service/          # 业务接口 + 实现（入住/退房事务）
│       ├── util/             # JWT/身份证校验/业务校验
│       └── vo/               # 出参 VO
├── frontend/                 # Vue3 前端（Vite 工程）
│   └── src/
│       ├── api/              # Axios 统一封装 + 各模块接口
│       ├── components/       # 通用组件（弹窗/表格/状态标签等）
│       ├── layout/           # 主布局（侧边栏/顶栏/面包屑）
│       ├── router/           # 路由 + 登录守卫
│       ├── stores/           # Pinia（用户/应用状态）
│       ├── types/            # TS 类型定义
│       ├── utils/            # 登录态/格式化/表单校验
│       └── views/            # 10 个页面
└── docs/architecture.html    # 系统架构与 ER 关系可视化（浏览器打开）
```

## 四、数据库配置与初始化



1. 安装并启动 MySQL 8.0，确认端口 `3306`。

2. 按顺序执行脚本（任选其一）：



```
# 方式一：命令行
mysql -uroot -p < sql/01_schema.sql
mysql -uroot -p < sql/02_init_data.sql
mysql -uroot -p < sql/03_queries.sql   # 仅查看示例，可跳过
mysql -uroot -p < sql/06_views.sql     # 可选
```



* `03_queries.sql / 04 / 05` 为示例说明脚本，已含说明注释，可直接在客户端运行学习。

1. 初始化数据包含：4 种房型、8 间示例房间（南 101… 北 202）及对应床位、4 名旅客、3 条在住记录、1 条已退房记录及已支付账单。

2. 默认账号：`admin / 123456`（管理员）、`receptionist / 123456`（前台）。密码以 BCrypt 存储；后端启动时会自动校正 admin 密码为 `123456`（幂等，不影响后续改密）。

## 五、后端启动



```
cd backend
# 1. 确认数据库账号密码与 application.yml 一致（默认已配 root/123456）
#    本机若不同，编辑 src/main/resources/application.yml 中的 spring.datasource.username/password
#    本机 8080 可能被系统进程占用，服务默认运行在 8081，可在 application.yml 中调整 server.port

# 2. 编译并启动（使用 JDK 17+，建议 17~21）
mvn spring-boot:run
# 或打包运行：
mvn -DskipTests package
java -jar target/hotel-room-management-1.0.0.jar
```



* 服务地址：`http://localhost:8081`

* 接口文档（Swagger）：`http://localhost:8081/swagger-ui.html`

* 注意：若本机存在多个 JDK，请确保 `JAVA_HOME` 指向完整 JDK（17\~21），否则可能出现 `javax.naming.NamingException` 或 Lombok 不生效问题。

## 六、前端启动



```
cd frontend
npm install
npm run dev
```



* 开发地址：`http://localhost:5173`（默认自动打开）

* 后端地址在 `.env.development` 中配置：`VITE_API_BASE_URL=http://localhost:8081`（已开启跨域，也可改用 vite proxy）

* 生产构建：`npm run build`，产物在 `dist/`

## 七、主要接口说明

统一返回格式：



```
{ "code": 200, "message": "success", "data": {} }
```



| 方法                  | 路径                            | 说明                  |
| ------------------- | ----------------------------- | ------------------- |
| POST                | /api/auth/login               | 登录，返回 JWT           |
| GET                 | /api/dashboard/stats          | 首页统计                |
| POST/GET/PUT/DELETE | /api/room-types               | 房型 CRUD             |
| GET                 | /api/room-types/all           | 全部启用房型（下拉框）         |
| POST/GET/PUT/DELETE | /api/rooms                    | 房间 CRUD（新增自动生成床位）   |
| GET                 | /api/rooms/{id}/beds          | 某房间床位状态             |
| GET                 | /api/beds                     | 床位分页查询              |
| POST/GET/PUT        | /api/guests                   | 旅客登记 / 查询 / 修改      |
| GET                 | /api/guests/{id}              | 旅客详情                |
| POST                | /api/check-ins                | 办理入住（指定床位或自动推荐）     |
| GET                 | /api/check-ins                | 入住记录分页              |
| GET                 | /api/check-ins/available-beds | 可用床位（按性别 / 朝向 / 房型） |
| POST                | /api/check-ins/{id}/check-out | 办理退房（结算并生成账单）       |
| GET                 | /api/bills                    | 账单分页                |
| GET                 | /api/bills/{id}               | 账单详情                |
| POST                | /api/bills/{id}/pay           | 账单支付（扩展）            |
| GET                 | /api/stats/occupancy          | 入住率统计（按房型 / 性别）     |
| GET                 | /api/stats/income             | 每日收入趋势              |

除登录外所有接口需携带请求头：`Authorization: Bearer <token>`

### 接口示例 1：登录



```
POST /api/auth/login
Content-Type: application/json

{"username":"admin","password":"123456"}
```



```
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "role": "ADMIN"
  }
}
```

### 接口示例 2：办理入住（自动推荐床位）



```
POST /api/check-ins
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestId": 5,
  "checkInDate": "2026-10-11",
  "orientation": "SOUTH",
  "roomTypeId": 1
}
```



```
{
  "code": 200,
  "message": "success",
  "data": {
    "recordId": 5,
    "guestName": "孙七",
    "roomNo": "南102",
    "bedNo": 2,
    "typeName": "朝南两人间",
    "dailyPriceSnapshot": 180.00,
    "status": "CHECKED_IN"
  }
}
```

### 接口示例 3：办理退房



```
POST /api/check-ins/5/check-out
Authorization: Bearer <token>
Content-Type: application/json

{"checkOutDate": "2026-10-12"}
```



```
{
  "code": 200,
  "message": "success",
  "data": {
    "billNo": "B20261012153012123",
    "roomNo": "南102",
    "bedNo": 2,
    "days": 2,
    "dailyPrice": 180.00,
    "totalAmount": 360.00,
    "payStatus": "UNPAID"
  }
}
```

### 接口示例 4：查询可用床位（入住弹窗推荐）



```
GET /api/check-ins/available-beds?gender=MALE&orientation=SOUTH&roomTypeId=1
```



```
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "bedId": 4,
      "bedNo": 2,
      "roomId": 2,
      "roomNo": "南102",
      "orientation": "SOUTH",
      "typeName": "朝南两人间",
      "dailyPrice": 180.00,
      "currentGender": null,
      "bedStatus": "FREE"
    }
  ]
}
```

### 接口示例 5：首页统计



```
GET /api/dashboard/stats
```



```
{
  "code": 200,
  "message": "success",
  "data": {
    "todayCheckIns": 3,
    "todayCheckOuts": 1,
    "todayIncome": 540.00,
    "totalRooms": 8,
    "availableRooms": 6,
    "occupiedRooms": 2,
    "maintenanceRooms": 0,
    "inStayGuests": 3,
    "occupancyRate": 25.00,
    "dueCheckOutList": [],
    "availableRoomList": []
  }
}
```

## 八、并发控制设计



| 机制                      | 说明                           |
| ----------------------- | ---------------------------- |
| `@Transactional`        | 入住 / 退房全程事务，异常整体回滚           |
| `SELECT ... FOR UPDATE` | 锁定床位行、房间行、入住记录行，串行化并发操作      |
| `uk_active_bed_id`      | 唯一索引兜底：同一床位同时只能有一条在住记录       |
| `uk_active_guest_id`    | 唯一索引兜底：同一旅客同时只能有一条在住记录       |
| `room.version` 乐观锁      | 房间状态更新带版本校验，防止更新覆盖           |
| `DuplicateKeyException` | 全局异常处理器转为友好提示 “床位 / 旅客被并发占用” |
| 事务隔离级别                  | 建议 REPEATABLE READ（MySQL 默认） |

## 九、常见问题



1. **后端启动报&#x20;**`javax.naming.NamingException`：JAVA\_HOME 指向了精简版 JDK，请切换到完整 JDK 17\~21（本机可用 IntelliJ 自带的 JBR）。

2. **Lombok 报错找不到 getter**：JDK 24/25 需 Lombok 1.18.40+，推荐使用 JDK 17\~21 编译运行。

3. **登录失败**：确认已执行 `01_schema.sql`、`02_init_data.sql`，且 application.yml 数据库账号密码正确。

4. **前端请求 401**：登录过期或未携带 token，重新登录即可；确认后端已启动且 `VITE_API_BASE_URL` 正确。

5. **删除房型 / 房间失败**：业务规则要求房型未被房间引用、房间无历史入住记录，请先清理关联数据。