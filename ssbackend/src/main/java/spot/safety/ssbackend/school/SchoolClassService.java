package spot.safety.ssbackend.school;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.exception.DuplicateTagException;
import spot.safety.ssbackend.exception.UsernameAlreadyTakenException;
import spot.safety.ssbackend.user.UserService;

@Service
@RequiredArgsConstructor
public class SchoolClassService {
   private final SchoolClassRepository schoolClassRepository;
   private final SchoolService schoolService;
   private final UserService userService;

   public SchoolClass newClass(long id, String name) {
      School school = schoolService.getSchoolById(id);

      if(schoolClassRepository.findByName(name).isPresent()) {
         throw new DuplicateTagException("A class named " + name + "already exists");
      }

      SchoolClass schoolClass = new SchoolClass();
      schoolClass.setName(name);
      schoolClass.setSchool(school);

      schoolClassRepository.save(schoolClass);
      return schoolClass;
   }
}
