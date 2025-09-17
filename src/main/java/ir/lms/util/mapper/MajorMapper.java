package ir.lms.util.mapper;

import ir.lms.model.Major;
import ir.lms.util.mapper.base.BaseMapper;
import ir.lms.util.dto.major.MajorDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MajorMapper extends BaseMapper<Major, MajorDTO> {

}
