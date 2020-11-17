package com.pensasha.school.finance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AmountPayableRepository extends JpaRepository<AmountPayable, Integer> {

	AmountPayable findByTermFormsStudentsSchoolCodeAndTermFormsFormAndTermTerm(int code, int form, int term);

}
