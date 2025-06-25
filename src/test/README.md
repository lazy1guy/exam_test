# 在线作业考试系统 - JUnit5 测试说明

## 测试概述

本项目为在线作业考试系统编写了全面的JUnit5测试用例，覆盖了系统的所有核心功能模块。测试用例按照不同层次进行组织，确保系统的可靠性和稳定性。

## 测试结构

```
src/test/
├── java/com/exam/exam_system/
│   ├── entity/                    # 实体类测试
│   │   ├── UserTest.java         # 用户实体测试
│   │   ├── ExamTest.java         # 考试实体测试
│   │   └── QuestionTest.java     # 题目实体测试
│   ├── repository/               # 数据访问层测试
│   │   ├── UserRepositoryTest.java
│   │   └── ExamRepositoryTest.java
│   ├── service/                  # 服务层测试
│   │   ├── AuthServiceTest.java  # 认证服务测试
│   │   └── ExamServiceTest.java  # 考试服务测试
│   ├── controller/               # 控制器层测试
│   │   ├── AuthControllerTest.java
│   │   └── ExamControllerTest.java
│   ├── util/                     # 工具类测试
│   │   └── JwtTokenProviderTest.java
│   ├── integration/              # 集成测试
│   │   └── ExamSystemIntegrationTest.java
│   ├── ExamSystemTestSuite.java  # 测试套件
│   ├── TestConfiguration.java    # 测试配置
│   └── ExamTestApplicationTests.java
└── resources/
    └── application-test.properties # 测试环境配置
```

## 测试覆盖范围

###  实体层测试
- **UserTest**: 用户实体的属性设置、权限管理、生命周期方法
- **ExamTest**: 考试实体的时间管理、状态控制、题目关联
- **QuestionTest**: 题目实体的不同类型处理、答案格式验证

###  数据访问层测试
- **UserRepositoryTest**: 用户CRUD操作、分页查询、唯一性约束
- **ExamRepositoryTest**: 考试查询、时间范围筛选、教师关联

###  服务层测试
- **AuthServiceTest**: 用户注册、登录、JWT令牌管理、权限验证
- **ExamServiceTest**: 考试流程、答案提交、成绩计算、缓存管理

###  控制器层测试
- **AuthControllerTest**: 认证相关HTTP接口的请求响应处理
- **ExamControllerTest**: 考试相关HTTP接口的参数验证和异常处理

###  工具类测试
- **JwtTokenProviderTest**: JWT令牌生成、验证、解析、安全性

###  集成测试
- **ExamSystemIntegrationTest**: 端到端业务流程、系统集成、并发处理

## 核心功能测试

###  用户管理模块
- 用户注册（用户名/邮箱唯一性验证）
- 用户登录（密码验证、令牌生成）
- 权限管理（学生、教师、管理员角色）
- 用户信息查询和更新

###  考试管理模块
- 考试创建和配置
- 考试时间控制（开始时间、结束时间、时长限制）
- 考试状态管理（未开始、进行中、已结束）
- 考试查询（按教师、按时间、按科目）

###  题目管理模块
- 单选题（选项管理、答案验证）
- 多选题（多答案处理、顺序无关性）
- 判断题（正确/错误验证）
- 简答题（文本答案处理）

###  答题流程模块
- 开始考试（权限检查、时间验证）
- 答案提交（防重复提交、并发控制）
- 自动批改（客观题自动评分）
- 成绩计算（异步处理、缓存更新）

###  安全认证模块
- JWT令牌生成和验证
- 用户会话管理
- 权限控制和访问限制
- 令牌黑名单机制

## 运行测试

### 运行所有测试
```bash
# Maven命令
mvn test

# 运行测试套件说明（不使用JUnit Platform Suite，直接运行所有测试）
mvn test -Dtest="*Test"
```

### 运行特定测试类
```bash
# 运行实体测试
mvn test -Dtest=UserTest,ExamTest,QuestionTest

# 运行服务层测试
mvn test -Dtest=AuthServiceTest,ExamServiceTest

# 运行控制器测试
mvn test -Dtest=AuthControllerTest,ExamControllerTest

# 运行集成测试
mvn test -Dtest=ExamSystemIntegrationTest
```

### 在IDE中运行
1. **运行所有测试**: 右键点击 `ExamSystemTestSuite.java` → Run Tests
2. **运行单个测试类**: 右键点击测试类文件 → Run Tests
3. **运行单个测试方法**: 点击测试方法左侧的运行按钮

## 测试环境配置

### 数据库配置
- 使用H2内存数据库进行测试隔离
- 每次测试后自动清理数据
- 支持SQL日志输出便于调试

### Redis配置
- 使用独立的Redis数据库（database=1）
- 支持缓存测试和令牌黑名单测试

### 安全配置
- 降低密码加密强度以提高测试速度
- 配置测试专用的JWT密钥

## 测试最佳实践

###  测试原则
1. **独立性**: 每个测试用例独立运行，不依赖其他测试
2. **可重复性**: 测试结果一致，可重复执行
3. **快速执行**: 优化测试性能，快速反馈
4. **全面覆盖**: 覆盖正常流程和异常情况

###  Mock使用
- 使用Mockito模拟外部依赖
- 隔离被测试的组件
- 验证方法调用和参数传递

###  断言策略
- 使用JUnit5的断言方法
- 验证返回值、异常、状态变化
- 使用自定义匹配器提高可读性

###  性能测试
- 并发测试验证线程安全性
- 大数据量测试验证性能
- 边界条件测试验证稳定性

## 测试报告

### 生成测试报告
```bash
# 生成Surefire测试报告
mvn surefire-report:report

# 生成覆盖率报告（需要JaCoCo插件）
mvn jacoco:report
```

### 查看测试结果
- 测试报告路径: `target/reports/surefire.html`

## 持续集成

### CI/CD集成
测试用例可以集成到CI/CD流水线中：

```yaml
# GitHub Actions示例
- name: Run Tests
  run: mvn test
  
- name: Generate Test Report
  run: mvn surefire-report:report
  
- name: Upload Test Results
  uses: actions/upload-artifact@v2
  with:
    name: test-results
    path: target/surefire-reports/
```

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查H2数据库配置
   - 确认测试环境配置文件加载正确

2. **Redis连接失败**
   - 检查Redis服务是否启动
   - 确认端口配置正确

3. **JWT令牌测试失败**
   - 检查JWT密钥配置
   - 确认时间设置正确

4. **权限测试失败**
   - 检查Spring Security配置
   - 确认Mock用户设置正确

### 调试技巧

1. **启用详细日志**
   ```properties
   logging.level.com.exam.exam_system=DEBUG
   logging.level.org.springframework.test=DEBUG
   ```

2. **使用@Sql注解**
   ```java
   @Sql("/test-data.sql")
   @Test
   void testWithPreparedData() {
       // 测试代码
   }
   ```

## 贡献指南

### 添加新测试
1. 确定测试类别（单元测试/集成测试）
2. 遵循命名规范（类名+Test）
3. 使用@DisplayName提供清晰的测试描述
4. 添加到相应的测试套件中

### 测试编写规范
1. 使用@BeforeEach进行测试准备
2. 使用@AfterEach进行资源清理
3. 使用有意义的测试方法名
4. 添加必要的注释说明

---