package ir.lms.dto.mapper;

import ir.lms.model.Major;
import ir.lms.dto.mapper.base.BaseMapper;
import ir.lms.dto.major.MajorDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MajorMapper extends BaseMapper<Major, MajorDTO> {

}
