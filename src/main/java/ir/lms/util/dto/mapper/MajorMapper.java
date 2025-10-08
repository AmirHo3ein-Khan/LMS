package ir.lms.util.dto.mapper;

import ir.lms.model.Major;
import ir.lms.util.dto.mapper.base.BaseMapper;
import ir.lms.util.dto.MajorDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MajorMapper extends BaseMapper<Major, MajorDTO> {

}
