package ir.lms.util.mapper;

import ir.lms.model.Major;
import ir.lms.model.Term;
import ir.lms.repository.MajorRepository;
import ir.lms.util.mapper.base.BaseMapper;
import ir.lms.util.dto.term.TermDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TermMapper implements BaseMapper<Term, TermDTO> {
    @Autowired
    private MajorRepository majorRepository;

    public abstract TermDTO toDto(Term entity);

    public abstract Term toEntity(TermDTO dto);

    @AfterMapping
    protected void afterToEntity(TermDTO dto, @MappingTarget Term term) {
        if (dto.getMajorName() != null) {
            Major major = majorRepository
                    .findByMajorName(dto.getMajorName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Major with name " + dto.getMajorName() + " not found"
                    ));
            term.setMajor(major);
        }
    }

    @AfterMapping
    protected void afterToDTO(Term term, @MappingTarget TermDTO dto) {
        if (term.getMajor().getMajorName() != null) {
            Major major = majorRepository
                    .findByMajorName(term.getMajor().getMajorName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Major with name " + dto.getMajorName() + " not found"
                    ));
            dto.setMajorName(major.getMajorName());
        }
    }
}
