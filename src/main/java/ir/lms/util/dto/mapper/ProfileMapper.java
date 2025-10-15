package ir.lms.util.dto.mapper;

import ir.lms.model.Person;
import ir.lms.util.dto.UpdateProfileDTO;
import ir.lms.util.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface ProfileMapper extends BaseMapper<Person, UpdateProfileDTO> {
}
