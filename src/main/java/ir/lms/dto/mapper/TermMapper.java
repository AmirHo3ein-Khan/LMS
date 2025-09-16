package ir.lms.dto.mapper;

import ir.lms.model.Term;
import ir.lms.dto.mapper.base.BaseMapper;
import ir.lms.dto.term.TermDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TermMapper extends BaseMapper<Term, TermDTO> {
}
