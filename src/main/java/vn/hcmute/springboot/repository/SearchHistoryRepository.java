package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hcmute.springboot.model.SearchHistory;
import vn.hcmute.springboot.model.User;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {

  List<SearchHistory> findBySearchKeyWord(String searchKeyWord);


  @Query("SELECT sh FROM SearchHistory sh ORDER BY sh.searchCount DESC")
  List<SearchHistory> findTop8ByOrderBySearchCountDesc();

  @Query("SELECT sh FROM SearchHistory sh WHERE sh.user = ?1 ORDER BY sh.searchCount DESC")
  List<SearchHistory> findTop8ByUserOrderBySearchCountDesc(User user);


  @Query("SELECT sh FROM SearchHistory sh WHERE  sh.user = ?1")
  List<SearchHistory> findByUser(User user);
}
