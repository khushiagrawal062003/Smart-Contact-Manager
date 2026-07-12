package com.smartcontact.repository;

import com.smartcontact.entity.Contact;
import com.smartcontact.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // 1. Pagination for retrieving user's contacts
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    Page<Contact> findContactsByUser(@Param("userId") Long userId, Pageable pageable);

    // 2. Counts for Dashboard
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.user.id = :userId")
    long countContactsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Contact c WHERE c.user.id = :userId AND c.favorite = true")
    long countFavoriteContactsByUserId(@Param("userId") Long userId);

    // 3. Recently Added (Fetch latest 5 contacts)
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId ORDER BY c.cId DESC")
    List<Contact> findTop5ByUser_IdOrderBycIdDesc(@Param("userId") Long userId, Pageable pageable);

    // 4. Category statistics for user
    @Query("SELECT c.category, COUNT(c) FROM Contact c WHERE c.user.id = :userId GROUP BY c.category")
    List<Object[]> countContactsByCategoryGrouped(@Param("userId") Long userId);

    // 5. Global Search: Name, Phone, Email, Company (Case-insensitive, Partial, Paginated)
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND (" +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.work) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Contact> searchGlobal(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    // 6. Multi-Filter Search (All fields optional, Paginated)
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId " +
            "AND (:name IS NULL OR :name = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:phone IS NULL OR :phone = '' OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
            "AND (:email IS NULL OR :email = '' OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:work IS NULL OR :work = '' OR LOWER(c.work) LIKE LOWER(CONCAT('%', :work, '%'))) " +
            "AND (:category IS NULL OR :category = '' OR c.category = :category) " +
            "AND (:favorite IS NULL OR c.favorite = :favorite)")
    Page<Contact> searchMultiFilter(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("work") String work,
            @Param("category") String category,
            @Param("favorite") Boolean favorite,
            Pageable pageable
    );

    // 7. Find all contacts for a user (needed for CSV Export)
    List<Contact> findByUser_Id(Long userId);
}
