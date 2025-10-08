package ir.lms.util.dto.mapper;

import ir.lms.util.dto.OptionDTO;
import ir.lms.util.dto.mapper.base.BaseMapper;
import ir.lms.model.Option;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionMapper extends BaseMapper<Option, OptionDTO> {
}
