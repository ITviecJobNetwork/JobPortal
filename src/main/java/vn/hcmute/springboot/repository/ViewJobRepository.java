package vn.hcmute.springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.ViewJobs;

public interface ViewJobRepository extends JpaRepository<ViewJobs, Integer> {
  Page<ViewJobs> findJobByIsViewedTrue(Pageable pageable);

  ViewJobs findJobByCandidate(User candidate);

  boolean existsByJob(Job job);
  void deleteByJob(Job job);
  @Query("SELECT v FROM ViewJobs v JOIN FETCH v.job j WHERE v.isViewed = true AND j.expireAt >= current_date ORDER BY j.expireAt ASC")
  Page<ViewJobs> findJobByIsViewedAndSortByExpireAt(Pageable pageable);

  @Query("SELECT v FROM ViewJobs v JOIN FETCH v.job j WHERE v.isViewed = true ORDER BY j.createdAt DESC")
  Page<ViewJobs> findJobByIsViewedAndSortByCreatedAt(Pageable pageable);

  @Query("SELECT v FROM ViewJobs v WHERE v.isViewed = true AND v.candidate = :user")
  Page<ViewJobs> findJobsViewedByUser(@Param("user") User user, Pageable pageable);

  @Query("SELECT v FROM ViewJobs v JOIN FETCH v.job j WHERE v.isViewed = true AND j.expireAt >= current_date AND v.candidate = :user ORDER BY j.expireAt ASC")
  Page<ViewJobs> findJobsViewedByUserAndSortByExpireAt(@Param("user") User user, Pageable pageable);

  @Query("SELECT v FROM ViewJobs v JOIN FETCH v.job j WHERE v.isViewed = true AND v.candidate = :user ORDER BY j.createdAt DESC")
  Page<ViewJobs> findJobsViewedByUserAndSortByCreatedAt(@Param("user") User user, Pageable pageable);


}
