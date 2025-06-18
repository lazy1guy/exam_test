package com.exam.exam_system.repository;

import com.exam.exam_system.entity.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // 添加分页支持
    Page<User> findByRole(String role, Pageable pageable);

    // 添加缓存提示
    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "usersCache")
    })
    Optional<User> findByUsername(String userName);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(@Param("id") Long id);

    // 添加批量查询
    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(@Param("ids") Set<Long> ids);

    @Query(value = "SELECT * FROM users u USE INDEX (idx_username) WHERE u.username = :username", nativeQuery = true)
    Optional<User> findByUsernameWithIndexHint(@Param("username") String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT'")
    List<User> findAllStudents();

    // 查找所有非管理员用户
    @Query("SELECT u FROM User u WHERE u.role != 'ADMIN'")
    List<User> findAllNonAdminUsers();

    // 根据 ID 删除用户
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteUserById(@Param("id") Long id);

    // 更新用户信息
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = :username, u.email = :email, u.role = :role WHERE u.id = :id")
    void updateUser(@Param("id") Long id, @Param("username") String username, @Param("email") String email, @Param("role") String role);
}