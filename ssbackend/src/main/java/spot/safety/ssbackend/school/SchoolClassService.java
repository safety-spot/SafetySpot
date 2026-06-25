package spot.safety.ssbackend.school;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.dto.school.UpdateSchoolClass;
import spot.safety.ssbackend.dto.user.UserResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.DuplicateTagException;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolClassService {
   private final SchoolClassRepository schoolClassRepository;
   private final SchoolService schoolService;
   private final UserService userService;

   public SchoolClass newClass(long id, String name) {
      School school = schoolService.getSchoolById(id);

      if (schoolClassRepository.existsByNameAndSchoolId(name, id)) {
         throw new DuplicateTagException("A class named '" + name + "' already exists in this school");
      }

      SchoolClass schoolClass = new SchoolClass();
      schoolClass.setName(name);
      schoolClass.setSchool(school);

      schoolClassRepository.save(schoolClass);
      return schoolClass;
   }

   public List<SchoolClass> getClasses(SecurityUser principal) {
      return principal.getUser().getRole() == Role.ADMIN
              ? schoolClassRepository.findAll()
              : schoolClassRepository.findAllByTeacherId(principal.getUser().getId());
   }

   public int getAmountOfStudent(long id, SecurityUser principal) {
      return userService.getUsers(id, UserPrincipal.from(principal.getUser())).size();
   }

   public List<UserResponse> getStudents(long id, SecurityUser principal) {
      return userService.getUsers(id, UserPrincipal.from(principal.getUser()));
   }

   public void updateClass (long id, SecurityUser principal, UpdateSchoolClass request) {
      SchoolClass schoolClass = schoolClassRepository.findById(id)
              .orElseThrow(() -> new spot.safety.ssbackend.exception.EntityNotFoundException("Class not found: " + id));
      if (request.name() != null) {
         schoolClass.setName(request.name());
      }
      if (request.teacherId() != null) {
         UserResponse ur = userService.getUserById(request.teacherId(), UserPrincipal.from(principal.getUser()));
         schoolClass.setTeacher(userService.findByUsername(ur.username()));
      }

      schoolClassRepository.save(schoolClass);
   }

   public void deleteClass(long id, SecurityUser principal) {
      SchoolClass schoolClass = schoolClassRepository.findById(id)
              .orElseThrow(() -> new spot.safety.ssbackend.exception.EntityNotFoundException("Class not found: " + id));

      int amount = userService.getUsers(id, UserPrincipal.from(principal.getUser())).size();
      if (amount != 0) {
         throw new DuplicateTagException("Class still has students");
      }

      schoolClassRepository.delete(schoolClass);
   }

}
