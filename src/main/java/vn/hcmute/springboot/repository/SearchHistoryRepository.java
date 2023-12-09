package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hcmute.springboot.model.SearchHistory;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {
  List<SearchHistory> findBySearchKeyWord(String searchKeyWord);

  @Query("SELECT keyword FROM search_history ORDER BY search_count DESC LIMIT 8")
  List<SearchHistory> findTop8ByOrderBySearchCountDesc();

  @Query(value = "SELECT * FROM search_history WHERE user_id = ?1 ORDER BY search_count DESC LIMIT 8")
  List<SearchHistory> findTop8ByUserIdOrderBySearchCountDesc(Integer userId);
}
