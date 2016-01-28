package taboleiro.model.repository.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import taboleiro.model.domain.course.SchoolYear;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SchoolYearRepository extends CrudRepository<SchoolYear, Long> {

    SchoolYear findBySchoolYearName(String schoolYearName);

    SchoolYear findSchoolYearBySchoolYearId(Long schoolYearId);

    Page<SchoolYear> findAll(Pageable pageable);

    List<SchoolYear> findSchoolYearByVisible(Boolean visible);

}
