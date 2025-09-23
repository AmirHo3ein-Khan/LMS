package ir.lms.mapper;

import ir.lms.model.Major;
import ir.lms.mapper.base.BaseMapper;
import ir.lms.dto.major.MajorDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MajorMapper extends BaseMapper<Major, MajorDTO> {

}
