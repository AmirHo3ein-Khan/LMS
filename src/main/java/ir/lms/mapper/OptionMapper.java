package ir.lms.mapper;

import ir.lms.dto.option.OptionDTO;
import ir.lms.mapper.base.BaseMapper;
import ir.lms.model.Option;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionMapper extends BaseMapper<Option, OptionDTO> {
}
