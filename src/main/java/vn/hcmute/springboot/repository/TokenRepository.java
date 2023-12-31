package vn.hcmute.springboot.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hcmute.springboot.model.Token;
import vn.hcmute.springboot.model.User;

public interface TokenRepository extends JpaRepository<Token, Integer>
{
  @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<Token> findAllValidTokenByUser(Integer id);

  List<Token> findByUserAndExpiredTrue(User user);


  Optional<Token> findByToken(String token);

  List<Token> findByUser(User user);
}
