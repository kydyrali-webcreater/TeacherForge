package kz.teacher.forge.teacherforge.controller.admin;

import kz.teacher.forge.teacherforge.models.Region;
import kz.teacher.forge.teacherforge.models.School;
import kz.teacher.forge.teacherforge.models.dto.SchoolDto;
import kz.teacher.forge.teacherforge.models.dto.SchoolRequest;
import kz.teacher.forge.teacherforge.models.exception.ApiError;
import kz.teacher.forge.teacherforge.models.exception.ApiException;
import kz.teacher.forge.teacherforge.repository.RegionRepository;
import kz.teacher.forge.teacherforge.repository.SchoolRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin/schools")
public class SchoolAdminController {
    private final SchoolRepository schoolRepository;
    private final RegionRepository regionRepository;
    @PostMapping
    public School createSchool(@RequestBody SchoolDto schoolDto) {
        School school = new School(schoolDto);
        return schoolRepository.save(school);
    }

    @PutMapping("/{schoolId}")
    public School editSchool(@PathVariable("schoolId") UUID schoolId,
                             @RequestBody SchoolDto schoolDto) {
        School school =  schoolRepository.findById(schoolId)
                .orElseThrow(()->new ApiException(ApiError.RESOURCE_NOT_FOUND , "not found schoolId: " + schoolDto.getId()));
        Optional.ofNullable(schoolDto.getAddress()).ifPresent(s -> school.setAddress(s));
        Optional.ofNullable(schoolDto.getType()).ifPresent(s -> school.setType(s));
        Optional.ofNullable(schoolDto.getStatus()).ifPresent(s -> school.setStatus(s));
        return schoolRepository.save(school);
    }

    @GetMapping
    @SneakyThrows
    public List<School> getSchoolList(@RequestBody SchoolRequest request){
        List<School> list =  schoolRepository.filter(request.getName() ,
                request.getRegionId(),
                request.getStatus() ,
                request.getType());
        for(School school: list){
           Optional<Region> region =  regionRepository.findById(school.getId());
           if(region.isPresent()) {
               school.setAddress(region.get().getName() + ", " + school.getAddress());
           }
        }
        return list;
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<School> getSchool(@PathVariable("schoolId") UUID schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ApiException(ApiError.RESOURCE_NOT_FOUND , "not found school"));
        return ResponseEntity.ok(school);
    }

    @DeleteMapping("/{schoolId}")
    public ResponseEntity<Object> deleteSchool(@PathVariable("schoolId") UUID schoolId){
        schoolRepository.deleteById(schoolId);
        return ResponseEntity.ok().build();
    }
}
