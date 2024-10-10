package study.jpashop.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.jpashop.domain.Member;
import study.jpashop.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        validatedDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validatedDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw  new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    /**
     * 회원 전체 조회
     **/
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOnd(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
