package ir.lms.model.dto;

public record AddRoleRequest(
        Long personId,
        String role
) {
}
