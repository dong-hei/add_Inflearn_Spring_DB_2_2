package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberSvc @트랜잭셔널: off
     * memberRepo @트랜잭셔널: on
     * logRepo  @트랜잭셔널: on
     */
    @Test
    void outerTxOff_success() {
        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //when : 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * memberSvc @트랜잭셔널: on
     * memberRepo @트랜잭셔널: off
     * logRepo  @트랜잭셔널: off
     */
    @Test
    void singleTx() {
        //given
        String username = "singleTx";

        //when
        memberService.joinV1(username);

        //when : 모든 데이터가 정상 저장된다.
        assertTrue (memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * MemberService @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     */
    @Test
    void outerTxOff_fail() {
        //given
        String username = "로그예외_outerTxOff_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
        //then: 완전히 롤백되지 않고, member 데이터가 남아서 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception 터진다
     */
    @Test
    void outerTxOn_fail() {
        //given
        String username = "로그예외_outerTxOn_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
        //then: 모든 데이터가 롤백된다
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }
  /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception 터진다
     */
    @Test
    void recoverException_fail() {
        //given
        String username = "로그예외_recoverException_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);
        //then: 멤버리포지토리를 남겨야 되는데 멤버리포지토리 마저 Empty다
        //Set Rollback Only 설정으로 넘겨지니까 모두 롤백처리된다.
        //RollbackOnly 상황에서 커밋이 발생하면 UnExpectedRollbackException 이 터진다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }
    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        String username = "로그예외_recoverException_success";
        //when
        memberService.joinV2(username);

        //then: 멤버리포지토리를 남겨야 되는데 멤버리포지토리 마저 Empty다
        //Set Rollback Only 설정으로 넘겨지니까 모두 롤백처리된다.
        //RollbackOnly 상황에서 커밋이 발생하면 UnExpectedRollbackException 이 터진다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberSvc @트랜잭셔널: on
     * memberRepo @트랜잭셔널: on
     * logRepo  @트랜잭셔널: on
     */
    @Test
    void outerTxOn_success() {
        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //when : 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }
}